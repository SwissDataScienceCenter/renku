package mcputils

import (
	"encoding/json"
	"fmt"
)

// ParamsParser provides a generic way to parse MCP tool arguments into typed structs
func ParamsParser[T any](args map[string]interface{}) (*T, error) {
	// Convert through JSON as a safe way to handle nested structures
	jsonData, err := json.Marshal(args)
	if err != nil {
		return nil, fmt.Errorf("failed to process arguments: %w", err)
	}

	var typedArgs T
	if err := json.Unmarshal(jsonData, &typedArgs); err != nil {
		return nil, fmt.Errorf("invalid argument structure: %w", err)
	}

	return &typedArgs, nil
}
