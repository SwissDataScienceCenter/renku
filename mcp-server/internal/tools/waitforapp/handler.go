// Package waitforapp is the wait_for_app tool.
package waitforapp

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/renkuapps"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "wait_for_app",
	Description: "Wait for an app to be `ready` or `failed`, then return it like get_app. When it is " +
		"ready, give the user the `url`; if it failed, call get_app_logs. Each call waits up to about " +
		"a minute and then returns the app with a note to call it again.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	AppName string `json:"app_name" jsonschema:"The app name, as returned by launch_app or list_apps."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		headers := toolkit.CallerHeaders(ctx, req)
		return toolkit.Wait(ctx, req, tool.Name, func(ctx context.Context) (toolkit.Status, error) {
			res, err := api.RenkuApps.GetAppsAppNameWithResponse(ctx, in.AppName, headers)
			if err != nil {
				return toolkit.Status{}, toolkit.RequestFailed(err)
			}
			if res.JSON200 == nil {
				return toolkit.Status{}, toolkit.APIError(res.StatusCode(), res.Body)
			}
			status := res.JSON200.Status
			return toolkit.Status{Out: res.JSON200, State: string(status), Done: status != renkuapps.Pending}, nil
		})
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
