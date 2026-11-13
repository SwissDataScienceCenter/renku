// Package listdataconnectorprojectlinks is the list_data_connector_project_links tool.
package listdataconnectorprojectlinks

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_data_connector_project_links",
	Description: "List the projects a data connector is linked to. Each link contains the project ID, " +
		"usable with get_project, and the link ID, needed by unlink_data_connector.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	DataConnectorID string `json:"data_connector_id" jsonschema:"The data connector ID, a ULID."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		res, err := api.DataConnectors.GetDataConnectorsDataConnectorIdProjectLinksWithResponse(ctx, in.DataConnectorID, toolkit.CallerHeaders(ctx, req))
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
