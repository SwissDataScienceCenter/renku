package state

import (
	"context"
	"errors"
	"log/slog"
	"net/url"
)

var SessionNotFound = errors.New("session not found")

type LocalSessionState struct {
	// TODO: add persistence layer (local database?)

	sessions map[string]LocalSession
}

func NewLocalSessionState() (state *LocalSessionState, err error) {
	state = &LocalSessionState{
		sessions: make(map[string]LocalSession),
	}
	return state, nil
}

type LocalSession struct {
	ID     string
	Spec   LocalSessionSpec
	Status string
}

type LocalSessionSpec struct {
	// Image to run for the session
	Image string

	// URL of the session
	URL *url.URL

	// Secrets of the session
	Secrets map[string]string
}

func (state *LocalSessionState) GetSessionIDs(ctx context.Context) []string {
	keys := make([]string, 0, len(state.sessions))
	for k := range state.sessions {
		keys = append(keys, k)
	}
	return keys
}

func (state *LocalSessionState) GetSession(ctx context.Context, sessionID string) (session LocalSession, err error) {
	session, found := state.sessions[sessionID]
	if !found {
		return LocalSession{}, SessionNotFound
	}
	return session, nil
}

func (state *LocalSessionState) UpsertSession(ctx context.Context, sessionID string, sessionSpec LocalSessionSpec) (session LocalSession, err error) {
	state.sessions[sessionID] = LocalSession{
		ID:     sessionID,
		Spec:   sessionSpec,
		Status: "",
	}
	slog.Info("Upsert session", "session", state.sessions[sessionID])
	return state.sessions[sessionID], nil
}

func (state *LocalSessionState) DeleteSession(ctx context.Context, sessionID string) error {
	delete(state.sessions, sessionID)
	slog.Info("Delete session", "sessionID", sessionID)
	return nil
}

func (state *LocalSessionState) UpdateSessionStatus(ctx context.Context, sessionID string, status string) (session LocalSession, err error) {
	session, found := state.sessions[sessionID]
	if !found {
		return LocalSession{}, SessionNotFound
	}
	slog.Info("Update session status", "sessionID", sessionID, "oldStatus", session.Status, "newStatus", status)
	session.Status = status
	state.sessions[sessionID] = session
	return state.sessions[sessionID], nil
}
