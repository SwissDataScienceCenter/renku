// Package stopsession is the stop_session tool.
package stopsession

import (
	"context"
	"net/http"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "stop_session",
	Description: "Stop and fully remove a session. Unsaved work in the session is lost, so confirm with " +
		"the user before calling this.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(true), IdempotentHint: true},
}

type input struct {
	SessionID string `json:"session_id" jsonschema:"The session name, as returned by start_session or list_sessions."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		res, err := api.Notebooks.DeleteSessionsSessionIdWithResponse(ctx, in.SessionID, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.StatusCode() != http.StatusNoContent {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return &mcp.CallToolResult{Content: []mcp.Content{&mcp.TextContent{Text: "Stopped session " + in.SessionID + "."}}}, nil, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
