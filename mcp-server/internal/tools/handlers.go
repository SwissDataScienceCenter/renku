package tools

import (
	"context"
	"fmt"
	"net/http"

	"github.com/mark3labs/mcp-go/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/gen/apiclient"
	mcputils "github.com/SwissDataScienceCenter/renku/mcp-server/internal/gen/helpers"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/gen/mcptools"
)

// definitions lists every implemented tool. Each one must also be allowlisted in apispec/tools.yaml.
var definitions = []definition{
	{mcptools.NewGetCurrentUserMCPTool, func(ctx context.Context, c *apiclient.Client, _ mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		return c.GetCurrentUser(ctx, edit)
	}},
	{mcptools.NewSearchMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		params, err := parseArgs[apiclient.SearchParams](req)
		if err != nil {
			return nil, err
		}
		return c.Search(ctx, params, edit)
	}},
	{mcptools.NewListProjectsMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		params, err := parseArgs[apiclient.ListProjectsParams](req)
		if err != nil {
			return nil, err
		}
		return c.ListProjects(ctx, params, edit)
	}},
	{mcptools.NewGetProjectMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "project_id")
		if err != nil {
			return nil, err
		}
		params, err := parseArgs[apiclient.GetProjectParams](req)
		if err != nil {
			return nil, err
		}
		return c.GetProject(ctx, id, params, edit)
	}},
	{mcptools.NewGetProjectBySlugMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		namespace, err := requiredString(req, "namespace")
		if err != nil {
			return nil, err
		}
		slug, err := requiredString(req, "slug")
		if err != nil {
			return nil, err
		}
		params, err := parseArgs[apiclient.GetProjectBySlugParams](req)
		if err != nil {
			return nil, err
		}
		return c.GetProjectBySlug(ctx, namespace, slug, params, edit)
	}},
	{mcptools.NewListGroupsMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		params, err := parseArgs[apiclient.ListGroupsParams](req)
		if err != nil {
			return nil, err
		}
		return c.ListGroups(ctx, params, edit)
	}},
	{mcptools.NewGetGroupMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		slug, err := requiredString(req, "group_slug")
		if err != nil {
			return nil, err
		}
		return c.GetGroup(ctx, slug, edit)
	}},
	{mcptools.NewListDataConnectorsMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		params, err := parseArgs[apiclient.ListDataConnectorsParams](req)
		if err != nil {
			return nil, err
		}
		return c.ListDataConnectors(ctx, params, edit)
	}},
	{mcptools.NewGetDataConnectorMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "data_connector_id")
		if err != nil {
			return nil, err
		}
		return c.GetDataConnector(ctx, id, edit)
	}},
	{mcptools.NewListProjectDataConnectorLinksMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "project_id")
		if err != nil {
			return nil, err
		}
		return c.ListProjectDataConnectorLinks(ctx, id, edit)
	}},
	{mcptools.NewListProjectSessionLaunchersMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "project_id")
		if err != nil {
			return nil, err
		}
		return c.ListProjectSessionLaunchers(ctx, id, edit)
	}},
	{mcptools.NewGetSessionLauncherMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "launcher_id")
		if err != nil {
			return nil, err
		}
		return c.GetSessionLauncher(ctx, id, edit)
	}},
	{mcptools.NewListSessionsMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		params, err := parseArgs[apiclient.ListSessionsParams](req)
		if err != nil {
			return nil, err
		}
		return c.ListSessions(ctx, params, edit)
	}},
	{mcptools.NewStartSessionMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		body, ok := req.GetArguments()["body"].(map[string]any)
		if !ok {
			return nil, argumentError{fmt.Errorf("body with launcher_id is required")}
		}
		parsed, err := mcputils.ParamsParser[apiclient.StartSessionJSONRequestBody](body)
		if err != nil {
			return nil, argumentError{err}
		}
		if parsed.LauncherId == "" {
			return nil, argumentError{fmt.Errorf("body.launcher_id is required")}
		}
		return c.StartSession(ctx, *parsed, edit)
	}},
	{mcptools.NewGetSessionMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "session_id")
		if err != nil {
			return nil, err
		}
		return c.GetSession(ctx, id, edit)
	}},
	{mcptools.NewGetSessionLogsMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "session_id")
		if err != nil {
			return nil, err
		}
		params, err := parseArgs[apiclient.GetSessionLogsParams](req)
		if err != nil {
			return nil, err
		}
		return c.GetSessionLogs(ctx, id, params, edit)
	}},
	{mcptools.NewStopSessionMCPTool, func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "session_id")
		if err != nil {
			return nil, err
		}
		return c.StopSession(ctx, id, edit)
	}},
}

// parseArgs decodes the tool arguments into a generated parameter struct, ignoring path parameters.
func parseArgs[T any](req mcp.CallToolRequest) (*T, error) {
	params, err := mcputils.ParamsParser[T](req.GetArguments())
	if err != nil {
		return nil, argumentError{err}
	}
	return params, nil
}

func requiredString(req mcp.CallToolRequest, name string) (string, error) {
	value, err := req.RequireString(name)
	if err != nil || value == "" {
		return "", argumentError{fmt.Errorf("%s is required", name)}
	}
	return value, nil
}
