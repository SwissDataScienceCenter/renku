// Package listdataconnectors is the list_data_connectors tool.
package listdataconnectors

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/dataconnectors"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_data_connectors",
	Description: "List data connectors (connections to external storage such as S3 buckets or WebDAV shares) " +
		"the user can access. Use `namespace` to restrict the list to a user or group namespace. " +
		"Returns data connector IDs usable with get_data_connector.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	Namespace string `json:"namespace,omitempty" jsonschema:"Only list the data connectors of this user or group namespace slug."`
	Page      *int   `json:"page,omitempty" jsonschema:"The page to return, starting from 1."`
	PerPage   *int   `json:"per_page,omitempty" jsonschema:"The number of results per page."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, *toolkit.List[dataconnectors.DataConnector]] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, *toolkit.List[dataconnectors.DataConnector], error) {
		query := &dataconnectors.DataConnectorsGetQuery{Page: in.Page, PerPage: in.PerPage}
		if in.Namespace != "" {
			query.Namespace = &in.Namespace
		}
		res, err := api.DataConnectors.GetDataConnectorsWithResponse(ctx, &dataconnectors.GetDataConnectorsParams{Params: query}, toolkit.CallerHeaders(ctx, req))
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
	toolkit.AddTool(s, &tool, handler(api))
}
