// Package getbuildlogs is the get_build_logs tool.
package getbuildlogs

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/session"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "get_build_logs",
	Description: "Get the most recent log lines of each container of an image build, keyed by container " +
		"name. Use it to find out why a build failed.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	BuildID  string `json:"build_id" jsonschema:"The build ID, as returned by list_environment_builds."`
	MaxLines *int   `json:"max_lines,omitempty" jsonschema:"The maximum number of most recent lines to return for each container."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		params := &session.GetBuildsBuildIdLogsParams{MaxLines: in.MaxLines}
		res, err := api.Session.GetBuildsBuildIdLogsWithResponse(ctx, in.BuildID, params, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON200 == nil {
			return nil, nil, toolkit.BuildsAPIError(res.StatusCode(), res.Body)
		}
		return nil, *res.JSON200, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
