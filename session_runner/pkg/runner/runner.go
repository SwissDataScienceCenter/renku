package runner

import (
	"context"
	"errors"
	"fmt"
	"log"
	"log/slog"
	"net/url"
	"time"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/session_runners"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/runner/reconciler"
)

var ErrRunnerStopped = errors.New("runner has been stopped")

type Runner struct {
	renkuURL          *url.URL
	registrationToken string

	runnerID string

	renkuClient *renku.RenkuClient

	contactTicker *time.Ticker
	reconciler    *reconciler.RunnerReconciler
}

func NewRunner(options ...RunnerOption) (runner *Runner, err error) {
	r := Runner{
		reconciler: &reconciler.RunnerReconciler{},
	}
	for _, opt := range options {
		err := opt(&r)
		if err != nil {
			return nil, err
		}
	}
	if err := r.validateNewRunner(); err != nil {
		return nil, err
	}
	return &r, nil
}

func (r *Runner) validateNewRunner() error {
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

	registerCtx, registerCancel := context.WithTimeout(ctx, time.Minute)
	defer registerCancel()
	if err := r.register(registerCtx); err != nil {
		return err
	}

	// res, err := r.renkuClient.SessionRunners().PostSessionRunnersSessionRunnerIdContactWithResponse()
	// res.GetJSON200()

	if err := r.startContactLoop(ctx); err != nil && !errors.Is(err, ErrRunnerStopped) {
		return err
	}

	<-ctx.Done()
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

	fmt.Printf("Registered as runner: %s\n", r.runnerID)

	return nil
}

func (r *Runner) startContactLoop(ctx context.Context) error {
	r.contactTicker = time.NewTicker(time.Minute)
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

	// TODO: also handle sessions from local state
	if resJSON.Sessions != nil {
		for _, session := range *resJSON.Sessions {
			r.reconciler.Reconcile(ctx, reconciler.SessionRef{ID: session})
		}
	}

	return nil
}
