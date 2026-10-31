package getproject

import (
	"context"
	"errors"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/project"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

const projectJSON = `{"id": "01J0000000000000000000000A", "name": "climate", "namespace": "my-group", "slug": "climate",
	"visibility": "public", "created_by": "user-1", "creation_date": "2023-11-01T17:32:28Z", "unknown_field": 1}`

// fakeGateway serves one project and records the requests it receives.
func fakeGateway(t *testing.T) (string, *[]*http.Request) {
	t.Helper()
	var requests []*http.Request
	srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		requests = append(requests, r)
		w.Header().Set("Content-Type", "application/json")
		if r.URL.Path != "/api/data/projects/01J0000000000000000000000A" {
			w.WriteHeader(http.StatusNotFound)
			_, _ = w.Write([]byte(`{"error": {"code": 1404, "message": "Project does not exist."}}`))
			return
		}
		_, _ = w.Write([]byte(projectJSON))
	}))
	t.Cleanup(srv.Close)
	return srv.URL + "/api/data", &requests
}

// callHandler calls the get_project handler directly, as the user user-1, without an MCP server.
func callHandler(t *testing.T, apiURL string, in input) (*project.Project, error) {
	t.Helper()
	api, err := renkuapi.NewClients(apiURL, http.DefaultClient)
	if err != nil {
		t.Fatal(err)
	}
	ctx := auth.WithPrincipal(context.Background(), auth.Principal{Subject: "user-1", Token: "the-token"})
	_, out, err := handler(api)(ctx, &mcp.CallToolRequest{}, in)
	if err != nil {
		return nil, err
	}
	p, ok := out.(*project.Project)
	if !ok {
		t.Fatalf("output is a %T", out)
	}
	return p, nil
}

func TestGetProject(t *testing.T) {
	apiURL, requests := fakeGateway(t)
	got, err := callHandler(t, apiURL, input{ProjectID: "01J0000000000000000000000A", WithDocumentation: true})
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if got.Name != "climate" || got.Namespace != "my-group" {
		t.Errorf("project = %+v", got)
	}
	r := (*requests)[0]
	if r.Header.Get("Authorization") != "Bearer the-token" || r.Header.Get("X-Request-Id") == "" {
		t.Errorf("the request is not sent on behalf of the caller: %v", r.Header)
	}
	if r.URL.RawQuery != "with_documentation=true" {
		t.Errorf("query = %q", r.URL.RawQuery)
	}
}

func TestGetProjectReportsTheAPIError(t *testing.T) {
	apiURL, _ := fakeGateway(t)
	_, err := callHandler(t, apiURL, input{ProjectID: "01J0000000000000000000000B"})
	if err == nil || !strings.Contains(err.Error(), "status 404: Project does not exist.") {
		t.Errorf("error = %v", err)
	}
}

func TestGetProjectReportsAnUnreachableAPI(t *testing.T) {
	srv := httptest.NewServer(http.NotFoundHandler())
	srv.Close()
	_, err := callHandler(t, srv.URL+"/api/data", input{ProjectID: "01J0000000000000000000000A"})
	var toolErr *toolkit.Error
	if !errors.As(err, &toolErr) || toolErr.Outcome != toolkit.OutcomeUnreachable {
		t.Errorf("error = %v", err)
	}
}
