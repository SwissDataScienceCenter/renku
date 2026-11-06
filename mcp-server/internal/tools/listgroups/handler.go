// Package listgroups is the list_groups tool.
package listgroups

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/namespace"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_groups",
	Description: "List Renku groups. Returns group slugs usable with get_group and as the `namespace` " +
		"filter of list_projects and list_data_connectors.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	DirectMember bool `json:"direct_member,omitempty" jsonschema:"Only list groups the user is a direct member of."`
	Page         *int `json:"page,omitempty" jsonschema:"The page to return, starting from 1."`
	PerPage      *int `json:"per_page,omitempty" jsonschema:"The number of results per page."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		params := &namespace.GetGroupsParams{Page: in.Page, PerPage: in.PerPage}
		if in.DirectMember {
			params.DirectMember = &in.DirectMember
		}
		res, err := api.Namespace.GetGroupsWithResponse(ctx, params, toolkit.CallerHeaders(ctx, req))
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
