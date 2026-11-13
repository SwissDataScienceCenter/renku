// Package linkdataconnector is the link_data_connector tool.
package linkdataconnector

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/dataconnectors"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "link_data_connector",
	Description: "Link an existing data connector to a project, so that it is mounted in the " +
		"project's sessions. Returns the link, whose ID is needed by unlink_data_connector.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(false)},
}

type input struct {
	DataConnectorID string `json:"data_connector_id" jsonschema:"The data connector ID, a ULID."`
	ProjectID       string `json:"project_id" jsonschema:"The project ID, a ULID."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		body := dataconnectors.DataConnectorToProjectLinkPost{ProjectId: in.ProjectID}
		res, err := api.DataConnectors.PostDataConnectorsDataConnectorIdProjectLinksWithResponse(ctx, in.DataConnectorID, body, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON201 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return nil, res.JSON201, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
