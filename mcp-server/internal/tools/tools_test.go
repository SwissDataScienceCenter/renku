package tools

import (
	"bytes"
	"context"
	"encoding/json"
	"io"
	"log/slog"
	"net/http"
	"net/http/httptest"
	"slices"
	"strings"
	"testing"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/apispec"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/toolspec"
)

// Fails if the tools cannot be built from tools.yaml and the module specs.
func TestToolsBuildFromTheEmbeddedSpecs(t *testing.T) {
	if err := Register(mcp.NewServer(&mcp.Implementation{Name: "test", Version: "dev"}, nil), Options{}); err != nil {
		t.Fatal(err)
	}
}

type recordedRequest struct {
	method, path, query, authorization, requestID, contentType string
	body                                                       []byte
}

// fakeGateway mimics the Renku gateway for the routes the tests use.
func fakeGateway(t *testing.T) (*httptest.Server, *[]recordedRequest) {
	t.Helper()
	var requests []recordedRequest
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		body, _ := io.ReadAll(r.Body)
		requests = append(requests, recordedRequest{
			method: r.Method, path: r.URL.EscapedPath(), query: r.URL.RawQuery, contentType: r.Header.Get("Content-Type"),
			authorization: r.Header.Get("Authorization"), requestID: r.Header.Get("X-Request-Id"), body: body,
		})
		w.Header().Set("Content-Type", "application/json")
		switch r.Method + " " + r.URL.Path {
		case "GET /api/data/projects":
			w.Header().Set("page", "1")
			w.Header().Set("per-page", "20")
			w.Header().Set("total", "45")
			w.Header().Set("total-pages", "3")
			_, _ = w.Write([]byte(`[ {"id": "01J0000000000000000000000A", "name": "climate"} ]`))
		case "GET /api/data/search/query":
			w.Header().Set("x-page", "2")
			w.Header().Set("x-per-page", "10")
			w.Header().Set("x-total", "15")
			w.Header().Set("x-total-pages", "2")
			_, _ = w.Write([]byte(`{"items": []}`))
		case "GET /api/data/projects/01J0000000000000000000000B":
			w.WriteHeader(http.StatusNotFound)
			_, _ = w.Write([]byte(`{"error": {"code": 1404, "message": "Project 01J0000000000000000000000B does not exist."}}`))
		case "GET /api/data/user":
			w.WriteHeader(http.StatusInternalServerError)
			_, _ = w.Write([]byte(`{"error": {"code": 1500, "message": "database exploded at 10.0.0.12"}}`))
		case "POST /api/data/sessions":
			w.WriteHeader(http.StatusCreated)
			_, _ = w.Write([]byte(`{"name": "session-1", "status": {"state": "starting"}}`))
		case "DELETE /api/data/sessions/session-1":
			w.WriteHeader(http.StatusNoContent)
		default:
			_, _ = w.Write([]byte(`{}`))
		}
	}))
	t.Cleanup(srv.Close)
	return srv, &requests
}

func tool(t *testing.T, name string) toolspec.Tool {
	t.Helper()
	tools, err := toolspec.Load(apispec.ToolsYAML, apispec.Modules)
	if err != nil {
		t.Fatal(err)
	}
	i := slices.IndexFunc(tools, func(tool toolspec.Tool) bool { return tool.Name == name })
	if i < 0 {
		t.Fatalf("tool %s not found", name)
	}
	return tools[i]
}

func callTool(t *testing.T, gatewayURL, name string, args map[string]any, logs io.Writer) *mcp.CallToolResult {
	t.Helper()
	if logs == nil {
		logs = io.Discard
	}
	opts := Options{APIURL: gatewayURL + "/api/data", HTTPClient: http.DefaultClient, Logger: slog.New(slog.NewJSONHandler(logs, nil))}
	return callHandler(t, handler(tool(t, name), opts), name, args)
}

func callHandler(t *testing.T, h mcp.ToolHandler, name string, args map[string]any) *mcp.CallToolResult {
	t.Helper()
	ctx := auth.WithPrincipal(context.Background(), auth.Principal{Subject: "user-1", Token: "the-token"})
	raw, err := json.Marshal(args)
	if err != nil {
		t.Fatal(err)
	}
	res, err := h(ctx, &mcp.CallToolRequest{Params: &mcp.CallToolParamsRaw{Name: name, Arguments: raw}})
	if err != nil {
		t.Fatalf("handler returned an error instead of a tool error: %v", err)
	}
	return res
}

func text(t *testing.T, res *mcp.CallToolResult) string {
	t.Helper()
	if len(res.Content) != 1 {
		t.Fatalf("expected one content item, got %d", len(res.Content))
	}
	c, ok := res.Content[0].(*mcp.TextContent)
	if !ok {
		t.Fatalf("expected text content, got %T", res.Content[0])
	}
	return c.Text
}

type requestCase struct {
	tool                string
	args                map[string]any
	method, path, query string
	body                string
}

// TestEveryToolSendsTheExpectedRequest covers the request encoding of every tool against the real spec.
func TestEveryToolSendsTheExpectedRequest(t *testing.T) {
	const ulid = "01J0000000000000000000000C"
	tests := []requestCase{
		{"get_current_user", nil, "GET", "/api/data/user", "", ""},
		{"search", map[string]any{"q": "type:project climate", "page": 2, "per_page": 50, "include_counts": true}, "GET", "/api/data/search/query", "include_counts=true&page=2&per_page=50&q=type%3Aproject+climate", ""},
		{"list_projects", map[string]any{"namespace": "my-group", "direct_member": true}, "GET", "/api/data/projects", "direct_member=true&namespace=my-group", ""},
		{"get_project", map[string]any{"project_id": ulid, "with_documentation": false}, "GET", "/api/data/projects/" + ulid, "with_documentation=false", ""},
		{"get_project_by_slug", map[string]any{"namespace": "my group", "slug": "my-project"}, "GET", "/api/data/namespaces/my%20group/projects/my-project", "", ""},
		{"list_groups", map[string]any{"page": 1}, "GET", "/api/data/groups", "page=1", ""},
		{"get_group", map[string]any{"group_slug": "my-group"}, "GET", "/api/data/groups/my-group", "", ""},
		{"list_data_connectors", map[string]any{"namespace": "my-group"}, "GET", "/api/data/data_connectors", "namespace=my-group", ""},
		{"get_data_connector", map[string]any{"data_connector_id": ulid}, "GET", "/api/data/data_connectors/" + ulid, "", ""},
		{"list_project_data_connector_links", map[string]any{"project_id": ulid}, "GET", "/api/data/projects/" + ulid + "/data_connector_links", "", ""},
		{"list_project_session_launchers", map[string]any{"project_id": ulid}, "GET", "/api/data/projects/" + ulid + "/session_launchers", "", ""},
		{"get_session_launcher", map[string]any{"launcher_id": ulid}, "GET", "/api/data/session_launchers/" + ulid, "", ""},
		{"list_sessions", map[string]any{"session_type": "interactive"}, "GET", "/api/data/sessions", "session_type=interactive", ""},
		{"start_session", map[string]any{"body": map[string]any{"launcher_id": ulid, "disk_storage": 10}}, "POST", "/api/data/sessions", "", `{"disk_storage":10,"launcher_id":"` + ulid + `"}`},
		{"get_session", map[string]any{"session_id": "session-1"}, "GET", "/api/data/sessions/session-1", "", ""},
		{"get_session_logs", map[string]any{"session_id": "session-1", "max_lines": 1000000}, "GET", "/api/data/sessions/session-1/logs", "max_lines=1000000", ""},
		{"stop_session", map[string]any{"session_id": "session-1"}, "DELETE", "/api/data/sessions/session-1", "", ""},
	}
	tools, err := toolspec.Load(apispec.ToolsYAML, apispec.Modules)
	if err != nil {
		t.Fatal(err)
	}
	for _, tool := range tools {
		if !slices.ContainsFunc(tests, func(tt requestCase) bool { return tt.tool == tool.Name }) {
			t.Errorf("tool %s is not covered", tool.Name)
		}
	}
	for _, tt := range tests {
		t.Run(tt.tool, func(t *testing.T) {
			gw, requests := fakeGateway(t)
			res := callTool(t, gw.URL, tt.tool, tt.args, nil)
			if len(*requests) != 1 {
				t.Fatalf("expected one request, got %d: %s", len(*requests), text(t, res))
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
		})
	}
}

func TestListToolForwardsTokenAndQueryAndReportsPagination(t *testing.T) {
	gw, requests := fakeGateway(t)
	res := callTool(t, gw.URL, "list_projects", map[string]any{"namespace": "my-group", "page": float64(1), "per_page": float64(20)}, nil)

	if res.IsError {
		t.Fatalf("unexpected error: %s", text(t, res))
	}
	r := (*requests)[0]
	if r.method != http.MethodGet || r.path != "/api/data/projects" {
		t.Errorf("request = %s %s", r.method, r.path)
	}
	if r.authorization != "Bearer the-token" {
		t.Errorf("Authorization = %q, the user's token must be forwarded unchanged", r.authorization)
	}
	if r.requestID == "" {
		t.Error("X-Request-Id is not set")
	}
	if r.query != "namespace=my-group&page=1&per_page=20" {
		t.Errorf("query = %q", r.query)
	}

	got := text(t, res)
	if !strings.HasPrefix(got, `[{"id":"01J0000000000000000000000A","name":"climate"}]`) {
		t.Errorf("expected compact JSON, got %q", got)
	}
	if !strings.HasSuffix(got, "Page 1 of 3 (45 total, 20 per page). Call again with page=2 for more.") {
		t.Errorf("missing pagination summary in %q", got)
	}
	structured := res.StructuredContent.(map[string]any)
	if p := structured["pagination"].(*Pagination); *p != (Pagination{Page: 1, PerPage: 20, Total: 45, TotalPages: 3}) {
		t.Errorf("pagination = %+v", p)
	}
}

func TestSearchUsesPrefixedPaginationHeaders(t *testing.T) {
	gw, _ := fakeGateway(t)
	res := callTool(t, gw.URL, "search", map[string]any{"q": "type:project climate", "page": float64(2)}, nil)
	if !strings.HasSuffix(text(t, res), "Page 2 of 2 (15 total, 10 per page).") {
		t.Errorf("unexpected text %q", text(t, res))
	}
}

func TestClientErrorIsReturnedToTheModel(t *testing.T) {
	gw, _ := fakeGateway(t)
	res := callTool(t, gw.URL, "get_project", map[string]any{"project_id": "01J0000000000000000000000B"}, nil)
	if !res.IsError {
		t.Fatal("expected a tool error")
	}
	if want := "status 404: Project 01J0000000000000000000000B does not exist."; !strings.Contains(text(t, res), want) {
		t.Errorf("text = %q, want it to contain %q", text(t, res), want)
	}
}

func TestServerErrorIsGeneric(t *testing.T) {
	gw, _ := fakeGateway(t)
	var logs bytes.Buffer
	res := callTool(t, gw.URL, "get_current_user", nil, &logs)
	if !res.IsError {
		t.Fatal("expected a tool error")
	}
	if strings.Contains(text(t, res), "10.0.0.12") {
		t.Errorf("internal details leaked to the model: %q", text(t, res))
	}
	if !strings.Contains(logs.String(), "10.0.0.12") {
		t.Errorf("upstream error body should be logged, logs: %s", logs.String())
	}
}

func TestLogsSubjectButNotTokenOrArguments(t *testing.T) {
	gw, _ := fakeGateway(t)
	var logs bytes.Buffer
	callTool(t, gw.URL, "list_projects", map[string]any{"namespace": "secret-namespace"}, &logs)

	var line map[string]any
	if err := json.Unmarshal(logs.Bytes(), &line); err != nil {
		t.Fatalf("expected one JSON log line, got %q", logs.String())
	}
	if line["tool"] != "list_projects" || line["sub"] != "user-1" || line["outcome"] != "ok" || line["status"] != float64(200) {
		t.Errorf("unexpected log line %v", line)
	}
	if strings.Contains(logs.String(), "the-token") || strings.Contains(logs.String(), "secret-namespace") {
		t.Errorf("token or arguments logged: %s", logs.String())
	}
}

// TestInvalidArguments covers the arguments that cannot be put into a request. All other checks are left to the API.
func TestInvalidArguments(t *testing.T) {
	tests := []struct {
		name, tool string
		args       map[string]any
		want       string
	}{
		{"missing path parameter", "get_project", map[string]any{}, "project_id"},
		{"unknown argument", "list_projects", map[string]any{"namespcae": "my-group"}, "namespcae"},
		{"object query parameter", "list_projects", map[string]any{"namespace": map[string]any{}}, "namespace"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			gw, requests := fakeGateway(t)
			var logs bytes.Buffer
			res := callTool(t, gw.URL, tt.tool, tt.args, &logs)
			if !res.IsError || !strings.HasPrefix(text(t, res), "Invalid arguments: ") || !strings.Contains(text(t, res), tt.want) {
				t.Errorf("expected an invalid arguments error mentioning %q, got %q", tt.want, text(t, res))
			}
			if !strings.Contains(logs.String(), `"outcome":"invalid_arguments"`) {
				t.Errorf("not logged as invalid arguments: %s", logs.String())
			}
			if len(*requests) != 0 {
				t.Errorf("invalid calls must not reach the API, got %d requests", len(*requests))
			}
		})
	}
}

func TestArgumentsAreValidatedByTheAPI(t *testing.T) {
	gw, requests := fakeGateway(t)
	callTool(t, gw.URL, "list_projects", map[string]any{"per_page": 1000, "page": "one"}, nil)
	if len(*requests) != 1 || (*requests)[0].query != "page=one&per_page=1000" {
		t.Errorf("arguments that do not match the input schema should be sent to the API, got %v", *requests)
	}
}

// panicTransport fails every request with a panic.
type panicTransport struct{}

func (panicTransport) RoundTrip(*http.Request) (*http.Response, error) { panic("boom") }

func TestPanicIsReturnedAsToolError(t *testing.T) {
	var logs bytes.Buffer
	opts := Options{APIURL: "http://gateway/api/data", HTTPClient: &http.Client{Transport: panicTransport{}}, Logger: slog.New(slog.NewJSONHandler(&logs, nil))}
	res := callHandler(t, handler(tool(t, "get_current_user"), opts), "get_current_user", nil)
	if !res.IsError || strings.Contains(text(t, res), "boom") {
		t.Errorf("unexpected result %q", text(t, res))
	}
	if !strings.Contains(logs.String(), `"outcome":"panic"`) {
		t.Errorf("panic not logged: %s", logs.String())
	}
}

func TestArgumentsMustBeAnObject(t *testing.T) {
	gw, requests := fakeGateway(t)
	h := handler(tool(t, "list_projects"), Options{APIURL: gw.URL + "/api/data", HTTPClient: http.DefaultClient, Logger: slog.New(slog.NewJSONHandler(io.Discard, nil))})
	res, err := h(context.Background(), &mcp.CallToolRequest{Params: &mcp.CallToolParamsRaw{Name: "list_projects", Arguments: json.RawMessage(`[1]`)}})
	if err != nil {
		t.Fatal(err)
	}
	if !res.IsError || !strings.Contains(text(t, res), "arguments must be a JSON object") {
		t.Errorf("unexpected result %q", text(t, res))
	}
	if len(*requests) != 0 {
		t.Errorf("invalid calls must not reach the API, got %d requests", len(*requests))
	}
}

func TestStartAndStopSession(t *testing.T) {
	gw, requests := fakeGateway(t)
	res := callTool(t, gw.URL, "start_session", map[string]any{"body": map[string]any{"launcher_id": "01J0000000000000000000000C"}}, nil)
	if res.IsError {
		t.Fatalf("unexpected error: %s", text(t, res))
	}
	var sent map[string]any
	if err := json.Unmarshal((*requests)[0].body, &sent); err != nil {
		t.Fatal(err)
	}
	if sent["launcher_id"] != "01J0000000000000000000000C" {
		t.Errorf("request body = %s", (*requests)[0].body)
	}

	res = callTool(t, gw.URL, "stop_session", map[string]any{"session_id": "session-1"}, nil)
	if res.IsError || text(t, res) != "Done (status 204)." {
		t.Errorf("unexpected result %q", text(t, res))
	}
	if r := (*requests)[1]; r.method != http.MethodDelete || r.path != "/api/data/sessions/session-1" {
		t.Errorf("request = %s %s", r.method, r.path)
	}
}
