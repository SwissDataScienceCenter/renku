package apispec

import "github.com/SwissDataScienceCenter/renku/mcp-server/internal/api2tool"

// Tools is the allowlist of renku-data-services operations exposed as MCP tools.
//
// The MCP server builds a tool for each operation listed here at startup, from the
// module spec each tool points to (see internal/api2tool); no hand-written code is
// needed per tool. To take the specs from another data-services tag, or to add a
// module, edit the list in cmd/fetchspecs and run `just update-spec <data-services tag>`.
//
// Conventions: snake_case names starting with a verb, no `renku_` prefix (MCP clients
// namespace tools by server), and descriptions that say which IDs a tool returns
// and which other tools consume them.
var Tools = []api2tool.ToolConfig{
	{
		Spec:   usersSpec,
		Method: "get",
		Path:   "/user",
		Name:   "get_current_user",
		Description: "Get the Renku user that is logged in, including their user ID and username. " +
			"The username is also the user's personal namespace, usable as the `namespace` " +
			"filter of list_projects and list_data_connectors.",
	},
	{
		Spec:   searchSpec,
		Method: "get",
		Path:   "/search/query",
		Name:   "search",
		Description: "Full-text search across Renku projects, groups, users and data connectors visible to the user. " +
			"The query supports filters such as `type:project`, `type:group`, `type:dataconnector`, " +
			"`namespace:<slug>`, `visibility:public` and `role:owner`, combined with free text, " +
			"e.g. `type:project climate`. Results contain IDs usable with get_project, get_group " +
			"and get_data_connector.",
		QueryDescriptions: map[string]string{
			"q": "The search query, free text optionally combined with filters such as `type:project`.",
		},
	},
	{
		Spec:   projectSpec,
		Method: "get",
		Path:   "/projects",
		Name:   "list_projects",
		Description: "List Renku projects the user can access, newest first. Use `namespace` to restrict the " +
			"list to a user or group namespace and `direct_member` to only list projects the user is " +
			"a member of. Returns project IDs usable with get_project, list_project_session_launchers " +
			"and list_project_data_connector_links.",
	},
	{
		Spec:        projectSpec,
		Method:      "get",
		Path:        "/projects/{project_id}",
		Name:        "get_project",
		Description: "Get a Renku project by its ID (a ULID such as 01HXXXXXXXXXXXXXXXXXXXXXXX).",
	},
	{
		Spec:   projectSpec,
		Method: "get",
		Path:   "/namespaces/{namespace}/projects/{slug}",
		Name:   "get_project_by_slug",
		Description: "Get a Renku project by its namespace and slug, e.g. namespace \"my-group\" and slug " +
			"\"my-project\" for the project at /p/my-group/my-project. Use this when the user refers " +
			"to a project by name or URL. Returns the project ID needed by other tools.",
	},
	{
		Spec:   namespaceSpec,
		Method: "get",
		Path:   "/groups",
		Name:   "list_groups",
		Description: "List Renku groups. Returns group slugs usable with get_group and as the `namespace` " +
			"filter of list_projects and list_data_connectors.",
	},
	{
		Spec:        namespaceSpec,
		Method:      "get",
		Path:        "/groups/{group_slug}",
		Name:        "get_group",
		Description: "Get a Renku group by its slug.",
	},
	{
		Spec:   dataConnectorsSpec,
		Method: "get",
		Path:   "/data_connectors",
		Name:   "list_data_connectors",
		Description: "List data connectors (connections to external storage such as S3 buckets or WebDAV shares) " +
			"the user can access. Use `namespace` to restrict the list to a user or group namespace. " +
			"Returns data connector IDs usable with get_data_connector.",
	},
	{
		Spec:        dataConnectorsSpec,
		Method:      "get",
		Path:        "/data_connectors/{data_connector_id}",
		Name:        "get_data_connector",
		Description: "Get a data connector by its ID, including its storage configuration and mount point.",
	},
	{
		Spec:   dataConnectorsSpec,
		Method: "get",
		Path:   "/projects/{project_id}/data_connector_links",
		Name:   "list_project_data_connector_links",
		Description: "List the data connectors linked to a project. Each link contains the data connector ID, " +
			"usable with get_data_connector.",
	},
	{
		Spec:   sessionSpec,
		Method: "get",
		Path:   "/projects/{project_id}/session_launchers",
		Name:   "list_project_session_launchers",
		Description: "List the session launchers of a project. A session launcher defines the environment " +
			"(image) a session runs. Returns launcher IDs needed by start_session.",
	},
	{
		Spec:        sessionSpec,
		Method:      "get",
		Path:        "/session_launchers/{launcher_id}",
		Name:        "get_session_launcher",
		Description: "Get a session launcher by its ID, including its environment and default resource class.",
	},
	{
		Spec:   notebooksSpec,
		Method: "get",
		Path:   "/sessions",
		Name:   "list_sessions",
		Description: "List the user's running and hibernated sessions. Returns session names usable with " +
			"get_session, get_session_logs and stop_session.",
	},
	{
		Spec:   notebooksSpec,
		Method: "post",
		Path:   "/sessions",
		Name:   "start_session",
		Description: "Start a session from a session launcher, or return the existing one if a session for " +
			"that launcher is already running. Usually only `launcher_id` is needed; get it from " +
			"list_project_session_launchers. The response contains the session name and its URL, " +
			"which the user can open in a browser once the status is `running`. Starting can take " +
			"a few minutes; poll with get_session.",
	},
	{
		Spec:   notebooksSpec,
		Method: "get",
		Path:   "/sessions/{session_id}",
		Name:   "get_session",
		Description: "Get a session by its name, including its status and URL. Use it to wait for a started " +
			"session to become `running`.",
		PathDescriptions: map[string]string{
			"session_id": sessionIDDescription,
		},
	},
	{
		Spec:   notebooksSpec,
		Method: "get",
		Path:   "/sessions/{session_id}/logs",
		Name:   "get_session_logs",
		Description: "Get the most recent log lines of each container of a session. Useful to find out why a " +
			"session failed to start.",
		PathDescriptions: map[string]string{
			"session_id": sessionIDDescription,
		},
	},
	{
		Spec:   notebooksSpec,
		Method: "delete",
		Path:   "/sessions/{session_id}",
		Name:   "stop_session",
		Description: "Stop and fully remove a session. Unsaved work in the session is lost, so confirm with " +
			"the user before calling this.",
		PathDescriptions: map[string]string{
			"session_id": sessionIDDescription,
		},
	},
}

// sessionIDDescription is shared by the tools that take a session name.
const sessionIDDescription = "The session name, as returned by start_session or list_sessions."
