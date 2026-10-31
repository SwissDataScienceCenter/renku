package tools

import (
	"context"
	"encoding/json"
	"io"
	"net/http"
	"net/http/httptest"
	"slices"
	"strings"
	"testing"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
)

const ulid = "01J0000000000000000000000C"

const sessionJSON = `{"name": "session-1", "image": "renku/renkulab-py", "launcher_id": "` + ulid + `",
	"project_id": "` + ulid + `", "resource_class_id": 1, "resources": {}, "session_type": "interactive",
	"started": null, "status": {"state": "starting", "ready_containers": 0, "total_containers": 2},
	"url": "https://renku.example.org/sessions/session-1"}`

type recordedRequest struct {
	method, path, query, authorization, contentType string
	body                                            []byte
}

// fakeGateway answers every request with the response of the route, or {} for routes not listed.
func fakeGateway(t *testing.T) (string, *[]recordedRequest) {
	t.Helper()
	var requests []recordedRequest
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		body, _ := io.ReadAll(r.Body)
		requests = append(requests, recordedRequest{
			method: r.Method, path: r.URL.EscapedPath(), query: r.URL.RawQuery, body: body,
			authorization: r.Header.Get("Authorization"), contentType: r.Header.Get("Content-Type"),
		})
		w.Header().Set("Content-Type", "application/json")
		switch r.Method + " " + r.URL.Path {
		case "GET /api/data/projects":
			w.Header().Set("page", "1")
			w.Header().Set("per-page", "20")
			w.Header().Set("total", "45")
			w.Header().Set("total-pages", "3")
			_, _ = w.Write([]byte(`[{"id": "` + ulid + `", "name": "climate", "namespace": "my-group", "slug": "climate",
				"visibility": "public", "created_by": "user-1", "creation_date": "2023-11-01T17:32:28Z"}]`))
		case "GET /api/data/search/query":
			_, _ = w.Write([]byte(`{"facets": {"entityType": {}, "keywords": {}}, "items": [
				{"type": "Project", "id": "` + ulid + `", "name": "climate", "slug": "climate", "path": "my-group/climate",
				 "visibility": "public", "creationDate": "2023-11-01T17:32:28Z"}],
				"pagingInfo": {"page": {"limit": 10, "offset": 0}, "totalPages": 1, "totalResult": 1}}`))
		case "GET /api/data/data_connectors/" + ulid:
			_, _ = w.Write([]byte(`{"id": "` + ulid + `", "name": "data", "slug": "data", "namespace": "my-group",
				"visibility": "private", "created_by": "user-1", "creation_date": "2023-11-01T17:32:28Z", "etag": "abc",
				"storage": {"configuration": {"type": "s3", "provider": "AWS"}, "readonly": true, "sensitive_fields": [],
				"source_path": "bucket/data", "target_path": "data", "storage_type": "s3"}}`))
		case "GET /api/data/groups", "GET /api/data/data_connectors", "GET /api/data/projects/" + ulid + "/data_connector_links",
			"GET /api/data/projects/" + ulid + "/session_launchers":
			_, _ = w.Write([]byte(`[]`))
		case "GET /api/data/sessions":
			_, _ = w.Write([]byte(`[` + sessionJSON + `]`))
		case "POST /api/data/sessions":
			w.WriteHeader(http.StatusCreated)
			_, _ = w.Write([]byte(sessionJSON))
		case "GET /api/data/sessions/session-1":
			_, _ = w.Write([]byte(sessionJSON))
		case "GET /api/data/sessions/session-1/logs":
			_, _ = w.Write([]byte(`{"amalthea-session": "line 1\nline 2"}`))
		case "DELETE /api/data/sessions/session-1":
			w.WriteHeader(http.StatusNoContent)
		default:
			_, _ = w.Write([]byte(`{}`))
		}
	}))
	t.Cleanup(srv.Close)
	return srv.URL + "/api/data", &requests
}

// connect serves all tools to an MCP client, which calls them as the user user-1.
func connect(t *testing.T, apiURL string) *mcp.ClientSession {
	t.Helper()
	api, err := renkuapi.NewClients(apiURL, http.DefaultClient)
	if err != nil {
		t.Fatal(err)
	}
	server := mcp.NewServer(&mcp.Implementation{Name: "test", Version: "dev"}, nil)
	Register(server, api)
	ctx := auth.WithPrincipal(context.Background(), auth.Principal{Subject: "user-1", Token: "the-token"})
	serverTransport, clientTransport := mcp.NewInMemoryTransports()
	serverSession, err := server.Connect(ctx, serverTransport, nil)
	if err != nil {
		t.Fatal(err)
	}
	t.Cleanup(func() { _ = serverSession.Close() })
	client := mcp.NewClient(&mcp.Implementation{Name: "test-client", Version: "dev"}, nil)
	session, err := client.Connect(ctx, clientTransport, nil)
	if err != nil {
		t.Fatal(err)
	}
	t.Cleanup(func() { _ = session.Close() })
	return session
}

func text(res *mcp.CallToolResult) string {
	var parts []string
	for _, c := range res.Content {
		if tc, ok := c.(*mcp.TextContent); ok {
			parts = append(parts, tc.Text)
		}
	}
	return strings.Join(parts, "\n")
}

type requestCase struct {
	tool                string
	args                map[string]any
	method, path, query string
	body                string
}

// TestEveryToolSendsTheExpectedRequest calls every tool through an MCP client, which also checks
// the input and output schemas the SDK infers and the outputs against them.
func TestEveryToolSendsTheExpectedRequest(t *testing.T) {
	tests := []requestCase{
		{"get_current_user", nil, "GET", "/api/data/user", "", ""},
		{"search", map[string]any{"q": "type:project climate", "page": 2, "per_page": 50, "include_counts": true}, "GET", "/api/data/search/query", "include_counts=true&page=2&per_page=50&q=type%3Aproject+climate", ""},
		{"list_projects", map[string]any{"namespace": "my-group", "direct_member": true}, "GET", "/api/data/projects", "direct_member=true&namespace=my-group", ""},
		{"get_project", map[string]any{"project_id": ulid, "with_documentation": false}, "GET", "/api/data/projects/" + ulid, "with_documentation=false", ""},
		{"get_project_by_slug", map[string]any{"namespace": "my group", "slug": "my-project"}, "GET", "/api/data/namespaces/my%20group/projects/my-project", "with_documentation=false", ""},
		{"list_groups", map[string]any{"page": 1}, "GET", "/api/data/groups", "page=1", ""},
		{"get_group", map[string]any{"group_slug": "my-group"}, "GET", "/api/data/groups/my-group", "", ""},
		{"list_data_connectors", map[string]any{"namespace": "my-group"}, "GET", "/api/data/data_connectors", "namespace=my-group", ""},
		{"get_data_connector", map[string]any{"data_connector_id": ulid}, "GET", "/api/data/data_connectors/" + ulid, "", ""},
		{"list_project_data_connector_links", map[string]any{"project_id": ulid}, "GET", "/api/data/projects/" + ulid + "/data_connector_links", "", ""},
		{"list_project_session_launchers", map[string]any{"project_id": ulid}, "GET", "/api/data/projects/" + ulid + "/session_launchers", "", ""},
		{"get_session_launcher", map[string]any{"launcher_id": ulid}, "GET", "/api/data/session_launchers/" + ulid, "", ""},
		{"list_sessions", map[string]any{"session_type": "interactive"}, "GET", "/api/data/sessions", "session_type=interactive", ""},
		{"start_session", map[string]any{"launcher_id": ulid, "disk_storage": 10}, "POST", "/api/data/sessions", "", `{"disk_storage":10,"launcher_id":"` + ulid + `"}`},
		{"get_session", map[string]any{"session_id": "session-1"}, "GET", "/api/data/sessions/session-1", "", ""},
		{"get_session_logs", map[string]any{"session_id": "session-1", "max_lines": 1000000}, "GET", "/api/data/sessions/session-1/logs", "max_lines=1000000", ""},
		{"stop_session", map[string]any{"session_id": "session-1"}, "DELETE", "/api/data/sessions/session-1", "", ""},
	}
	apiURL, _ := fakeGateway(t)
	listed, err := connect(t, apiURL).ListTools(context.Background(), nil)
	if err != nil {
		t.Fatal(err)
	}
	for _, tool := range listed.Tools {
		if !slices.ContainsFunc(tests, func(tt requestCase) bool { return tt.tool == tool.Name }) {
			t.Errorf("tool %s is not covered", tool.Name)
		}
	}
	for _, tt := range tests {
		t.Run(tt.tool, func(t *testing.T) {
			apiURL, requests := fakeGateway(t)
			res, err := connect(t, apiURL).CallTool(context.Background(), &mcp.CallToolParams{Name: tt.tool, Arguments: tt.args})
			if err != nil {
				t.Fatal(err)
			}
			if res.IsError {
				t.Fatalf("tool error: %s", text(res))
			}
			if len(*requests) != 1 {
				t.Fatalf("expected one request, got %d", len(*requests))
			}
			r := (*requests)[0]
			if r.method != tt.method || r.path != tt.path || r.query != tt.query {
				t.Errorf("request = %s %s?%s, want %s %s?%s", r.method, r.path, r.query, tt.method, tt.path, tt.query)
			}
			if string(r.body) != tt.body {
				t.Errorf("body = %s, want %s", r.body, tt.body)
			}
			if wantJSON := tt.body != ""; wantJSON != (r.contentType == "application/json") {
				t.Errorf("Content-Type = %q", r.contentType)
			}
			if r.authorization != "Bearer the-token" {
				t.Errorf("Authorization = %q, the user's token must be forwarded", r.authorization)
			}
		})
	}
}

func TestListToolReportsPagination(t *testing.T) {
	apiURL, _ := fakeGateway(t)
	res, err := connect(t, apiURL).CallTool(context.Background(), &mcp.CallToolParams{Name: "list_projects"})
	if err != nil {
		t.Fatal(err)
	}
	if !strings.HasSuffix(text(res), "Page 1 of 3 (45 total, 20 per page). Call again with page=2 for more.") {
		t.Errorf("missing pagination summary in %q", text(res))
	}
	var out struct {
		Items []struct {
			ID string `json:"id"`
		} `json:"items"`
		Pagination struct {
			TotalPages int `json:"total_pages"`
		} `json:"pagination"`
	}
	raw, _ := json.Marshal(res.StructuredContent)
	if err := json.Unmarshal(raw, &out); err != nil {
		t.Fatal(err)
	}
	if len(out.Items) != 1 || out.Items[0].ID != ulid || out.Pagination.TotalPages != 3 {
		t.Errorf("structured content = %s", raw)
	}
}

func TestStartSessionReportsWhetherItCreatedTheSession(t *testing.T) {
	apiURL, _ := fakeGateway(t)
	res, err := connect(t, apiURL).CallTool(context.Background(), &mcp.CallToolParams{Name: "start_session", Arguments: map[string]any{"launcher_id": ulid}})
	if err != nil {
		t.Fatal(err)
	}
	out, _ := res.StructuredContent.(map[string]any)
	if out["created"] != true {
		t.Errorf("structured content = %v", res.StructuredContent)
	}
}

func TestMissingRequiredArgumentIsRejected(t *testing.T) {
	apiURL, requests := fakeGateway(t)
	res, err := connect(t, apiURL).CallTool(context.Background(), &mcp.CallToolParams{Name: "get_session", Arguments: map[string]any{}})
	if err != nil {
		t.Fatal(err)
	}
	if !res.IsError || !strings.Contains(text(res), "session_id") {
		t.Errorf("expected an error about session_id, got %q", text(res))
	}
	if len(*requests) != 0 {
		t.Errorf("invalid calls must not reach the API, got %d requests", len(*requests))
	}
}
