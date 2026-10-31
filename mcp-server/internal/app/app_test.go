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
	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
)

const issuer = "https://renku.example.org/auth/realms/Renku"

const projectID = "01J0000000000000000000000A"

type testEnv struct {
	mcpURL string
	server *mcp.Server
	key    *rsa.PrivateKey
	// gatewayAuth is the Authorization header the fake gateway received last.
	gatewayAuth string
	// gatewayRealIP is the X-Real-IP header the fake gateway received last.
	gatewayRealIP string
	// gatewayRequestID is the X-Request-Id header the fake gateway received last.
	gatewayRequestID string
	// gatewayDeletes counts the deletes of the session launcher with ID projectID.
	gatewayDeletes int
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
		env.gatewayRequestID = r.Header.Get("X-Request-Id")
		if r.Method == http.MethodDelete && r.URL.Path == "/api/data/session_launchers/"+projectID {
			env.gatewayDeletes++
			w.WriteHeader(http.StatusNoContent)
			return
		}
		if r.URL.Path != "/api/data/projects/"+projectID {
			w.WriteHeader(http.StatusNotFound)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write([]byte(`{"id": "` + projectID + `", "name": "climate", "namespace": "my-group", "slug": "climate",
			"visibility": "public", "created_by": "user-1", "creation_date": "2023-11-01T17:32:28Z"}`))
	}))
	t.Cleanup(gateway.Close)

	logger := slog.New(slog.NewTextHandler(io.Discard, nil))
	s, err := NewMCPServer(Options{APIURL: gateway.URL + "/api/data", Version: "test", Logger: logger})
	if err != nil {
		t.Fatal(err)
	}
	env.server = s
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

	httpClient := &http.Client{Transport: headerTransport{"Authorization": "Bearer " + token, "X-Real-IP": "203.0.113.7", "X-Request-Id": "req-1"}}
	c, err := mcp.NewClient(&mcp.Implementation{Name: "test", Version: "1"}, nil).
		Connect(ctx, &mcp.StreamableClientTransport{Endpoint: env.mcpURL, HTTPClient: httpClient, DisableStandaloneSSE: true}, nil)
	if err != nil {
		t.Fatal(err)
	}
	defer func() { _ = c.Close() }()
	if name := c.InitializeResult().ServerInfo.Name; name != "renku" {
		t.Errorf("server name = %q", name)
	}

	toolsRes, err := c.ListTools(ctx, nil)
	if err != nil {
		t.Fatal(err)
	}
	if !slices.ContainsFunc(toolsRes.Tools, func(tool *mcp.Tool) bool { return tool.Name == "get_project" }) {
		t.Errorf("tools/list does not contain get_project: %v", toolsRes.Tools)
	}

	assertGetProject(t, c)
	if env.gatewayAuth != "Bearer "+token {
		t.Errorf("gateway received Authorization %q, want the caller's token", env.gatewayAuth)
	}
	if env.gatewayRealIP != "203.0.113.7" {
		t.Errorf("gateway received X-Real-IP %q, want the end user's address", env.gatewayRealIP)
	}
	if env.gatewayRequestID != "req-1" {
		t.Errorf("gateway received X-Request-Id %q, want the ID of the HTTP request", env.gatewayRequestID)
	}
}

// TestDeleteIsConfirmedOverStatelessHTTP covers asking the user without a session: the tool returns
// an input request and the client calls it again with the answer.
func TestDeleteIsConfirmedOverStatelessHTTP(t *testing.T) {
	env := newTestEnv(t)
	ctx := context.Background()
	var asked int
	client := mcp.NewClient(&mcp.Implementation{Name: "test", Version: "1"}, &mcp.ClientOptions{
		ElicitationHandler: func(context.Context, *mcp.ElicitRequest) (*mcp.ElicitResult, error) {
			asked++
			return &mcp.ElicitResult{Action: "accept", Content: map[string]any{"confirm": true}}, nil
		},
	})
	httpClient := &http.Client{Transport: headerTransport{"Authorization": "Bearer " + env.token(t)}}
	c, err := client.Connect(ctx, &mcp.StreamableClientTransport{Endpoint: env.mcpURL, HTTPClient: httpClient, DisableStandaloneSSE: true}, nil)
	if err != nil {
		t.Fatal(err)
	}
	defer func() { _ = c.Close() }()
	res, err := c.CallTool(ctx, &mcp.CallToolParams{Name: "delete_session_launcher", Arguments: map[string]any{"launcher_id": projectID}})
	if err != nil {
		t.Fatal(err)
	}
	if res.IsError || asked != 1 || env.gatewayDeletes != 1 {
		t.Errorf("result %+v after asking %d times and deleting %d times", res, asked, env.gatewayDeletes)
	}
}

// TestOldClientIsNotAskedOverStatelessHTTP covers clients older than protocol 2026-07-28, which a
// stateless server cannot send an elicitation request to. The delete goes ahead without asking.
func TestOldClientIsNotAskedOverStatelessHTTP(t *testing.T) {
	env := newTestEnv(t)
	body := `{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"delete_session_launcher","arguments":{"launcher_id":"` + projectID + `"}}}`
	req, _ := http.NewRequest(http.MethodPost, env.mcpURL, strings.NewReader(body))
	req.Header.Set("Authorization", "Bearer "+env.token(t))
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Accept", "application/json, text/event-stream")
	req.Header.Set("MCP-Protocol-Version", "2025-06-18")
	res, err := http.DefaultClient.Do(req)
	if err != nil {
		t.Fatal(err)
	}
	defer func() { _ = res.Body.Close() }()
	raw, _ := io.ReadAll(res.Body)
	if !strings.Contains(string(raw), "Deleted session launcher") || env.gatewayDeletes != 1 {
		t.Errorf("status %d, response %s after deleting %d times", res.StatusCode, raw, env.gatewayDeletes)
	}
}

// TestStdioUsesPrincipalFromContext covers the stdio mode of cmd/mcp-server, where the
// principal is stored in the context passed to Run instead of coming from a request.
func TestStdioUsesPrincipalFromContext(t *testing.T) {
	env := newTestEnv(t)
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()
	serverTransport, clientTransport := mcp.NewInMemoryTransports()
	go func() {
		_ = env.server.Run(auth.WithPrincipal(ctx, auth.Principal{Subject: "stdio", Token: "local-token"}), serverTransport)
	}()
	c, err := mcp.NewClient(&mcp.Implementation{Name: "test", Version: "1"}, nil).Connect(ctx, clientTransport, nil)
	if err != nil {
		t.Fatal(err)
	}
	defer func() { _ = c.Close() }()

	assertGetProject(t, c)
	if env.gatewayAuth != "Bearer local-token" {
		t.Errorf("gateway received Authorization %q, want the token of the context", env.gatewayAuth)
	}
}

func assertGetProject(t *testing.T, c *mcp.ClientSession) {
	t.Helper()
	res, err := c.CallTool(context.Background(), &mcp.CallToolParams{Name: "get_project", Arguments: map[string]any{"project_id": projectID}})
	if err != nil {
		t.Fatal(err)
	}
	if res.IsError || len(res.Content) != 1 || !strings.Contains(res.Content[0].(*mcp.TextContent).Text, `"name":"climate"`) {
		t.Errorf("unexpected result %+v", res)
	}
}

// headerTransport adds fixed headers to every request.
type headerTransport map[string]string

func (h headerTransport) RoundTrip(r *http.Request) (*http.Response, error) {
	r = r.Clone(r.Context())
	for k, v := range h {
		r.Header.Set(k, v)
	}
	return http.DefaultTransport.RoundTrip(r)
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

func TestRateLimitMustBePositiveOnlyIfEnabled(t *testing.T) {
	opts := Options{APIURL: "http://renku-gateway/api/data", Logger: slog.New(slog.DiscardHandler)}
	if _, err := NewMCPServer(opts); err != nil {
		t.Errorf("disabled rate limit without limits: %v", err)
	}
	opts.RateLimit = true
	if _, err := NewMCPServer(opts); err == nil {
		t.Error("enabled rate limit without limits was accepted")
	}
	opts.ToolCallsPerMinute, opts.ToolCallBurst = 60, 20
	if _, err := NewMCPServer(opts); err != nil {
		t.Errorf("enabled rate limit with limits: %v", err)
	}
}
