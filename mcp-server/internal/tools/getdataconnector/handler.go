// Package getdataconnector is the get_data_connector tool.
package getdataconnector

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name:        "get_data_connector",
	Description: "Get a data connector by its ID, including its storage configuration and mount point.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	DataConnectorID string `json:"data_connector_id" jsonschema:"The data connector ID, a ULID."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		res, err := api.DataConnectors.GetDataConnectorsDataConnectorIdWithResponse(ctx, in.DataConnectorID, toolkit.CallerHeaders(ctx, req))
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
