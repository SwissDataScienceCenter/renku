// Package createdataconnector is the create_data_connector tool.
package createdataconnector

import (
	"context"
	"encoding/json"
	"errors"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/dataconnectors"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "create_data_connector",
	Description: "Create a data connector, a connection to external storage such as an S3 bucket, a " +
		"WebDAV share, SFTP or Polybox, that is mounted in the sessions of the projects it is linked to. " +
		"For a dataset with a DOI (Zenodo, EnviDat, ...), use create_global_data_connector instead.\n\n" +
		"Pass `project_id` to link the connector to that project in the same call; without it, the " +
		"connector appears in no project until link_data_connector is called. `namespace` is a user or " +
		"group namespace from list_namespaces, or `<namespace>/<project slug>` to make the connector part " +
		"of the project.\n\n" +
		"`storage` takes either `storage_url` (e.g. `s3://bucket/folder`), or `configuration` with the " +
		"rclone options and `source_path`. Examples of `configuration`:\n" +
		"- S3: {\"type\": \"s3\", \"provider\": \"Other\", \"endpoint\": \"https://...\"}, source_path \"bucket/folder\"\n" +
		"- WebDAV: {\"type\": \"webdav\", \"url\": \"https://...\"}, source_path \"/\"\n" +
		"- SFTP: {\"type\": \"sftp\", \"host\": \"...\"}, source_path \"/path\"\n" +
		"- Polybox: {\"type\": \"polybox\", \"provider\": \"shared\", \"public_link\": \"https://...\"}, source_path \"/\"\n" +
		"The storage is mounted at `target_path`, relative to the session's working directory.\n\n" +
		"Credentials (access keys, passwords, tokens, user names) are refused, not ignored: create the " +
		"connector without them and tell the user to add them in the Renku UI.\n\n" +
		"Returns the data connector, whose ID is usable with get_data_connector, update_data_connector " +
		"and link_data_connector, and the project link if `project_id` was given.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(false)},
}

type storage struct {
	StorageURL    string         `json:"storage_url,omitempty" jsonschema:"A URL of the storage, e.g. s3://bucket/folder. Leave out configuration and source_path when set."`
	Configuration map[string]any `json:"configuration,omitempty" jsonschema:"The rclone options of the storage, with at least type, without credentials."`
	SourcePath    string         `json:"source_path,omitempty" jsonschema:"The path to mount, usually starting with the bucket name. Required with configuration."`
	TargetPath    string         `json:"target_path" jsonschema:"Where to mount the storage, relative to the session's working directory, e.g. data/climate."`
	Readonly      *bool          `json:"readonly,omitempty" jsonschema:"Whether to mount the storage read-only, true by default."`
}

type input struct {
	Name        string   `json:"name" jsonschema:"The data connector name."`
	Namespace   string   `json:"namespace" jsonschema:"A user or group namespace slug, or <namespace>/<project slug> for a connector that is part of a project."`
	Slug        *string  `json:"slug,omitempty" jsonschema:"The URL slug of the connector, derived from the name if left out."`
	Visibility  string   `json:"visibility,omitempty" jsonschema:"private (the default) or public."`
	Description *string  `json:"description,omitempty"`
	Keywords    []string `json:"keywords,omitempty"`
	Storage     storage  `json:"storage"`
	ProjectID   string   `json:"project_id,omitempty" jsonschema:"A project ID to link the connector to, a ULID. Recommended."`
}

type output struct {
	DataConnector *dataconnectors.DataConnector              `json:"data_connector"`
	Link          *dataconnectors.DataConnectorToProjectLink `json:"link,omitempty"`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		if err := toolkit.RejectSecrets(map[string]any{"configuration": in.Storage.Configuration, "storage_url": in.Storage.StorageURL}, "storage"); err != nil {
			return nil, nil, err
		}
		body := dataconnectors.DataConnectorPost{
			Name:        in.Name,
			Namespace:   &in.Namespace,
			Slug:        in.Slug,
			Description: in.Description,
		}
		if in.Visibility != "" {
			body.Visibility = (*dataconnectors.Visibility)(&in.Visibility)
		}
		if in.Keywords != nil {
			body.Keywords = &in.Keywords
		}
		if err := setStorage(&body.Storage, in.Storage); err != nil {
			return nil, nil, err
		}
		headers := toolkit.CallerHeaders(ctx, req)
		res, err := api.DataConnectors.PostDataConnectorsWithResponse(ctx, body, headers)
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON201 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		out := output{DataConnector: res.JSON201}
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
			return nil, nil, toolkit.Prefixed(err, "The data connector "+out.DataConnector.Id+" was created, but linking it to "+
				"project "+in.ProjectID+" failed; retry with link_data_connector. ")
		}
		out.Link = link.JSON201
		return nil, out, nil
	}
}

// setStorage sets the variant of the storage union that s describes.
func setStorage(dst *dataconnectors.DataConnectorPost_Storage, s storage) error {
	if s.StorageURL != "" {
		if s.Configuration != nil || s.SourcePath != "" {
			return errors.New("storage takes either storage_url, or configuration and source_path, not both")
		}
		return dst.FromCloudStorageUrlV2(dataconnectors.CloudStorageUrlV2{StorageUrl: s.StorageURL, TargetPath: s.TargetPath, Readonly: s.Readonly})
	}
	if s.Configuration == nil {
		return errors.New("storage needs either storage_url, or configuration and source_path")
	}
	config, err := rcloneConfig(s.Configuration)
	if err != nil {
		return err
	}
	return dst.FromCloudStorageCorePost(dataconnectors.CloudStorageCorePost{
		Configuration: config,
		SourcePath:    s.SourcePath,
		TargetPath:    s.TargetPath,
		Readonly:      s.Readonly,
	})
}

// rcloneConfig converts rclone options, as decoded JSON, to the type of the generated client.
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
