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
	"strconv"
	"strings"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
)

// Caller is who a tool call is made for.
type Caller struct {
	Principal auth.Principal
	// ClientIP is the user's address as reported by the ingress, empty over stdio.
	ClientIP string
	// RequestID identifies the call in the logs of the MCP server and the Renku API.
	RequestID string
}

type callerKey struct{}

// WithCaller returns a copy of ctx that CallerOf takes the caller from, so that everything
// handling one call uses the same request ID.
func WithCaller(ctx context.Context, c Caller) context.Context {
	return context.WithValue(ctx, callerKey{}, c)
}

// CallerOf returns the caller of req, as stored by WithCaller. Otherwise, over HTTP, it comes
// from the request; over stdio the principal is stored in ctx and the request ID is generated.
func CallerOf(ctx context.Context, req *mcp.CallToolRequest) Caller {
	if c, ok := ctx.Value(callerKey{}).(Caller); ok {
		return c
	}
	var c Caller
	var ok bool
	if req.Extra != nil {
		c.Principal, ok = auth.PrincipalFromTokenInfo(req.Extra.TokenInfo)
		c.ClientIP = req.Extra.Header.Get("X-Real-IP")
		// Set by the HTTP server, so that the HTTP request and the tool call are logged with the same ID.
		c.RequestID = req.Extra.Header.Get("X-Request-Id")
	}
	if !ok {
		c.Principal, _ = auth.PrincipalFrom(ctx)
	}
	if c.RequestID == "" {
		c.RequestID = newRequestID()
	}
	return c
}

// CallerHeaders returns a request editor that sends an API request on behalf of the caller of req.
// It is assignable to the RequestEditorFn of every generated client, e.g.
// c.GetProjectsProjectIdWithResponse(ctx, id, nil, toolkit.CallerHeaders(ctx, req)).
// Create it once per tool call, so that all API requests of the call share the request ID.
func CallerHeaders(ctx context.Context, req *mcp.CallToolRequest) func(context.Context, *http.Request) error {
	c := CallerOf(ctx, req)
	return func(_ context.Context, r *http.Request) error {
		if c.Principal.Token != "" {
			r.Header.Set("Authorization", "Bearer "+string(c.Principal.Token))
		}
		r.Header.Set("X-Request-Id", c.RequestID)
		// Lets the gateway rate limit per end user instead of per MCP server pod when it trusts X-Real-IP.
		if c.ClientIP != "" {
			r.Header.Set("X-Real-IP", c.ClientIP)
		}
		return nil
	}
}

// Outcomes of failed tool calls, as logged.
const (
	OutcomeUnreachable      = "upstream_unreachable"
	OutcomeResponseTooLarge = "response_too_large"
	OutcomeUpstreamError    = "upstream_error"
	OutcomeClientError      = "client_error"
)

// Error is a failed API call. Its message is shown to the model, the other fields are only logged.
type Error struct {
	Message string
	Outcome string
	// Status is the status of the API response, 0 if there was none.
	Status int
	// Body is the body of a server error response, which is not shown to the model.
	Body []byte
	// Err is the error of a request without a response.
	Err error
}

func (e *Error) Error() string { return e.Message }

func (e *Error) Unwrap() error { return e.Err }

// RequestFailed is the tool error for an API request that returned err instead of a response.
func RequestFailed(err error) error {
	if errors.Is(err, renkuapi.ErrResponseTooLarge) {
		return &Error{
			Message: "The response of the Renku API is too large. Narrow down the request, e.g. with a smaller per_page.",
			Outcome: OutcomeResponseTooLarge,
			Err:     err,
		}
	}
	return &Error{Message: "The Renku API could not be reached. Try again later.", Outcome: OutcomeUnreachable, Err: err}
}

// APIError is the tool error for an API response that is not a success. Client errors carry the
// API's message, so the model can correct the call; server errors do not, as they can leak internals.
func APIError(status int, body []byte) error {
	if status >= 500 {
		return &Error{
			Message: fmt.Sprintf("The Renku API failed with status %d. Try again later.", status),
			Outcome: OutcomeUpstreamError,
			Status:  status,
			Body:    body,
		}
	}
	return &Error{
		Message: fmt.Sprintf("The Renku API rejected the request with status %d: %s", status, errorMessage(body)),
		Outcome: OutcomeClientError,
		Status:  status,
	}
}

// Prefixed returns err with prefix before its message, keeping what is logged of a *Error.
func Prefixed(err error, prefix string) error {
	var e *Error
	if errors.As(err, &e) {
		c := *e
		c.Message = prefix + c.Message
		return &c
	}
	return fmt.Errorf("%s%w", prefix, err)
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
		return Truncate(text, 500)
	}
	return "no details"
}

// Pagination is the paging information returned by list endpoints.
type Pagination struct {
	Page       int `json:"page"`
	PerPage    int `json:"per_page"`
	Total      int `json:"total"`
	TotalPages int `json:"total_pages"`
}

// PaginationFrom reads the paging headers of a list response, or returns nil if they are missing.
func PaginationFrom(h http.Header) *Pagination {
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

// Summary tells the model where it is in the list and how to get the next page.
func (p *Pagination) Summary() string {
	s := fmt.Sprintf("Page %d of %d (%d total, %d per page).", p.Page, p.TotalPages, p.Total, p.PerPage)
	if p.Page < p.TotalPages {
		s += fmt.Sprintf(" Call again with page=%d for more.", p.Page+1)
	}
	return s
}

// List is the output of a list tool. MCP clients expect an object as structured output, not an array.
type List[T any] struct {
	Items []T `json:"items"`
	// Pagination is nil if the API returned no paging headers.
	Pagination *Pagination `json:"pagination,omitempty"`
}

// ListResult returns the result of a list tool for the items of a response with headers h. The
// pagination summary is added after the items, so the model knows to ask for the next page.
// The output is a *List[T].
func ListResult[T any](items []T, h http.Header) (*mcp.CallToolResult, any, error) {
	out := &List[T]{Items: items, Pagination: PaginationFrom(h)}
	if out.Items == nil {
		out.Items = []T{}
	}
	if out.Pagination == nil {
		// The SDK returns out as JSON text.
		return nil, out, nil
	}
	return ResultWithNote(out, out.Pagination.Summary())
}

// ResultWithNote returns out, the output of a tool, as JSON text followed by note, which tells the
// model what to do next.
func ResultWithNote(out any, note string) (*mcp.CallToolResult, any, error) {
	text, err := json.Marshal(out)
	if err != nil {
		return nil, nil, fmt.Errorf("encoding the result: %w", err)
	}
	res := &mcp.CallToolResult{Content: []mcp.Content{
		&mcp.TextContent{Text: string(text)},
		&mcp.TextContent{Text: note},
	}}
	return res, out, nil
}

// Truncate shortens s to n bytes, marking it with "..." if it was cut.
func Truncate(s string, n int) string {
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
