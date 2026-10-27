package mcptools

import (
	"context"
	"fmt"
	"github.com/mark3labs/mcp-go/mcp"
)

// Input Schema for the ListProjects tool
const ListProjectsInputSchema = `{
  "properties": {
    "direct_member": {
      "default": false,
      "description": "A flag to filter projects where the user is a direct member.",
      "type": "boolean"
    },
    "namespace": {
      "default": "",
      "description": "A namespace, used as a filter.",
      "type": "string"
    },
    "page": {
      "default": 1,
      "description": "Result's page number starting from 1",
      "minimum": 1,
      "type": "integer"
    },
    "per_page": {
      "default": 20,
      "description": "The number of results per page",
      "maximum": 100,
      "minimum": 1,
      "type": "integer"
    }
  },
  "type": "object"
}`

// NewListProjectsMCPTool creates the MCP Tool instance for ListProjects
func NewListProjectsMCPTool() mcp.Tool {
	return mcp.NewToolWithRawSchema(
		"ListProjects",
		"List Renku projects the user can access, newest first. Use 'namespace' to restrict the list to a user or group namespace and 'direct_member' to only list projects the user is a member of. Returns project IDs usable with get_project, list_project_session_launchers and list_project_data_connector_links.",
		[]byte(ListProjectsInputSchema),
	)
}

// ListProjectsHandler is the handler function for the ListProjects tool.
// This function is automatically generated. Users should implement the actual
// logic within this function body to integrate with backend APIs.
// You can generate types, http client and helpers for parsing request params to facilitate the implementation.
func ListProjectsHandler(ctx context.Context, request mcp.CallToolRequest) (*mcp.CallToolResult, error) {

	// IMPORTANT: Replace the following placeholder implementation with your actual logic.
	// Use the 'request' parameter to access tool call arguments.
	// Make HTTP calls or interact with services as needed.
	// Return an *mcp.CallToolResult with the response payload, or an error.

	// Example placeholder implementation:
	// Extract the parameters from the request and parse them.
	// Call your backend API or perform the necessary operations using 'params'.
	// Handle the response and errors accordingly.
	return nil, fmt.Errorf("%s not implemented", "ListProjects")
}
