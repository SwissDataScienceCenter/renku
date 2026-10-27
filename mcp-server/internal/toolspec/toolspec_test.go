package toolspec

import (
	"context"
	"encoding/json"
	"io"
	"strings"
	"testing"
)

const spec = `
openapi: 3.1.0
paths:
  /projects:
    get:
      operationId: list_projects
      description: List projects.
      parameters:
        - {name: namespace, in: query, description: A namespace., schema: {type: string}}
        - {name: page, in: query, schema: {type: integer, minimum: 1, default: 1}}
        - {name: tags, in: query, style: form, explode: true, schema: {type: array, items: {type: string}}}
        - {name: archived, in: query, schema: {type: [boolean, "null"]}}
    post:
      operationId: create_project
      description: Create a project.
      requestBody:
        required: true
        description: The project.
        content:
          application/json:
            schema:
              type: object
              required: [name]
              properties:
                name: {type: string}
  /projects/{project_id}/files/{path}:
    get:
      operationId: get_file
      description: Get a file.
      parameters:
        - {name: project_id, in: path, required: true, schema: {type: string}}
        - {name: path, in: path, required: true, schema: {type: string}}
`

func load(t *testing.T) map[string]Tool {
	t.Helper()
	tools, err := Load([]byte(spec))
	if err != nil {
		t.Fatal(err)
	}
	byName := map[string]Tool{}
	for _, tool := range tools {
		byName[tool.Name] = tool
	}
	return byName
}

func TestLoadBuildsInputSchemas(t *testing.T) {
	tools := load(t)
	if len(tools) != 3 || tools["list_projects"].Description != "List projects." {
		t.Fatalf("unexpected tools %v", tools)
	}

	raw, err := json.Marshal(tools["list_projects"].InputSchema)
	if err != nil {
		t.Fatal(err)
	}
	var list map[string]any
	if err := json.Unmarshal(raw, &list); err != nil {
		t.Fatal(err)
	}
	props := list["properties"].(map[string]any)
	if len(props) != 4 || list["additionalProperties"] != false || list["type"] != "object" {
		t.Errorf("expected a closed object with the 4 query parameters, got %s", raw)
	}
	if props["namespace"].(map[string]any)["description"] != "A namespace." {
		t.Errorf("parameter description not applied: %s", raw)
	}

	create := tools["create_project"].InputSchema
	if len(create.Required) != 1 || create.Required[0] != BodyArgument {
		t.Errorf("the required request body should be the required body argument, got %v", create.Required)
	}
	if body := create.Properties[BodyArgument]; body.Description != "The project." || body.Required[0] != "name" {
		t.Errorf("body schema = %+v", body)
	}

	if required := tools["get_file"].InputSchema.Required; len(required) != 2 {
		t.Errorf("path parameters must be required, got %v", required)
	}
}

func request(t *testing.T, tool Tool, args map[string]any) (method, target, body string) {
	t.Helper()
	if err := tool.Validate(args); err != nil {
		t.Fatalf("Validate: %v", err)
	}
	r, err := tool.Request(context.Background(), "http://gateway/api/data/", args)
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
		{"path parameters are escaped", "get_file", map[string]any{"project_id": "p1", "path": "dir/file name.txt"}, "GET http://gateway/api/data/projects/p1/files/dir%2Ffile%20name.txt", ""},
		{"body", "create_project", map[string]any{"body": map[string]any{"name": "climate"}}, "POST http://gateway/api/data/projects", `{"name":"climate"}`},
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

func TestValidateRejectsUnknownArguments(t *testing.T) {
	err := load(t)["list_projects"].Validate(map[string]any{"namespcae": "x"})
	if err == nil || !strings.Contains(err.Error(), "namespcae") {
		t.Errorf("expected an error naming the unknown argument, got %v", err)
	}
}

func TestLoadErrors(t *testing.T) {
	tests := []struct {
		name, old, new, want string
	}{
		{"openapi 3.0", "openapi: 3.1.0", "openapi: 3.0.3", "only OpenAPI 3.1.x"},
		{"missing operationId", "operationId: get_file", "", "operationId and description are required"},
		{"duplicate name", "operationId: get_file", "operationId: list_projects", "used by more than one operation"},
		{"header parameter", "name: namespace, in: query", "name: namespace, in: header", "only path and query parameters"},
		{"not exploded", "style: form, explode: true", "style: form, explode: false", "only exploded form style"},
		{"object query parameter", "name: namespace, in: query, description: A namespace., schema: {type: string}", "name: namespace, in: query, schema: {type: object}", "must be flattened"},
		{"optional path parameter", "name: path, in: path, required: true", "name: path, in: path, required: false", "must be required"},
		{"path template mismatch", "/projects/{project_id}/files/{path}", "/projects/{project_id}/files/{file}", "do not match the path template"},
		{"body argument name", "name: namespace, in: query", "name: body, in: query", "argument name is already used"},
		{"non JSON body", "application/json", "multipart/form-data", "only application/json"},
		{"pattern Go cannot compile", "name: {type: string}", "name: {type: string, pattern: '^(?!x)'}", "pattern"},
		{"invalid default", "minimum: 1, default: 1", "minimum: 1, default: 0", "properties/page: minimum"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			changed := strings.Replace(spec, tt.old, tt.new, 1)
			if changed == spec {
				t.Fatalf("%q is not in the spec", tt.old)
			}
			_, err := Load([]byte(changed))
			if err == nil || !strings.Contains(err.Error(), tt.want) {
				t.Fatalf("expected an error containing %q, got %v", tt.want, err)
			}
		})
	}
}
