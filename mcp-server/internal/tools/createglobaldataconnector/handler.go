// Package createglobaldataconnector is the create_global_data_connector tool.
package createglobaldataconnector

import (
	"context"
	"encoding/json"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/dataconnectors"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "create_global_data_connector",
	Description: "Get or create the global data connector of a dataset with a DOI, e.g. on Zenodo or " +
		"EnviDat. Global data connectors are public, belong to no namespace and are shared by everyone, " +
		"so if one already exists for the DOI it is returned with `created: false`. Renku names it " +
		"and chooses its mount path from the dataset's metadata. For other storage, use " +
		"create_data_connector.\n\n" +
		"Pass `project_id` to link the connector to that project in the same call; without it, the " +
		"connector appears in no project until link_data_connector is called. Returns the data " +
		"connector, whose ID is usable with get_data_connector and link_data_connector, and the " +
		"project link if `project_id` was given.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(false), IdempotentHint: true},
}

type input struct {
	DOI       string `json:"doi" jsonschema:"The DOI of the dataset, e.g. 10.5281/zenodo.1234567."`
	ProjectID string `json:"project_id,omitempty" jsonschema:"A project ID to link the connector to, a ULID. Recommended."`
}

type output struct {
	Created       bool                                       `json:"created"`
	DataConnector *dataconnectors.DataConnector              `json:"data_connector"`
	Link          *dataconnectors.DataConnectorToProjectLink `json:"link,omitempty"`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		raw, err := json.Marshal(map[string]string{"type": "doi", "doi": in.DOI})
		if err != nil {
			return nil, nil, err
		}
		var config dataconnectors.RCloneConfig
		if err := json.Unmarshal(raw, &config); err != nil {
			return nil, nil, err
		}
		// Renku replaces the target path with one derived from the dataset's metadata.
		readonly := true
		var body dataconnectors.GlobalDataConnectorPost
		if err := body.Storage.FromCloudStorageCorePost(dataconnectors.CloudStorageCorePost{Configuration: config, Readonly: &readonly}); err != nil {
			return nil, nil, err
		}
		headers := toolkit.CallerHeaders(ctx, req)
		res, err := api.DataConnectors.PostDataConnectorsGlobalWithResponse(ctx, body, headers)
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		out := output{Created: res.JSON201 != nil, DataConnector: res.JSON201}
		if res.JSON200 != nil {
			out.DataConnector = res.JSON200
		}
		if out.DataConnector == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		if in.ProjectID == "" {
			return nil, out, nil
		}
		link, err := api.DataConnectors.PostDataConnectorsDataConnectorIdProjectLinksWithResponse(ctx, out.DataConnector.Id,
			dataconnectors.DataConnectorToProjectLinkPost{ProjectId: in.ProjectID}, headers)
		if err != nil {
			err = toolkit.RequestFailed(err)
		} else if link.JSON201 == nil {
			err = toolkit.APIError(link.StatusCode(), link.Body)
		}
		if err != nil {
			return nil, nil, toolkit.Prefixed(err, "The data connector "+out.DataConnector.Id+" exists, but linking it to "+
				"project "+in.ProjectID+" failed; retry with link_data_connector, unless it is already linked. ")
		}
		out.Link = link.JSON201
		return nil, out, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
