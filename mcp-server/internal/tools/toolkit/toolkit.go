// Package toolkit holds what the tool packages share: forwarding the caller to the Renku API
// and turning failed API calls into tool errors the model can act on.
package toolkit

import (
	"context"
	"crypto/rand"
	"encoding/hex"
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
	"strings"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
)

// ErrUnreachable is returned by tools when the Renku API could not be reached.
var ErrUnreachable = errors.New("The Renku API could not be reached. Try again later.") //nolint:staticcheck // Shown to the model as is.

// CallerHeaders returns a request editor that sends an API request on behalf of the caller of req.
// It is assignable to the RequestEditorFn of every generated client, e.g.
// c.GetProjectsProjectIdWithResponse(ctx, id, nil, toolkit.CallerHeaders(req)).
// Create it once per tool call, so that all API requests of the call share the request ID.
func CallerHeaders(req *mcp.CallToolRequest) func(context.Context, *http.Request) error {
	var principal auth.Principal
	var fromRequest bool
	var clientIP, requestID string
	if req.Extra != nil {
		principal, fromRequest = auth.PrincipalFromTokenInfo(req.Extra.TokenInfo)
		clientIP = req.Extra.Header.Get("X-Real-IP")
		// Set by the HTTP server, so that the HTTP request and the tool call are logged with the same ID.
		requestID = req.Extra.Header.Get("X-Request-Id")
	}
	if requestID == "" {
		requestID = newRequestID()
	}
	return func(ctx context.Context, r *http.Request) error {
		p := principal
		// Over stdio the principal is stored in the context of the call, which is the context of the API request.
		if !fromRequest {
			p, _ = auth.PrincipalFrom(ctx)
		}
		if p.Token != "" {
			r.Header.Set("Authorization", "Bearer "+string(p.Token))
		}
		r.Header.Set("X-Request-Id", requestID)
		// Lets the gateway rate limit per end user instead of per MCP server pod when it trusts X-Real-IP.
		if clientIP != "" {
			r.Header.Set("X-Real-IP", clientIP)
		}
		return nil
	}
}

// APIError is the tool error for an API response that is not a success. Client errors carry the
// API's message, so the model can correct the call; server errors do not, as they can leak internals.
func APIError(status int, body []byte) error {
	if status >= 500 {
		return fmt.Errorf("The Renku API failed with status %d. Try again later.", status) //nolint:staticcheck // Shown to the model as is.
	}
	return fmt.Errorf("The Renku API rejected the request with status %d: %s", status, errorMessage(body)) //nolint:staticcheck // Shown to the model as is.
}

// errorMessage extracts the message of a data-services error response.
func errorMessage(body []byte) string {
	var e struct {
		Error struct {
			Message string `json:"message"`
			Detail  string `json:"detail"`
		} `json:"error"`
	}
	if err := json.Unmarshal(body, &e); err == nil && e.Error.Message != "" {
		if e.Error.Detail != "" {
			return e.Error.Message + " " + e.Error.Detail
		}
		return e.Error.Message
	}
	if text := strings.TrimSpace(string(body)); text != "" {
		return truncate(text, 500)
	}
	return "no details"
}

func truncate(s string, n int) string {
	if len(s) <= n {
		return s
	}
	return s[:n] + "..."
}

func newRequestID() string {
	b := make([]byte, 16)
	_, _ = rand.Read(b)
	return hex.EncodeToString(b)
}
