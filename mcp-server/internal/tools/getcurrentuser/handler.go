// Package getcurrentuser is the get_current_user tool.
package getcurrentuser

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "get_current_user",
	Description: "Get the Renku user that is logged in, including their user ID and username. " +
		"The username is also the user's personal namespace, usable as the `namespace` " +
		"filter of list_projects and list_data_connectors.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct{}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, _ input) (*mcp.CallToolResult, any, error) {
		res, err := api.Users.GetUserWithResponse(ctx, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON200 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return nil, res.JSON200, nil
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
