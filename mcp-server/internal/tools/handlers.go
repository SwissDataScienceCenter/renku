package tools

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/mark3labs/mcp-go/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/apiclient"
)

// calls implements every tool of apispec/mcp-tools.json, keyed by tool name.
var calls = map[string]call{
	"get_current_user": func(ctx context.Context, c *apiclient.Client, _ mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		return c.GetCurrentUser(ctx, edit)
	},
	"search": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		params, err := parseArgs[apiclient.SearchParams](req)
		if err != nil {
			return nil, err
		}
		return c.Search(ctx, params, edit)
	},
	"list_projects": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		params, err := parseArgs[apiclient.ListProjectsParams](req)
		if err != nil {
			return nil, err
		}
		return c.ListProjects(ctx, params, edit)
	},
	"get_project": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "project_id")
		if err != nil {
			return nil, err
		}
		params, err := parseArgs[apiclient.GetProjectParams](req)
		if err != nil {
			return nil, err
		}
		return c.GetProject(ctx, id, params, edit)
	},
	"get_project_by_slug": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
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
	},
	"list_groups": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		params, err := parseArgs[apiclient.ListGroupsParams](req)
		if err != nil {
			return nil, err
		}
		return c.ListGroups(ctx, params, edit)
	},
	"get_group": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		slug, err := requiredString(req, "group_slug")
		if err != nil {
			return nil, err
		}
		return c.GetGroup(ctx, slug, edit)
	},
	"list_data_connectors": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		params, err := parseArgs[apiclient.ListDataConnectorsParams](req)
		if err != nil {
			return nil, err
		}
		return c.ListDataConnectors(ctx, params, edit)
	},
	"get_data_connector": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "data_connector_id")
		if err != nil {
			return nil, err
		}
		return c.GetDataConnector(ctx, id, edit)
	},
	"list_project_data_connector_links": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "project_id")
		if err != nil {
			return nil, err
		}
		return c.ListProjectDataConnectorLinks(ctx, id, edit)
	},
	"list_project_session_launchers": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "project_id")
		if err != nil {
			return nil, err
		}
		return c.ListProjectSessionLaunchers(ctx, id, edit)
	},
	"get_session_launcher": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "launcher_id")
		if err != nil {
			return nil, err
		}
		return c.GetSessionLauncher(ctx, id, edit)
	},
	"list_sessions": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		params, err := parseArgs[apiclient.ListSessionsParams](req)
		if err != nil {
			return nil, err
		}
		return c.ListSessions(ctx, params, edit)
	},
	"start_session": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		body, ok := req.GetArguments()["body"].(map[string]any)
		if !ok {
			return nil, argumentError{fmt.Errorf("body with launcher_id is required")}
		}
		parsed, err := decode[apiclient.StartSessionJSONRequestBody](body)
		if err != nil {
			return nil, err
		}
		if parsed.LauncherId == "" {
			return nil, argumentError{fmt.Errorf("body.launcher_id is required")}
		}
		return c.StartSession(ctx, *parsed, edit)
	},
	"get_session": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "session_id")
		if err != nil {
			return nil, err
		}
		return c.GetSession(ctx, id, edit)
	},
	"get_session_logs": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "session_id")
		if err != nil {
			return nil, err
		}
		params, err := parseArgs[apiclient.GetSessionLogsParams](req)
		if err != nil {
			return nil, err
		}
		return c.GetSessionLogs(ctx, id, params, edit)
	},
	"stop_session": func(ctx context.Context, c *apiclient.Client, req mcp.CallToolRequest, edit apiclient.RequestEditorFn) (*http.Response, error) {
		id, err := requiredString(req, "session_id")
		if err != nil {
			return nil, err
		}
		return c.StopSession(ctx, id, edit)
	},
}

// parseArgs decodes the tool arguments into a generated parameter struct, ignoring path parameters.
func parseArgs[T any](req mcp.CallToolRequest) (*T, error) {
	return decode[T](req.GetArguments())
}

// decode converts tool arguments into a generated type by going through JSON.
func decode[T any](args any) (*T, error) {
	raw, err := json.Marshal(args)
	if err != nil {
		return nil, argumentError{err}
	}
	var out T
	if err := json.Unmarshal(raw, &out); err != nil {
		return nil, argumentError{err}
	}
	return &out, nil
}

func requiredString(req mcp.CallToolRequest, name string) (string, error) {
	value, err := req.RequireString(name)
	if err != nil || value == "" {
		return "", argumentError{fmt.Errorf("%s is required", name)}
	}
	return value, nil
}
