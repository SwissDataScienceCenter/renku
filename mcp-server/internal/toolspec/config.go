package toolspec

import (
	"fmt"
	"regexp"
	"strings"

	v3 "github.com/pb33f/libopenapi/datamodel/high/v3"
)

// ToolConfig is one allowlisted API operation exposed as an MCP tool, see apispec.Tools.
type ToolConfig struct {
	// Spec is the module spec that defines the operation, see Parse.
	Spec        *v3.Document
	Method      string
	Path        string
	Name        string
	Description string
	// PathDescriptions replaces the descriptions the spec gives path parameters, keyed by
	// parameter name. Use it where the spec's description is too vague for a model. A key that
	// matches no path parameter fails the build.
	PathDescriptions map[string]string
	// QueryDescriptions replaces the descriptions the spec gives query parameters, keyed by tool
	// argument name: the parameter name, or for an exploded object parameter the name of one of
	// its properties (e.g. `q` of the search `params`). Use it where the spec's description is
	// too vague for a model. A key that matches no query parameter fails the build.
	QueryDescriptions map[string]string
}

var toolNamePattern = regexp.MustCompile(`^[a-z][a-z0-9_]*$`)

var supportedMethods = map[string]bool{"get": true, "post": true, "put": true, "patch": true, "delete": true}

// validate checks the tools for mistakes that would otherwise surface as confusing build errors.
func validate(tools []ToolConfig) error {
	if len(tools) == 0 {
		return fmt.Errorf("tools must not be empty")
	}
	names := map[string]bool{}
	operations := map[string]bool{}
	for i, t := range tools {
		if !toolNamePattern.MatchString(t.Name) {
			return fmt.Errorf("tools[%d]: name %q must match %s", i, t.Name, toolNamePattern)
		}
		if names[t.Name] {
			return fmt.Errorf("tools[%d]: duplicate name %q", i, t.Name)
		}
		names[t.Name] = true
		if t.Spec == nil {
			return fmt.Errorf("tools[%d] (%s): spec is required", i, t.Name)
		}
		if !supportedMethods[t.Method] {
			return fmt.Errorf("tools[%d] (%s): unsupported method %q, use lowercase get/post/put/patch/delete", i, t.Name, t.Method)
		}
		if !strings.HasPrefix(t.Path, "/") {
			return fmt.Errorf("tools[%d] (%s): path %q must start with /", i, t.Name, t.Path)
		}
		op := t.Method + " " + t.Path
		if operations[op] {
			return fmt.Errorf("tools[%d] (%s): operation %s is listed twice", i, t.Name, op)
		}
		operations[op] = true
		if strings.TrimSpace(t.Description) == "" {
			return fmt.Errorf("tools[%d] (%s): description is required", i, t.Name)
		}
	}
	return nil
}
