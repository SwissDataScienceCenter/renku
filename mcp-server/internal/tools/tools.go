// Package tools registers the MCP tools. Each tool is a package exporting only its Register function;
// to add a tool, copy an existing package, adapt it and add its Register function to all.
package tools

import (
	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/createproject"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/createsessionlauncher"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/deleteapp"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/deletedataconnector"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/deletesessionlauncher"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getapp"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getapplogs"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getbuild"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getbuildlogs"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getcurrentuser"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getdataconnector"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getgroup"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getproject"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getprojectbyslug"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getsession"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getsessionlauncher"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getsessionlogs"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/launchapp"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/linkdataconnector"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listapps"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listdataconnectorprojectlinks"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listdataconnectors"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listenvironmentbuilds"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listenvironments"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listgroupmembers"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listgroups"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listnamespaces"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listprojectdataconnectorlinks"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listprojects"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listprojectsessionlaunchers"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listresourcepools"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listsessionlaunchers"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/listsessions"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/search"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/startsession"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/stopsession"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/unlinkdataconnector"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/updateproject"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/updatesessionlauncher"
)

var all = []func(*mcp.Server, *renkuapi.Clients){
	getcurrentuser.Register,
	search.Register,
	listprojects.Register,
	getproject.Register,
	getprojectbyslug.Register,
	listnamespaces.Register,
	createproject.Register,
	updateproject.Register,
	listgroups.Register,
	getgroup.Register,
	listgroupmembers.Register,
	listdataconnectors.Register,
	getdataconnector.Register,
	listprojectdataconnectorlinks.Register,
	listdataconnectorprojectlinks.Register,
	linkdataconnector.Register,
	unlinkdataconnector.Register,
	deletedataconnector.Register,
	listprojectsessionlaunchers.Register,
	listsessionlaunchers.Register,
	getsessionlauncher.Register,
	createsessionlauncher.Register,
	updatesessionlauncher.Register,
	deletesessionlauncher.Register,
	listenvironments.Register,
	listenvironmentbuilds.Register,
	getbuild.Register,
	getbuildlogs.Register,
	listresourcepools.Register,
	listsessions.Register,
	startsession.Register,
	getsession.Register,
	getsessionlogs.Register,
	stopsession.Register,
	listapps.Register,
	getapp.Register,
	getapplogs.Register,
	launchapp.Register,
	deleteapp.Register,
}

// Register adds all tools to s. They call the Renku API with api.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	for _, register := range all {
		register(s, api)
	}
}
