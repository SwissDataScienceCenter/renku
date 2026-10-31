package toolkit

import (
	"context"
	"errors"
	"strings"
	"testing"
	"time"

	"github.com/modelcontextprotocol/go-sdk/mcp"
)

// fastWait makes Wait poll every millisecond and give up after maxWait.
func fastWait(t *testing.T, maxWait time.Duration) {
	t.Helper()
	oldMax, oldInterval := MaxWait, PollInterval
	MaxWait, PollInterval = maxWait, time.Millisecond
	t.Cleanup(func() { MaxWait, PollInterval = oldMax, oldInterval })
}

func TestWaitReturnsOnceDone(t *testing.T) {
	fastWait(t, time.Minute)
	polls := 0
	res, out, err := Wait(context.Background(), &mcp.CallToolRequest{}, "wait_for_thing", func(context.Context) (Status, error) {
		polls++
		return Status{Out: polls, State: "starting", Done: polls == 3}, nil
	})
	if err != nil || res != nil || out != 3 {
		t.Errorf("Wait = %v, %v, %v after %d polls", res, out, err, polls)
	}
}

func TestWaitTellsTheModelToCallAgain(t *testing.T) {
	fastWait(t, 20*time.Millisecond)
	res, out, err := Wait(context.Background(), &mcp.CallToolRequest{}, "wait_for_thing", func(context.Context) (Status, error) {
		return Status{Out: map[string]string{"status": "starting"}, State: "starting"}, nil
	})
	if err != nil || out == nil {
		t.Fatalf("Wait = %v, %v", out, err)
	}
	note := res.Content[1].(*mcp.TextContent).Text
	if !strings.Contains(note, "Still `starting`") || !strings.Contains(note, "Call wait_for_thing again") {
		t.Errorf("note = %q", note)
	}
}

func TestWaitStopsOnAToolError(t *testing.T) {
	fastWait(t, time.Minute)
	apiErr := APIError(404, []byte(`{"error": {"message": "Session does not exist."}}`))
	_, _, err := Wait(context.Background(), &mcp.CallToolRequest{}, "wait_for_thing", func(context.Context) (Status, error) {
		return Status{}, apiErr
	})
	if !errors.Is(err, apiErr) {
		t.Errorf("err = %v", err)
	}
}

func TestWaitEndsWhenTheCallIsCancelled(t *testing.T) {
	fastWait(t, time.Minute)
	ctx, cancel := context.WithCancel(context.Background())
	_, _, err := Wait(ctx, &mcp.CallToolRequest{}, "wait_for_thing", func(context.Context) (Status, error) {
		cancel()
		return Status{Out: 1, State: "starting"}, nil
	})
	if !errors.Is(err, context.Canceled) {
		t.Errorf("err = %v", err)
	}
}
