// Package getapp is the get_app tool.
package getapp

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "get_app",
	Description: "Get an app by its name, including its status and URL. The status is `pending`, `ready` " +
		"or `failed`. After launch_app, call wait_for_app until it is `ready` or `failed`, then give " +
		"the user the `url`. An idle app scales to zero but still reports `ready`; it starts again on " +
		"the next visit. If it failed, call get_app_logs.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	AppName string `json:"app_name" jsonschema:"The app name, as returned by launch_app or list_apps."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		res, err := api.RenkuApps.GetAppsAppNameWithResponse(ctx, in.AppName, toolkit.CallerHeaders(ctx, req))
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
	mcp.AddTool(s, &tool, handler(api))
}
