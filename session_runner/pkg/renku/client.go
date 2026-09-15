package renku

import (
	"context"
	"net/http"
	"net/url"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/session_runners"
)

type RenkuClient struct {
	sessionRunnersClient session_runners.ClientWithResponsesInterface

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

	sessionRunnersOpts := []session_runners.ClientOption{}
	if rc.auth != nil {
		sessionRunnersOpts = append(sessionRunnersOpts, session_runners.WithRequestEditorFn(session_runners.RequestEditorFn(rc.auth.RequestEditor())))
	}
	sessionRunnersClient, err := session_runners.NewClientWithResponses(apiURLStr, sessionRunnersOpts...)
	if err != nil {
		return nil, err
	}
	rc.sessionRunnersClient = sessionRunnersClient

	return &rc, nil
}

func (rc *RenkuClient) SessionRunners() session_runners.ClientWithResponsesInterface {
	return rc.sessionRunnersClient
}
