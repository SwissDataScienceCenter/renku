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
	// Clients that get structured content show the model only that, without the note.
	if err != nil || res == nil || out != nil {
		t.Fatalf("Wait = %v, %v, %v", res, out, err)
	}
	if got := res.Content[0].(*mcp.TextContent).Text; got != `{"status":"starting"}` {
		t.Errorf("content = %q", got)
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

func TestWaitReportsTheLastStateWhenMaxWaitCutsOffAPoll(t *testing.T) {
	fastWait(t, 20*time.Millisecond)
	polls := 0
	res, _, err := Wait(context.Background(), &mcp.CallToolRequest{}, "wait_for_thing", func(ctx context.Context) (Status, error) {
		polls++
		if polls == 1 {
			return Status{Out: map[string]string{"status": "starting"}, State: "starting"}, nil
		}
		<-ctx.Done() // a request that outlives MaxWait
		return Status{}, ctx.Err()
	})
	if err != nil || res == nil {
		t.Fatalf("Wait = %v, %v", res, err)
	}
	if got := res.Content[0].(*mcp.TextContent).Text; got != `{"status":"starting"}` {
		t.Errorf("content = %q", got)
	}
	if note := res.Content[1].(*mcp.TextContent).Text; !strings.Contains(note, "Still `starting`") {
		t.Errorf("note = %q", note)
	}
}

func TestWaitTellsTheModelToCallAgainWhenTheFirstPollIsCutOff(t *testing.T) {
	fastWait(t, 20*time.Millisecond)
	_, _, err := Wait(context.Background(), &mcp.CallToolRequest{}, "wait_for_thing", func(ctx context.Context) (Status, error) {
		<-ctx.Done()
		return Status{}, ctx.Err()
	})
	var toolErr *Error
	if !errors.As(err, &toolErr) || !strings.Contains(toolErr.Message, "Call wait_for_thing again") {
		t.Fatalf("err = %v", err)
	}
	if !errors.Is(err, context.DeadlineExceeded) {
		t.Errorf("err does not wrap the deadline: %v", err)
	}
}

func TestWaitEndsWhenTheCallIsCancelledDuringAPoll(t *testing.T) {
	fastWait(t, time.Minute)
	ctx, cancel := context.WithCancel(context.Background())
	_, _, err := Wait(ctx, &mcp.CallToolRequest{}, "wait_for_thing", func(pollCtx context.Context) (Status, error) {
		cancel()
		<-pollCtx.Done()
		return Status{}, pollCtx.Err()
	})
	if !errors.Is(err, context.Canceled) {
		t.Errorf("err = %v", err)
	}
}

func TestWaitReturnsADonePollThatEndsAtMaxWait(t *testing.T) {
	fastWait(t, 20*time.Millisecond)
	_, out, err := Wait(context.Background(), &mcp.CallToolRequest{}, "wait_for_thing", func(ctx context.Context) (Status, error) {
		<-ctx.Done()
		return Status{Out: "running", State: "running", Done: true}, nil
	})
	if err != nil || out != "running" {
		t.Errorf("Wait = %v, %v", out, err)
	}
}
