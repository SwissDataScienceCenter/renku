package apptainerEngine

import (
	"log/slog"
	"os/exec"
)

const (
	wstunnel = "wstunnel"
)

type ApptainerEngine struct {
}

func NewApptainerEngine() (ae *ApptainerEngine, err error) {
	ae = &ApptainerEngine{}

	wstunnelPath, err := exec.LookPath(wstunnel)
	if err != nil {
		return nil, err
	}
	slog.Info("Found wstunnel CLI", "path", wstunnelPath)

	return ae, nil
}
