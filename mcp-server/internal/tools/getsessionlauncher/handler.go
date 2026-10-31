// Package getsessionlauncher is the get_session_launcher tool.
package getsessionlauncher

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/session"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name:        "get_session_launcher",
	Description: "Get a session launcher by its ID, including its environment and default resource class.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	LauncherID string `json:"launcher_id" jsonschema:"The session launcher ID, a ULID."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, *session.SessionLauncher] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, *session.SessionLauncher, error) {
		res, err := api.Session.GetSessionLaunchersLauncherIdWithResponse(ctx, in.LauncherID, toolkit.CallerHeaders(ctx, req))
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
