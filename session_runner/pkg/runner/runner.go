package runner

import (
	"context"
	"fmt"
	"net/url"
	"time"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku"
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/session_runners"
)

type Runner struct {
	renkuURL          *url.URL
	registrationToken string

	runnerID string

	renkuClient *renku.RenkuClient
}

func NewRunner(options ...RunnerOption) (runner *Runner, err error) {
	r := Runner{}
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

	renkuClient, err := renku.NewRenkuClient(r.renkuURL)
	if err != nil {
		return err
	}
	r.renkuClient = renkuClient

	registerCtx, registerCancel := context.WithTimeout(ctx, time.Minute)
	defer registerCancel()
	if err := r.register(registerCtx); err != nil {
		return err
	}

	// TODO: this waits until cancellation of ctx
	<-ctx.Done()
	return ctx.Err()
}

func (r *Runner) register(ctx context.Context) error {
	registerResponse, err := r.renkuClient.SessionRunners().PostSessionRunnersRegisterWithResponse(ctx, session_runners.SessionRunnerRegisterPost{
		RegistrationToken: r.registrationToken,
	})
	if err != nil {
		return fmt.Errorf("failed to register runner: %w", err)
	}
	// fmt.Printf("response: %s\n", registerResponse.HTTPResponse.Status)
	if registerResponse.GetJSON200() == nil {
		message := ""
		if res := registerResponse.GetJSONDefault(); res != nil {
			message = fmt.Sprintf("%s, detail: %s", res.Error.Message, *res.Error.Detail)
		} else {
			message = registerResponse.HTTPResponse.Status
		}
		return fmt.Errorf("failed to register runner: %s", message)
	}

	registerResponseJSON := registerResponse.GetJSON200()
	fmt.Printf("%+v\n", registerResponseJSON.Runner)

	return fmt.Errorf("not fully implemented")
}
