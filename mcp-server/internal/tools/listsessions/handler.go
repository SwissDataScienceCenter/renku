// Package listsessions is the list_sessions tool.
package listsessions

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/notebooks"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_sessions",
	Description: "List the user's running and hibernated sessions. Returns session names usable with " +
		"get_session, get_session_logs and stop_session.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	SessionType string `json:"session_type,omitempty" jsonschema:"Only list sessions of this type: interactive or non-interactive (jobs)."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		params := &notebooks.GetSessionsParams{}
		if in.SessionType != "" {
			params.SessionType = (*notebooks.SessionType)(&in.SessionType)
		}
		res, err := api.Notebooks.GetSessionsWithResponse(ctx, params, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON200 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return toolkit.ListResult(*res.JSON200, res.HTTPResponse.Header)
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
