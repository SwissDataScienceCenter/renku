// Package waitforsession is the wait_for_session tool.
package waitforsession

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/notebooks"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "wait_for_session",
	Description: "Wait for a session or job to finish starting, then return it like get_session. A " +
		"session is ready when its status is `running`; a job (`session_type` `non-interactive`) is " +
		"done when it is `succeeded`. The wait also ends on `failed`, `stopping` or `hibernated`; if " +
		"it failed, call get_session_logs. Each call waits up to about a minute and then returns the " +
		"session with a note to call it again. Give the user the `external_url` of the session, never `url`.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	SessionID string `json:"session_id" jsonschema:"The session name, as returned by start_session or list_sessions."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		headers := toolkit.CallerHeaders(ctx, req)
		return toolkit.Wait(ctx, req, tool.Name, func(ctx context.Context) (toolkit.Status, error) {
			res, err := api.Notebooks.GetSessionsSessionIdWithResponse(ctx, in.SessionID, headers)
			if err != nil {
				return toolkit.Status{}, toolkit.RequestFailed(err)
			}
			if res.JSON200 == nil {
				return toolkit.Status{}, toolkit.APIError(res.StatusCode(), res.Body)
			}
			s := res.JSON200
			return toolkit.Status{Out: s, State: string(s.Status.State), Done: done(s)}, nil
		})
	}
}

// done reports whether waiting longer will not change the state of s. A job keeps running until it
// succeeds, an interactive session never does.
func done(s *notebooks.SessionResponse) bool {
	switch s.Status.State {
	case notebooks.SessionStatusStateStarting:
		return false
	case notebooks.SessionStatusStateRunning:
		return s.SessionType != notebooks.NonInteractive
	default:
		return true
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
