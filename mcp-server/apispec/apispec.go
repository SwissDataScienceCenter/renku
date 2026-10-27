// Package apispec embeds the tool allowlist so the server can check that every allowlisted tool is implemented.
package apispec

import _ "embed"

// ToolsYAML is the content of tools.yaml.
//
//go:embed tools.yaml
var ToolsYAML []byte
