package renku

import "github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/session_runners"

type RenkuClient struct {
	sessionRunnersClient session_runners.ClientWithResponsesInterface
}

func NewRenkuClient(server string) (client *RenkuClient, err error) {
	rc := RenkuClient{}
	sessionRunnersClient, err := session_runners.NewClientWithResponses(server)
	if err != nil {
		return nil, err
	}
	rc.sessionRunnersClient = sessionRunnersClient
	return &rc, nil
}

func (rc *RenkuClient) SessionRunners() session_runners.ClientWithResponsesInterface {
	return rc.sessionRunnersClient
}
