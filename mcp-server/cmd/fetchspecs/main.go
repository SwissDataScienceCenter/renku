// Command fetchspecs copies the renku-data-services module specs listed in modules,
// at a git ref on GitHub or from a local checkout, into the packages of internal/renkuapi,
// where `go generate` turns them into API clients.
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
)

// repository is the GitHub repository the module specs are taken from.
const repository = "SwissDataScienceCenter/renku-data-services"

// modules maps the paths of the module specs inside the repository to the package
// directories under internal/renkuapi they are written to, as api.spec.yaml.
var modules = map[string]string{
	"components/renku_data_services/users/api.spec.yaml":           "users",
	"components/renku_data_services/search/api.spec.yaml":          "search",
	"components/renku_data_services/project/api.spec.yaml":         "project",
	"components/renku_data_services/namespace/api.spec.yaml":       "namespace",
	"components/renku_data_services/data_connectors/api.spec.yaml": "dataconnectors",
	"components/renku_data_services/session/api.spec.yaml":         "session",
	"components/renku_data_services/notebooks/api.spec.yaml":       "notebooks",
	"components/renku_data_services/crc/api.spec.yaml":             "crc",
	"components/renku_data_services/renku_apps/api.spec.yaml":      "renkuapps",
}

func main() {
	ref := flag.String("ref", "", "git ref of renku-data-services on GitHub to take the specs from")
	localDir := flag.String("local-dir", "", "local renku-data-services checkout to take the specs from instead of GitHub")
	out := flag.String("out", "internal/renkuapi", "directory holding the client packages")
	flag.Parse()

	if (*ref == "") == (*localDir == "") {
		log.Fatal("exactly one of -ref and -local-dir is required")
	}
	// origin is where the specs are taken from, for the messages and the SOURCE file.
	origin := fmt.Sprintf("%s@%s", repository, *ref)
	if *localDir != "" {
		origin = "the local checkout " + *localDir
	}
	// Fetch everything before writing anything, so that a failure leaves the specs as they were.
	fetched := map[string][]byte{}
	for m, pkg := range modules {
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
		fetched[pkg] = content
	}
	for pkg, content := range fetched {
		if err := os.WriteFile(filepath.Join(*out, pkg, "api.spec.yaml"), content, 0o644); err != nil {
			log.Fatal(err)
		}
	}
	source := fmt.Sprintf("The api.spec.yaml files were copied unchanged from %s by fetchspecs.\n", origin)
	if err := os.WriteFile(filepath.Join(*out, "SOURCE"), []byte(source), 0o644); err != nil {
		log.Fatal(err)
	}
	log.Printf("wrote %d module specs from %s to %s, run `go generate ./...` to update the clients", len(fetched), origin, *out)
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
