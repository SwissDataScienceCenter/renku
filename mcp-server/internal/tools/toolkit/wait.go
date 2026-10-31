package toolkit

import (
	"context"
	"fmt"
	"time"

	"github.com/modelcontextprotocol/go-sdk/mcp"
)

// MaxWait is how long a wait tool waits before it returns "still waiting". It stays below the 60 s
// idle timeout of common proxies, as nothing is sent to clients that do not ask for progress, and
// below the WriteTimeout of the HTTP server. Variable for tests.
var MaxWait = 50 * time.Second

// PollInterval is how often a wait tool asks the API. Variable for tests.
var PollInterval = 5 * time.Second

// Status is what one poll of a wait tool found.
type Status struct {
	// Out is the API response, returned as the result of the tool.
	Out any
	// State is the status of the resource, e.g. `starting`, shown in progress messages.
	State string
	// Done is true once the state will not change by waiting longer.
	Done bool
}

// Wait calls poll every PollInterval until it reports Done, and returns the last Out. After MaxWait it
// returns the last Out with a second text block telling the model to call tool again. poll returns a
// tool error to stop waiting, e.g. from APIError.
//
// Each poll sends a progress notification if the client sent a progress token. The call ends with an
// error when ctx is done, which is how the client cancelling the call or disconnecting arrives.
func Wait(ctx context.Context, req *mcp.CallToolRequest, tool string, poll func(context.Context) (Status, error)) (*mcp.CallToolResult, any, error) {
	start := time.Now()
	deadline, cancel := context.WithTimeout(ctx, MaxWait)
	defer cancel()
	var last Status
	for {
		s, err := poll(deadline)
		switch {
		case ctx.Err() != nil:
			return nil, nil, ctx.Err()
		case err != nil && last.Out != nil && deadline.Err() != nil:
			// MaxWait cut off the request, report the state of the previous poll.
			return stillWaiting(tool, start, last)
		case err != nil:
			return nil, nil, err
		case s.Done:
			return nil, s.Out, nil
		}
		last = s
		notifyProgress(ctx, req, start, s.State)
		select {
		case <-deadline.Done():
			if ctx.Err() != nil {
				return nil, nil, ctx.Err()
			}
			return stillWaiting(tool, start, last)
		case <-time.After(PollInterval):
		}
	}
}

func stillWaiting(tool string, start time.Time, last Status) (*mcp.CallToolResult, any, error) {
	note := fmt.Sprintf("Still `%s` after %d s. Call %s again to keep waiting.", last.State, int(time.Since(start).Seconds()), tool)
	return ResultWithNote(last.Out, note)
}

// notifyProgress tells the client how long the call has been waiting, if it asked for progress.
// Failing to send it does not fail the call.
func notifyProgress(ctx context.Context, req *mcp.CallToolRequest, start time.Time, state string) {
	if req.Params == nil || req.Session == nil {
		return
	}
	token := req.Params.GetProgressToken()
	if token == nil {
		return
	}
	elapsed := time.Since(start).Seconds()
	_ = req.Session.NotifyProgress(ctx, &mcp.ProgressNotificationParams{
		ProgressToken: token,
		Message:       fmt.Sprintf("Status `%s` after %d s", state, int(elapsed)),
		// Progress must increase with every notification; the total is when the tool gives up.
		Progress: elapsed,
		Total:    MaxWait.Seconds(),
	})
}
