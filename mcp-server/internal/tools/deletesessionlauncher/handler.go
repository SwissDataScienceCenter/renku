// Package deletesessionlauncher is the delete_session_launcher tool.
package deletesessionlauncher

import (
	"context"
	"net/http"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "delete_session_launcher",
	Description: "Delete a session launcher. The user is asked to confirm when the client supports " +
		"that; otherwise confirm with the user before calling this.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(true), IdempotentHint: true},
}

type input struct {
	LauncherID string `json:"launcher_id" jsonschema:"The session launcher ID, a ULID."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		if res, err := toolkit.Confirm(req, "Delete session launcher "+in.LauncherID); res != nil || err != nil {
			return res, nil, err
		}
		res, err := api.Session.DeleteSessionLaunchersLauncherIdWithResponse(ctx, in.LauncherID, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.StatusCode() != http.StatusNoContent {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return &mcp.CallToolResult{Content: []mcp.Content{&mcp.TextContent{Text: "Deleted session launcher " + in.LauncherID + "."}}}, nil, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
