package mcp

import (
	"bytes"
	"context"
	"log/slog"
	"strings"
	"testing"
	"time"

	sdkmcp "github.com/modelcontextprotocol/go-sdk/mcp"
	"golang.org/x/time/rate"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
)

// rateLimitedSession connects a client to a server with a single echo tool behind middleware, called as subject.
func rateLimitedSession(t *testing.T, middleware sdkmcp.Middleware, subject string) *sdkmcp.ClientSession {
	t.Helper()
	ctx, cancel := context.WithCancel(context.Background())
	t.Cleanup(cancel)
	s := sdkmcp.NewServer(&sdkmcp.Implementation{Name: "test", Version: "dev"}, nil)
	s.AddTool(&sdkmcp.Tool{Name: "echo", InputSchema: map[string]any{"type": "object"}}, func(context.Context, *sdkmcp.CallToolRequest) (*sdkmcp.CallToolResult, error) {
		return &sdkmcp.CallToolResult{Content: []sdkmcp.Content{&sdkmcp.TextContent{Text: "ok"}}}, nil
	})
	s.AddReceivingMiddleware(middleware)
	serverTransport, clientTransport := sdkmcp.NewInMemoryTransports()
	go func() { _ = s.Run(auth.WithPrincipal(ctx, auth.Principal{Subject: subject}), serverTransport) }()
	c, err := sdkmcp.NewClient(&sdkmcp.Implementation{Name: "test", Version: "1"}, nil).Connect(ctx, clientTransport, nil)
	if err != nil {
		t.Fatal(err)
	}
	t.Cleanup(func() { _ = c.Close() })
	return c
}

func callEcho(t *testing.T, c *sdkmcp.ClientSession) *sdkmcp.CallToolResult {
	t.Helper()
	res, err := c.CallTool(context.Background(), &sdkmcp.CallToolParams{Name: "echo"})
	if err != nil {
		t.Fatal(err)
	}
	return res
}

func TestToolCallsOverTheLimitAreReturnedAsToolErrors(t *testing.T) {
	var logs bytes.Buffer
	// One call per minute, so that no token is refilled during the test.
	middleware := RateLimit(RateLimitOptions{PerMinute: 1, Burst: 2, Logger: slog.New(slog.NewJSONHandler(&logs, nil))})
	alice := rateLimitedSession(t, middleware, "alice")
	bob := rateLimitedSession(t, middleware, "bob")

	for i := range 2 {
		if res := callEcho(t, alice); res.IsError {
			t.Fatalf("call %d within the burst failed: %s", i+1, text(t, res))
		}
	}
	res := callEcho(t, alice)
	if !res.IsError || !strings.Contains(text(t, res), "Too many tool calls") {
		t.Errorf("call over the burst returned %q (error %v), want a rate limit tool error", text(t, res), res.IsError)
	}
	if !strings.Contains(logs.String(), `"outcome":"rate_limited"`) || !strings.Contains(logs.String(), `"sub":"alice"`) {
		t.Errorf("rate limited call not logged with the subject: %s", logs.String())
	}

	if res := callEcho(t, bob); res.IsError {
		t.Errorf("another user is limited too: %s", text(t, res))
	}
	if _, err := alice.ListTools(context.Background(), nil); err != nil {
		t.Errorf("listing tools is limited too: %v", err)
	}
}

func TestOnlyFullBucketsAreDropped(t *testing.T) {
	now := time.Now()
	l := &limiters{limit: rate.Every(time.Hour), burst: 2, byUser: map[string]*rate.Limiter{}, now: func() time.Time { return now }, lastScan: now}
	l.allow("alice")
	l.allow("bob")
	l.allow("bob")

	// After the scan interval alice's bucket is full again but bob's is still empty.
	now = now.Add(time.Hour + limiterScanInterval)
	l.allow("carol")
	if _, ok := l.byUser["alice"]; ok {
		t.Error("the full bucket of alice was kept")
	}
	if _, ok := l.byUser["bob"]; !ok {
		t.Fatal("the depleted bucket of bob was dropped, which would reset his limit")
	}
	if !l.allow("bob") || l.allow("bob") {
		t.Error("bob's bucket should have refilled exactly one token")
	}
}
