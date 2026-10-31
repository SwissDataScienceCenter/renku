// Package updateproject is the update_project tool.
package updateproject

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/project"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "update_project",
	Description: "Update a Renku project. Fields that are left out stay unchanged. Pass the `etag` " +
		"of the project as returned by get_project; if the project changed since, the update fails " +
		"and you need to get the project again.\n\n" +
		"`keywords` and `repositories` replace the whole list: to add a repository, send the " +
		"existing ones from get_project plus the new one. Use `description` for a short summary " +
		"shown in listings, and `documentation` for longer Markdown such as usage instructions.\n\n" +
		"Never change `visibility` without asking the user first. The user is also asked to confirm " +
		"it when the client supports that.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(false), IdempotentHint: true},
}

type input struct {
	ProjectID     string   `json:"project_id" jsonschema:"The project ID, a ULID."`
	Etag          string   `json:"etag" jsonschema:"The etag field of the project, from get_project."`
	Name          *string  `json:"name,omitempty"`
	Description   *string  `json:"description,omitempty" jsonschema:"A short description, shown in listings."`
	Documentation *string  `json:"documentation,omitempty" jsonschema:"Longer documentation in Markdown."`
	Keywords      []string `json:"keywords,omitempty" jsonschema:"Replaces all keywords of the project."`
	Repositories  []string `json:"repositories,omitempty" jsonschema:"Git repository URLs. Replaces all repositories of the project."`
	Visibility    string   `json:"visibility,omitempty" jsonschema:"private or public. Only change it when the user asked for it."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		if in.Visibility != "" {
			if res, err := toolkit.Confirm(req, "Make project "+in.ProjectID+" "+in.Visibility); res != nil || err != nil {
				return res, nil, err
			}
		}
		body := project.ProjectPatch{Name: in.Name, Description: in.Description, Documentation: in.Documentation}
		if in.Keywords != nil {
			body.Keywords = &in.Keywords
		}
		if in.Repositories != nil {
			body.Repositories = &in.Repositories
		}
		if in.Visibility != "" {
			body.Visibility = (*project.Visibility)(&in.Visibility)
		}
		params := &project.PatchProjectsProjectIdParams{IfMatch: in.Etag}
		res, err := api.Project.PatchProjectsProjectIdWithResponse(ctx, in.ProjectID, params, body, toolkit.CallerHeaders(ctx, req))
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
	mcp.AddTool(s, &tool, handler(api))
}
