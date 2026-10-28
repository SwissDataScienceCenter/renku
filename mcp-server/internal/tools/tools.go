// Package tools serves the operations allowlisted in apispec.Tools as MCP tools, see internal/toolspec.
package tools

import (
	"bytes"
	"context"
	"crypto/rand"
	"encoding/hex"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"log/slog"
	"net/http"
	"runtime/debug"
	"strconv"
	"strings"
	"time"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/apispec"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/toolspec"
)

// maxResponseBytes caps how much of an API response is read, to protect the server and the model's context.
const maxResponseBytes = 4 << 20

// Options configures how tools call the Renku API.
type Options struct {
	// APIURL is the base URL of the Renku data API, e.g. http://renku-gateway/api/data.
	APIURL     string
	HTTPClient *http.Client
	Logger     *slog.Logger
	// LogArguments adds the tool arguments to the per-call log line. They can contain free text entered by users.
	LogArguments bool
}

// argumentError is an invalid tool argument, reported to the model so it can correct the call.
type argumentError struct{ err error }

func (e argumentError) Error() string { return e.err.Error() }

// Register adds a tool for every operation allowlisted in apispec.Tools to s.
func Register(s *mcp.Server, opts Options) error {
	tools, err := toolspec.Build(apispec.Tools)
	if err != nil {
		return fmt.Errorf("building the tools: %w", err)
	}
	for _, t := range tools {
		s.AddTool(&mcp.Tool{Name: t.Name, Description: t.Description, InputSchema: t.InputSchema}, handler(t, opts))
	}
	return nil
}

func handler(tool toolspec.Tool, opts Options) mcp.ToolHandler {
	return func(ctx context.Context, req *mcp.CallToolRequest) (result *mcp.CallToolResult, _ error) {
		start := time.Now()
		principal, clientIP, requestID := caller(ctx, req)
		attrs := []any{"tool", tool.Name, "sub", principal.Subject, "request_id", requestID}
		// The SDK does not recover panics of tool handlers, which would take down the whole server.
		defer func() {
			if r := recover(); r != nil {
				opts.Logger.Error("tool call", append(attrs, "outcome", "panic", "error", r, "stack", string(debug.Stack()), "duration", time.Since(start))...)
				result = toolError("The tool failed unexpectedly. Try again later.")
			}
		}()
		args, err := arguments(req)
		if opts.LogArguments {
			attrs = append(attrs, "arguments", args)
		}

		var res *http.Response
		if err == nil {
			res, err = send(ctx, tool, opts, args, func(r *http.Request) {
				if principal.Token != "" {
					r.Header.Set("Authorization", "Bearer "+principal.Token)
				}
				r.Header.Set("X-Request-Id", requestID)
				// Lets the gateway rate limit per end user instead of per MCP server pod when it trusts X-Real-IP.
				if clientIP != "" {
					r.Header.Set("X-Real-IP", clientIP)
				}
			})
		}
		if err != nil {
			var argErr argumentError
			if errors.As(err, &argErr) {
				opts.Logger.Info("tool call", append(attrs, "outcome", "invalid_arguments", "duration", time.Since(start))...)
				return toolError("Invalid arguments: " + argErr.Error()), nil
			}
			opts.Logger.Error("tool call", append(attrs, "outcome", "upstream_unreachable", "error", err, "duration", time.Since(start))...)
			return toolError("The Renku API could not be reached. Try again later."), nil
		}
		defer func() { _ = res.Body.Close() }()
		body, err := io.ReadAll(io.LimitReader(res.Body, maxResponseBytes+1))
		if err != nil {
			opts.Logger.Error("tool call", append(attrs, "outcome", "upstream_read_failed", "status", res.StatusCode, "error", err, "duration", time.Since(start))...)
			return toolError("The response of the Renku API could not be read. Try again later."), nil
		}
		attrs = append(attrs, "status", res.StatusCode, "duration", time.Since(start))

		switch {
		case res.StatusCode >= 500:
			opts.Logger.Error("tool call", append(attrs, "outcome", "upstream_error", "body", truncate(string(body), 1000))...)
			return toolError(fmt.Sprintf("The Renku API failed with status %d. Try again later.", res.StatusCode)), nil
		case res.StatusCode >= 400:
			opts.Logger.Info("tool call", append(attrs, "outcome", "client_error")...)
			return toolError(fmt.Sprintf("The Renku API rejected the request with status %d: %s", res.StatusCode, errorMessage(body))), nil
		case len(body) > maxResponseBytes:
			opts.Logger.Warn("tool call", append(attrs, "outcome", "response_too_large")...)
			return toolError("The response of the Renku API is too large. Narrow down the request, e.g. with a smaller per_page."), nil
		}
		opts.Logger.Info("tool call", append(attrs, "outcome", "ok")...)
		return successResult(res, body), nil
	}
}

// caller returns the user a tool is called for, their address as reported by the ingress and the request ID.
// Over HTTP all come from the request, over stdio the principal is stored in ctx and the request ID is generated.
func caller(ctx context.Context, req *mcp.CallToolRequest) (auth.Principal, string, string) {
	var principal auth.Principal
	var ok bool
	var clientIP, requestID string
	if req.Extra != nil {
		principal, ok = auth.PrincipalFromTokenInfo(req.Extra.TokenInfo)
		clientIP = req.Extra.Header.Get("X-Real-IP")
		// Set by the HTTP server, so that the HTTP request and the tool call are logged with the same ID.
		requestID = req.Extra.Header.Get("X-Request-Id")
	}
	if !ok {
		principal, _ = auth.PrincipalFrom(ctx)
	}
	if requestID == "" {
		requestID = newRequestID()
	}
	return principal, clientIP, requestID
}

// send performs the API request of a call. Arguments that cannot be put into a request are returned as argumentError.
func send(ctx context.Context, tool toolspec.Tool, opts Options, args map[string]any, edit func(*http.Request)) (*http.Response, error) {
	r, err := tool.Request(ctx, opts.APIURL, args)
	if err != nil {
		return nil, argumentError{err}
	}
	edit(r)
	return opts.HTTPClient.Do(r)
}

// arguments decodes the raw tool arguments. They are not validated against the input schema, the API validates them.
func arguments(req *mcp.CallToolRequest) (map[string]any, error) {
	args := map[string]any{}
	if len(req.Params.Arguments) > 0 {
		if err := json.Unmarshal(req.Params.Arguments, &args); err != nil {
			return nil, argumentError{fmt.Errorf("arguments must be a JSON object: %w", err)}
		}
	}
	return args, nil
}

func toolError(message string) *mcp.CallToolResult {
	return &mcp.CallToolResult{IsError: true, Content: []mcp.Content{&mcp.TextContent{Text: message}}}
}

// Pagination is the paging information returned by list endpoints.
type Pagination struct {
	Page       int `json:"page"`
	PerPage    int `json:"per_page"`
	Total      int `json:"total"`
	TotalPages int `json:"total_pages"`
}

func paginationFrom(h http.Header) *Pagination {
	// Most endpoints use page/per-page/total/total-pages, the search API uses x- prefixed headers.
	for _, prefix := range []string{"", "x-"} {
		page, err1 := strconv.Atoi(h.Get(prefix + "page"))
		perPage, err2 := strconv.Atoi(h.Get(prefix + "per-page"))
		total, err3 := strconv.Atoi(h.Get(prefix + "total"))
		totalPages, err4 := strconv.Atoi(h.Get(prefix + "total-pages"))
		if err1 == nil && err2 == nil && err3 == nil && err4 == nil {
			return &Pagination{Page: page, PerPage: perPage, Total: total, TotalPages: totalPages}
		}
	}
	return nil
}

func successResult(res *http.Response, body []byte) *mcp.CallToolResult {
	if len(bytes.TrimSpace(body)) == 0 {
		return &mcp.CallToolResult{
			Content:           []mcp.Content{&mcp.TextContent{Text: fmt.Sprintf("Done (status %d).", res.StatusCode)}},
			StructuredContent: map[string]any{"status": res.StatusCode},
		}
	}
	var compact bytes.Buffer
	if err := json.Compact(&compact, body); err != nil {
		// Not JSON, e.g. plain text; pass it through as is.
		return &mcp.CallToolResult{Content: []mcp.Content{&mcp.TextContent{Text: string(body)}}}
	}
	structured := map[string]any{"data": json.RawMessage(compact.Bytes())}
	text := compact.String()
	if p := paginationFrom(res.Header); p != nil {
		structured["pagination"] = p
		text += "\n\n" + p.summary()
	}
	return &mcp.CallToolResult{
		Content:           []mcp.Content{&mcp.TextContent{Text: text}},
		StructuredContent: structured,
	}
}

func (p *Pagination) summary() string {
	s := fmt.Sprintf("Page %d of %d (%d total, %d per page).", p.Page, p.TotalPages, p.Total, p.PerPage)
	if p.Page < p.TotalPages {
		s += fmt.Sprintf(" Call again with page=%d for more.", p.Page+1)
	}
	return s
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
