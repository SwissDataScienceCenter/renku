// Package deleteapp is the delete_app tool.
package deleteapp

import (
	"context"
	"net/http"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "delete_app",
	Description: "Delete an app, taking it offline for its visitors. The user is asked to confirm " +
		"when the client supports that; otherwise confirm with the user before calling this.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(true), IdempotentHint: true},
}

type input struct {
	AppName string `json:"app_name" jsonschema:"The app name, as returned by launch_app or list_apps."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		if res, err := toolkit.Confirm(req, "Delete app "+in.AppName+", taking it offline for its visitors"); res != nil || err != nil {
			return res, nil, err
		}
		res, err := api.RenkuApps.DeleteAppsAppNameWithResponse(ctx, in.AppName, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.StatusCode() != http.StatusNoContent {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return &mcp.CallToolResult{Content: []mcp.Content{&mcp.TextContent{Text: "Deleted app " + in.AppName + "."}}}, nil, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
