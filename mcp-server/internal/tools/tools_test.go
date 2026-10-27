package tools

import (
	"bytes"
	"context"
	"encoding/json"
	"io"
	"log/slog"
	"maps"
	"net/http"
	"net/http/httptest"
	"slices"
	"strings"
	"testing"

	"github.com/mark3labs/mcp-go/mcp"
	"github.com/mark3labs/mcp-go/server"

	"github.com/SwissDataScienceCenter/renku/mcp-server/apispec"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/apiclient"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/specprep"
)

func TestEveryAllowlistedToolIsImplemented(t *testing.T) {
	cfg, err := specprep.ParseConfig(apispec.ToolsYAML)
	if err != nil {
		t.Fatal(err)
	}
	var allowlisted []string
	for _, tool := range cfg.Tools {
		allowlisted = append(allowlisted, tool.Name)
	}
	implemented := slices.Collect(maps.Keys(calls))
	slices.Sort(allowlisted)
	slices.Sort(implemented)
	if !slices.Equal(allowlisted, implemented) {
		t.Fatalf("tools.yaml and internal/tools disagree\nallowlisted: %v\nimplemented: %v", allowlisted, implemented)
	}
	// Fails if apispec/mcp-tools.json was not regenerated after editing tools.yaml.
	if err := Register(server.NewMCPServer("test", "dev"), Options{}); err != nil {
		t.Fatal(err)
	}
}

type recordedRequest struct {
	method, path, query, authorization, requestID string
	body                                          []byte
}

// fakeGateway mimics the Renku gateway for the routes the tests use.
func fakeGateway(t *testing.T) (*httptest.Server, *[]recordedRequest) {
	t.Helper()
	var requests []recordedRequest
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		body, _ := io.ReadAll(r.Body)
		requests = append(requests, recordedRequest{
			method: r.Method, path: r.URL.Path, query: r.URL.RawQuery,
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
			w.WriteHeader(http.StatusTeapot)
		}
	}))
	t.Cleanup(srv.Close)
	return srv, &requests
}

func callTool(t *testing.T, gatewayURL, name string, args map[string]any, logs io.Writer) *mcp.CallToolResult {
	t.Helper()
	client, err := apiclient.NewClient(gatewayURL + "/api/data")
	if err != nil {
		t.Fatal(err)
	}
	if logs == nil {
		logs = io.Discard
	}
	opts := Options{Client: client, Logger: slog.New(slog.NewJSONHandler(logs, nil))}
	fn, ok := calls[name]
	if !ok {
		t.Fatalf("tool %s not found", name)
	}
	ctx := auth.WithPrincipal(context.Background(), auth.Principal{Subject: "user-1", Token: "the-token"})
	req := mcp.CallToolRequest{}
	req.Params.Name = name
	req.Params.Arguments = args
	res, err := handler(name, fn, opts)(ctx, req)
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
	c, ok := res.Content[0].(mcp.TextContent)
	if !ok {
		t.Fatalf("expected text content, got %T", res.Content[0])
	}
	return c.Text
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
	for _, want := range []string{"namespace=my-group", "page=1", "per_page=20"} {
		if !strings.Contains(r.query, want) {
			t.Errorf("query %q does not contain %q", r.query, want)
		}
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

func TestInvalidArguments(t *testing.T) {
	gw, requests := fakeGateway(t)
	res := callTool(t, gw.URL, "get_project", map[string]any{}, nil)
	if !res.IsError || !strings.Contains(text(t, res), "project_id is required") {
		t.Errorf("unexpected result %+v", res)
	}
	res = callTool(t, gw.URL, "start_session", map[string]any{"body": map[string]any{}}, nil)
	if !res.IsError || !strings.Contains(text(t, res), "launcher_id is required") {
		t.Errorf("unexpected result %+v", res)
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
