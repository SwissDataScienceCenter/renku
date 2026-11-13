// Package listnamespaces is the list_namespaces tool.
package listnamespaces

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/namespace"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_namespaces",
	Description: "List the namespaces the user can access: their own user namespace and their groups. " +
		"Returns slugs usable as the `namespace` of create_project and as the `namespace` filter of " +
		"list_projects and list_data_connectors. To find where the user can create a project, use " +
		"`minimum_role: editor`.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	MinimumRole string   `json:"minimum_role,omitempty" jsonschema:"Only list namespaces where the user has at least this role: viewer, editor or owner."`
	Kinds       []string `json:"kinds,omitempty" jsonschema:"Only list namespaces of these kinds: user, group or project."`
	Page        *int     `json:"page,omitempty" jsonschema:"The page to return, starting from 1."`
	PerPage     *int     `json:"per_page,omitempty" jsonschema:"The number of results per page."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		params := &namespace.GetNamespacesParams{Page: in.Page, PerPage: in.PerPage}
		if in.MinimumRole != "" {
			params.MinimumRole = (*namespace.GroupRole)(&in.MinimumRole)
		}
		if len(in.Kinds) > 0 {
			kinds := make(namespace.NamespaceKinds, len(in.Kinds))
			for i, k := range in.Kinds {
				kinds[i] = namespace.NamespaceKind(k)
			}
			params.Kinds = &kinds
		}
		res, err := api.Namespace.GetNamespacesWithResponse(ctx, params, toolkit.CallerHeaders(ctx, req))
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
