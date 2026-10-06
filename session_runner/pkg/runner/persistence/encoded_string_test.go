package persistence

import (
	"encoding"
	"encoding/json"
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

var _ encoding.TextMarshaler = (*EncodedString)(nil)
var _ encoding.TextUnmarshaler = (*EncodedString)(nil)

var _ json.Marshaler = (*EncodedString)(nil)
var _ json.Unmarshaler = (*EncodedString)(nil)

func TestEncode(t *testing.T) {
	value := EncodedString("hello")
	data, err := json.Marshal(value)
	require.NoError(t, err)
	assert.Equal(t, "\"aGVsbG8\"", string(data))
}

func TestDecode(t *testing.T) {
	encoded := "\"aGVsbG8\""
	var value EncodedString
	err := json.Unmarshal([]byte(encoded), &value)
	require.NoError(t, err)
	assert.Equal(t, "hello", string(value))
}
