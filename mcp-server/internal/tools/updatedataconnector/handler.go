// Package updatedataconnector is the update_data_connector tool.
package updatedataconnector

import (
	"context"
	"encoding/json"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/dataconnectors"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "update_data_connector",
	Description: "Update a data connector. Fields that are left out stay unchanged. Pass the `etag` " +
		"of the data connector as returned by get_data_connector; if it changed since, the update " +
		"fails and you need to get it again.\n\n" +
		"`keywords` replaces the whole list. `storage.configuration` is merged into the existing " +
		"rclone options: set an option to null to remove it.\n\n" +
		"To detach a data connector that is part of a project (namespace `<namespace>/<project slug>`) " +
		"from that project without deleting it, first move it to the user's namespace (the username " +
		"from get_current_user) or a group namespace with `namespace`, then remove the link with " +
		"unlink_data_connector.\n\n" +
		"Credentials (access keys, passwords, tokens, user names) are refused: tell the user to add " +
		"them in the Renku UI. Never change `visibility` without asking the user first. The user is " +
		"also asked to confirm it when the client supports that.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(false), IdempotentHint: true},
}

type storage struct {
	Configuration map[string]any `json:"configuration,omitempty" jsonschema:"rclone options to change, without credentials. They are merged into the existing options; null removes one."`
	SourcePath    *string        `json:"source_path,omitempty" jsonschema:"The path to mount, usually starting with the bucket name."`
	TargetPath    *string        `json:"target_path,omitempty" jsonschema:"Where to mount the storage, relative to the session's working directory."`
	Readonly      *bool          `json:"readonly,omitempty" jsonschema:"Whether to mount the storage read-only."`
}

type input struct {
	DataConnectorID string   `json:"data_connector_id" jsonschema:"The data connector ID, a ULID."`
	Etag            string   `json:"etag" jsonschema:"The etag field of the data connector, from get_data_connector."`
	Name            *string  `json:"name,omitempty"`
	Namespace       *string  `json:"namespace,omitempty" jsonschema:"A user or group namespace slug, or <namespace>/<project slug> to make the connector part of a project."`
	Slug            *string  `json:"slug,omitempty"`
	Visibility      string   `json:"visibility,omitempty" jsonschema:"private or public. Only change it when the user asked for it."`
	Description     *string  `json:"description,omitempty"`
	Keywords        []string `json:"keywords,omitempty" jsonschema:"Replaces all keywords of the data connector."`
	Storage         *storage `json:"storage,omitempty"`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		body := dataconnectors.DataConnectorPatch{Name: in.Name, Namespace: in.Namespace, Slug: in.Slug, Description: in.Description}
		if in.Storage != nil {
			if err := toolkit.RejectSecrets(map[string]any{"configuration": in.Storage.Configuration}, "storage"); err != nil {
				return nil, nil, err
			}
			patch := &dataconnectors.CloudStorageCorePatch{SourcePath: in.Storage.SourcePath, TargetPath: in.Storage.TargetPath, Readonly: in.Storage.Readonly}
			if in.Storage.Configuration != nil {
				config, err := rcloneConfig(in.Storage.Configuration)
				if err != nil {
					return nil, nil, err
				}
				patch.Configuration = &config
			}
			body.Storage = patch
		}
		if in.Visibility != "" {
			if res, err := toolkit.Confirm(req, "Make data connector "+in.DataConnectorID+" "+in.Visibility); res != nil || err != nil {
				return res, nil, err
			}
			body.Visibility = (*dataconnectors.Visibility)(&in.Visibility)
		}
		if in.Keywords != nil {
			body.Keywords = &in.Keywords
		}
		params := &dataconnectors.PatchDataConnectorsDataConnectorIdParams{IfMatch: in.Etag}
		res, err := api.DataConnectors.PatchDataConnectorsDataConnectorIdWithResponse(ctx, in.DataConnectorID, params, body, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON200 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return nil, res.JSON200, nil
	}
}

// rcloneConfig converts rclone options, as decoded JSON, to the type of the generated client.
// Null values are kept, as they remove an option.
func rcloneConfig(options map[string]any) (dataconnectors.RCloneConfig, error) {
	raw, err := json.Marshal(options)
	if err != nil {
		return nil, err
	}
	var config dataconnectors.RCloneConfig
	if err := json.Unmarshal(raw, &config); err != nil {
		return nil, err
	}
	return config, nil
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
