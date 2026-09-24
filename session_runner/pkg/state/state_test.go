package state

import (
	"crypto/rand"
	"fmt"
	"net/url"
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestUpdateStatus(t *testing.T) {
	state, err := NewLocalSessionState()
	require.NoError(t, err)

	sessionID := fmt.Sprintf("my-session-%s", rand.Text())
	spec := LocalSessionSpec{
		Image:   "image:test",
		URL:     &url.URL{Path: "/test"},
		Secrets: map[string]string{},
	}
	session, err := state.UpsertSession(t.Context(), sessionID, spec)
	require.NoError(t, err)
	assert.Equal(t, "", session.Status)

	session, err = state.UpdateSessionStatus(t.Context(), sessionID, "hello")
	require.NoError(t, err)
	assert.Equal(t, "hello", session.Status)
}
