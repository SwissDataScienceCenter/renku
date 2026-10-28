package toolspec

import (
	"fmt"
	"regexp"
	"strings"
)

// Config is the tool allowlist, see apispec.Tools.
type Config struct {
	Source Source
	Tools  []ToolConfig
}

// Source describes where the per-module specs come from.
type Source struct {
	// Repository is the GitHub repository, e.g. SwissDataScienceCenter/renku-data-services.
	Repository string
	// Modules are the paths of the module specs inside the repository, and inside apispec/modules.
	Modules []string
}

// ToolConfig is one allowlisted API operation exposed as an MCP tool.
type ToolConfig struct {
	Method      string
	Path        string
	Name        string
	Description string
	// Parameters overrides the descriptions of individual parameters, keyed by parameter name.
	Parameters map[string]string
}

var toolNamePattern = regexp.MustCompile(`^[a-z][a-z0-9_]*$`)

var supportedMethods = map[string]bool{"get": true, "post": true, "put": true, "patch": true, "delete": true}

// Validate checks the config for mistakes that would otherwise surface as confusing build errors.
func (c *Config) Validate() error {
	if c.Source.Repository == "" {
		return fmt.Errorf("source.repository is required")
	}
	if len(c.Source.Modules) == 0 {
		return fmt.Errorf("source.modules must not be empty")
	}
	if len(c.Tools) == 0 {
		return fmt.Errorf("tools must not be empty")
	}
	names := map[string]bool{}
	operations := map[string]bool{}
	for i, t := range c.Tools {
		if !toolNamePattern.MatchString(t.Name) {
			return fmt.Errorf("tools[%d]: name %q must match %s", i, t.Name, toolNamePattern)
		}
		if names[t.Name] {
			return fmt.Errorf("tools[%d]: duplicate name %q", i, t.Name)
		}
		names[t.Name] = true
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
