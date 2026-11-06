// Package waitforbuild is the wait_for_build tool.
package waitforbuild

import (
	"context"
	"fmt"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/session"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "wait_for_build",
	Description: "Wait for an image build to end, then return it like get_build: `succeeded`, `failed` " +
		"or `cancelled`. A session can only start once the build succeeded; if it failed, call " +
		"get_build_logs. Builds take several minutes. Each call waits up to about a minute and then " +
		"returns the build with a note to call it again.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	BuildID string `json:"build_id" jsonschema:"The build ID, as returned by list_environment_builds."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		headers := toolkit.CallerHeaders(ctx, req)
		return toolkit.Wait(ctx, req, tool.Name, func(ctx context.Context) (toolkit.Status, error) {
			res, err := api.Session.GetBuildsBuildIdWithResponse(ctx, in.BuildID, headers)
			if err != nil {
				return toolkit.Status{}, toolkit.RequestFailed(err)
			}
			if res.JSON200 == nil {
				return toolkit.Status{}, toolkit.BuildsAPIError(res.StatusCode(), res.Body)
			}
			// The generated discriminator reads the status field of the oneOf.
			status, err := res.JSON200.Discriminator()
			if err != nil {
				return toolkit.Status{}, fmt.Errorf("reading the build status: %w", err)
			}
			done := status != string(session.BuildNotCompletedPartStatusInProgress)
			return toolkit.Status{Out: res.JSON200, State: status, Done: done}, nil
		})
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
