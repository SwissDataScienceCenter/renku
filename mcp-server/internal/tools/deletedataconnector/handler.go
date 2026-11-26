// Package deletedataconnector is the delete_data_connector tool.
package deletedataconnector

import (
	"context"
	"net/http"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "delete_data_connector",
	Description: "Delete a data connector, removing it from every project it is linked to. The data " +
		"in the external storage is not touched. Works for data connectors in any namespace, " +
		"including a project's own. To only remove it from one project, use unlink_data_connector " +
		"instead.\n\n" +
		"The user is asked to confirm when the client supports that; otherwise confirm with the " +
		"user before calling this.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(true), IdempotentHint: true},
}

type input struct {
	DataConnectorID string `json:"data_connector_id" jsonschema:"The data connector ID, a ULID."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		if res, err := toolkit.Confirm(req, "Delete data connector "+in.DataConnectorID); res != nil || err != nil {
			return res, nil, err
		}
		res, err := api.DataConnectors.DeleteDataConnectorsDataConnectorIdWithResponse(ctx, in.DataConnectorID, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.StatusCode() != http.StatusNoContent {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return &mcp.CallToolResult{Content: []mcp.Content{&mcp.TextContent{Text: "Deleted data connector " + in.DataConnectorID + "."}}}, nil, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
