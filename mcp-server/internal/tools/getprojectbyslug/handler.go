// Package getprojectbyslug is the get_project_by_slug tool.
package getprojectbyslug

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/project"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "get_project_by_slug",
	Description: "Get a Renku project by its namespace and slug, e.g. namespace \"my-group\" and slug " +
		"\"my-project\" for the project at /p/my-group/my-project. Use this when the user refers " +
		"to a project by name or URL. Returns the project ID needed by other tools.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	Namespace         string `json:"namespace" jsonschema:"The slug of the user or group namespace of the project."`
	Slug              string `json:"slug" jsonschema:"The slug of the project."`
	WithDocumentation bool   `json:"with_documentation,omitempty" jsonschema:"Also return the project documentation."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, *project.Project] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, *project.Project, error) {
		params := &project.GetNamespacesNamespaceProjectsSlugParams{WithDocumentation: &in.WithDocumentation}
		res, err := api.Project.GetNamespacesNamespaceProjectsSlugWithResponse(ctx, in.Namespace, in.Slug, params, toolkit.CallerHeaders(ctx, req))
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
