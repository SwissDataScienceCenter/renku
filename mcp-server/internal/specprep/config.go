// Package specprep merges the per-module renku-data-services API specs, prunes
// them down to the allowlisted operations and applies the curated tool names
// and descriptions, producing a single OpenAPI 3.0 document for mcpgen.
package specprep

import (
	"fmt"
	"os"
	"regexp"
	"strings"

	"sigs.k8s.io/yaml"
)

// Config is the content of apispec/tools.yaml.
type Config struct {
	Source Source `json:"source"`
	Tools  []Tool `json:"tools"`
}

// Source describes where the per-module specs come from.
type Source struct {
	// Repository is the GitHub repository, e.g. SwissDataScienceCenter/renku-data-services.
	Repository string `json:"repository"`
	// Modules are the paths of the module specs inside the repository.
	Modules []string `json:"modules"`
}

// Tool is one allowlisted API operation exposed as an MCP tool.
type Tool struct {
	Method      string `json:"method"`
	Path        string `json:"path"`
	Name        string `json:"name"`
	Description string `json:"description"`
	// Parameters overrides the descriptions of individual parameters, keyed by parameter name.
	Parameters map[string]string `json:"parameters,omitempty"`
}

var toolNamePattern = regexp.MustCompile(`^[a-z][a-z0-9_]*$`)

var supportedMethods = map[string]bool{"get": true, "post": true, "put": true, "patch": true, "delete": true}

// LoadConfig reads and validates a tools.yaml file.
func LoadConfig(path string) (*Config, error) {
	raw, err := os.ReadFile(path)
	if err != nil {
		return nil, err
	}
	cfg, err := ParseConfig(raw)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", path, err)
	}
	return cfg, nil
}

// ParseConfig parses and validates the content of a tools.yaml file.
func ParseConfig(raw []byte) (*Config, error) {
	var cfg Config
	if err := yaml.UnmarshalStrict(raw, &cfg); err != nil {
		return nil, fmt.Errorf("parsing tools config: %w", err)
	}
	if err := cfg.Validate(); err != nil {
		return nil, fmt.Errorf("invalid tools config: %w", err)
	}
	return &cfg, nil
}

// Validate checks the config for mistakes that would otherwise surface as confusing generator errors.
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
