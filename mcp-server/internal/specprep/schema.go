package specprep

import (
	"fmt"
	"regexp"
	"strings"
)

// clean prepares an inlined OpenAPI 3.1 schema for use as the JSON Schema of a tool
// argument. It drops extensions and the OpenAPI specific keywords, which are noise for
// the model, and patterns that the Go regexp package cannot compile, which would make
// the server fail to start. The API still enforces those patterns.
// at is the location of s, used in the warnings about dropped patterns.
func clean(s map[string]any, at string, warn func(string)) map[string]any {
	out := map[string]any{}
	for key, val := range s {
		switch key {
		case "properties":
			props := map[string]any{}
			for name, prop := range val.(map[string]any) {
				props[name] = clean(prop.(map[string]any), at+"."+name, warn)
			}
			out[key] = props
		case "items", "not":
			out[key] = clean(val.(map[string]any), at+"."+key, warn)
		case "additionalProperties":
			if sub, ok := val.(map[string]any); ok {
				out[key] = clean(sub, at+"."+key, warn)
			} else {
				out[key] = val
			}
		case "allOf", "oneOf", "anyOf":
			var list []any
			for i, item := range val.([]any) {
				list = append(list, clean(item.(map[string]any), fmt.Sprintf("%s.%s[%d]", at, key, i), warn))
			}
			out[key] = list
		case "example":
			// Deprecated in OpenAPI 3.1 in favour of the JSON Schema examples.
			if _, ok := s["examples"]; !ok {
				out["examples"] = []any{val}
			}
		case "pattern":
			if _, err := regexp.Compile(val.(string)); err != nil {
				warn(fmt.Sprintf("%s: dropped pattern %q, Go cannot compile it: %v", at, val, err))
			} else {
				out[key] = val
			}
		case "discriminator", "xml", "externalDocs":
		default:
			if !strings.HasPrefix(key, "x-") {
				out[key] = val
			}
		}
	}
	return out
}
