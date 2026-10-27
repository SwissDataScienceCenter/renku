// Package apispec embeds the tool allowlist and the MCP tool definitions generated from it by specprep.
package apispec

import _ "embed"

// ToolsYAML is the content of tools.yaml.
//
//go:embed tools.yaml
var ToolsYAML []byte

// MCPTools is the content of mcp-tools.json, the generated list of MCP tool definitions.
//
//go:embed mcp-tools.json
var MCPTools []byte
