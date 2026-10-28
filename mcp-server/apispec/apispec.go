// Package apispec embeds the tool allowlist and the renku-data-services API specs the tools are built from.
package apispec

import (
	"embed"
	"io/fs"
)

// ToolsYAML is the content of tools.yaml.
//
//go:embed tools.yaml
var ToolsYAML []byte

//go:embed modules
var modules embed.FS

// Modules holds the module specs listed in tools.yaml, at their paths in the renku-data-services
// repository. They are copied unchanged from the repository by 'just update-spec <ref>'.
var Modules, _ = fs.Sub(modules, "modules")
