// Package tools implements the MCP tools defined in apispec/mcp-tools.json on
// top of the generated API client in internal/apiclient.
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
	"slices"
	"strconv"
	"strings"
	"time"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/apispec"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/apiclient"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
)

// maxResponseBytes caps how much of an API response is read, to protect the server and the model's context.
const maxResponseBytes = 4 << 20

// Options configures how tools call the Renku API.
type Options struct {
	Client *apiclient.Client
	Logger *slog.Logger
	// LogArguments adds the tool arguments to the per-call log line. They can contain free text entered by users.
	LogArguments bool
}

// call performs the API request of one tool. Argument errors are reported back to the model.
type call func(ctx context.Context, c *apiclient.Client, args map[string]any, edit apiclient.RequestEditorFn) (*http.Response, error)

// definition is one tool of apispec/mcp-tools.json.
type definition struct {
	Name        string          `json:"name"`
	Description string          `json:"description"`
	InputSchema json.RawMessage `json:"inputSchema"`
}

// argumentError is an invalid tool argument, reported to the model so it can correct the call.
type argumentError struct{ err error }

func (e argumentError) Error() string { return e.err.Error() }

// Register adds the tools of apispec/mcp-tools.json to s. It fails unless every tool there is implemented by calls and vice versa.
func Register(s *mcp.Server, opts Options) error {
	var definitions []definition
	if err := json.Unmarshal(apispec.MCPTools, &definitions); err != nil {
		return fmt.Errorf("parsing apispec/mcp-tools.json: %w", err)
	}
	for _, d := range definitions {
		fn, ok := calls[d.Name]
		if !ok {
			return fmt.Errorf("tool %s of apispec/mcp-tools.json is not implemented", d.Name)
		}
		s.AddTool(&mcp.Tool{Name: d.Name, Description: d.Description, InputSchema: d.InputSchema}, handler(d.Name, fn, opts))
	}
	for name := range calls {
		if !slices.ContainsFunc(definitions, func(d definition) bool { return d.Name == name }) {
			return fmt.Errorf("tool %s is implemented but missing from apispec/mcp-tools.json", name)
		}
	}
	return nil
}

func handler(name string, fn call, opts Options) mcp.ToolHandler {
	return func(ctx context.Context, req *mcp.CallToolRequest) (result *mcp.CallToolResult, _ error) {
		start := time.Now()
		requestID := newRequestID()
		principal, clientIP := caller(ctx, req)
		attrs := []any{"tool", name, "sub", principal.Subject, "request_id", requestID}
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

		edit := func(_ context.Context, r *http.Request) error {
			if principal.Token != "" {
				r.Header.Set("Authorization", "Bearer "+principal.Token)
			}
			r.Header.Set("X-Request-Id", requestID)
			// Lets the gateway rate limit per end user instead of per MCP server pod when it trusts X-Real-IP.
			if clientIP != "" {
				r.Header.Set("X-Real-IP", clientIP)
			}
			return nil
		}
		var res *http.Response
		if err == nil {
			res, err = fn(ctx, opts.Client, args, edit)
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

// caller returns the user a tool is called for and their address as reported by the ingress.
// Over HTTP both come from the request, over stdio the principal is stored in ctx.
func caller(ctx context.Context, req *mcp.CallToolRequest) (auth.Principal, string) {
	var principal auth.Principal
	var ok bool
	var clientIP string
	if req.Extra != nil {
		principal, ok = auth.PrincipalFromTokenInfo(req.Extra.TokenInfo)
		clientIP = req.Extra.Header.Get("X-Real-IP")
	}
	if !ok {
		principal, _ = auth.PrincipalFrom(ctx)
	}
	return principal, clientIP
}

// arguments decodes the raw tool arguments, which the SDK does not validate for tools with a raw input schema.
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
