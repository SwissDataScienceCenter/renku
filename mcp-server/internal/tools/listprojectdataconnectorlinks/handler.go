// Package listprojectdataconnectorlinks is the list_project_data_connector_links tool.
package listprojectdataconnectorlinks

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/dataconnectors"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_project_data_connector_links",
	Description: "List the data connectors linked to a project. Each link contains the data connector ID, " +
		"usable with get_data_connector.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	ProjectID string `json:"project_id" jsonschema:"The project ID, a ULID."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, *toolkit.List[dataconnectors.DataConnectorToProjectLink]] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, *toolkit.List[dataconnectors.DataConnectorToProjectLink], error) {
		res, err := api.DataConnectors.GetProjectsProjectIdDataConnectorLinksWithResponse(ctx, in.ProjectID, toolkit.CallerHeaders(ctx, req))
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
