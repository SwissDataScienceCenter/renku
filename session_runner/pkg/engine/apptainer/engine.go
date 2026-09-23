package apptainerEngine

import (
	"context"
	"errors"
	"fmt"
	"log"
	"log/slog"
	"os/exec"
	"strings"
	"sync"
	"time"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/state"
)

const (
	apptainer = "apptainer"
	wstunnel  = "wstunnel"
)

var ErrEngineStopped = errors.New("engine has been stopped")

type ApptainerEngine struct {
	state *state.LocalSessionState

	ticker       *time.Ticker
	handles      map[string]sessionHandle
	handlesMutex sync.RWMutex
}

type sessionHandle struct {
	apptainerInstance string
	wstunnelCmd       *exec.Cmd
}

func NewApptainerEngine(state *state.LocalSessionState) (ae *ApptainerEngine, err error) {
	ae = &ApptainerEngine{
		state:        state,
		handles:      make(map[string]sessionHandle),
		handlesMutex: sync.RWMutex{},
	}
	if ae.state == nil {
		return nil, fmt.Errorf("state not provided")
	}

	apptainerPath, err := exec.LookPath(apptainer)
	if err != nil {
		return nil, err
	}
	slog.Info("Found apptainer CLI", "path", apptainerPath)
	wstunnelPath, err := exec.LookPath(wstunnel)
	if err != nil {
		return nil, err
	}
	slog.Info("Found wstunnel CLI", "path", wstunnelPath)
	return ae, nil
}

func (ae *ApptainerEngine) Start(ctx context.Context) error {
	if err := ae.startReconcileLoop(ctx); err != nil && !errors.Is(err, ErrEngineStopped) {
		return err
	}

	<-ctx.Done()
	if err := ctx.Err(); err != nil && !errors.Is(err, context.Canceled) {
		return err
	}
	return nil
}

func (ae *ApptainerEngine) startReconcileLoop(ctx context.Context) error {
	ae.ticker = time.NewTicker(10 * time.Second)
	ch := make(chan error, 1)
	go ae.reconcileLoop(ctx, ch)
	err := <-ch
	ae.ticker.Stop()
	return err
}

func (ae *ApptainerEngine) reconcileLoop(ctx context.Context, ch chan<- error) {
	for {
		select {
		case <-ae.ticker.C:
			reconcileCtx, reconcileCancel := context.WithTimeout(ctx, time.Minute)
			err := ae.reconcile(reconcileCtx)
			reconcileCancel()
			if err != nil {
				log.Printf("Engine error: %s\n", err.Error())
			}
		case <-ctx.Done():
			ch <- ErrEngineStopped
			return
		}
	}
}

func (ae *ApptainerEngine) reconcile(ctx context.Context) error {
	for _, sessionID := range ae.state.GetSessionIDs(ctx) {
		session, err := ae.state.GetSession(ctx, sessionID)
		if err != nil {
			slog.Warn("Error getting session local state", "sessionID", sessionID, "error", err)
		}
		slog.Info("Engine reconcile", "session", session)
		err = ae.reconcileSession(ctx, session)
		if err != nil {
			slog.Warn("Error handling session", "sessionID", sessionID, "error", err, "session", session)
		}
	}
	return nil
}

func (ae *ApptainerEngine) reconcileSession(ctx context.Context, session state.LocalSession) error {
	// TODO: container handling
	err := ae.reconcileSessionContainer(ctx, session)
	if err != nil {
		return err
	}

	// wstunnel handling
	wstunnelSecret := session.Spec.Secrets["RENKU_WSTUNNEL_SECRET"]
	if wstunnelSecret == "" {
		slog.Info("Engine: wstunnel secret not found, skipping", "sessionID", session.ID)
	} else {
		ae.handlesMutex.Lock()
		defer ae.handlesMutex.Unlock()
		handle, found := ae.handles[session.ID]
		if !found || handle.wstunnelCmd == nil {
			// Create a new handle for wstunnel
			wstunnelURLStr := fmt.Sprintf("wss://%s:443", session.Spec.URL.Hostname())
			wstunnelPathPrefix := fmt.Sprintf("%s/__amalthea__/tunnel", strings.TrimSuffix(session.Spec.URL.EscapedPath(), "/"))
			wstunnelAuth := fmt.Sprintf("Authorization: Bearer %s", wstunnelSecret)
			handle.wstunnelCmd = exec.CommandContext(context.Background(), wstunnel, "client", "-R", "tcp://0.0.0.0:8888:localhost:9999",
				wstunnelURLStr, "-P", wstunnelPathPrefix,
				"-H", wstunnelAuth, "--tls-verify-certificate",
			)
			slog.Info("wstunnel command", "command", handle.wstunnelCmd.String())
			ae.handles[session.ID] = handle

			// TODO: show logs?
			// TODO: detach?
			go func() {
				out, err := handle.wstunnelCmd.Output()
				slog.Info("wstunnel", "out", string(out), "err", err)
			}()

			// err := handle.wstunnelCmd.Start()
			// if err != nil {
			// 	return err
			// }
			// err = handle.wstunnelCmd.Process.Release()
			// if err != nil {
			// 	return err
			// }
		}
	}

	return nil
}

func (ae *ApptainerEngine) reconcileSessionContainer(ctx context.Context, session state.LocalSession) error {
	ae.handlesMutex.Lock()
	defer ae.handlesMutex.Unlock()
	handle, found := ae.handles[session.ID]
	if !found || handle.apptainerInstance == "" {
		handle.apptainerInstance = fmt.Sprintf("renku-%s", session.ID)
		ae.handles[session.ID] = handle

		// TODO: pull as a separate step?
		apptainerImage := fmt.Sprintf("docker://%s", session.Spec.Image)
		cmd := exec.Command(apptainer, "instance", "run",
			"--writable-tmpfs", "--contain",
			"--bind", "/home/flora/test:/workspace", // TODO
			"--env", "RENKU_SESSION_PORT=9999",
			"--env", fmt.Sprintf("RENKU_BASE_URL_PATH=%s", session.Spec.URL.EscapedPath()),
			"--no-init", "--no-eval",
			apptainerImage,
			handle.apptainerInstance,
		)
		cmd.Env = append(cmd.Env, "APPTAINER_TMPDIR=/home/flora/tmp") // TODO
		slog.Info("apptainer command", "command", cmd.String())

		// TODO: show logs?
		// TODO: detach?
		go func() {
			out, err := cmd.Output()
			slog.Info("apptainer", "out", string(out), "err", err)
		}()
	}
	return nil
}
