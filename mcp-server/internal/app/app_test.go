package app

import (
	"context"
	"crypto/rand"
	"crypto/rsa"
	"encoding/json"
	"io"
	"log/slog"
	"net/http"
	"net/http/httptest"
	"slices"
	"strings"
	"testing"
	"time"

	"github.com/go-jose/go-jose/v4"
	"github.com/go-jose/go-jose/v4/jwt"
	"github.com/mark3labs/mcp-go/client"
	"github.com/mark3labs/mcp-go/client/transport"
	"github.com/mark3labs/mcp-go/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/apispec"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/specprep"
)

const issuer = "https://renku.example.org/auth/realms/Renku"

type testEnv struct {
	mcpURL string
	key    *rsa.PrivateKey
	// gatewayAuth is the Authorization header the fake gateway received last.
	gatewayAuth string
	// gatewayRealIP is the X-Real-IP header the fake gateway received last.
	gatewayRealIP string
}

func newTestEnv(t *testing.T) *testEnv {
	t.Helper()
	env := &testEnv{}
	key, err := rsa.GenerateKey(rand.Reader, 2048)
	if err != nil {
		t.Fatal(err)
	}
	env.key = key
	jwks := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, _ *http.Request) {
		_ = json.NewEncoder(w).Encode(jose.JSONWebKeySet{Keys: []jose.JSONWebKey{{Key: &key.PublicKey, KeyID: "k", Algorithm: "RS256", Use: "sig"}}})
	}))
	t.Cleanup(jwks.Close)

	gateway := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		env.gatewayAuth = r.Header.Get("Authorization")
		env.gatewayRealIP = r.Header.Get("X-Real-IP")
		if r.URL.Path != "/api/data/user" {
			w.WriteHeader(http.StatusNotFound)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`{"id": "user-1", "username": "jane"}`))
	}))
	t.Cleanup(gateway.Close)

	logger := slog.New(slog.NewTextHandler(io.Discard, nil))
	s, err := NewMCPServer(Options{APIURL: gateway.URL + "/api/data", Version: "test", Logger: logger})
	if err != nil {
		t.Fatal(err)
	}
	// The resource URL only matters for the metadata, the test server URL is not known before it starts.
	authenticator, err := auth.New(context.Background(), auth.Config{
		Issuer: issuer, JWKSURL: jwks.URL, Audience: "renku", AuthorizedParty: "renku-mcp", ResourceURL: "https://renku.example.org/mcp",
	}, logger)
	if err != nil {
		t.Fatal(err)
	}
	srv := httptest.NewServer(NewHTTPHandler(s, authenticator, logger))
	t.Cleanup(srv.Close)
	env.mcpURL = srv.URL + MCPPath
	return env
}

func (e *testEnv) token(t *testing.T) string {
	t.Helper()
	signer, err := jose.NewSigner(jose.SigningKey{Algorithm: jose.RS256, Key: e.key}, (&jose.SignerOptions{}).WithHeader("kid", "k"))
	if err != nil {
		t.Fatal(err)
	}
	raw, err := jwt.Signed(signer).Claims(map[string]any{
		"iss": issuer, "aud": "renku", "azp": "renku-mcp", "sub": "user-1",
		"exp": time.Now().Add(time.Minute).Unix(),
	}).Serialize()
	if err != nil {
		t.Fatal(err)
	}
	return raw
}

func TestEndToEnd(t *testing.T) {
	env := newTestEnv(t)
	token := env.token(t)
	ctx := context.Background()

	c, err := client.NewStreamableHttpClient(env.mcpURL, transport.WithHTTPHeaders(map[string]string{"Authorization": "Bearer " + token, "X-Real-IP": "203.0.113.7"}))
	if err != nil {
		t.Fatal(err)
	}
	defer func() { _ = c.Close() }()
	if err := c.Start(ctx); err != nil {
		t.Fatal(err)
	}
	initReq := mcp.InitializeRequest{}
	initReq.Params.ProtocolVersion = mcp.LATEST_PROTOCOL_VERSION
	initReq.Params.ClientInfo = mcp.Implementation{Name: "test", Version: "1"}
	initRes, err := c.Initialize(ctx, initReq)
	if err != nil {
		t.Fatal(err)
	}
	if initRes.ServerInfo.Name != "renku" {
		t.Errorf("server name = %q", initRes.ServerInfo.Name)
	}

	toolsRes, err := c.ListTools(ctx, mcp.ListToolsRequest{})
	if err != nil {
		t.Fatal(err)
	}
	var listed []string
	for _, tool := range toolsRes.Tools {
		listed = append(listed, tool.Name)
	}
	cfg, err := specprep.ParseConfig(apispec.ToolsYAML)
	if err != nil {
		t.Fatal(err)
	}
	var allowlisted []string
	for _, tool := range cfg.Tools {
		allowlisted = append(allowlisted, tool.Name)
	}
	slices.Sort(listed)
	slices.Sort(allowlisted)
	if !slices.Equal(listed, allowlisted) {
		t.Errorf("tools/list = %v, want exactly the allowlist %v", listed, allowlisted)
	}

	callReq := mcp.CallToolRequest{}
	callReq.Params.Name = "get_current_user"
	res, err := c.CallTool(ctx, callReq)
	if err != nil {
		t.Fatal(err)
	}
	if res.IsError || len(res.Content) != 1 || !strings.Contains(res.Content[0].(mcp.TextContent).Text, `"username":"jane"`) {
		t.Errorf("unexpected result %+v", res)
	}
	if env.gatewayAuth != "Bearer "+token {
		t.Errorf("gateway received Authorization %q, want the caller's token", env.gatewayAuth)
	}
	if env.gatewayRealIP != "203.0.113.7" {
		t.Errorf("gateway received X-Real-IP %q, want the end user's address", env.gatewayRealIP)
	}
}

func TestUnauthenticatedRequestIsRejectedWithChallenge(t *testing.T) {
	env := newTestEnv(t)
	res, err := http.Post(env.mcpURL, "application/json", strings.NewReader(`{"jsonrpc":"2.0","id":1,"method":"tools/list"}`))
	if err != nil {
		t.Fatal(err)
	}
	defer func() { _ = res.Body.Close() }()
	if res.StatusCode != http.StatusUnauthorized {
		t.Fatalf("status = %d", res.StatusCode)
	}
	if !strings.Contains(res.Header.Get("WWW-Authenticate"), "/.well-known/oauth-protected-resource/mcp") {
		t.Errorf("WWW-Authenticate = %q", res.Header.Get("WWW-Authenticate"))
	}
}

func TestHealthEndpoints(t *testing.T) {
	env := newTestEnv(t)
	base := strings.TrimSuffix(env.mcpURL, MCPPath)
	for _, path := range []string{"/healthz", "/readyz", "/.well-known/oauth-protected-resource/mcp"} {
		res, err := http.Get(base + path)
		if err != nil {
			t.Fatal(err)
		}
		_ = res.Body.Close()
		if res.StatusCode != http.StatusOK {
			t.Errorf("GET %s = %d", path, res.StatusCode)
		}
	}
}
