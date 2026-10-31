// Package tools registers the MCP tools. Each tool is a package exporting only its Register function;
// to add a tool, copy an existing package, adapt it and add its Register function to all.
package tools

import (
	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getcurrentuser"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getdataconnector"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getgroup"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getproject"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getprojectbyslug"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getsession"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getsessionlauncher"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getsessionlogs"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listdataconnectors"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listgroups"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listprojectdataconnectorlinks"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listprojects"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listprojectsessionlaunchers"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listsessions"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/search"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/startsession"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/stopsession"
)

var all = []func(*mcp.Server, *renkuapi.Clients){
	getcurrentuser.Register,
	search.Register,
	listprojects.Register,
	getproject.Register,
	getprojectbyslug.Register,
	listgroups.Register,
	getgroup.Register,
	listdataconnectors.Register,
	getdataconnector.Register,
	listprojectdataconnectorlinks.Register,
	listprojectsessionlaunchers.Register,
	getsessionlauncher.Register,
	listsessions.Register,
	startsession.Register,
	getsession.Register,
	getsessionlogs.Register,
	stopsession.Register,
}

// Register adds all tools to s. They call the Renku API with api.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	for _, register := range all {
		register(s, api)
	}
}
