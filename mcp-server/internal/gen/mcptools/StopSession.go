package mcptools

import (
	"context"
	"fmt"
	"github.com/mark3labs/mcp-go/mcp"
)

// Input Schema for the StopSession tool
const StopSessionInputSchema = `{
  "properties": {
    "session_id": {
      "description": "The session name, as returned by start_session or list_sessions.",
      "type": "string"
    }
  },
  "required": [
    "session_id"
  ],
  "type": "object"
}`

// NewStopSessionMCPTool creates the MCP Tool instance for StopSession
func NewStopSessionMCPTool() mcp.Tool {
	return mcp.NewToolWithRawSchema(
		"StopSession",
		"Stop and fully remove a session. Unsaved work in the session is lost, so confirm with the user before calling this.",
		[]byte(StopSessionInputSchema),
	)
}

// StopSessionHandler is the handler function for the StopSession tool.
// This function is automatically generated. Users should implement the actual
// logic within this function body to integrate with backend APIs.
// You can generate types, http client and helpers for parsing request params to facilitate the implementation.
func StopSessionHandler(ctx context.Context, request mcp.CallToolRequest) (*mcp.CallToolResult, error) {

	// IMPORTANT: Replace the following placeholder implementation with your actual logic.
	// Use the 'request' parameter to access tool call arguments.
	// Make HTTP calls or interact with services as needed.
	// Return an *mcp.CallToolResult with the response payload, or an error.

	// Example placeholder implementation:
	// Extract the parameters from the request and parse them.
	// Call your backend API or perform the necessary operations using 'params'.
	// Handle the response and errors accordingly.
	return nil, fmt.Errorf("%s not implemented", "StopSession")
}
