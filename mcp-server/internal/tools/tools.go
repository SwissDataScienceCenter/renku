// Package tools registers the MCP tools. Each tool is a package exporting only its Register function;
// to add a tool, copy an existing package, adapt it and add its Register function to all.
package tools

import (
	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/getproject"
)

var all = []func(*mcp.Server, *renkuapi.Clients){
	getproject.Register,
}

// Register adds all tools to s. They call the Renku API with api.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	for _, register := range all {
		register(s, api)
	}
}
