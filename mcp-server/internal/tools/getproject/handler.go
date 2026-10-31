// Package getproject is the get_project tool.
package getproject

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/project"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name:        "get_project",
	Description: "Get a Renku project by its ID (a ULID such as 01HXXXXXXXXXXXXXXXXXXXXXXX).",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	ProjectID         string `json:"project_id" jsonschema:"The project ID, a ULID."`
	WithDocumentation bool   `json:"with_documentation,omitempty" jsonschema:"Also return the project documentation."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, *project.Project] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, *project.Project, error) {
		params := &project.GetProjectsProjectIdParams{WithDocumentation: &in.WithDocumentation}
		res, err := api.Project.GetProjectsProjectIdWithResponse(ctx, in.ProjectID, params, toolkit.CallerHeaders(ctx, req))
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
