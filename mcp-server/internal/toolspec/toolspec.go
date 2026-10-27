// Package toolspec turns the operations of apispec/openapi.yaml into MCP tools: it
// builds the input schema of each tool and the API request of a tool call.
//
// The spec is the pruned OpenAPI 3.1 document written by specprep, so every schema
// is inlined and is a JSON Schema already. The arguments of a tool are its path and
// query parameters plus the request body as the body argument.
package toolspec

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"regexp"
	"slices"
	"strconv"
	"strings"

	"github.com/google/jsonschema-go/jsonschema"
	"sigs.k8s.io/yaml"
)

// BodyArgument is the tool argument that holds the request body.
const BodyArgument = "body"

// Tool is one operation of the spec exposed as an MCP tool.
type Tool struct {
	Name        string
	Description string
	// InputSchema is the JSON Schema of the arguments, advertised to clients.
	InputSchema *jsonschema.Schema

	method      string
	path        string
	pathParams  []string
	queryParams []string
	hasBody     bool
	resolved    *jsonschema.Resolved
}

type document struct {
	OpenAPI string                          `json:"openapi"`
	Paths   map[string]map[string]operation `json:"paths"`
}

type operation struct {
	OperationID string       `json:"operationId"`
	Description string       `json:"description"`
	Parameters  []parameter  `json:"parameters"`
	RequestBody *requestBody `json:"requestBody"`
}

type parameter struct {
	Name        string         `json:"name"`
	In          string         `json:"in"`
	Description string         `json:"description"`
	Required    bool           `json:"required"`
	Style       string         `json:"style"`
	Explode     *bool          `json:"explode"`
	Schema      map[string]any `json:"schema"`
}

type requestBody struct {
	Description string                                     `json:"description"`
	Required    bool                                       `json:"required"`
	Content     map[string]struct{ Schema map[string]any } `json:"content"`
}

var templateParam = regexp.MustCompile(`\{([^}]+)\}`)

// Load reads the tools of a spec, sorted by name. It fails for operations whose
// requests cannot be built by Tool.Request, so that unsupported API features are
// noticed when the spec is updated instead of when a tool is called.
func Load(spec []byte) ([]Tool, error) {
	var doc document
	if err := yaml.Unmarshal(spec, &doc); err != nil {
		return nil, fmt.Errorf("parsing spec: %w", err)
	}
	if !strings.HasPrefix(doc.OpenAPI, "3.1.") {
		return nil, fmt.Errorf("only OpenAPI 3.1.x is supported, got %q", doc.OpenAPI)
	}
	var tools []Tool
	for path, item := range doc.Paths {
		for method, op := range item {
			tool, err := newTool(method, path, op)
			if err != nil {
				return nil, fmt.Errorf("%s %s: %w", method, path, err)
			}
			tools = append(tools, tool)
		}
	}
	slices.SortFunc(tools, func(a, b Tool) int { return strings.Compare(a.Name, b.Name) })
	for i := 1; i < len(tools); i++ {
		if tools[i].Name == tools[i-1].Name {
			return nil, fmt.Errorf("tool name %s is used by more than one operation", tools[i].Name)
		}
	}
	return tools, nil
}

func newTool(method, path string, op operation) (Tool, error) {
	if op.OperationID == "" || strings.TrimSpace(op.Description) == "" {
		return Tool{}, fmt.Errorf("operationId and description are required, they are the name and description of the tool")
	}
	t := Tool{Name: op.OperationID, Description: op.Description, method: strings.ToUpper(method), path: path}
	properties := map[string]any{}
	required := []string{}
	for _, p := range op.Parameters {
		if _, ok := properties[p.Name]; ok || p.Name == BodyArgument {
			return Tool{}, fmt.Errorf("parameter %s: argument name is already used", p.Name)
		}
		if err := checkParameter(p); err != nil {
			return Tool{}, fmt.Errorf("parameter %s: %w", p.Name, err)
		}
		if p.In == "path" {
			t.pathParams = append(t.pathParams, p.Name)
		} else {
			t.queryParams = append(t.queryParams, p.Name)
		}
		schema := p.Schema
		if p.Description != "" {
			schema["description"] = p.Description
		}
		properties[p.Name] = schema
		if p.Required {
			required = append(required, p.Name)
		}
	}
	var inTemplate []string
	for _, m := range templateParam.FindAllStringSubmatch(path, -1) {
		inTemplate = append(inTemplate, m[1])
	}
	if !slices.Equal(slices.Sorted(slices.Values(inTemplate)), slices.Sorted(slices.Values(t.pathParams))) {
		return Tool{}, fmt.Errorf("path parameters %v do not match the path template", t.pathParams)
	}

	if body := op.RequestBody; body != nil {
		media, ok := body.Content["application/json"]
		if !ok || len(body.Content) != 1 {
			return Tool{}, fmt.Errorf("request body: only application/json is supported")
		}
		schema := media.Schema
		if body.Description != "" {
			schema["description"] = body.Description
		}
		properties[BodyArgument] = schema
		if body.Required {
			required = append(required, BodyArgument)
		}
		t.hasBody = true
	}

	// Unknown arguments are rejected so that the model notices misspelled ones.
	raw, err := json.Marshal(map[string]any{"type": "object", "properties": properties, "required": required, "additionalProperties": false})
	if err != nil {
		return Tool{}, err
	}
	t.InputSchema = &jsonschema.Schema{}
	if err := json.Unmarshal(raw, t.InputSchema); err != nil {
		return Tool{}, fmt.Errorf("input schema: %w", err)
	}
	t.resolved, err = t.InputSchema.Resolve(&jsonschema.ResolveOptions{ValidateDefaults: true})
	if err != nil {
		return Tool{}, fmt.Errorf("input schema: %w", err)
	}
	return t, nil
}

// checkParameter rejects parameters that Request does not serialize the way the API expects.
func checkParameter(p parameter) error {
	switch p.In {
	case "path":
		if !p.Required {
			return fmt.Errorf("path parameters must be required")
		}
		if p.Style != "" && p.Style != "simple" {
			return fmt.Errorf("unsupported style %q", p.Style)
		}
		if hasType(p.Schema, "array") || hasType(p.Schema, "object") {
			return fmt.Errorf("path parameters must be scalars")
		}
	case "query":
		if (p.Style != "" && p.Style != "form") || (p.Explode != nil && !*p.Explode) {
			return fmt.Errorf("only exploded form style query parameters are supported")
		}
		if hasType(p.Schema, "object") {
			return fmt.Errorf("object query parameters must be flattened by specprep")
		}
		if items, ok := p.Schema["items"].(map[string]any); ok && (hasType(items, "array") || hasType(items, "object")) {
			return fmt.Errorf("query parameter arrays must contain scalars")
		}
	default:
		return fmt.Errorf("only path and query parameters are supported, got %q", p.In)
	}
	if _, ok := p.Schema["type"]; !ok {
		return fmt.Errorf("schema has no type")
	}
	return nil
}

// hasType reports whether schema allows the JSON type typ, given as a string or a list of strings.
func hasType(schema map[string]any, typ string) bool {
	switch t := schema["type"].(type) {
	case string:
		return t == typ
	case []any:
		return slices.Contains(t, any(typ))
	}
	return false
}

// Validate checks the arguments of a call against the input schema.
func (t Tool) Validate(args map[string]any) error {
	return t.resolved.Validate(args)
}

// Request builds the API request of a call with validated arguments. baseURL is the
// URL the paths of the spec are relative to, e.g. http://renku-gateway/api/data.
func (t Tool) Request(ctx context.Context, baseURL string, args map[string]any) (*http.Request, error) {
	path := t.path
	for _, name := range t.pathParams {
		value, err := scalar(args[name])
		if err != nil {
			return nil, fmt.Errorf("%s: %w", name, err)
		}
		path = strings.ReplaceAll(path, "{"+name+"}", url.PathEscape(value))
	}
	query := url.Values{}
	for _, name := range t.queryParams {
		values, ok := args[name].([]any)
		if !ok {
			values = []any{args[name]}
		}
		for _, v := range values {
			// Absent and null arguments are left out, so the API applies its defaults.
			if v == nil {
				continue
			}
			s, err := scalar(v)
			if err != nil {
				return nil, fmt.Errorf("%s: %w", name, err)
			}
			query.Add(name, s)
		}
	}
	target := strings.TrimSuffix(baseURL, "/") + path
	if len(query) > 0 {
		target += "?" + query.Encode()
	}

	var body io.Reader
	if value, ok := args[BodyArgument]; t.hasBody && ok {
		raw, err := json.Marshal(value)
		if err != nil {
			return nil, fmt.Errorf("%s: %w", BodyArgument, err)
		}
		body = bytes.NewReader(raw)
	}
	req, err := http.NewRequestWithContext(ctx, t.method, target, body)
	if err != nil {
		return nil, err
	}
	if body != nil {
		req.Header.Set("Content-Type", "application/json")
	}
	return req, nil
}

// scalar formats a JSON scalar as a parameter value.
func scalar(v any) (string, error) {
	switch v := v.(type) {
	case string:
		return v, nil
	case bool:
		return strconv.FormatBool(v), nil
	case float64:
		// 'f' instead of fmt's default, which formats large numbers like 1e+06.
		return strconv.FormatFloat(v, 'f', -1, 64), nil
	case json.Number:
		return v.String(), nil
	}
	return "", fmt.Errorf("expected a string, number or boolean, got %T", v)
}
