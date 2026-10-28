package toolspec

import (
	"fmt"
	"maps"
	"regexp"
	"slices"
	"strings"

	"github.com/pb33f/libopenapi"
	"github.com/pb33f/libopenapi/datamodel/high/base"
	v3 "github.com/pb33f/libopenapi/datamodel/high/v3"
	"sigs.k8s.io/yaml"
)

var templateParam = regexp.MustCompile(`\{([^}]+)\}`)

// Parse parses a module spec, which must be OpenAPI 3.1.
func Parse(raw []byte) (*v3.Document, error) {
	doc, err := libopenapi.NewDocument(raw)
	if err != nil {
		return nil, err
	}
	if version := doc.GetVersion(); !strings.HasPrefix(version, "3.1.") {
		return nil, fmt.Errorf("only OpenAPI 3.1.x is supported, got %q", version)
	}
	model, err := doc.BuildV3Model()
	if err != nil {
		return nil, err
	}
	return &model.Model, nil
}

// Build builds one tool per allowlisted operation, from the spec each tool points to.
//
// The schemas of the parameters and the request body are inlined with all references
// resolved and otherwise kept as they are upstream, so clients see everything the API
// spec says about the arguments.
func Build(configs []ToolConfig) ([]Tool, error) {
	if err := validate(configs); err != nil {
		return nil, err
	}
	// The tool paths are relative to one base URL.
	serverURL := specServerURL(configs[0].Spec)
	for _, t := range configs {
		if url := specServerURL(t.Spec); url != serverURL {
			return nil, fmt.Errorf("tool %s: server url %q of its spec differs from %q", t.Name, url, serverURL)
		}
	}

	var tools []Tool
	for _, t := range configs {
		tool, err := newTool(t)
		if err != nil {
			return nil, fmt.Errorf("tool %s (%s %s): %w", t.Name, t.Method, t.Path, err)
		}
		tools = append(tools, tool)
	}
	return tools, nil
}

func specServerURL(spec *v3.Document) string {
	if len(spec.Servers) == 0 {
		return ""
	}
	return spec.Servers[0].URL
}

// newTool builds the MCP tool of an allowlisted operation from the spec that defines it.
func newTool(t ToolConfig) (Tool, error) {
	var item *v3.PathItem
	var op *v3.Operation
	if t.Spec.Paths != nil {
		item, _ = t.Spec.Paths.PathItems.Get(t.Path)
	}
	if item != nil {
		op, _ = item.GetOperations().Get(t.Method)
	}
	if op == nil {
		return Tool{}, fmt.Errorf("operation not found in its spec")
	}

	tool := Tool{Name: t.Name, Description: strings.TrimSpace(t.Description), Method: strings.ToUpper(t.Method), Path: t.Path}
	properties := map[string]any{}
	required := []string{}
	add := func(name string, schema map[string]any, isRequired bool) error {
		if _, ok := properties[name]; ok || name == BodyArgument {
			return fmt.Errorf("argument name %s is already used", name)
		}
		properties[name] = schema
		if isRequired {
			required = append(required, name)
		}
		return nil
	}

	var params []*v3.Parameter
	for _, p := range slices.Concat(item.Parameters, op.Parameters) {
		// Operation level parameters override path level ones with the same name and location.
		params = slices.DeleteFunc(params, func(m *v3.Parameter) bool { return m.Name == p.Name && m.In == p.In })
		params = append(params, p)
	}
	for _, p := range params {
		if err := addParameter(&tool, p, add); err != nil {
			return Tool{}, fmt.Errorf("parameter %s: %w", p.Name, err)
		}
	}
	for name, description := range t.Parameters {
		prop, ok := properties[name].(map[string]any)
		if !ok {
			return Tool{}, fmt.Errorf("parameter override %q does not match any parameter", name)
		}
		prop["description"] = description
	}

	if rb := op.RequestBody; rb != nil {
		if rb.Content == nil {
			return Tool{}, fmt.Errorf("request body has no content")
		}
		media, ok := rb.Content.Get("application/json")
		if !ok {
			return Tool{}, fmt.Errorf("request body: only application/json is supported")
		}
		schema, err := inline(media.Schema)
		if err != nil {
			return Tool{}, fmt.Errorf("request body: %w", err)
		}
		if rb.Description != "" {
			schema["description"] = rb.Description
		}
		properties[BodyArgument] = schema
		if rb.Required != nil && *rb.Required {
			required = append(required, BodyArgument)
		}
		tool.Body = true
	}

	var inTemplate []string
	for _, m := range templateParam.FindAllStringSubmatch(t.Path, -1) {
		inTemplate = append(inTemplate, m[1])
	}
	if !slices.Equal(slices.Sorted(slices.Values(inTemplate)), slices.Sorted(slices.Values(tool.PathParameters))) {
		return Tool{}, fmt.Errorf("path parameters %v do not match the path template", tool.PathParameters)
	}

	// Unknown arguments are rejected, see Tool.Request.
	tool.InputSchema = map[string]any{"type": "object", "properties": properties, "required": required, "additionalProperties": false}
	return tool, nil
}

// addParameter adds the arguments of a parameter to tool. Exploded object query
// parameters are flattened into one argument per property, which is equivalent on the wire.
// Parameters that Tool.Request cannot serialize the way the API expects are rejected.
func addParameter(tool *Tool, p *v3.Parameter, add func(string, map[string]any, bool) error) error {
	if p.Schema == nil {
		return fmt.Errorf("only parameters with a schema are supported")
	}
	schema, err := inline(p.Schema)
	if err != nil {
		return err
	}
	isRequired := p.Required != nil && *p.Required
	switch p.In {
	case "path":
		if !isRequired {
			return fmt.Errorf("path parameters must be required")
		}
		if p.Style != "" && p.Style != "simple" {
			return fmt.Errorf("unsupported style %q", p.Style)
		}
		if err := checkScalar(schema, false); err != nil {
			return err
		}
		tool.PathParameters = append(tool.PathParameters, p.Name)
	case "query":
		if (p.Style != "" && p.Style != "form") || (p.Explode != nil && !*p.Explode) {
			return fmt.Errorf("only exploded form style query parameters are supported")
		}
		if props, propsRequired, ok := objectProperties(schema); ok {
			for _, name := range slices.Sorted(maps.Keys(props)) {
				prop, _ := props[name].(map[string]any)
				if err := checkScalar(prop, true); err != nil {
					return fmt.Errorf("property %s: %w", name, err)
				}
				if err := add(name, prop, slices.Contains(propsRequired, name)); err != nil {
					return err
				}
				tool.QueryParameters = append(tool.QueryParameters, name)
			}
			return nil
		}
		if err := checkScalar(schema, true); err != nil {
			return err
		}
		tool.QueryParameters = append(tool.QueryParameters, p.Name)
	default:
		return fmt.Errorf("only path and query parameters are supported, got %q", p.In)
	}

	// Keep what the parameter says about the value, the schema is the only place clients see it.
	if p.Description != "" {
		schema["description"] = p.Description
	}
	if p.Deprecated {
		schema["deprecated"] = true
	}
	if examples, err := parameterExamples(p); err != nil {
		return err
	} else if _, ok := schema["examples"]; !ok && len(examples) > 0 {
		schema["examples"] = examples
	}
	return add(p.Name, schema, isRequired)
}

// parameterExamples returns the example values of a parameter.
func parameterExamples(p *v3.Parameter) ([]any, error) {
	var examples []any
	if p.Example != nil {
		var v any
		if err := p.Example.Decode(&v); err != nil {
			return nil, fmt.Errorf("example: %w", err)
		}
		examples = append(examples, v)
	}
	if p.Examples != nil {
		for name, e := range p.Examples.FromOldest() {
			if e.Value == nil {
				continue
			}
			var v any
			if err := e.Value.Decode(&v); err != nil {
				return nil, fmt.Errorf("example %s: %w", name, err)
			}
			examples = append(examples, v)
		}
	}
	return examples, nil
}

// checkScalar rejects schemas whose values are not scalars, or with allowArray arrays of scalars.
func checkScalar(schema map[string]any, allowArray bool) error {
	if _, ok := schema["type"]; !ok {
		return fmt.Errorf("schema has no type")
	}
	if hasType(schema, "object") {
		return fmt.Errorf("object values are not supported")
	}
	if hasType(schema, "array") {
		if !allowArray {
			return fmt.Errorf("array values are not supported")
		}
		if items, ok := schema["items"].(map[string]any); ok && (hasType(items, "array") || hasType(items, "object")) {
			return fmt.Errorf("arrays must contain scalars")
		}
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

// objectProperties collects the properties of an object schema, including
// those of its allOf parts. ok is false when the schema is not a plain object.
func objectProperties(s map[string]any) (props map[string]any, required []string, ok bool) {
	if s["oneOf"] != nil || s["anyOf"] != nil {
		return nil, nil, false
	}
	props = map[string]any{}
	allOf, hasAllOf := s["allOf"].([]any)
	for _, part := range allOf {
		partMap, _ := part.(map[string]any)
		partProps, partRequired, ok := objectProperties(partMap)
		if !ok {
			return nil, nil, false
		}
		maps.Copy(props, partProps)
		required = append(required, partRequired...)
	}
	own, hasProps := s["properties"].(map[string]any)
	if !hasAllOf && !hasProps && s["type"] != "object" {
		return nil, nil, false
	}
	maps.Copy(props, own)
	ownRequired, _ := s["required"].([]any)
	for _, r := range ownRequired {
		if name, ok := r.(string); ok {
			required = append(required, name)
		}
	}
	return props, required, true
}

// inline renders a schema with all references resolved.
func inline(proxy *base.SchemaProxy) (map[string]any, error) {
	schema, err := proxy.BuildSchema()
	if err != nil {
		return nil, err
	}
	raw, err := schema.RenderInline()
	if err != nil {
		return nil, err
	}
	var out map[string]any
	if err := yaml.Unmarshal(raw, &out); err != nil {
		return nil, err
	}
	// libopenapi keeps the reference that closes a cycle.
	if ref := findRef(out); ref != "" {
		return nil, fmt.Errorf("recursive schema %s cannot be inlined", ref)
	}
	return out, nil
}

func findRef(v any) string {
	switch t := v.(type) {
	case map[string]any:
		if ref, ok := t["$ref"].(string); ok {
			return ref
		}
		for _, val := range t {
			if ref := findRef(val); ref != "" {
				return ref
			}
		}
	case []any:
		for _, val := range t {
			if ref := findRef(val); ref != "" {
				return ref
			}
		}
	}
	return ""
}
