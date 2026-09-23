package reconciler

import (
	"context"
	"fmt"
	"log/slog"
	"net/url"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/session_runners"
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
	sessionDetails, err := r.getSession(ctx, session.ID)
	if err != nil {
		return err
	}
	sessionURLStr := ""
	if sessionDetails.Spec.Url != nil {
		sessionURLStr = *sessionDetails.Spec.Url
	}
	var sessionURL *url.URL = nil
	if sessionURLStr != "" {
		sessionURL_, err := url.Parse(sessionURLStr)
		if err == nil {
			sessionURL = sessionURL_
		}
	}

	sessionSecrets, err := r.getSessionSecrets(ctx, session.ID)
	if err != nil {
		slog.Error("Could not get session secrets", "sessionID", session.ID)
	}

	specSecrets := make(map[string]string, len(sessionSecrets))
	for _, secret := range sessionSecrets {
		specSecrets[secret.Name] = secret.Value
	}
	spec := state.LocalSessionSpec{
		Image:   sessionDetails.Spec.Image,
		URL:     sessionURL,
		Secrets: specSecrets,
	}
	_, err = r.state.UpsertSession(ctx, session.ID, spec)
	if err != nil {
		return err
	}

	return nil
}

func (r *RunnerReconciler) getSession(ctx context.Context, sessionID string) (session session_runners.AssignedSessionDetails, err error) {
	res, err := r.client.SessionRunners().GetSessionRunnersSessionRunnerIdSessionsSessionIdWithResponse(ctx, r.runnerID, sessionID)
	if err != nil {
		return session, fmt.Errorf("failed to get session: %w", err)
	}
	resJSON := res.GetJSON200()
	if resJSON == nil {
		message := ""
		resJSONDefault := res.GetJSONDefault()
		if resJSONDefault != nil {
			message = resJSONDefault.Error.Message
			if resJSONDefault.Error.Detail != nil {
				message += fmt.Sprintf(", detail: %s", *resJSONDefault.Error.Detail)
			}
		} else {
			message = res.HTTPResponse.Status
		}
		return session, fmt.Errorf("failed to get session: %s", message)
	}
	slog.Info("Received from Renku", "status", res.HTTPResponse.Status, "response", *resJSON)
	return *resJSON, nil
}

func (r *RunnerReconciler) getSessionSecrets(ctx context.Context, sessionID string) (secrets session_runners.AssignedSessionSecrets, err error) {
	res, err := r.client.SessionRunners().GetSessionRunnersSessionRunnerIdSessionsSessionIdSecretsWithResponse(ctx, r.runnerID, sessionID)
	if err != nil {
		return secrets, fmt.Errorf("failed to get session: %w", err)
	}
	resJSON := res.GetJSON200()
	if resJSON == nil {
		message := ""
		resJSONDefault := res.GetJSONDefault()
		if resJSONDefault != nil {
			message = resJSONDefault.Error.Message
			if resJSONDefault.Error.Detail != nil {
				message += fmt.Sprintf(", detail: %s", *resJSONDefault.Error.Detail)
			}
		} else {
			message = res.HTTPResponse.Status
		}
		return secrets, fmt.Errorf("failed to get session: %s", message)
	}
	slog.Info("Received from Renku", "status", res.HTTPResponse.Status, "response", *resJSON)
	return *resJSON, nil
}
