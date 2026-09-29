package runner

import (
	"context"
	"errors"
	"fmt"
	"log"
	"log/slog"
	"net/url"
	"time"

	apptainerEngine "github.com/SwissDataScienceCenter/renku/session_runner/pkg/engine/apptainer"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/session_runners"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/runner/persistence"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/runner/reconciler"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/state"
)

var ErrRunnerStopped = errors.New("runner has been stopped")

type Runner struct {
	renkuURL          *url.URL
	registrationToken string

	runnerID string

	renkuClient *renku.RenkuClient

	contactTicker *time.Ticker
	state         *state.LocalSessionState
	reconciler    *reconciler.RunnerReconciler
	engine        *apptainerEngine.ApptainerEngine

	persist *persistence.Persistence
}

func NewRunner(options ...RunnerOption) (runner *Runner, err error) {
	r := Runner{}
	for _, opt := range options {
		err := opt(&r)
		if err != nil {
			return nil, err
		}
	}
	state, err := state.NewLocalSessionState()
	if err != nil {
		return nil, err
	}
	r.state = state
	engine, err := apptainerEngine.NewApptainerEngine(state)
	if err != nil {
		return nil, err
	}
	r.engine = engine
	persist, err := persistence.NewPersistence()
	if err != nil {
		return nil, err
	}
	r.persist = persist
	if r.renkuURL.String() == "" && r.registrationToken == "" {
		err := r.recoverRunner()
		if err != nil {
			slog.Warn("could not resume runner", "err", err)
		}
	}
	if err := r.validateNewRunner(); err != nil {
		return nil, err
	}
	return &r, nil
}

func (r *Runner) recoverRunner() error {
	state, err := r.persist.Get()
	if err != nil {
		return err
	}

	slog.Info("recovered state", "state", state)
	if state.RunnerID != "" {
		r.runnerID = state.RunnerID
	}
	if state.ServerURL != "" {
		parsedURL, err := url.Parse(state.ServerURL)
		if err == nil {
			r.renkuURL = parsedURL
		}
	}
	if state.Auth != nil && string(state.Auth.RefreshToken) != "" {
		renkuAuth, err := renku.NewRenkuAuth(r.renkuURL, "", string(state.Auth.RefreshToken))
		if err != nil {
			return err
		}
		renkuClient, err := renku.NewRenkuClient(r.renkuURL, renku.WithAuth(renkuAuth))
		if err != nil {
			return err
		}
		r.renkuClient = renkuClient
	}
	if r.runnerID != "" && r.renkuClient != nil {
		rec, err := reconciler.NewRunnerReconciler(r.state, r.renkuClient, r.runnerID)
		if err != nil {
			return err
		}
		r.reconciler = rec
	}
	return nil
}

func (r *Runner) validateNewRunner() error {
	// Validate recovered client
	if r.runnerID != "" {
		if r.renkuClient == nil {
			return fmt.Errorf("Renku client not recovered")
		}
		if r.reconciler == nil {
			return fmt.Errorf("reconciler not recovered")
		}
		return nil
	}

	if r.renkuURL == nil {
		return fmt.Errorf("Renku URL not provided")
	}
	if !r.renkuURL.IsAbs() {
		return fmt.Errorf("provided URL '%s' is not absolute", r.renkuURL.String())
	}
	if r.registrationToken == "" {
		return fmt.Errorf("registration token not provided")
	}
	return nil
}

type RunnerOption func(*Runner) error

func WithRenkuURL(renkuURL string) RunnerOption {
	return func(r *Runner) error {
		parsedURL, err := url.Parse(renkuURL)
		if err != nil {
			return err
		}
		r.renkuURL = parsedURL
		return nil
	}
}

func WithRegistrationToken(token string) RunnerOption {
	return func(r *Runner) error {
		r.registrationToken = token
		return nil
	}
}

func (r *Runner) Start(ctx context.Context) error {
	// TODO

	if err := r.lock(); err != nil {
		return err
	}
	defer r.unlock()

	if r.runnerID == "" {
		r.persist.Set(persistence.PersistedRunnerState{ServerURL: r.renkuURL.String()})
		registerCtx, registerCancel := context.WithTimeout(ctx, time.Minute)
		defer registerCancel()
		if err := r.register(registerCtx); err != nil {
			return err
		}
	}

	// TODO: use wait group?
	go func() {
		if err := r.engine.Start(ctx); err != nil && !errors.Is(err, apptainerEngine.ErrEngineStopped) {
			panic(err)
		}
	}()

	if err := r.startContactLoop(ctx); err != nil && !errors.Is(err, ErrRunnerStopped) {
		return err
	}

	<-ctx.Done()
	if err := r.persist.Close(); err != nil {
		return err
	}
	if err := ctx.Err(); err != nil && !errors.Is(err, context.Canceled) {
		return err
	}
	return nil
}

func (r *Runner) register(ctx context.Context) error {
	renkuClient, err := renku.NewRenkuClient(r.renkuURL)
	if err != nil {
		return err
	}

	registerResponse, err := renkuClient.SessionRunners().PostSessionRunnersRegisterWithResponse(ctx, session_runners.SessionRunnerRegisterPost{
		RegistrationToken: r.registrationToken,
	})
	if err != nil {
		return fmt.Errorf("failed to register runner: %w", err)
	}
	registerResponseJSON := registerResponse.GetJSON200()
	if registerResponseJSON == nil {
		message := ""
		resJSONDefault := registerResponse.GetJSONDefault()
		if resJSONDefault != nil {
			message = resJSONDefault.Error.Message
			if resJSONDefault.Error.Detail != nil {
				message += fmt.Sprintf(", detail: %s", *resJSONDefault.Error.Detail)
			}
		} else {
			message = registerResponse.HTTPResponse.Status
		}
		return fmt.Errorf("failed to register runner: %s", message)
	}

	r.runnerID = registerResponseJSON.Runner.Id
	accessToken := registerResponseJSON.Auth.AccessToken
	refreshToken := registerResponseJSON.Auth.RefreshToken

	renkuAuth, err := renku.NewRenkuAuth(r.renkuURL, accessToken, refreshToken)
	if err != nil {
		return err
	}
	renkuClient, err = renku.NewRenkuClient(r.renkuURL, renku.WithAuth(renkuAuth))
	if err != nil {
		return err
	}
	r.renkuClient = renkuClient
	rec, err := reconciler.NewRunnerReconciler(r.state, renkuClient, r.runnerID)
	if err != nil {
		return err
	}
	r.reconciler = rec

	r.persist.Set(persistence.PersistedRunnerState{
		RunnerID:  r.runnerID,
		ServerURL: r.renkuURL.String(),
		Auth: &persistence.PersistedRunnerStateAuth{
			RefreshToken: persistence.EncodedString(refreshToken),
		},
	})

	fmt.Printf("Registered as runner: %s\n", r.runnerID)

	return nil
}

func (r *Runner) startContactLoop(ctx context.Context) error {
	if r.reconciler == nil {
		return fmt.Errorf("reconciler is not set")
	}

	r.contactTicker = time.NewTicker(10 * time.Second)
	ch := make(chan error, 1)
	go r.contactLoop(ctx, ch)
	err := <-ch
	r.contactTicker.Stop()
	return err
}

func (r *Runner) contactLoop(ctx context.Context, ch chan<- error) {
	// Immediately contact the API (the ticker delays the first loop)
	contactCtx, contactCancel := context.WithTimeout(ctx, time.Minute)
	err := r.contact(contactCtx)
	contactCancel()
	if err != nil {
		log.Printf("Could not contact Renku instance: %s\n", err.Error())
	}

	for {
		select {
		case <-r.contactTicker.C:
			contactCtx, contactCancel := context.WithTimeout(ctx, time.Minute)
			err := r.contact(contactCtx)
			contactCancel()
			if err != nil {
				log.Printf("Could not contact Renku instance: %s\n", err.Error())
			}
		case <-ctx.Done():
			fmt.Printf("\nStopping runner: %s\n", context.Cause(ctx))
			ch <- ErrRunnerStopped
			return
		}
	}
}

func (r *Runner) contact(ctx context.Context) error {
	body := session_runners.SessionRunnerContactPost{
		// TODO: handle status (?)
		Status: session_runners.SessionRunnerContactPostStatusReady,
	}
	slog.Info("Sending to Renku", "body", body)
	res, err := r.renkuClient.SessionRunners().PostSessionRunnersSessionRunnerIdContactWithResponse(ctx, r.runnerID, body)
	if err != nil {
		return fmt.Errorf("failed to contact Renku: %w", err)
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
		return fmt.Errorf("failed to contact Renku: %s", message)
	}
	slog.Info("Received from Renku", "response", *resJSON)

	// Merge existing session IDs with new ones from the POST response
	existingSessionIDs := r.state.GetSessionIDs(ctx)
	sessionIDs := make(map[string]struct{}, len(existingSessionIDs)+len(resJSON.Sessions))
	for _, sessionID := range existingSessionIDs {
		sessionIDs[sessionID] = struct{}{}
	}
	for _, sessionID := range resJSON.Sessions {
		sessionIDs[sessionID] = struct{}{}
	}
	for sessionID := range sessionIDs {
		err := r.reconciler.Reconcile(ctx, reconciler.SessionRef{ID: sessionID})
		if err != nil {
			slog.Error("Failed to reconcile", "sessionID", sessionID, "error", err)
		}
	}

	return nil
}
