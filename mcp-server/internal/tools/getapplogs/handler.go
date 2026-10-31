// Package getapplogs is the get_app_logs tool.
package getapplogs

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/renkuapps"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "get_app_logs",
	Description: "Get the most recent log lines of an app, keyed by `<pod>/<container>`. Useful to find " +
		"out why an app failed; the most common cause is an app that does not listen on " +
		"`$RENKU_SESSION_PORT` on 0.0.0.0. No logs are returned while the app has scaled to zero, " +
		"which is not an error.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	AppName  string `json:"app_name" jsonschema:"The app name, as returned by launch_app or list_apps."`
	MaxLines *int   `json:"max_lines,omitempty" jsonschema:"The maximum number of most recent lines to return for each container."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		params := &renkuapps.GetAppsAppNameLogsParams{MaxLines: in.MaxLines}
		res, err := api.RenkuApps.GetAppsAppNameLogsWithResponse(ctx, in.AppName, params, toolkit.CallerHeaders(ctx, req))
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
	mcp.AddTool(s, &tool, handler(api))
}
