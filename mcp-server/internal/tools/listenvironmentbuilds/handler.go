// Package listenvironmentbuilds is the list_environment_builds tool.
package listenvironmentbuilds

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_environment_builds",
	Description: "List the image builds of an environment that is built from code. After creating or " +
		"updating a session launcher with `environment_image_source: build`, call this with the " +
		"launcher's `environment.id` to find the build, then call wait_for_build with its ID until it " +
		"succeeds or fails.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	EnvironmentID string `json:"environment_id" jsonschema:"The environment ID, the environment.id of a session launcher."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		res, err := api.Session.GetEnvironmentsEnvironmentIdBuildsWithResponse(ctx, in.EnvironmentID, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON200 == nil {
			return nil, nil, toolkit.BuildsAPIError(res.StatusCode(), res.Body)
		}
		return toolkit.ListResult(*res.JSON200, res.HTTPResponse.Header)
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
