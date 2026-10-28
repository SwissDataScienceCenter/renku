// Package apispec embeds the tool allowlist and the renku-data-services API specs the tools are built from.
package apispec

import (
	"embed"
	"io/fs"
)

//go:embed modules
var modules embed.FS

// Modules holds the module specs listed in Tools, at their paths in the renku-data-services
// repository. They are copied unchanged from the repository by 'just update-spec <ref>'.
var Modules, _ = fs.Sub(modules, "modules")
