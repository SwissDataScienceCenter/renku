package gen

import (
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/gen/mcptools"
	"github.com/mark3labs/mcp-go/server"
)

// NewMCPServer creates and returns an MCP server with all tools registered
func NewMCPServer() *server.MCPServer {
	// Create a new MCP server
	s := server.NewMCPServer(
		"MCP Server",
		"1.0.0",
		server.WithToolCapabilities(true),
		server.WithLogging(),
	)

	// Register all tools
	s.AddTool(mcptools.NewGetCurrentUserMCPTool(), mcptools.GetCurrentUserHandler)
	s.AddTool(mcptools.NewGetDataConnectorMCPTool(), mcptools.GetDataConnectorHandler)
	s.AddTool(mcptools.NewGetGroupMCPTool(), mcptools.GetGroupHandler)
	s.AddTool(mcptools.NewGetProjectMCPTool(), mcptools.GetProjectHandler)
	s.AddTool(mcptools.NewGetProjectBySlugMCPTool(), mcptools.GetProjectBySlugHandler)
	s.AddTool(mcptools.NewGetSessionMCPTool(), mcptools.GetSessionHandler)
	s.AddTool(mcptools.NewGetSessionLauncherMCPTool(), mcptools.GetSessionLauncherHandler)
	s.AddTool(mcptools.NewGetSessionLogsMCPTool(), mcptools.GetSessionLogsHandler)
	s.AddTool(mcptools.NewListDataConnectorsMCPTool(), mcptools.ListDataConnectorsHandler)
	s.AddTool(mcptools.NewListGroupsMCPTool(), mcptools.ListGroupsHandler)
	s.AddTool(mcptools.NewListProjectDataConnectorLinksMCPTool(), mcptools.ListProjectDataConnectorLinksHandler)
	s.AddTool(mcptools.NewListProjectSessionLaunchersMCPTool(), mcptools.ListProjectSessionLaunchersHandler)
	s.AddTool(mcptools.NewListProjectsMCPTool(), mcptools.ListProjectsHandler)
	s.AddTool(mcptools.NewListSessionsMCPTool(), mcptools.ListSessionsHandler)
	s.AddTool(mcptools.NewSearchMCPTool(), mcptools.SearchHandler)
	s.AddTool(mcptools.NewStartSessionMCPTool(), mcptools.StartSessionHandler)
	s.AddTool(mcptools.NewStopSessionMCPTool(), mcptools.StopSessionHandler)

	return s
}
