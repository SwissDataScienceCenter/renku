package runner

import (
	"errors"
	"fmt"
	"io"
	"log/slog"
	"os"
	"strconv"
	"strings"
	"syscall"

	"github.com/adrg/xdg"
)

const lockFileRelativePath = "renku/session_runner/runner.pid"

var ErrAlreadyRunning = errors.New("a runner is already running")

// lock tries to aquire a PID file lock for the runner
func (r *Runner) lock() error {
	lockFile, err := lockFilePath()
	if err != nil {
		return err
	}

	file, err := os.OpenFile(lockFile, os.O_RDWR|os.O_CREATE|os.O_EXCL, 0600)
	if err != nil && errors.Is(err, os.ErrExist) {
		return r.retryLock(lockFile)
	}
	if err != nil {
		return err
	}
	defer file.Close()

	pid := os.Getpid()
	_, err = io.WriteString(file, fmt.Sprintf("%d\n", pid))
	if err != nil {
		return err
	}

	return nil
}

func (r *Runner) retryLock(lockFile string) error {
	file, err := os.Open(lockFile)
	if err != nil {
		return err
	}
	defer file.Close()

	rawPid, err := io.ReadAll(file)
	if err != nil {
		return err
	}

	pid, err := strconv.ParseInt(strings.TrimSpace(string(rawPid)), 10, 0)
	if err != nil {
		pid = 0
	}

	if pid > 0 {
		proc, err := os.FindProcess(int(pid))
		if err != nil {
			return err
		}

		if err := proc.Signal(syscall.Signal(0)); err == nil {
			return fmt.Errorf("%w, pid = %d", ErrAlreadyRunning, pid)
		}
	}

	fileW, err := os.OpenFile(lockFile, os.O_RDWR|os.O_CREATE|os.O_TRUNC, 0600)
	if err != nil {
		return err
	}
	defer fileW.Close()

	_, err = io.WriteString(fileW, fmt.Sprintf("%d\n", os.Getpid()))
	if err != nil {
		return err
	}

	slog.Warn("runner may have crashed", "pid", pid)

	return nil
}

// unlock releases the PID file lock for the runner
func (r *Runner) unlock() error {
	lockFile, err := lockFilePath()
	if err != nil {
		return err
	}

	if err := os.Remove(lockFile); err != nil {
		return err
	}

	return nil
}

func lockFilePath() (string, error) {
	return xdg.StateFile(lockFileRelativePath)
}
