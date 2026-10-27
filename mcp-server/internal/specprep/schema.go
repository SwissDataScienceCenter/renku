package specprep

import (
	"fmt"
	"slices"
	"strings"
)

// bodyArgument is the tool argument that holds the request body.
const bodyArgument = "body"

// inputSchema builds the JSON Schema of the arguments of a tool from its prepared
// operation. Every parameter becomes a top level argument and the JSON request body
// becomes the body argument.
func (p *preparer) inputSchema(op map[string]any) (map[string]any, error) {
	properties := map[string]any{}
	required := []any{}
	params, _ := op["parameters"].([]any)
	for _, item := range params {
		param := item.(map[string]any)
		name, _ := param["name"].(string)
		if param["in"] != "path" && param["in"] != "query" {
			return nil, fmt.Errorf("parameter %s: only path and query parameters are supported, got %v", name, param["in"])
		}
		if _, ok := properties[name]; ok || name == bodyArgument {
			return nil, fmt.Errorf("parameter %s: argument name is already used", name)
		}
		schema, err := p.jsonSchema(param["schema"], nil)
		if err != nil {
			return nil, fmt.Errorf("parameter %s: %w", name, err)
		}
		if description, ok := param["description"]; ok {
			schema["description"] = description
		}
		properties[name] = schema
		if param["required"] == true {
			required = append(required, name)
		}
	}

	if op["requestBody"] != nil {
		body, err := p.resolve(op["requestBody"], "requestBodies")
		if err != nil {
			return nil, fmt.Errorf("request body: %w", err)
		}
		content, _ := body["content"].(map[string]any)
		media, ok := content["application/json"].(map[string]any)
		if !ok {
			return nil, fmt.Errorf("request body: only application/json is supported")
		}
		schema, err := p.jsonSchema(media["schema"], nil)
		if err != nil {
			return nil, fmt.Errorf("request body: %w", err)
		}
		if description, ok := body["description"]; ok {
			schema["description"] = description
		}
		properties[bodyArgument] = schema
		if body["required"] == true {
			required = append(required, bodyArgument)
		}
	}

	schema := map[string]any{"type": "object", "properties": properties}
	if len(required) > 0 {
		schema["required"] = required
	}
	return schema, nil
}

// jsonSchema converts an OpenAPI 3.0 schema into a self-contained JSON Schema by
// inlining references and translating the OpenAPI specific keywords. refs holds the
// references being inlined, to reject recursive schemas.
func (p *preparer) jsonSchema(v any, refs []string) (map[string]any, error) {
	s, ok := v.(map[string]any)
	if !ok {
		return nil, fmt.Errorf("schema is not an object")
	}
	if ref, ok := s["$ref"].(string); ok {
		if slices.Contains(refs, ref) {
			return nil, fmt.Errorf("recursive schema %s cannot be inlined", ref)
		}
		def, err := p.resolve(s, "schemas")
		if err != nil {
			return nil, err
		}
		return p.jsonSchema(def, append(slices.Clip(refs), ref))
	}

	out := map[string]any{}
	for key, val := range s {
		var err error
		switch key {
		case "properties":
			props := map[string]any{}
			for name, prop := range val.(map[string]any) {
				if props[name], err = p.jsonSchema(prop, refs); err != nil {
					return nil, fmt.Errorf("property %s: %w", name, err)
				}
			}
			out[key] = props
		case "items", "not":
			out[key], err = p.jsonSchema(val, refs)
		case "additionalProperties":
			if _, isBool := val.(bool); isBool {
				out[key] = val
			} else {
				out[key], err = p.jsonSchema(val, refs)
			}
		case "allOf", "oneOf", "anyOf":
			var list []any
			for _, item := range val.([]any) {
				converted, err := p.jsonSchema(item, refs)
				if err != nil {
					return nil, err
				}
				list = append(list, converted)
			}
			out[key] = list
		case "example":
			out["examples"] = []any{val}
		case "nullable", "exclusiveMinimum", "exclusiveMaximum", "discriminator", "xml", "externalDocs":
			// Translated below or not meaningful for tool arguments.
		default:
			if !strings.HasPrefix(key, "x-") {
				out[key] = deepCopy(val)
			}
		}
		if err != nil {
			return nil, err
		}
	}

	// OpenAPI 3.0 exclusive bounds are booleans modifying minimum and maximum.
	for bound, limit := range map[string]string{"exclusiveMinimum": "minimum", "exclusiveMaximum": "maximum"} {
		if s[bound] == true {
			out[bound] = out[limit]
			delete(out, limit)
		}
	}
	if s["nullable"] == true {
		if t, ok := out["type"].(string); ok {
			out["type"] = []any{t, "null"}
		} else {
			return map[string]any{"anyOf": []any{out, map[string]any{"type": "null"}}}, nil
		}
	}
	return out, nil
}

// resolve follows a reference to a collected component of the given kind.
func (p *preparer) resolve(v any, kind string) (map[string]any, error) {
	m, ok := v.(map[string]any)
	if !ok {
		return nil, fmt.Errorf("not an object")
	}
	ref, ok := m["$ref"].(string)
	if !ok {
		return m, nil
	}
	key, err := parseRef(ref)
	if err != nil {
		return nil, err
	}
	def, ok := p.components[key].(map[string]any)
	if key.kind != kind || !ok {
		return nil, fmt.Errorf("%s does not point to one of the %s", ref, kind)
	}
	return p.resolve(def, kind)
}
