package renku

import (
	"context"
	"net/http"
	"net/url"

	sessionRunners "github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/session_runners"
)

type RenkuClient struct {
	sessionRunnersClient sessionRunners.ClientWithResponsesInterface

	auth *RenkuAuth
}

// ClientOption allows setting custom parameters during construction
type ClientOption func(*RenkuClient) error

// RequestEditorFn is the function signature for the RequestEditor callback function
type RequestEditorFn func(ctx context.Context, req *http.Request) error

// WithAuth sets the authentication for the Renku client
func WithAuth(ra *RenkuAuth) ClientOption {
	return func(rc *RenkuClient) error {
		rc.auth = ra
		return nil
	}
}

func NewRenkuClient(serverURL *url.URL, options ...ClientOption) (client *RenkuClient, err error) {
	rc := RenkuClient{}

	apiURL := serverURL.ResolveReference(&url.URL{Path: "/api/data"})
	apiURLStr := apiURL.String()

	// Handle options
	for _, opt := range options {
		if err := opt(&rc); err != nil {
			return nil, err
		}
	}

	sessionRunnersOpts := []sessionRunners.ClientOption{}
	if rc.auth != nil {
		sessionRunnersOpts = append(sessionRunnersOpts, sessionRunners.WithRequestEditorFn(sessionRunners.RequestEditorFn(rc.auth.RequestEditor())))
	}
	sessionRunnersClient, err := sessionRunners.NewClientWithResponses(apiURLStr, sessionRunnersOpts...)
	if err != nil {
		return nil, err
	}
	rc.sessionRunnersClient = sessionRunnersClient

	return &rc, nil
}

func (rc *RenkuClient) SessionRunners() sessionRunners.ClientWithResponsesInterface {
	return rc.sessionRunnersClient
}
