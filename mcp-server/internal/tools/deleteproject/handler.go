// Package deleteproject is the delete_project tool.
package deleteproject

import (
	"context"
	"net/http"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "delete_project",
	Description: "Delete a Renku project, together with its session launchers and the data " +
		"connectors that belong to the project itself. Data connectors from other namespaces " +
		"are only unlinked, and the data in external storage and Git repositories is not touched. " +
		"This cannot be undone.\n\n" +
		"The user is asked to confirm when the client supports that; otherwise confirm with the " +
		"user before calling this.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(true), IdempotentHint: true},
}

type input struct {
	ProjectID string `json:"project_id" jsonschema:"The project ID, a ULID."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		if res, err := toolkit.Confirm(req, "Delete project "+in.ProjectID+" with its session launchers and own data connectors"); res != nil || err != nil {
			return res, nil, err
		}
		res, err := api.Project.DeleteProjectsProjectIdWithResponse(ctx, in.ProjectID, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.StatusCode() != http.StatusNoContent {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return &mcp.CallToolResult{Content: []mcp.Content{&mcp.TextContent{Text: "Deleted project " + in.ProjectID + "."}}}, nil, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
