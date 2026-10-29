// Package apispec embeds the renku-data-services module specs and the tool allowlist built from them.
package apispec

import (
	"embed"
	"fmt"

	v3 "github.com/pb33f/libopenapi/datamodel/high/v3"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/api2tool"
)

// The module specs are copied unchanged from renku-data-services by cmd/fetchspecs,
// at their paths in the repository.
//
//go:embed modules
var modules embed.FS

// The parsed module specs the tools in Tools point to.
var (
	usersSpec          = mustParse("modules/components/renku_data_services/users/api.spec.yaml")
	searchSpec         = mustParse("modules/components/renku_data_services/search/api.spec.yaml")
	projectSpec        = mustParse("modules/components/renku_data_services/project/api.spec.yaml")
	namespaceSpec      = mustParse("modules/components/renku_data_services/namespace/api.spec.yaml")
	dataConnectorsSpec = mustParse("modules/components/renku_data_services/data_connectors/api.spec.yaml")
	sessionSpec        = mustParse("modules/components/renku_data_services/session/api.spec.yaml")
	notebooksSpec      = mustParse("modules/components/renku_data_services/notebooks/api.spec.yaml")
)

// mustParse parses an embedded module spec. The specs are fixed at build time,
// so a failure is a bug caught by any test that imports this package.
func mustParse(path string) *v3.Document {
	raw, err := modules.ReadFile(path)
	if err != nil {
		panic(err)
	}
	spec, err := api2tool.Parse(raw)
	if err != nil {
		panic(fmt.Sprintf("module %s: %v", path, err))
	}
	return spec
}
