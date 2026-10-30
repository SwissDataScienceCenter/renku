package renkuapi

import (
	"errors"
	"fmt"
	"io"
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

// MaxResponseBytes caps how much of an API response is read, to protect the server and the model's context.
const MaxResponseBytes = 4 << 20

// ErrResponseTooLarge is returned by the clients for responses larger than MaxResponseBytes.
var ErrResponseTooLarge = errors.New("response too large")

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
// Responses larger than MaxResponseBytes fail with ErrResponseTooLarge.
func NewClients(apiURL string, httpClient *http.Client) (*Clients, error) {
	doer := limitedDoer{httpClient, MaxResponseBytes}
	var c Clients
	var err error
	// The modules share the base URL, their paths in the specs are relative to it.
	if c.CRC, err = crc.NewClientWithResponses(apiURL, crc.WithHTTPClient(doer)); err != nil {
		return nil, fmt.Errorf("crc client: %w", err)
	}
	if c.DataConnectors, err = dataconnectors.NewClientWithResponses(apiURL, dataconnectors.WithHTTPClient(doer)); err != nil {
		return nil, fmt.Errorf("data connectors client: %w", err)
	}
	if c.Namespace, err = namespace.NewClientWithResponses(apiURL, namespace.WithHTTPClient(doer)); err != nil {
		return nil, fmt.Errorf("namespace client: %w", err)
	}
	if c.Notebooks, err = notebooks.NewClientWithResponses(apiURL, notebooks.WithHTTPClient(doer)); err != nil {
		return nil, fmt.Errorf("notebooks client: %w", err)
	}
	if c.Project, err = project.NewClientWithResponses(apiURL, project.WithHTTPClient(doer)); err != nil {
		return nil, fmt.Errorf("project client: %w", err)
	}
	if c.RenkuApps, err = renkuapps.NewClientWithResponses(apiURL, renkuapps.WithHTTPClient(doer)); err != nil {
		return nil, fmt.Errorf("renku apps client: %w", err)
	}
	if c.Search, err = search.NewClientWithResponses(apiURL, search.WithHTTPClient(doer)); err != nil {
		return nil, fmt.Errorf("search client: %w", err)
	}
	if c.Session, err = session.NewClientWithResponses(apiURL, session.WithHTTPClient(doer)); err != nil {
		return nil, fmt.Errorf("session client: %w", err)
	}
	if c.Users, err = users.NewClientWithResponses(apiURL, users.WithHTTPClient(doer)); err != nil {
		return nil, fmt.Errorf("users client: %w", err)
	}
	return &c, nil
}

// limitedDoer fails reading response bodies larger than max. The generated clients read the whole body.
type limitedDoer struct {
	client *http.Client
	max    int64
}

func (d limitedDoer) Do(req *http.Request) (*http.Response, error) {
	res, err := d.client.Do(req)
	if err != nil {
		return nil, err
	}
	res.Body = &limitedBody{ReadCloser: res.Body, left: d.max}
	return res, nil
}

type limitedBody struct {
	io.ReadCloser
	left int64
}

func (b *limitedBody) Read(p []byte) (int, error) {
	// Read one byte more than allowed, to tell a body of exactly the limit from a larger one.
	if int64(len(p)) > b.left+1 {
		p = p[:b.left+1]
	}
	n, err := b.ReadCloser.Read(p)
	b.left -= int64(n)
	if b.left < 0 {
		return 0, ErrResponseTooLarge
	}
	return n, err
}
