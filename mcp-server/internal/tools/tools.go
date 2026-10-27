// Package tools implements the MCP tools on top of the generated tool
// definitions and API client in internal/gen.
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
	"strconv"
	"strings"
	"time"
	"unicode"

	"github.com/mark3labs/mcp-go/mcp"
	"github.com/mark3labs/mcp-go/server"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/gen/apiclient"
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
type call func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error)

type definition struct {
	tool func() mcp.Tool
	call call
}

type clientIPKey struct{}

// WithClientIP stores the address of the end user, as reported by the ingress, so it can be passed on to the gateway.
func WithClientIP(ctx context.Context, ip string) context.Context {
	return context.WithValue(ctx, clientIPKey{}, ip)
}

// argumentError is an invalid tool argument, reported to the model so it can correct the call.
type argumentError struct{ err error }

func (e argumentError) Error() string { return e.err.Error() }

// Register adds all tools to s.
func Register(s *server.MCPServer, opts Options) {
	for _, d := range definitions {
		tool := d.tool()
		tool.Name = toolName(tool.Name)
		s.AddTool(tool, handler(tool.Name, d.call, opts))
	}
}

// Names returns the names of all implemented tools.
func Names() []string {
	names := make([]string, 0, len(definitions))
	for _, d := range definitions {
		names = append(names, toolName(d.tool().Name))
	}
	return names
}

// toolName converts the CamelCase name mcpgen derives from the operationId back into the snake_case operationId.
func toolName(generated string) string {
	var b strings.Builder
	for i, r := range generated {
		if unicode.IsUpper(r) {
			if i > 0 {
				b.WriteByte('_')
			}
			r = unicode.ToLower(r)
		}
		b.WriteRune(r)
	}
	return b.String()
}

func handler(name string, fn call, opts Options) server.ToolHandlerFunc {
	return func(ctx context.Context, req mcp.CallToolRequest) (*mcp.CallToolResult, error) {
		start := time.Now()
		requestID := newRequestID()
		principal, _ := auth.PrincipalFrom(ctx)
		attrs := []any{"tool", name, "sub", principal.Subject, "request_id", requestID}
		if opts.LogArguments {
			attrs = append(attrs, "arguments", req.GetArguments())
		}

		edit := func(_ context.Context, r *http.Request) error {
			if principal.Token != "" {
				r.Header.Set("Authorization", "Bearer "+principal.Token)
			}
			r.Header.Set("X-Request-Id", requestID)
			// Lets the gateway rate limit per end user instead of per MCP server pod when it trusts X-Real-IP.
			if ip, ok := ctx.Value(clientIPKey{}).(string); ok && ip != "" {
				r.Header.Set("X-Real-IP", ip)
			}
			return nil
		}
		res, err := fn(ctx, opts.Client, req, edit)
		if err != nil {
			var argErr argumentError
			if errors.As(err, &argErr) {
				opts.Logger.Info("tool call", append(attrs, "outcome", "invalid_arguments", "duration", time.Since(start))...)
				return mcp.NewToolResultError("Invalid arguments: " + argErr.Error()), nil
			}
			opts.Logger.Error("tool call", append(attrs, "outcome", "upstream_unreachable", "error", err, "duration", time.Since(start))...)
			return mcp.NewToolResultError("The Renku API could not be reached. Try again later."), nil
		}
		defer func() { _ = res.Body.Close() }()
		body, err := io.ReadAll(io.LimitReader(res.Body, maxResponseBytes+1))
		if err != nil {
			opts.Logger.Error("tool call", append(attrs, "outcome", "upstream_read_failed", "status", res.StatusCode, "error", err, "duration", time.Since(start))...)
			return mcp.NewToolResultError("The response of the Renku API could not be read. Try again later."), nil
		}
		attrs = append(attrs, "status", res.StatusCode, "duration", time.Since(start))

		switch {
		case res.StatusCode >= 500:
			opts.Logger.Error("tool call", append(attrs, "outcome", "upstream_error", "body", truncate(string(body), 1000))...)
			return mcp.NewToolResultError(fmt.Sprintf("The Renku API failed with status %d. Try again later.", res.StatusCode)), nil
		case res.StatusCode >= 400:
			opts.Logger.Info("tool call", append(attrs, "outcome", "client_error")...)
			return mcp.NewToolResultError(fmt.Sprintf("The Renku API rejected the request with status %d: %s", res.StatusCode, errorMessage(body))), nil
		case len(body) > maxResponseBytes:
			opts.Logger.Warn("tool call", append(attrs, "outcome", "response_too_large")...)
			return mcp.NewToolResultError("The response of the Renku API is too large. Narrow down the request, e.g. with a smaller per_page."), nil
		}
		opts.Logger.Info("tool call", append(attrs, "outcome", "ok")...)
		return successResult(res, body), nil
	}
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
			Content:           []mcp.Content{mcp.NewTextContent(fmt.Sprintf("Done (status %d).", res.StatusCode))},
			StructuredContent: map[string]any{"status": res.StatusCode},
		}
	}
	var compact bytes.Buffer
	if err := json.Compact(&compact, body); err != nil {
		// Not JSON, e.g. plain text; pass it through as is.
		return mcp.NewToolResultText(string(body))
	}
	structured := map[string]any{"data": json.RawMessage(compact.Bytes())}
	text := compact.String()
	if p := paginationFrom(res.Header); p != nil {
		structured["pagination"] = p
		text += "\n\n" + p.summary()
	}
	return &mcp.CallToolResult{
		Content:           []mcp.Content{mcp.NewTextContent(text)},
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
