// Package listenvironments is the list_environments tool.
package listenvironments

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/session"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_environments",
	Description: "List the global environments: ready-made images (such as Python or R with Jupyter) " +
		"provided by the Renku admins. Use one when the user has neither their own image nor a " +
		"repository to build from. Show the user the list and let them pick. The environment ID goes " +
		"in the `environment.id` of create_session_launcher or update_session_launcher.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	IncludeArchived bool `json:"include_archived,omitempty" jsonschema:"Also list archived environments, which cannot be used for new launchers."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		params := &session.GetEnvironmentsParams{}
		if in.IncludeArchived {
			params.GetEnvironmentParams = &struct {
				IncludeArchived *bool `json:"include_archived,omitempty"`
			}{IncludeArchived: &in.IncludeArchived}
		}
		res, err := api.Session.GetEnvironmentsWithResponse(ctx, params, toolkit.CallerHeaders(ctx, req))
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
