package mcptools

import (
	"context"
	"fmt"
	"github.com/mark3labs/mcp-go/mcp"
)

// Input Schema for the StartSession tool
const StartSessionInputSchema = `{
  "properties": {
    "body": {
      "properties": {
        "data_connectors_overrides": {
          "items": {
            "properties": {
              "configuration": {
                "additionalProperties": {
                  "oneOf": [
                    {
                      "type": "integer"
                    },
                    {
                      "type": [
                        "string",
                        "null"
                      ]
                    },
                    {
                      "type": "boolean"
                    },
                    {
                      "type": "object"
                    }
                  ]
                },
                "description": "Dictionary of rclone key:value pairs (based on schema from '/storage_schema')",
                "type": "object"
              },
              "data_connector_id": {
                "allOf": [
                  {
                    "description": "ULID identifier",
                    "maxLength": 26,
                    "minLength": 26,
                    "pattern": "^[0-7][0-9A-HJKMNP-TV-Z]{25}$",
                    "type": "string"
                  },
                  {
                    "description": "The 'data_connector_id' must match an existing data connector from the session launcher's project.\n"
                  }
                ]
              },
              "readonly": {
                "default": true,
                "description": "Whether this storage should be mounted readonly or not",
                "type": "boolean"
              },
              "skip": {
                "default": false,
                "description": "The corresponding data connector will not be mounted if 'skip' is set to 'true'.",
                "type": "boolean"
              },
              "source_path": {
                "description": "the source path to mount, usually starts with bucket/container name",
                "example": "bucket/my/storage/folder/",
                "type": "string"
              },
              "target_path": {
                "description": "the target path relative to the working directory where the storage should be mounted",
                "example": "my/project/folder",
                "type": "string"
              }
            },
            "required": [
              "data_connector_id"
            ],
            "type": "object"
          },
          "type": "array"
        },
        "disk_storage": {
          "description": "The size of disk storage for the session, in gigabytes",
          "type": "integer"
        },
        "env_variable_overrides": {
          "description": "Environment variable overrides for the session pod",
          "items": {
            "description": "Override an env variable defined in the session launcher",
            "properties": {
              "name": {
                "example": "MY_VAR",
                "maxLength": 256,
                "pattern": "^[a-zA-Z_][a-zA-Z0-9_]*$",
                "type": "string"
              },
              "value": {
                "maxLength": 500,
                "type": "string"
              }
            },
            "required": [
              "name",
              "value"
            ],
            "type": "object"
          },
          "type": "array"
        },
        "job_args_override": {
          "items": {
            "type": "string"
          },
          "type": [
            "array",
            "null"
          ]
        },
        "job_command_override": {
          "items": {
            "type": "string"
          },
          "type": [
            "array",
            "null"
          ]
        },
        "launcher_id": {
          "description": "ULID identifier",
          "maxLength": 26,
          "minLength": 26,
          "pattern": "^[0-7][0-9A-HJKMNP-TV-Z]{25}$",
          "type": "string"
        },
        "resource_class_id": {
          "type": [
            "integer",
            "null"
          ]
        },
        "submission_id": {
          "description": "When submitting a job, i.e. the launcher used is a job\nlauncher, the submission id is required to deduplicate\nsame job submissions and allows retries.\n",
          "pattern": "^[a-z][-0-9a-z]{3,19}$",
          "type": "string"
        }
      },
      "required": [
        "launcher_id"
      ],
      "type": "object"
    }
  },
  "required": [
    "body"
  ],
  "type": "object"
}`

// NewStartSessionMCPTool creates the MCP Tool instance for StartSession
func NewStartSessionMCPTool() mcp.Tool {
	return mcp.NewToolWithRawSchema(
		"StartSession",
		"Start a session from a session launcher, or return the existing one if a session for that launcher is already running. Usually only 'launcher_id' is needed; get it from list_project_session_launchers. The response contains the session name and its URL, which the user can open in a browser once the status is 'running'. Starting can take a few minutes; poll with get_session.",
		[]byte(StartSessionInputSchema),
	)
}

// StartSessionHandler is the handler function for the StartSession tool.
// This function is automatically generated. Users should implement the actual
// logic within this function body to integrate with backend APIs.
// You can generate types, http client and helpers for parsing request params to facilitate the implementation.
func StartSessionHandler(ctx context.Context, request mcp.CallToolRequest) (*mcp.CallToolResult, error) {

	// IMPORTANT: Replace the following placeholder implementation with your actual logic.
	// Use the 'request' parameter to access tool call arguments.
	// Make HTTP calls or interact with services as needed.
	// Return an *mcp.CallToolResult with the response payload, or an error.

	// Example placeholder implementation:
	// Extract the parameters from the request and parse them.
	// Call your backend API or perform the necessary operations using 'params'.
	// Handle the response and errors accordingly.
	return nil, fmt.Errorf("%s not implemented", "StartSession")
}
