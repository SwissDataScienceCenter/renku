package toolspec

import (
	"encoding/json"
	"fmt"
	"maps"
	"regexp"
	"slices"
	"strings"

	"github.com/google/jsonschema-go/jsonschema"
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

	var params []*v3.Parameter
	for _, p := range slices.Concat(item.Parameters, op.Parameters) {
		// Operation level parameters override path level ones with the same name and location.
		params = slices.DeleteFunc(params, func(m *v3.Parameter) bool { return m.Name == p.Name && m.In == p.In })
		params = append(params, p)
	}
	var pathArgs, queryArgs []argument
	for _, p := range params {
		switch p.In {
		case "path":
			arg, err := pathArgument(p)
			if err != nil {
				return Tool{}, fmt.Errorf("parameter %s: %w", p.Name, err)
			}
			pathArgs = append(pathArgs, arg)
		case "query":
			args, err := queryArguments(p)
			if err != nil {
				return Tool{}, fmt.Errorf("parameter %s: %w", p.Name, err)
			}
			queryArgs = append(queryArgs, args...)
		default:
			return Tool{}, fmt.Errorf("parameter %s: only path and query parameters are supported, got %q", p.Name, p.In)
		}
	}

	pathParams := argumentNames(pathArgs)
	var inTemplate []string
	for _, m := range templateParam.FindAllStringSubmatch(t.Path, -1) {
		inTemplate = append(inTemplate, m[1])
	}
	if !slices.Equal(slices.Sorted(slices.Values(inTemplate)), slices.Sorted(slices.Values(pathParams))) {
		return Tool{}, fmt.Errorf("path parameters %v do not match the path template", pathParams)
	}

	if err := overrideDescriptions(pathArgs, t.PathDescriptions, "path"); err != nil {
		return Tool{}, err
	}
	if err := overrideDescriptions(queryArgs, t.QueryDescriptions, "query"); err != nil {
		return Tool{}, err
	}
	args := slices.Concat(pathArgs, queryArgs)
	if slices.ContainsFunc(args, func(a argument) bool { return a.name == BodyArgument }) {
		return Tool{}, fmt.Errorf("argument name %s is reserved for the request body", BodyArgument)
	}
	if rb := op.RequestBody; rb != nil {
		arg, err := bodyArgument(rb)
		if err != nil {
			return Tool{}, fmt.Errorf("request body: %w", err)
		}
		args = append(args, arg)
	}
	schema, err := inputSchema(args)
	if err != nil {
		return Tool{}, err
	}

	return Tool{
		Name:        t.Name,
		Description: strings.TrimSpace(t.Description),
		InputSchema: schema,
		Binding: Binding{
			Method:          strings.ToUpper(t.Method),
			Path:            t.Path,
			PathParameters:  pathParams,
			QueryParameters: argumentNames(queryArgs),
			Body:            op.RequestBody != nil,
		},
	}, nil
}

// argument is a single tool argument of an operation.
type argument struct {
	name     string
	schema   *jsonschema.Schema
	required bool
}

// overrideDescriptions replaces the descriptions of the arguments named in descriptions.
// kind is "path" or "query" and only used in errors.
func overrideDescriptions(args []argument, descriptions map[string]string, kind string) error {
	for name, description := range descriptions {
		i := slices.IndexFunc(args, func(a argument) bool { return a.name == name })
		if i < 0 {
			return fmt.Errorf("%s description override %q does not match any %s parameter", kind, name, kind)
		}
		args[i].schema.Description = description
	}
	return nil
}

// inputSchema builds the input schema of a tool from its arguments.
func inputSchema(args []argument) (*jsonschema.Schema, error) {
	schema := &jsonschema.Schema{
		Type:       "object",
		Properties: map[string]*jsonschema.Schema{},
		// Unknown arguments are rejected, see Binding.Request. {"not": {}} is the false schema.
		AdditionalProperties: &jsonschema.Schema{Not: &jsonschema.Schema{}},
	}
	for _, arg := range args {
		if _, ok := schema.Properties[arg.name]; ok {
			return nil, fmt.Errorf("argument name %s is already used", arg.name)
		}
		schema.Properties[arg.name] = arg.schema
		if arg.required {
			schema.Required = append(schema.Required, arg.name)
		}
	}
	return schema, nil
}

// argumentNames returns the names of args.
func argumentNames(args []argument) []string {
	var names []string
	for _, arg := range args {
		names = append(names, arg.name)
	}
	return names
}

// bodyArgument builds the argument of a request body.
func bodyArgument(rb *v3.RequestBody) (argument, error) {
	if rb.Content == nil {
		return argument{}, fmt.Errorf("no content")
	}
	media, ok := rb.Content.Get("application/json")
	if !ok {
		return argument{}, fmt.Errorf("only application/json is supported")
	}
	schema, err := inline(media.Schema)
	if err != nil {
		return argument{}, err
	}
	if rb.Description != "" {
		schema.Description = rb.Description
	}
	return argument{name: BodyArgument, schema: schema, required: rb.Required != nil && *rb.Required}, nil
}

// pathArgument builds the argument of a path parameter. Path parameters that
// Binding.Request cannot serialize the way the API expects are rejected.
func pathArgument(p *v3.Parameter) (argument, error) {
	schema, err := parameterSchema(p)
	if err != nil {
		return argument{}, err
	}
	if p.Required == nil || !*p.Required {
		return argument{}, fmt.Errorf("path parameters must be required")
	}
	if p.Style != "" && p.Style != "simple" {
		return argument{}, fmt.Errorf("unsupported style %q", p.Style)
	}
	if err := checkScalar(schema, false); err != nil {
		return argument{}, err
	}
	if err := describeParameter(schema, p); err != nil {
		return argument{}, err
	}
	return argument{name: p.Name, schema: schema, required: true}, nil
}

// queryArguments builds the arguments of a query parameter. Exploded object query
// parameters are flattened into one argument per property, which is equivalent on the wire.
// Query parameters that Binding.Request cannot serialize the way the API expects are rejected.
func queryArguments(p *v3.Parameter) ([]argument, error) {
	schema, err := parameterSchema(p)
	if err != nil {
		return nil, err
	}
	if (p.Style != "" && p.Style != "form") || (p.Explode != nil && !*p.Explode) {
		return nil, fmt.Errorf("only exploded form style query parameters are supported")
	}
	if props, propsRequired, ok := objectProperties(schema); ok {
		var args []argument
		for _, name := range slices.Sorted(maps.Keys(props)) {
			prop := props[name]
			if err := checkScalar(prop, true); err != nil {
				return nil, fmt.Errorf("property %s: %w", name, err)
			}
			args = append(args, argument{name: name, schema: prop, required: slices.Contains(propsRequired, name)})
		}
		return args, nil
	}
	if err := checkScalar(schema, true); err != nil {
		return nil, err
	}
	if err := describeParameter(schema, p); err != nil {
		return nil, err
	}
	return []argument{{name: p.Name, schema: schema, required: p.Required != nil && *p.Required}}, nil
}

// parameterSchema returns the schema of a parameter with all references resolved.
func parameterSchema(p *v3.Parameter) (*jsonschema.Schema, error) {
	if p.Schema == nil {
		return nil, fmt.Errorf("only parameters with a schema are supported")
	}
	return inline(p.Schema)
}

// describeParameter copies what the parameter says about its value into schema,
// the schema is the only place clients see it.
func describeParameter(schema *jsonschema.Schema, p *v3.Parameter) error {
	if p.Description != "" {
		schema.Description = p.Description
	}
	if p.Deprecated {
		schema.Deprecated = true
	}
	examples, err := parameterExamples(p)
	if err != nil {
		return err
	}
	if schema.Examples == nil && len(examples) > 0 {
		schema.Examples = examples
	}
	return nil
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
func checkScalar(schema *jsonschema.Schema, allowArray bool) error {
	if schema == nil || (schema.Type == "" && schema.Types == nil) {
		return fmt.Errorf("schema has no type")
	}
	if hasType(schema, "object") {
		return fmt.Errorf("object values are not supported")
	}
	if hasType(schema, "array") {
		if !allowArray {
			return fmt.Errorf("array values are not supported")
		}
		if items := schema.Items; items != nil && (hasType(items, "array") || hasType(items, "object")) {
			return fmt.Errorf("arrays must contain scalars")
		}
	}
	return nil
}

// hasType reports whether schema allows the JSON type typ.
func hasType(schema *jsonschema.Schema, typ string) bool {
	return schema.Type == typ || slices.Contains(schema.Types, typ)
}

// objectProperties collects the properties of an object schema, including
// those of its allOf parts. ok is false when the schema is not a plain object.
func objectProperties(s *jsonschema.Schema) (props map[string]*jsonschema.Schema, required []string, ok bool) {
	if s == nil || s.OneOf != nil || s.AnyOf != nil {
		return nil, nil, false
	}
	props = map[string]*jsonschema.Schema{}
	for _, part := range s.AllOf {
		partProps, partRequired, ok := objectProperties(part)
		if !ok {
			return nil, nil, false
		}
		maps.Copy(props, partProps)
		required = append(required, partRequired...)
	}
	if s.AllOf == nil && s.Properties == nil && s.Type != "object" {
		return nil, nil, false
	}
	maps.Copy(props, s.Properties)
	required = append(required, s.Required...)
	return props, required, true
}

// inline renders a schema with all references resolved.
func inline(proxy *base.SchemaProxy) (*jsonschema.Schema, error) {
	schema, err := proxy.BuildSchema()
	if err != nil {
		return nil, err
	}
	rawYAML, err := schema.RenderInline()
	if err != nil {
		return nil, err
	}
	raw, err := yaml.YAMLToJSON(rawYAML)
	if err != nil {
		return nil, err
	}
	// libopenapi keeps the reference that closes a cycle. It is looked for in the plain
	// document because jsonschema.Schema has no exported way to walk all its subschemas.
	var doc any
	if err := json.Unmarshal(raw, &doc); err != nil {
		return nil, err
	}
	if ref := findRef(doc); ref != "" {
		return nil, fmt.Errorf("recursive schema %s cannot be inlined", ref)
	}
	var out jsonschema.Schema
	if err := json.Unmarshal(raw, &out); err != nil {
		return nil, err
	}
	return &out, nil
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
