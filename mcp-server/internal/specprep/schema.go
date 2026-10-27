package specprep

import (
	"fmt"
	"strings"
)

// bodyArgument is the tool argument that holds the request body.
const bodyArgument = "body"

// inputSchema builds the JSON Schema of the arguments of a tool from its prepared
// operation. Every parameter becomes a top level argument and the JSON request body
// becomes the body argument.
func inputSchema(op map[string]any) (map[string]any, error) {
	properties := map[string]any{}
	required := []any{}
	params, _ := op["parameters"].([]map[string]any)
	for _, param := range params {
		name, _ := param["name"].(string)
		if param["in"] != "path" && param["in"] != "query" {
			return nil, fmt.Errorf("parameter %s: only path and query parameters are supported, got %v", name, param["in"])
		}
		if _, ok := properties[name]; ok || name == bodyArgument {
			return nil, fmt.Errorf("parameter %s: argument name is already used", name)
		}
		schema := jsonSchema(param["schema"].(map[string]any))
		if description, ok := param["description"]; ok {
			schema["description"] = description
		}
		properties[name] = schema
		if param["required"] == true {
			required = append(required, name)
		}
	}

	if body, ok := op["requestBody"].(map[string]any); ok {
		media := body["content"].(map[string]any)["application/json"].(map[string]any)
		schema := jsonSchema(media["schema"].(map[string]any))
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

// jsonSchema converts an inlined OpenAPI 3.0 schema into a JSON Schema by
// translating the OpenAPI specific keywords.
func jsonSchema(s map[string]any) map[string]any {
	out := map[string]any{}
	for key, val := range s {
		switch key {
		case "properties":
			props := map[string]any{}
			for name, prop := range val.(map[string]any) {
				props[name] = jsonSchema(prop.(map[string]any))
			}
			out[key] = props
		case "items", "not":
			out[key] = jsonSchema(val.(map[string]any))
		case "additionalProperties":
			if sub, ok := val.(map[string]any); ok {
				out[key] = jsonSchema(sub)
			} else {
				out[key] = val
			}
		case "allOf", "oneOf", "anyOf":
			var list []any
			for _, item := range val.([]any) {
				list = append(list, jsonSchema(item.(map[string]any)))
			}
			out[key] = list
		case "example":
			out["examples"] = []any{val}
		case "nullable", "exclusiveMinimum", "exclusiveMaximum", "discriminator", "xml", "externalDocs":
			// Translated below or not meaningful for tool arguments.
		default:
			if !strings.HasPrefix(key, "x-") {
				out[key] = val
			}
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
			return map[string]any{"anyOf": []any{out, map[string]any{"type": "null"}}}
		}
	}
	return out
}
