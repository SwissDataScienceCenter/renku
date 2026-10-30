package renkuapi

import (
	"fmt"
	"net/http"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/crc"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/dataconnectors"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/namespace"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/notebooks"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/project"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/renkuapps"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/search"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/session"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/users"
)

// Clients holds a client for every module of the Renku API. They are shared by all users:
// the user's token is added to each request, see toolkit.CallerHeaders.
type Clients struct {
	CRC            *crc.ClientWithResponses
	DataConnectors *dataconnectors.ClientWithResponses
	Namespace      *namespace.ClientWithResponses
	Notebooks      *notebooks.ClientWithResponses
	Project        *project.ClientWithResponses
	RenkuApps      *renkuapps.ClientWithResponses
	Search         *search.ClientWithResponses
	Session        *session.ClientWithResponses
	Users          *users.ClientWithResponses
}

// NewClients creates the clients for the Renku API at apiURL, e.g. http://renku-gateway/api/data.
func NewClients(apiURL string, httpClient *http.Client) (*Clients, error) {
	var c Clients
	var err error
	// The modules share the base URL, their paths in the specs are relative to it.
	if c.CRC, err = crc.NewClientWithResponses(apiURL, crc.WithHTTPClient(httpClient)); err != nil {
		return nil, fmt.Errorf("crc client: %w", err)
	}
	if c.DataConnectors, err = dataconnectors.NewClientWithResponses(apiURL, dataconnectors.WithHTTPClient(httpClient)); err != nil {
		return nil, fmt.Errorf("data connectors client: %w", err)
	}
	if c.Namespace, err = namespace.NewClientWithResponses(apiURL, namespace.WithHTTPClient(httpClient)); err != nil {
		return nil, fmt.Errorf("namespace client: %w", err)
	}
	if c.Notebooks, err = notebooks.NewClientWithResponses(apiURL, notebooks.WithHTTPClient(httpClient)); err != nil {
		return nil, fmt.Errorf("notebooks client: %w", err)
	}
	if c.Project, err = project.NewClientWithResponses(apiURL, project.WithHTTPClient(httpClient)); err != nil {
		return nil, fmt.Errorf("project client: %w", err)
	}
	if c.RenkuApps, err = renkuapps.NewClientWithResponses(apiURL, renkuapps.WithHTTPClient(httpClient)); err != nil {
		return nil, fmt.Errorf("renku apps client: %w", err)
	}
	if c.Search, err = search.NewClientWithResponses(apiURL, search.WithHTTPClient(httpClient)); err != nil {
		return nil, fmt.Errorf("search client: %w", err)
	}
	if c.Session, err = session.NewClientWithResponses(apiURL, session.WithHTTPClient(httpClient)); err != nil {
		return nil, fmt.Errorf("session client: %w", err)
	}
	if c.Users, err = users.NewClientWithResponses(apiURL, users.WithHTTPClient(httpClient)); err != nil {
		return nil, fmt.Errorf("users client: %w", err)
	}
	return &c, nil
}
