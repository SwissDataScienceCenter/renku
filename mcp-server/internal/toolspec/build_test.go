package toolspec

import (
	"slices"
	"strings"
	"testing"

	v3 "github.com/pb33f/libopenapi/datamodel/high/v3"
)

const projectsModule = `
openapi: 3.1.0
info: {title: projects, version: v1}
servers: [{url: /api/data}]
paths:
  /projects:
    get:
      summary: Get all projects
      parameters:
        - in: query
          name: params
          style: form
          explode: true
          schema:
            $ref: "#/components/schemas/ProjectGetQuery"
      responses:
        "200":
          description: ok
          content:
            application/json:
              schema: {$ref: "#/components/schemas/ProjectsList"}
        default: {$ref: "#/components/responses/Error"}
    post:
      summary: Create a project
      requestBody:
        required: true
        content:
          application/json:
            schema: {$ref: "#/components/schemas/ProjectPost"}
      responses:
        default: {$ref: "#/components/responses/Error"}
  /projects/{project_id}:
    parameters:
      - in: path
        name: project_id
        required: true
        schema: {$ref: "#/components/schemas/Ulid"}
    get:
      summary: Get a project
      parameters:
        - {in: query, name: with_documentation, description: Include the documentation, deprecated: true, example: true, schema: {type: boolean}}
      responses:
        "200":
          description: ok
          content:
            application/json:
              schema: {$ref: "#/components/schemas/Project"}
components:
  schemas:
    Ulid: {type: string, minLength: 26, maxLength: 26}
    PaginationRequest:
      type: object
      properties:
        page: {type: integer, description: "Page number"}
        per_page: {type: integer, default: 20}
    ProjectGetQuery:
      allOf:
        - $ref: "#/components/schemas/PaginationRequest"
        - properties:
            namespace: {type: string, description: "A namespace filter, use ` + "`my-group`" + `"}
          required: [namespace]
    ProjectsList:
      type: array
      items: {$ref: "#/components/schemas/Project"}
    Project:
      type: object
      properties:
        id: {$ref: "#/components/schemas/Ulid"}
    ProjectPost:
      type: object
      required: [owner]
      properties:
        owner: {$ref: "#/components/schemas/Ulid"}
        description: {type: [string, "null"], example: "A project", x-internal: true}
        name: {type: string, pattern: "^(?!x)", discriminator: {propertyName: name}}
        tags: {type: array, items: {type: string}}
    ErrorResponse:
      type: object
      properties:
        message: {type: string}
  responses:
    Error:
      description: error
      content:
        application/json:
          schema: {$ref: "#/components/schemas/ErrorResponse"}
`

const sessionsModule = `
openapi: 3.1.0
info: {title: sessions, version: v1}
servers: [{url: /api/data}]
paths:
  /sessions:
    get:
      summary: List sessions
      responses:
        "200":
          description: ok
        default: {$ref: "#/components/responses/Error"}
  /sessions/{session_id}:
    get:
      summary: Get a session
      parameters:
        - in: path
          name: session_id
          required: true
          schema: {$ref: "#/components/schemas/Ulid"}
      responses:
        "200":
          description: ok
components:
  schemas:
    Ulid: {type: string, minLength: 26, maxLength: 26}
    ErrorResponse:
      type: object
      properties:
        message: {type: string}
  responses:
    Error:
      description: error
      content:
        application/json:
          schema: {$ref: "#/components/schemas/ErrorResponse"}
`

var (
	projectsSpec = mustParse(projectsModule)
	sessionsSpec = mustParse(sessionsModule)
)

func mustParse(raw string) *v3.Document {
	spec, err := Parse([]byte(raw))
	if err != nil {
		panic(err)
	}
	return spec
}

// build runs Build and returns the tools by name.
func build(t *testing.T, configs ...ToolConfig) map[string]Tool {
	t.Helper()
	tools, err := Build(configs)
	if err != nil {
		t.Fatalf("Build: %v", err)
	}
	byName := map[string]Tool{}
	for _, tool := range tools {
		byName[tool.Name] = tool
	}
	return byName
}

func get(t *testing.T, v any, path ...string) any {
	t.Helper()
	for _, p := range path {
		m, ok := v.(map[string]any)
		if !ok {
			t.Fatalf("%v: not an object at %q", path, p)
		}
		v, ok = m[p]
		if !ok {
			t.Fatalf("%v: missing %q", path, p)
		}
	}
	return v
}

func TestBuildBuildsTheAllowlistedTools(t *testing.T) {
	tools := build(t,
		ToolConfig{Spec: projectsSpec, Method: "get", Path: "/projects", Name: "list_projects", Description: `List "projects".`},
		ToolConfig{Spec: projectsSpec, Method: "get", Path: "/projects/{project_id}", Name: "get_project", Description: "Get a project."},
		ToolConfig{Spec: sessionsSpec, Method: "get", Path: "/sessions/{session_id}", Name: "get_session", Description: "Get a session."},
		ToolConfig{Spec: projectsSpec, Method: "post", Path: "/projects", Name: "create_project", Description: "Create a project."},
	)
	if len(tools) != 4 {
		t.Fatalf("expected only the 4 allowlisted tools, got %v", tools)
	}

	list := tools["list_projects"]
	if list.Description != `List "projects".` || list.Binding.Method != "GET" || list.Binding.Path != "/projects" || list.Binding.Body {
		t.Errorf("unexpected tool %+v", list)
	}
	if list.InputSchema["type"] != "object" || list.InputSchema["additionalProperties"] != false {
		t.Errorf("the input schema should be a closed object, got %v", list.InputSchema)
	}

	session := tools["get_session"]
	if !slices.Equal(session.Binding.PathParameters, []string{"session_id"}) {
		t.Errorf("path parameters = %v", session.Binding.PathParameters)
	}
	if get(t, session.InputSchema, "properties", "session_id", "minLength") != float64(26) {
		t.Errorf("the Ulid reference should be inlined, got %v", session.InputSchema)
	}
	if required := get(t, session.InputSchema, "required").([]string); !slices.Equal(required, []string{"session_id"}) {
		t.Errorf("required = %v", required)
	}

	create := tools["create_project"]
	if !create.Binding.Body || len(create.Binding.PathParameters)+len(create.Binding.QueryParameters) != 0 {
		t.Errorf("unexpected tool %+v", create)
	}
	if required := get(t, create.InputSchema, "required").([]string); !slices.Equal(required, []string{BodyArgument}) {
		t.Errorf("the required request body should be the required body argument, got %v", required)
	}
	if get(t, create.InputSchema, "properties", "body", "properties", "owner", "minLength") != float64(26) {
		t.Errorf("the body schema should be inlined, got %v", create.InputSchema)
	}
}

func TestBuildFlattensExplodedQueryObjects(t *testing.T) {
	tool := build(t, ToolConfig{Spec: projectsSpec, Method: "get", Path: "/projects", Name: "list_projects", Description: "List projects."})["list_projects"]

	if !slices.Equal(tool.Binding.QueryParameters, []string{"namespace", "page", "per_page"}) {
		t.Fatalf("expected page, per_page and namespace parameters, got %v", tool.Binding.QueryParameters)
	}
	if required := get(t, tool.InputSchema, "required").([]string); !slices.Equal(required, []string{"namespace"}) {
		t.Errorf("required flags not taken from the object schema: %v", required)
	}
	props := get(t, tool.InputSchema, "properties")
	if get(t, props, "namespace", "description") != "A namespace filter, use `my-group`" {
		t.Errorf("description = %v", get(t, props, "namespace"))
	}
	if get(t, props, "per_page", "default") != float64(20) {
		t.Errorf("per_page default lost: %v", get(t, props, "per_page"))
	}
}

func TestBuildHoistsPathParametersAndAppliesOverrides(t *testing.T) {
	tool := build(t, ToolConfig{
		Spec: projectsSpec, Method: "get", Path: "/projects/{project_id}", Name: "get_project", Description: "Get a project.",
		Parameters: map[string]string{"project_id": "The project ULID."},
	})["get_project"]

	if !slices.Equal(tool.Binding.PathParameters, []string{"project_id"}) || !slices.Equal(tool.Binding.QueryParameters, []string{"with_documentation"}) {
		t.Fatalf("expected the path level parameter on the operation, got %+v", tool)
	}
	if get(t, tool.InputSchema, "properties", "project_id", "description") != "The project ULID." {
		t.Errorf("override not applied: %v", tool.InputSchema)
	}
}

func TestBuildKeepsTheUpstreamSchemas(t *testing.T) {
	tools := build(t,
		ToolConfig{Spec: projectsSpec, Method: "post", Path: "/projects", Name: "create_project", Description: "Create a project."},
		ToolConfig{Spec: projectsSpec, Method: "get", Path: "/projects/{project_id}", Name: "get_project", Description: "Get a project."},
	)

	body := get(t, tools["create_project"].InputSchema, "properties", "body", "properties")
	description := get(t, body, "description").(map[string]any)
	if types := description["type"].([]any); len(types) != 2 || types[1] != "null" {
		t.Errorf("the 3.1 null type should be kept, got %v", description)
	}
	if description["example"] != "A project" || description["x-internal"] != true {
		t.Errorf("examples and extensions should be kept, got %v", description)
	}
	name := get(t, body, "name").(map[string]any)
	if name["pattern"] != "^(?!x)" || name["discriminator"] == nil {
		t.Errorf("the pattern and the discriminator should be kept, got %v", name)
	}

	param := get(t, tools["get_project"].InputSchema, "properties", "with_documentation").(map[string]any)
	if param["description"] != "Include the documentation" || param["deprecated"] != true {
		t.Errorf("the description and deprecation of the parameter should be on its schema, got %v", param)
	}
	if examples, _ := param["examples"].([]any); len(examples) != 1 || examples[0] != true {
		t.Errorf("the example of the parameter should be on its schema, got %v", param)
	}
}

func TestBuildRejectsRecursiveSchemas(t *testing.T) {
	recursive := strings.Replace(projectsModule, "tags: {type: array, items: {type: string}}", `tags: {type: array, items: {$ref: "#/components/schemas/ProjectPost"}}`, 1)
	_, err := Build([]ToolConfig{{Spec: mustParse(recursive), Method: "post", Path: "/projects", Name: "create_project", Description: "Create a project."}})
	if err == nil || !strings.Contains(err.Error(), "recursive schema #/components/schemas/ProjectPost") {
		t.Fatalf("expected a recursive schema error, got %v", err)
	}
}

func TestBuildUsesTheComponentsOfEachModule(t *testing.T) {
	conflicting := strings.Replace(sessionsModule, "Ulid: {type: string, minLength: 26, maxLength: 26}", "Ulid: {type: string, minLength: 20, maxLength: 26}", 1)
	tools := build(t,
		ToolConfig{Spec: projectsSpec, Method: "get", Path: "/projects/{project_id}", Name: "get_project", Description: "Get a project."},
		ToolConfig{Spec: mustParse(conflicting), Method: "get", Path: "/sessions/{session_id}", Name: "get_session", Description: "Get a session."},
	)
	if get(t, tools["get_project"].InputSchema, "properties", "project_id", "minLength") != float64(26) {
		t.Error("get_project should use the Ulid of the projects module")
	}
	if get(t, tools["get_session"].InputSchema, "properties", "session_id", "minLength") != float64(20) {
		t.Error("get_session should use the Ulid of the sessions module")
	}
}

func TestBuildRejectsParametersTheServerCannotSend(t *testing.T) {
	tests := []struct {
		name, old, new, want string
	}{
		{"header parameter", "in: path", "in: header", "only path and query parameters"},
		{"path template mismatch", "name: session_id", "name: id", "do not match the path template"},
		{"object path parameter", "schema: {$ref: \"#/components/schemas/Ulid\"}\n      responses:", "schema: {type: object}\n      responses:", "object values are not supported"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			changed := strings.Replace(sessionsModule, tt.old, tt.new, 1)
			if changed == sessionsModule {
				t.Fatalf("%q is not in the module", tt.old)
			}
			_, err := Build([]ToolConfig{{Spec: mustParse(changed), Method: "get", Path: "/sessions/{session_id}", Name: "get_session", Description: "Get a session."}})
			if err == nil || !strings.Contains(err.Error(), tt.want) {
				t.Fatalf("expected an error containing %q, got %v", tt.want, err)
			}
		})
	}
}

func TestParseRequiresOpenAPI31(t *testing.T) {
	old := strings.Replace(sessionsModule, "openapi: 3.1.0", "openapi: 3.0.2", 1)
	_, err := Parse([]byte(old))
	if err == nil || !strings.Contains(err.Error(), "only OpenAPI 3.1.x is supported") {
		t.Fatalf("expected a version error, got %v", err)
	}
}

func TestBuildErrors(t *testing.T) {
	tests := []struct {
		name string
		tool ToolConfig
		want string
	}{
		{"unknown operation", ToolConfig{Spec: projectsSpec, Method: "delete", Path: "/projects", Name: "delete_projects", Description: "x"}, "operation not found in its spec"},
		{"operation in another spec", ToolConfig{Spec: projectsSpec, Method: "get", Path: "/sessions", Name: "list_sessions", Description: "x"}, "operation not found in its spec"},
		{"missing spec", ToolConfig{Method: "get", Path: "/sessions", Name: "list_sessions", Description: "x"}, "spec is required"},
		{"unknown parameter override", ToolConfig{Spec: sessionsSpec, Method: "get", Path: "/sessions", Name: "list_sessions", Description: "x", Parameters: map[string]string{"nope": "x"}}, `parameter override "nope"`},
		{"invalid name", ToolConfig{Spec: sessionsSpec, Method: "get", Path: "/sessions", Name: "ListSessions", Description: "x"}, "must match"},
		{"missing description", ToolConfig{Spec: sessionsSpec, Method: "get", Path: "/sessions", Name: "list_sessions"}, "description is required"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			_, err := Build([]ToolConfig{tt.tool})
			if err == nil || !strings.Contains(err.Error(), tt.want) {
				t.Fatalf("expected error containing %q, got %v", tt.want, err)
			}
		})
	}
}

func TestBuildRequiresOneServerURL(t *testing.T) {
	otherServer := mustParse(strings.Replace(sessionsModule, "servers: [{url: /api/data}]", "servers: [{url: /api/other}]", 1))
	_, err := Build([]ToolConfig{
		{Spec: projectsSpec, Method: "get", Path: "/projects", Name: "list_projects", Description: "x"},
		{Spec: otherServer, Method: "get", Path: "/sessions", Name: "list_sessions", Description: "x"},
	})
	if err == nil || !strings.Contains(err.Error(), `server url "/api/other" of its spec differs from "/api/data"`) {
		t.Fatalf("expected a server url error, got %v", err)
	}
}
