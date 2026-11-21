// Package listsessionlaunchers is the list_session_launchers tool.
package listsessionlaunchers

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_session_launchers",
	Description: "List the session launchers of all projects the user can access. To list the launchers " +
		"of one project, use list_project_session_launchers instead. Returns launcher IDs needed by " +
		"start_session and launch_app.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct{}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, _ input) (*mcp.CallToolResult, any, error) {
		res, err := api.Session.GetSessionLaunchersWithResponse(ctx, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON200 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return toolkit.ListResult(*res.JSON200, res.HTTPResponse.Header)
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
