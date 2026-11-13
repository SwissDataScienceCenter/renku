// Package listprojectsessionlaunchers is the list_project_session_launchers tool.
package listprojectsessionlaunchers

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_project_session_launchers",
	Description: "List the session launchers of a project. A session launcher defines the environment " +
		"(image) a session runs. Returns launcher IDs needed by start_session.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	ProjectID string `json:"project_id" jsonschema:"The project ID, a ULID."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		res, err := api.Session.GetProjectsProjectIdSessionLaunchersWithResponse(ctx, in.ProjectID, toolkit.CallerHeaders(ctx, req))
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
