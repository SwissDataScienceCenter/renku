// Package listresourcepools is the list_resource_pools tool.
package listresourcepools

import (
	"context"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/crc"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "list_resource_pools",
	Description: "List the resource pools the user can use, each with its resource classes (CPU, memory, " +
		"GPUs and storage of a session). Call it before creating a session launcher or starting a " +
		"session or job, passing what the workload needs as `cpu`, `memory`, `gpu` and `max_storage`. " +
		"Classes that satisfy all of them have `matching: true`; choose the smallest of those. " +
		"The class `id` is the `resource_class_id` of create_session_launcher, update_session_launcher " +
		"and start_session.",
	Annotations: &mcp.ToolAnnotations{ReadOnlyHint: true},
}

type input struct {
	CPU        *float32 `json:"cpu,omitempty" jsonschema:"The minimum number of CPU cores."`
	Memory     *int     `json:"memory,omitempty" jsonschema:"The minimum memory, in GB."`
	GPU        *int     `json:"gpu,omitempty" jsonschema:"The minimum number of GPUs."`
	MaxStorage *int     `json:"max_storage,omitempty" jsonschema:"The minimum disk storage the class must allow, in GB."`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		params := &crc.GetResourcePoolsParams{}
		if in.CPU != nil || in.Memory != nil || in.GPU != nil || in.MaxStorage != nil {
			params.ResourcePoolsParams = &struct {
				Cpu        *crc.CpuFilter     `json:"cpu,omitempty"`
				Gpu        *crc.Gpu           `json:"gpu,omitempty"`
				MaxStorage *crc.StorageFilter `json:"max_storage,omitempty"`
				Memory     *crc.MemoryFilter  `json:"memory,omitempty"`
			}{Cpu: in.CPU, Gpu: in.GPU, MaxStorage: in.MaxStorage, Memory: in.Memory}
		}
		res, err := api.CRC.GetResourcePoolsWithResponse(ctx, params, toolkit.CallerHeaders(ctx, req))
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
