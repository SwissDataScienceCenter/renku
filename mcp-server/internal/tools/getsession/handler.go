// Package getsession is the get_session tool.
package getsession

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/notebooks"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "get_session",
	Description: "Get a session by its name, including its status and URL. Use it to wait for a started " +
		"session to become `running`: call it about every 10 seconds until the status is `running` " +
		"or `failed`. Give the user the `external_url` of the session, which is its page in the " +
		"Renku UI, never `url`. Only if `external_url` is null, give them " +
		"{renku_url}/p/<namespace>/<project_slug>/sessions/show/<session_name>.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	SessionID string `json:"session_id" jsonschema:"The session name, as returned by start_session or list_sessions."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, *notebooks.SessionResponse] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, *notebooks.SessionResponse, error) {
		res, err := api.Notebooks.GetSessionsSessionIdWithResponse(ctx, in.SessionID, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON200 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return nil, res.JSON200, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	toolkit.AddTool(s, &tool, handler(api))
}
