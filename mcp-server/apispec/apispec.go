// Package apispec embeds the tool allowlist and the pruned API spec generated from it by specprep.
package apispec

import _ "embed"

// ToolsYAML is the content of tools.yaml.
//
//go:embed tools.yaml
var ToolsYAML []byte

// OpenAPI is the content of openapi.yaml, the generated spec the MCP tools are served from.
//
//go:embed openapi.yaml
var OpenAPI []byte
