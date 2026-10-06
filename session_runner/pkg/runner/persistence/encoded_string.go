package persistence

import (
	"encoding/base64"
	"encoding/json"
	"fmt"
)

type EncodedString string

func (s EncodedString) MarshalText() (data []byte, err error) {
	encoded := base64.RawURLEncoding.EncodeToString([]byte(s))
	return []byte(encoded), nil
}

func (s *EncodedString) UnmarshalText(data []byte) error {
	decoded, err := base64.RawURLEncoding.DecodeString(string(data))
	if err == nil {
		*s = EncodedString(string(decoded))
	}
	return err
}

func (s EncodedString) MarshalJSON() (data []byte, err error) {
	encoded, err := s.MarshalText()
	return fmt.Appendf(nil, "\"%s\"", encoded), err
}

func (s *EncodedString) UnmarshalJSON(data []byte) error {
	var str string
	err := json.Unmarshal(data, &str)
	if err != nil {
		return err
	}
	return s.UnmarshalText([]byte(str))
}
