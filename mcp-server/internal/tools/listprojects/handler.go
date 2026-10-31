// Package listprojects is the list_projects tool.
package listprojects

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/project"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_projects",
	Description: "List Renku projects the user can access, newest first. Use `namespace` to restrict the " +
		"list to a user or group namespace and `direct_member` to only list projects the user is " +
		"a member of. Returns project IDs usable with get_project, list_project_session_launchers " +
		"and list_project_data_connector_links.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	Namespace    string `json:"namespace,omitempty" jsonschema:"Only list the projects of this user or group namespace slug."`
	DirectMember bool   `json:"direct_member,omitempty" jsonschema:"Only list projects the user is a direct member of."`
	Page         *int   `json:"page,omitempty" jsonschema:"The page to return, starting from 1."`
	PerPage      *int   `json:"per_page,omitempty" jsonschema:"The number of results per page."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		query := &project.ProjectGetQuery{Page: in.Page, PerPage: in.PerPage}
		if in.Namespace != "" {
			query.Namespace = &in.Namespace
		}
		if in.DirectMember {
			query.DirectMember = &in.DirectMember
		}
		res, err := api.Project.GetProjectsWithResponse(ctx, &project.GetProjectsParams{Params: query}, toolkit.CallerHeaders(ctx, req))
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
