// Command fetchspecs copies the renku-data-services module specs listed in modules,
// at a git ref on GitHub or from a local checkout, into apispec/modules, where they
// are embedded and parsed by package apispec. It fails if a spec cannot be parsed.
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

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/api2tool"
)

// repository is the GitHub repository the module specs are taken from.
const repository = "SwissDataScienceCenter/renku-data-services"

// modules are the paths of the module specs inside the repository, and inside apispec/modules.
var modules = []string{
	"components/renku_data_services/users/api.spec.yaml",
	"components/renku_data_services/search/api.spec.yaml",
	"components/renku_data_services/project/api.spec.yaml",
	"components/renku_data_services/namespace/api.spec.yaml",
	"components/renku_data_services/data_connectors/api.spec.yaml",
	"components/renku_data_services/session/api.spec.yaml",
	"components/renku_data_services/notebooks/api.spec.yaml",
}

func main() {
	ref := flag.String("ref", "", "git ref (tag) of renku-data-services on GitHub to take the specs from")
	localDir := flag.String("local-dir", "", "local renku-data-services checkout to take the specs from instead of GitHub")
	out := flag.String("out", "apispec/modules", "directory the module specs are written to, it is replaced")
	flag.Parse()

	if (*ref == "") == (*localDir == "") {
		log.Fatal("exactly one of -ref and -local-dir is required")
	}
	// origin is where the specs are taken from, for the messages and the SOURCE file.
	origin := fmt.Sprintf("%s@%s", repository, *ref)
	if *localDir != "" {
		origin = "the local checkout " + *localDir
	}
	fetched := map[string][]byte{}
	for _, m := range modules {
		var content []byte
		var err error
		if *localDir != "" {
			content, err = os.ReadFile(filepath.Join(*localDir, m))
		} else {
			content, err = fetch(fmt.Sprintf("https://raw.githubusercontent.com/%s/%s/%s", repository, *ref, m))
		}
		if err != nil {
			log.Fatalf("reading module %s: %v", m, err)
		}
		if _, err := api2tool.Parse(content); err != nil {
			log.Fatalf("module %s of %s: %v", m, origin, err)
		}
		fetched[m] = content
	}

	// Replace the directory so that modules no longer listed are removed.
	if err := os.RemoveAll(*out); err != nil {
		log.Fatal(err)
	}
	for name, content := range fetched {
		path := filepath.Join(*out, name)
		if err := os.MkdirAll(filepath.Dir(path), 0o755); err != nil {
			log.Fatal(err)
		}
		if err := os.WriteFile(path, content, 0o644); err != nil {
			log.Fatal(err)
		}
	}
	source := fmt.Sprintf("Copied unchanged from %s by fetchspecs.\n", origin)
	if err := os.WriteFile(filepath.Join(*out, "SOURCE"), []byte(source), 0o644); err != nil {
		log.Fatal(err)
	}
	log.Printf("wrote %d module specs from %s to %s", len(fetched), origin, *out)
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
