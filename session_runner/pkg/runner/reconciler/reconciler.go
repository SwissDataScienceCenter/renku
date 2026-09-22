package reconciler

import (
	"context"
	"fmt"
	"log"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/state"
)

type RunnerReconciler struct {
	state    *state.LocalSessionState
	client   *renku.RenkuClient
	runnerID string
}

func NewRunnerReconciler(state *state.LocalSessionState, client *renku.RenkuClient, runnerID string) (rec *RunnerReconciler, err error) {
	rec = &RunnerReconciler{
		state:    state,
		client:   client,
		runnerID: runnerID,
	}
	if rec.state == nil {
		return nil, fmt.Errorf("state not provided")
	}
	if rec.client == nil {
		return nil, fmt.Errorf("client not provided")
	}
	if rec.runnerID == "" {
		return nil, fmt.Errorf("runnerID not provided")
	}
	return rec, nil
}

type SessionRef struct {
	ID string
}

func (r *RunnerReconciler) Reconcile(ctx context.Context, session SessionRef) error {
	log.Printf("TODO: handle Reconcile(): %+v", session)
	return nil
}

// func (r *RunnerReconciler) getSession(ctx context.Context, sessionID string) {
// 	res, err := r.client.SessionRunners().GetSessionRunnersSessionRunnerIdSessionsSessionIdWithResponse(ctx, r.runnerID, sessionID)
// 	if err != nil {
// 		return fmt.Errorf("failed to get session: %w", err)
// 	}
// 	// resJSON := res.GetJSON200()
// 	// if resJSON == nil {
// 	// 	message := ""
// 	// 	resJSONDefault := res.GetJSONDefault()
// 	// 	if resJSONDefault != nil {
// 	// 		message = resJSONDefault.Error.Message
// 	// 		if resJSONDefault.Error.Detail != nil {
// 	// 			message += fmt.Sprintf(", detail: %s", *resJSONDefault.Error.Detail)
// 	// 		}
// 	// 	} else {
// 	// 		message = res.HTTPResponse.Status
// 	// 	}
// 	// 	return fmt.Errorf("failed to get session: %s", message)
// 	// }
// 	slog.Info("Received from Renku", "response", *resJSON)

// }
