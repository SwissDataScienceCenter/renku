// Package unlinkdataconnector is the unlink_data_connector tool.
package unlinkdataconnector

import (
	"context"
	"net/http"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "unlink_data_connector",
	Description: "Remove the link between a data connector and a project. The data connector itself " +
		"is kept. Get the link ID from list_project_data_connector_links or " +
		"list_data_connector_project_links.\n\n" +
		"This only works for data connectors in a user or group namespace. A data connector whose " +
		"namespace is the project itself (`<group>/<project>`) belongs to the project: to remove it " +
		"entirely, use delete_data_connector; to keep it, move it to a user or group namespace with " +
		"update_data_connector first.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(true), IdempotentHint: true},
}

type input struct {
	DataConnectorID string `json:"data_connector_id" jsonschema:"The data connector ID, a ULID."`
	LinkID          string `json:"link_id" jsonschema:"The ID of the link, a ULID."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		res, err := api.DataConnectors.DeleteDataConnectorsDataConnectorIdProjectLinksLinkIdWithResponse(ctx, in.DataConnectorID, in.LinkID, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.StatusCode() != http.StatusNoContent {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return &mcp.CallToolResult{Content: []mcp.Content{&mcp.TextContent{Text: "Removed link " + in.LinkID + " of data connector " + in.DataConnectorID + "."}}}, nil, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
