package persistence

import (
	"encoding/json"
	"io"
	"os"
	"sync"

	"github.com/adrg/xdg"
)

const persistenceFileRelativePath = "renku/session_runner/runner_state.json"

type PersistedRunnerState struct {
	RunnerID  string                    `json:"runner_id,omitempty"`
	ServerURL string                    `json:"server_url,omitempty"`
	Auth      *PersistedRunnerStateAuth `json:"auth,omitempty"`
}

type PersistedRunnerStateAuth struct {
	RefreshToken EncodedString `json:"refresh_token,omitempty"`
}

type Persistence struct {
	filePath string
	f        *os.File
	mutex    sync.Mutex
}

func NewPersistence() (p *Persistence, err error) {
	p = &Persistence{
		mutex: sync.Mutex{},
	}
	filepath, err := persistenceFilePath()
	if err != nil {
		return nil, err
	}
	p.filePath = filepath
	file, err := os.OpenFile(filepath, os.O_RDWR|os.O_CREATE, 0600)
	if err != nil {
		return nil, err
	}
	p.f = file
	return p, nil
}

func (p *Persistence) Close() error {
	p.mutex.Lock()
	defer p.mutex.Unlock()
	return p.f.Close()
}

func (p *Persistence) Get() (state PersistedRunnerState, err error) {
	p.mutex.Lock()
	defer p.mutex.Unlock()
	if _, err := p.f.Seek(0, io.SeekStart); err != nil {
		return state, err
	}
	raw, err := io.ReadAll(p.f)
	if err != nil {
		return state, err
	}
	if err := json.Unmarshal(raw, &state); err != nil {
		return state, err
	}
	return state, nil
}

func (p *Persistence) Set(state PersistedRunnerState) error {
	p.mutex.Lock()
	defer p.mutex.Unlock()
	raw, err := json.Marshal(state)
	if err != nil {
		return err
	}
	if err := p.f.Truncate(0); err != nil {
		return err
	}
	if _, err := p.f.Seek(0, io.SeekStart); err != nil {
		return err
	}
	if _, err := p.f.Write(raw); err != nil {
		return err
	}
	return p.f.Sync()
}

func persistenceFilePath() (string, error) {
	return xdg.DataFile(persistenceFileRelativePath)
}
