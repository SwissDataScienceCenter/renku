// Command specprep fetches the per-module renku-data-services API specs at a
// git ref and writes the merged and pruned spec the API client is generated
// from, together with the MCP tool definitions.
package main

import (
	"context"
	"flag"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"time"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/specprep"
)

func main() {
	configPath := flag.String("config", "apispec/tools.yaml", "path to the tools allowlist")
	ref := flag.String("ref", "", "git ref (tag) of renku-data-services to take the specs from")
	out := flag.String("out", "apispec/openapi.yaml", "path of the generated spec")
	toolsOut := flag.String("tools-out", "apispec/mcp-tools.json", "path of the generated MCP tool definitions")
	localDir := flag.String("local-dir", "", "read the module specs from a local renku-data-services checkout instead of GitHub")
	flag.Parse()

	if *ref == "" {
		log.Fatal("-ref is required")
	}
	cfg, err := specprep.LoadConfig(*configPath)
	if err != nil {
		log.Fatal(err)
	}
	modules := map[string][]byte{}
	for _, m := range cfg.Source.Modules {
		var content []byte
		if *localDir != "" {
			content, err = os.ReadFile(filepath.Join(*localDir, m))
		} else {
			content, err = fetch(fmt.Sprintf("https://raw.githubusercontent.com/%s/%s/%s", cfg.Source.Repository, *ref, m))
		}
		if err != nil {
			log.Fatalf("reading module %s: %v", m, err)
		}
		modules[m] = content
	}
	output, err := specprep.Prepare(cfg, *ref, modules)
	if err != nil {
		log.Fatal(err)
	}
	if err := os.WriteFile(*out, output.Spec, 0o644); err != nil {
		log.Fatal(err)
	}
	if err := os.WriteFile(*toolsOut, output.Tools, 0o644); err != nil {
		log.Fatal(err)
	}
	log.Printf("wrote %s and %s with %d tools from %s@%s", *out, *toolsOut, len(cfg.Tools), cfg.Source.Repository, *ref)
}

func fetch(url string) ([]byte, error) {
	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}
	res, err := http.DefaultClient.Do(req)
	if err != nil {
		return nil, err
	}
	defer func() { _ = res.Body.Close() }()
	if res.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("GET %s: %s", url, res.Status)
	}
	return io.ReadAll(res.Body)
}
