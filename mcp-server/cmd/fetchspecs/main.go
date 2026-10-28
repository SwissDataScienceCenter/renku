// Command fetchspecs downloads the renku-data-services module specs listed in
// apispec/tools.yaml at a git ref into apispec/modules, which the MCP server
// builds its tools from at startup. It fails if the tools cannot be built from them.
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

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/toolspec"
)

func main() {
	configPath := flag.String("config", "apispec/tools.yaml", "path to the tools allowlist")
	ref := flag.String("ref", "", "git ref (tag) of renku-data-services to take the specs from")
	out := flag.String("out", "apispec/modules", "directory the module specs are written to, it is replaced")
	localDir := flag.String("local-dir", "", "read the module specs from a local renku-data-services checkout instead of GitHub")
	flag.Parse()

	if *ref == "" {
		log.Fatal("-ref is required")
	}
	cfg, err := toolspec.LoadConfig(*configPath)
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
	if _, err := toolspec.Build(cfg, modules); err != nil {
		log.Fatalf("the MCP server cannot build its tools from %s@%s: %v", cfg.Source.Repository, *ref, err)
	}

	// Replace the directory so that modules no longer listed in tools.yaml are removed.
	if err := os.RemoveAll(*out); err != nil {
		log.Fatal(err)
	}
	for name, content := range modules {
		path := filepath.Join(*out, name)
		if err := os.MkdirAll(filepath.Dir(path), 0o755); err != nil {
			log.Fatal(err)
		}
		if err := os.WriteFile(path, content, 0o644); err != nil {
			log.Fatal(err)
		}
	}
	source := fmt.Sprintf("Copied unchanged from %s@%s by 'just update-spec %s'.\n", cfg.Source.Repository, *ref, *ref)
	if err := os.WriteFile(filepath.Join(*out, "SOURCE"), []byte(source), 0o644); err != nil {
		log.Fatal(err)
	}
	log.Printf("wrote %d module specs from %s@%s to %s", len(modules), cfg.Source.Repository, *ref, *out)
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
