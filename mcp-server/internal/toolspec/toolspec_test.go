package toolspec

import (
	"context"
	"io"
	"strings"
	"testing"
)

func load(*testing.T) map[string]Binding {
	return map[string]Binding{
		"list_projects":  {Method: "GET", Path: "/projects", QueryParameters: []string{"namespace", "page", "tags", "archived"}},
		"create_project": {Method: "POST", Path: "/projects", Body: true},
		"get_file":       {Method: "GET", Path: "/projects/{project_id}/files/{path}", PathParameters: []string{"project_id", "path"}},
	}
}

func request(t *testing.T, binding Binding, args map[string]any) (method, target, body string) {
	t.Helper()
	r, err := binding.Request(context.Background(), "http://gateway/api/data/", args)
	if err != nil {
		t.Fatalf("Request: %v", err)
	}
	if r.Body != nil {
		raw, _ := io.ReadAll(r.Body)
		body = string(raw)
		if r.Header.Get("Content-Type") != "application/json" {
			t.Errorf("Content-Type = %q", r.Header.Get("Content-Type"))
		}
	}
	return r.Method, r.URL.String(), body
}

func TestRequest(t *testing.T) {
	tools := load(t)
	tests := []struct {
		name, tool string
		args       map[string]any
		want       string
		wantBody   string
	}{
		{"no arguments", "list_projects", map[string]any{}, "GET http://gateway/api/data/projects", ""},
		{"scalars", "list_projects", map[string]any{"namespace": "a b&c", "page": float64(2), "archived": false}, "GET http://gateway/api/data/projects?archived=false&namespace=a+b%26c&page=2", ""},
		{"large numbers are not in exponent notation", "list_projects", map[string]any{"page": float64(1e6)}, "GET http://gateway/api/data/projects?page=1000000", ""},
		{"exploded arrays repeat the parameter", "list_projects", map[string]any{"tags": []any{"x", "y"}}, "GET http://gateway/api/data/projects?tags=x&tags=y", ""},
		{"null is left out", "list_projects", map[string]any{"archived": nil}, "GET http://gateway/api/data/projects", ""},
		{"values are not validated", "list_projects", map[string]any{"page": "one"}, "GET http://gateway/api/data/projects?page=one", ""},
		{"path parameters are escaped", "get_file", map[string]any{"project_id": "p1", "path": "dir/file name.txt"}, "GET http://gateway/api/data/projects/p1/files/dir%2Ffile%20name.txt", ""},
		{"body", "create_project", map[string]any{"body": map[string]any{"name": "climate"}}, "POST http://gateway/api/data/projects", `{"name":"climate"}`},
		{"missing body is left to the API", "create_project", map[string]any{}, "POST http://gateway/api/data/projects", ""},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			method, target, body := request(t, tools[tt.tool], tt.args)
			if got := method + " " + target; got != tt.want {
				t.Errorf("request = %s, want %s", got, tt.want)
			}
			if body != tt.wantBody {
				t.Errorf("body = %q, want %q", body, tt.wantBody)
			}
		})
	}
}

func TestRequestErrors(t *testing.T) {
	tools := load(t)
	tests := []struct {
		name, tool string
		args       map[string]any
		want       string
	}{
		{"unknown argument", "list_projects", map[string]any{"namespcae": "x"}, "unknown argument namespcae"},
		{"body of a tool without one", "list_projects", map[string]any{"body": map[string]any{}}, "unknown argument body"},
		{"missing path parameter", "get_file", map[string]any{"project_id": "p1"}, "missing required argument path"},
		{"object path parameter", "get_file", map[string]any{"project_id": "p1", "path": map[string]any{}}, "path: expected a string"},
		{"empty path parameter", "get_file", map[string]any{"project_id": "", "path": "a"}, `project_id: "" is not a valid path segment`},
		{"dot path parameter", "get_file", map[string]any{"project_id": "p1", "path": "."}, `path: "." is not a valid path segment`},
		{"dot dot path parameter", "get_file", map[string]any{"project_id": "..", "path": "a"}, `project_id: ".." is not a valid path segment`},
		{"object query parameter", "list_projects", map[string]any{"namespace": map[string]any{}}, "namespace: expected a string"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			_, err := tools[tt.tool].Request(context.Background(), "http://gateway", tt.args)
			if err == nil || !strings.Contains(err.Error(), tt.want) {
				t.Fatalf("expected an error containing %q, got %v", tt.want, err)
			}
		})
	}
}
