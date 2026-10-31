// Package getsessionlogs is the get_session_logs tool.
package getsessionlogs

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/notebooks"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "get_session_logs",
	Description: "Get the most recent log lines of each container of a session, keyed by container name. " +
		"The `amalthea-session` container runs the session itself. Useful to find out why a " +
		"session failed to start.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	SessionID string `json:"session_id" jsonschema:"The session name, as returned by start_session or list_sessions."`
	MaxLines  *int   `json:"max_lines,omitempty" jsonschema:"The maximum number of most recent lines to return for each container."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, notebooks.SessionLogsResponse] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, notebooks.SessionLogsResponse, error) {
		params := &notebooks.GetSessionsSessionIdLogsParams{MaxLines: in.MaxLines}
		res, err := api.Notebooks.GetSessionsSessionIdLogsWithResponse(ctx, in.SessionID, params, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON200 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return nil, *res.JSON200, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	toolkit.AddTool(s, &tool, handler(api))
}
