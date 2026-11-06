package renkuapi

import (
	"context"
	"errors"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

func TestResponsesAreCapped(t *testing.T) {
	tests := []struct {
		name     string
		size     int
		tooLarge bool
	}{
		{"at the limit", MaxResponseBytes, false},
		{"over the limit", MaxResponseBytes + 1, true},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			srv := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
				// Not JSON, so the client fails to decode a body it read completely.
				_, _ = w.Write([]byte(strings.Repeat("x", tt.size)))
			}))
			t.Cleanup(srv.Close)
			api, err := NewClients(srv.URL, http.DefaultClient)
			if err != nil {
				t.Fatal(err)
			}
			res, err := api.Users.GetUserWithResponse(context.Background())
			if got := errors.Is(err, ErrResponseTooLarge); got != tt.tooLarge {
				t.Fatalf("error = %v, want too large: %v", err, tt.tooLarge)
			}
			if !tt.tooLarge && (err != nil || len(res.Body) != tt.size) {
				t.Errorf("error = %v, body of %d bytes", err, len(res.Body))
			}
		})
	}
}
