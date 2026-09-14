package renku

import (
	"context"
	"net/http"
	"net/url"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/auth"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/session_runners"
)

type RenkuClient struct {
	authClient           auth.ClientWithResponsesInterface
	sessionRunnersClient session_runners.ClientWithResponsesInterface

	requestEditors []RequestEditorFn
}

// ClientOption allows setting custom parameters during construction
type ClientOption func(*RenkuClient) error

// RequestEditorFn is the function signature for the RequestEditor callback function
type RequestEditorFn func(ctx context.Context, req *http.Request) error

// WithRequestEditorFn allows setting request editors
func WithRequestEditorFn(fn RequestEditorFn) ClientOption {
	return func(rc *RenkuClient) error {
		rc.requestEditors = append(rc.requestEditors, fn)
		return nil
	}
}

func NewRenkuClient(serverURL *url.URL, options ...ClientOption) (client *RenkuClient, err error) {
	rc := RenkuClient{}

	apiURL := serverURL.JoinPath("/api/data")
	apiURLStr := apiURL.String()

	// Handle options
	for _, opt := range options {
		if err := opt(&rc); err != nil {
			return nil, err
		}
	}

	authOpts := []auth.ClientOption{}
	for _, fn := range rc.requestEditors {
		authOpts = append(authOpts, auth.WithRequestEditorFn(auth.RequestEditorFn(fn)))
	}
	authClient, err := auth.NewClientWithResponses(apiURLStr, authOpts...)
	if err != nil {
		return nil, err
	}
	rc.authClient = authClient

	sessionRunnersOpts := []session_runners.ClientOption{}
	for _, fn := range rc.requestEditors {
		sessionRunnersOpts = append(sessionRunnersOpts, session_runners.WithRequestEditorFn(session_runners.RequestEditorFn(fn)))
	}
	sessionRunnersClient, err := session_runners.NewClientWithResponses(apiURLStr, sessionRunnersOpts...)
	if err != nil {
		return nil, err
	}
	rc.sessionRunnersClient = sessionRunnersClient

	return &rc, nil
}

func (rc *RenkuClient) Auth() auth.ClientWithResponsesInterface {
	return rc.authClient
}

func (rc *RenkuClient) SessionRunners() session_runners.ClientWithResponsesInterface {
	return rc.sessionRunnersClient
}
