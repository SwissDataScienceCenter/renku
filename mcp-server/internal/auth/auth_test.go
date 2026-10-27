package auth

import (
	"context"
	"crypto/rand"
	"crypto/rsa"
	"encoding/json"
	"io"
	"log/slog"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"github.com/go-jose/go-jose/v4"
	"github.com/go-jose/go-jose/v4/jwt"
)

const (
	testIssuer   = "https://renku.example.org/auth/realms/Renku"
	testResource = "https://renku.example.org/mcp"
)

type testKeycloak struct {
	key    *rsa.PrivateKey
	server *httptest.Server
}

func newTestKeycloak(t *testing.T) *testKeycloak {
	t.Helper()
	key, err := rsa.GenerateKey(rand.Reader, 2048)
	if err != nil {
		t.Fatal(err)
	}
	jwks := jose.JSONWebKeySet{Keys: []jose.JSONWebKey{{Key: &key.PublicKey, KeyID: "test", Algorithm: "RS256", Use: "sig"}}}
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		_ = json.NewEncoder(w).Encode(jwks)
	}))
	t.Cleanup(server.Close)
	return &testKeycloak{key: key, server: server}
}

func (k *testKeycloak) token(t *testing.T, claims map[string]any) string {
	return signToken(t, k.key, claims)
}

func signToken(t *testing.T, key *rsa.PrivateKey, claims map[string]any) string {
	t.Helper()
	signer, err := jose.NewSigner(jose.SigningKey{Algorithm: jose.RS256, Key: key}, (&jose.SignerOptions{}).WithHeader("kid", "test"))
	if err != nil {
		t.Fatal(err)
	}
	raw, err := jwt.Signed(signer).Claims(claims).Serialize()
	if err != nil {
		t.Fatal(err)
	}
	return raw
}

func validClaims() map[string]any {
	return map[string]any{
		"iss": testIssuer,
		"aud": []string{"renku", "account"},
		"azp": "renku-mcp",
		"sub": "user-1",
		"exp": time.Now().Add(5 * time.Minute).Unix(),
		"iat": time.Now().Unix(),
	}
}

func newTestAuthenticator(t *testing.T, kc *testKeycloak) *Authenticator {
	t.Helper()
	a, err := New(context.Background(), Config{
		Issuer:          testIssuer,
		JWKSURL:         kc.server.URL,
		Audience:        "renku",
		AuthorizedParty: "renku-mcp",
		ResourceURL:     testResource,
	}, slog.New(slog.NewTextHandler(io.Discard, nil)))
	if err != nil {
		t.Fatal(err)
	}
	return a
}

func TestMiddleware(t *testing.T) {
	kc := newTestKeycloak(t)
	a := newTestAuthenticator(t, kc)
	otherKey, err := rsa.GenerateKey(rand.Reader, 2048)
	if err != nil {
		t.Fatal(err)
	}

	with := func(key string, value any) map[string]any {
		c := validClaims()
		c[key] = value
		return c
	}
	tests := []struct {
		name          string
		header        string
		wantStatus    int
		wantErrorCode bool
	}{
		{"valid token", "Bearer " + kc.token(t, validClaims()), http.StatusOK, false},
		{"no header", "", http.StatusUnauthorized, false},
		{"not bearer", "Basic dXNlcjpwYXNz", http.StatusUnauthorized, false},
		{"wrong issuer", "Bearer " + kc.token(t, with("iss", "https://evil.example.org/auth/realms/Renku")), http.StatusUnauthorized, true},
		{"wrong audience", "Bearer " + kc.token(t, with("aud", []string{"account"})), http.StatusUnauthorized, true},
		{"wrong authorized party", "Bearer " + kc.token(t, with("azp", "renku-cli")), http.StatusUnauthorized, true},
		{"expired", "Bearer " + kc.token(t, with("exp", time.Now().Add(-time.Minute).Unix())), http.StatusUnauthorized, true},
		{"bad signature", "Bearer " + signToken(t, otherKey, validClaims()), http.StatusUnauthorized, true},
		{"garbage", "Bearer not-a-jwt", http.StatusUnauthorized, true},
	}

	var seen Principal
	handler := a.Middleware(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		seen, _ = PrincipalFrom(r.Context())
		w.WriteHeader(http.StatusOK)
	}))
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			seen = Principal{}
			req := httptest.NewRequest(http.MethodPost, "/mcp", nil)
			if tt.header != "" {
				req.Header.Set("Authorization", tt.header)
			}
			rec := httptest.NewRecorder()
			handler.ServeHTTP(rec, req)

			if rec.Code != tt.wantStatus {
				t.Fatalf("status = %d, want %d", rec.Code, tt.wantStatus)
			}
			if tt.wantStatus == http.StatusOK {
				if seen.Subject != "user-1" || seen.Token == "" {
					t.Errorf("principal not set: %+v", seen)
				}
				return
			}
			challenge := rec.Header().Get("WWW-Authenticate")
			want := `Bearer resource_metadata="https://renku.example.org/.well-known/oauth-protected-resource/mcp"`
			if !strings.HasPrefix(challenge, want) {
				t.Errorf("WWW-Authenticate = %q, want prefix %q", challenge, want)
			}
			if got := strings.Contains(challenge, `error="invalid_token"`); got != tt.wantErrorCode {
				t.Errorf("WWW-Authenticate = %q, invalid_token expected: %v", challenge, tt.wantErrorCode)
			}
		})
	}
}

func TestMetadata(t *testing.T) {
	a := newTestAuthenticator(t, newTestKeycloak(t))
	if a.MetadataPath() != "/.well-known/oauth-protected-resource/mcp" {
		t.Fatalf("metadata path = %s", a.MetadataPath())
	}
	rec := httptest.NewRecorder()
	a.MetadataHandler().ServeHTTP(rec, httptest.NewRequest(http.MethodGet, a.MetadataPath(), nil))
	if rec.Code != http.StatusOK {
		t.Fatalf("status = %d", rec.Code)
	}
	var metadata struct {
		Resource             string   `json:"resource"`
		AuthorizationServers []string `json:"authorization_servers"`
	}
	if err := json.Unmarshal(rec.Body.Bytes(), &metadata); err != nil {
		t.Fatal(err)
	}
	if metadata.Resource != testResource || len(metadata.AuthorizationServers) != 1 || metadata.AuthorizationServers[0] != testIssuer {
		t.Errorf("unexpected metadata: %s", rec.Body.String())
	}
}

func TestReady(t *testing.T) {
	kc := newTestKeycloak(t)
	a := newTestAuthenticator(t, kc)
	if !a.Ready(context.Background()) {
		t.Error("expected ready when the JWKS is reachable")
	}

	kc.server.Close()
	b := newTestAuthenticator(t, kc)
	if b.Ready(context.Background()) {
		t.Error("expected not ready when the JWKS is unreachable")
	}
}
