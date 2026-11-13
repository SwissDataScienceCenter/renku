// Package search is the search tool.
package search

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/search"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "search",
	Description: "Full-text search across Renku projects, groups, users and data connectors visible to the user. " +
		"The query supports filters such as `type:project`, `type:group`, `type:dataconnector`, " +
		"`namespace:<slug>`, `visibility:public` and `role:owner`, combined with free text, " +
		"e.g. `type:project climate`. Results contain IDs usable with get_project, get_group " +
		"and get_data_connector.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	Q             string `json:"q" jsonschema:"The search query, free text optionally combined with filters such as type:project."`
	IncludeCounts bool   `json:"include_counts,omitempty" jsonschema:"Include the number of projects and data connectors of each namespace."`
	Page          *int   `json:"page,omitempty" jsonschema:"The page to return, starting from 1."`
	PerPage       *int   `json:"per_page,omitempty" jsonschema:"The number of results per page."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		query := &search.SearchQuery{Q: &in.Q, Page: in.Page, PerPage: in.PerPage}
		if in.IncludeCounts {
			query.IncludeCounts = &in.IncludeCounts
		}
		res, err := api.Search.GetSearchQueryWithResponse(ctx, &search.GetSearchQueryParams{Params: query}, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON200 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		// The result has its own paging information, pagingInfo.
		return nil, res.JSON200, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
