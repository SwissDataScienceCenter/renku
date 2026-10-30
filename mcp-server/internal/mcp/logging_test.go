package mcp

import (
	"bytes"
	"context"
	"encoding/json"
	"log/slog"
	"net/http"
	"strings"
	"testing"

	sdkmcp "github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

type echoInput struct {
	Text string `json:"text,omitempty"`
}

// loggedSession connects a client, as user-1, to a server with tools that succeed, fail and panic behind
// LogToolCalls. requestIDs receives the request ID each call of the echo tool sees.
func loggedSession(t *testing.T, opts LogOptions) (*sdkmcp.ClientSession, *[]string) {
	t.Helper()
	ctx, cancel := context.WithCancel(context.Background())
	t.Cleanup(cancel)
	var requestIDs []string
	s := sdkmcp.NewServer(&sdkmcp.Implementation{Name: "test", Version: "dev"}, nil)
	sdkmcp.AddTool(s, &sdkmcp.Tool{Name: "echo"}, func(ctx context.Context, req *sdkmcp.CallToolRequest, in echoInput) (*sdkmcp.CallToolResult, any, error) {
		requestIDs = append(requestIDs, toolkit.CallerOf(ctx, req).RequestID)
		return &sdkmcp.CallToolResult{Content: []sdkmcp.Content{&sdkmcp.TextContent{Text: in.Text}}}, nil, nil
	})
	sdkmcp.AddTool(s, &sdkmcp.Tool{Name: "upstream_error"}, func(context.Context, *sdkmcp.CallToolRequest, echoInput) (*sdkmcp.CallToolResult, any, error) {
		return nil, nil, toolkit.APIError(http.StatusInternalServerError, []byte(`{"error": {"message": "database exploded at 10.0.0.12"}}`))
	})
	sdkmcp.AddTool(s, &sdkmcp.Tool{Name: "panic"}, func(context.Context, *sdkmcp.CallToolRequest, echoInput) (*sdkmcp.CallToolResult, any, error) {
		panic("boom")
	})
	s.AddReceivingMiddleware(LogToolCalls(opts))
	serverTransport, clientTransport := sdkmcp.NewInMemoryTransports()
	go func() {
		_ = s.Run(auth.WithPrincipal(ctx, auth.Principal{Subject: "user-1", Token: "the-token"}), serverTransport)
	}()
	c, err := sdkmcp.NewClient(&sdkmcp.Implementation{Name: "test", Version: "1"}, nil).Connect(ctx, clientTransport, nil)
	if err != nil {
		t.Fatal(err)
	}
	t.Cleanup(func() { _ = c.Close() })
	return c, &requestIDs
}

func call(t *testing.T, c *sdkmcp.ClientSession, name string, args map[string]any) *sdkmcp.CallToolResult {
	t.Helper()
	res, err := c.CallTool(context.Background(), &sdkmcp.CallToolParams{Name: name, Arguments: args})
	if err != nil {
		t.Fatal(err)
	}
	return res
}

func text(t *testing.T, res *sdkmcp.CallToolResult) string {
	t.Helper()
	if len(res.Content) != 1 {
		t.Fatalf("expected one content item, got %d", len(res.Content))
	}
	c, ok := res.Content[0].(*sdkmcp.TextContent)
	if !ok {
		t.Fatalf("expected text content, got %T", res.Content[0])
	}
	return c.Text
}

// logLine decodes the only log line in logs.
func logLine(t *testing.T, logs *bytes.Buffer) map[string]any {
	t.Helper()
	var line map[string]any
	if err := json.Unmarshal(logs.Bytes(), &line); err != nil {
		t.Fatalf("expected one JSON log line, got %q", logs.String())
	}
	return line
}

func TestLogsSubjectAndRequestIDButNotTokenOrArguments(t *testing.T) {
	var logs bytes.Buffer
	c, requestIDs := loggedSession(t, LogOptions{Logger: slog.New(slog.NewJSONHandler(&logs, nil))})
	call(t, c, "echo", map[string]any{"text": "secret-namespace"})

	line := logLine(t, &logs)
	if line["tool"] != "echo" || line["sub"] != "user-1" || line["outcome"] != "ok" {
		t.Errorf("unexpected log line %v", line)
	}
	if line["request_id"] == "" || line["request_id"] != (*requestIDs)[0] {
		t.Errorf("logged request ID %v, the tool saw %v", line["request_id"], *requestIDs)
	}
	if strings.Contains(logs.String(), "the-token") || strings.Contains(logs.String(), "secret-namespace") {
		t.Errorf("token or arguments logged: %s", logs.String())
	}
}

func TestLogsArgumentsIfEnabled(t *testing.T) {
	var logs bytes.Buffer
	c, _ := loggedSession(t, LogOptions{Logger: slog.New(slog.NewJSONHandler(&logs, nil)), LogArguments: true})
	call(t, c, "echo", map[string]any{"text": "my-group"})
	if args, _ := logLine(t, &logs)["arguments"].(map[string]any); args["text"] != "my-group" {
		t.Errorf("arguments not logged: %s", logs.String())
	}
}

func TestUpstreamErrorBodyIsLoggedOnly(t *testing.T) {
	var logs bytes.Buffer
	c, _ := loggedSession(t, LogOptions{Logger: slog.New(slog.NewJSONHandler(&logs, nil))})
	res := call(t, c, "upstream_error", nil)
	if !res.IsError || strings.Contains(text(t, res), "10.0.0.12") {
		t.Errorf("unexpected result %q", text(t, res))
	}
	line := logLine(t, &logs)
	if line["outcome"] != toolkit.OutcomeUpstreamError || line["status"] != float64(500) || !strings.Contains(line["body"].(string), "10.0.0.12") {
		t.Errorf("unexpected log line %v", line)
	}
}

func TestPanicIsReturnedAsToolError(t *testing.T) {
	var logs bytes.Buffer
	c, _ := loggedSession(t, LogOptions{Logger: slog.New(slog.NewJSONHandler(&logs, nil))})
	res := call(t, c, "panic", nil)
	if !res.IsError || strings.Contains(text(t, res), "boom") {
		t.Errorf("unexpected result %q", text(t, res))
	}
	if logLine(t, &logs)["outcome"] != "panic" {
		t.Errorf("panic not logged: %s", logs.String())
	}
	if res := call(t, c, "echo", nil); res.IsError {
		t.Errorf("the server does not work after a panic: %s", text(t, res))
	}
}

func TestInvalidArgumentsAreToolErrors(t *testing.T) {
	var logs bytes.Buffer
	c, requestIDs := loggedSession(t, LogOptions{Logger: slog.New(slog.NewJSONHandler(&logs, nil))})
	res := call(t, c, "echo", map[string]any{"text": 1})
	if !res.IsError {
		t.Errorf("unexpected result %q", text(t, res))
	}
	if logLine(t, &logs)["outcome"] != "tool_error" {
		t.Errorf("not logged as a tool error: %s", logs.String())
	}
	if len(*requestIDs) != 0 {
		t.Error("the tool was called with invalid arguments")
	}
}
