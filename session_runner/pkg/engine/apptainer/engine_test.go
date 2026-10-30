package apptainerEngine

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestFindSessionPort(t *testing.T) {
	port, err := findSessionPort()
	require.NoError(t, err)
	assert.NotZero(t, port, "returned port is not valid")
}
