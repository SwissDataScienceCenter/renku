// Package listgroupmembers is the list_group_members tool.
package listgroupmembers

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name:        "list_group_members",
	Description: "List the members of a Renku group with their role (owner, editor or viewer) and username.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	GroupSlug string `json:"group_slug" jsonschema:"The group slug, as returned by list_groups or list_namespaces."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		res, err := api.Namespace.GetGroupsGroupSlugMembersWithResponse(ctx, in.GroupSlug, toolkit.CallerHeaders(ctx, req))
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
