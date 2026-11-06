// Package createproject is the create_project tool.
package createproject

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/project"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "create_project",
	Description: "Create a Renku project in a user or group namespace. Get the namespace slug from " +
		"list_namespaces. If the user already has a git repository for the project, pass it in " +
		"`repositories` here instead of adding it with update_project afterwards. Returns the " +
		"project ID, usable with create_session_launcher, link_data_connector and update_project.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(false)},
}

type input struct {
	Name         string   `json:"name" jsonschema:"The project name."`
	Namespace    string   `json:"namespace" jsonschema:"The slug of the user or group namespace to create the project in."`
	Slug         *string  `json:"slug,omitempty" jsonschema:"The URL slug of the project, derived from the name if left out."`
	Visibility   string   `json:"visibility,omitempty" jsonschema:"private (the default) or public."`
	Description  *string  `json:"description,omitempty" jsonschema:"A short description, shown in listings."`
	Keywords     []string `json:"keywords,omitempty"`
	Repositories []string `json:"repositories,omitempty" jsonschema:"Git repository URLs."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		body := project.ProjectPost{Name: in.Name, Namespace: in.Namespace, Slug: in.Slug, Description: in.Description}
		if in.Visibility != "" {
			body.Visibility = (*project.Visibility)(&in.Visibility)
		}
		if in.Keywords != nil {
			body.Keywords = &in.Keywords
		}
		if in.Repositories != nil {
			body.Repositories = &in.Repositories
		}
		res, err := api.Project.PostProjectsWithResponse(ctx, body, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON201 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return nil, res.JSON201, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
