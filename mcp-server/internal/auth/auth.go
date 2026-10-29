// Package auth makes the MCP server an OAuth 2.0 resource server: it validates
// the Keycloak access tokens presented by MCP clients and advertises the
// authorization server through RFC 9728 protected resource metadata.
package auth

import (
	"context"
	"errors"
	"fmt"
	"io"
	"log/slog"
	"net/http"
	"net/url"
	"path"
	"slices"
	"strings"
	"sync/atomic"

	"github.com/coreos/go-oidc/v3/oidc"
	"github.com/go-jose/go-jose/v4"
	"github.com/go-jose/go-jose/v4/jwt"
	sdkauth "github.com/modelcontextprotocol/go-sdk/auth"
	"github.com/modelcontextprotocol/go-sdk/oauthex"
)

// wellKnownPath is the RFC 9728 well-known prefix of the protected resource metadata.
const wellKnownPath = "/.well-known/oauth-protected-resource"

// tokenKey is the key of the raw access token in the Extra field of the token info.
const tokenKey = "token"

// adminKey is the key of the admin flag in the Extra field of the token info.
const adminKey = "admin"

// adminRole is the Keycloak realm role of Renku administrators.
const adminRole = "renku-admin"

// Config configures token validation.
type Config struct {
	// Issuer is the Keycloak realm URL that must match the iss claim, e.g. https://renku.example.org/auth/realms/Renku.
	Issuer string
	// JWKSURL is where the realm signing keys are fetched from. It may point to an
	// in-cluster Keycloak address. Defaults to the Keycloak certs endpoint of Issuer.
	JWKSURL string
	// Audience must be contained in the aud claim.
	Audience string
	// AuthorizedParty must match the azp claim, i.e. the Keycloak client the token was issued to.
	AuthorizedParty string
	// ResourceURL is the public URL of the MCP endpoint, e.g. https://renku.example.org/mcp.
	ResourceURL string
}

// redacted replaces an access token wherever it would be printed.
const redacted = "[REDACTED]"

// AccessToken is a raw access token. It is redacted when formatted, logged or encoded, so it
// cannot leak by accident; string(token) is needed to use its value.
type AccessToken string

// Format implements fmt.Formatter for all verbs.
func (AccessToken) Format(f fmt.State, _ rune) {
	_, _ = io.WriteString(f, redacted)
}

// LogValue implements slog.LogValuer.
func (AccessToken) LogValue() slog.Value {
	return slog.StringValue(redacted)
}

// MarshalText implements encoding.TextMarshaler, which encoding/json uses as well.
func (AccessToken) MarshalText() ([]byte, error) {
	return []byte(redacted), nil
}

// Principal is the authenticated caller.
type Principal struct {
	// Subject is the Keycloak user ID (sub claim).
	Subject string
	// Token is the raw access token, forwarded to the Renku API.
	Token AccessToken
}

type contextKey struct{}

// WithPrincipal returns a context carrying p.
func WithPrincipal(ctx context.Context, p Principal) context.Context {
	return context.WithValue(ctx, contextKey{}, p)
}

// PrincipalFrom returns the principal stored in ctx, if any.
func PrincipalFrom(ctx context.Context) (Principal, bool) {
	p, ok := ctx.Value(contextKey{}).(Principal)
	return p, ok
}

// Authenticator validates bearer tokens.
type Authenticator struct {
	cfg      Config
	resource *url.URL
	keySet   *oidc.RemoteKeySet
	verifier *oidc.IDTokenVerifier
	logger   *slog.Logger
	ready    atomic.Bool
}

// New creates an Authenticator. Signing keys are fetched lazily and cached.
func New(ctx context.Context, cfg Config, logger *slog.Logger) (*Authenticator, error) {
	if cfg.Issuer == "" || cfg.Audience == "" || cfg.AuthorizedParty == "" || cfg.ResourceURL == "" {
		return nil, errors.New("issuer, audience, authorized party and resource URL are required")
	}
	if cfg.JWKSURL == "" {
		cfg.JWKSURL = strings.TrimSuffix(cfg.Issuer, "/") + "/protocol/openid-connect/certs"
	}
	resource, err := url.Parse(cfg.ResourceURL)
	if err != nil || !resource.IsAbs() {
		return nil, fmt.Errorf("resource URL %q must be an absolute URL", cfg.ResourceURL)
	}
	keySet := oidc.NewRemoteKeySet(ctx, cfg.JWKSURL)
	return &Authenticator{
		cfg:      cfg,
		resource: resource,
		keySet:   keySet,
		// The verifier is meant for ID tokens, but Keycloak access tokens are JWTs with the
		// same iss/aud/exp semantics, which is all that is checked here besides azp.
		verifier: oidc.NewVerifier(cfg.Issuer, keySet, &oidc.Config{ClientID: cfg.Audience}),
		logger:   logger,
	}, nil
}

type claims struct {
	AuthorizedParty string `json:"azp"`
	RealmAccess     struct {
		Roles []string `json:"roles"`
	} `json:"realm_access"`
}

// isAdmin reports whether the claims grant the Renku administrator role.
func (c claims) isAdmin() bool {
	return slices.Contains(c.RealmAccess.Roles, adminRole)
}

// IsAdminToken reports whether a raw access token grants the Renku administrator role. The
// signature is not verified, which is enough to refuse a token: the role cannot be removed
// from a genuine token without invalidating it, and the Renku API rejects forged tokens.
func IsAdminToken(raw string) (bool, error) {
	token, err := jwt.ParseSigned(raw, []jose.SignatureAlgorithm{
		jose.RS256, jose.RS384, jose.RS512, jose.PS256, jose.PS384, jose.PS512,
		jose.ES256, jose.ES384, jose.ES512, jose.EdDSA,
	})
	if err != nil {
		return false, fmt.Errorf("parsing access token: %w", err)
	}
	var c claims
	if err := token.UnsafeClaimsWithoutVerification(&c); err != nil {
		return false, fmt.Errorf("decoding claims: %w", err)
	}
	return c.isAdmin(), nil
}

// verify validates a raw access token and returns it with its claims.
func (a *Authenticator) verify(ctx context.Context, raw string) (*oidc.IDToken, claims, error) {
	var c claims
	token, err := a.verifier.Verify(ctx, raw)
	if err != nil {
		return nil, c, err
	}
	a.ready.Store(true)
	if err := token.Claims(&c); err != nil {
		return nil, c, fmt.Errorf("decoding claims: %w", err)
	}
	if c.AuthorizedParty != a.cfg.AuthorizedParty {
		return nil, c, fmt.Errorf("token was issued to client %q, expected %q", c.AuthorizedParty, a.cfg.AuthorizedParty)
	}
	if token.Subject == "" {
		return nil, c, errors.New("token has no subject")
	}
	return token, c, nil
}

// verifyToken is the token verifier of Middleware. Why a token was rejected is only
// logged, the client gets a generic invalid token error.
func (a *Authenticator) verifyToken(ctx context.Context, raw string, _ *http.Request) (*sdkauth.TokenInfo, error) {
	token, c, err := a.verify(ctx, raw)
	if err != nil {
		a.logger.Info("rejected access token", "error", err)
		return nil, sdkauth.ErrInvalidToken
	}
	return &sdkauth.TokenInfo{
		// The SDK binds MCP sessions to UserID, so another user cannot take them over.
		UserID:     token.Subject,
		Expiration: token.Expiry,
		Extra:      map[string]any{tokenKey: AccessToken(raw), adminKey: c.isAdmin()},
	}, nil
}

// PrincipalFromTokenInfo returns the principal of the token info stored by Middleware.
func PrincipalFromTokenInfo(info *sdkauth.TokenInfo) (Principal, bool) {
	if info == nil {
		return Principal{}, false
	}
	token, _ := info.Extra[tokenKey].(AccessToken)
	return Principal{Subject: info.UserID, Token: token}, true
}

// MetadataPath is the path the protected resource metadata is served at.
func (a *Authenticator) MetadataPath() string {
	if p := strings.Trim(a.resource.Path, "/"); p != "" {
		return path.Join(wellKnownPath, p)
	}
	return wellKnownPath
}

// MetadataHandler serves the RFC 9728 protected resource metadata.
func (a *Authenticator) MetadataHandler() http.Handler {
	return sdkauth.ProtectedResourceMetadataHandler(&oauthex.ProtectedResourceMetadata{
		Resource:               a.cfg.ResourceURL,
		AuthorizationServers:   []string{a.cfg.Issuer},
		BearerMethodsSupported: []string{"header"},
		ResourceName:           "Renku",
	})
}

// metadataURL is the absolute URL of the protected resource metadata, used in WWW-Authenticate.
func (a *Authenticator) metadataURL() string {
	u := *a.resource
	u.Path, u.RawQuery, u.Fragment = a.MetadataPath(), "", ""
	return u.String()
}

// Middleware rejects requests without a valid bearer token with a challenge pointing to the
// protected resource metadata. The MCP SDK passes the token info it stores in the request
// context on to the tool handlers, see PrincipalFromTokenInfo.
func (a *Authenticator) Middleware(next http.Handler) http.Handler {
	return sdkauth.RequireBearerToken(a.verifyToken, &sdkauth.RequireBearerTokenOptions{
		ResourceMetadataURL: a.metadataURL(),
	})(rejectAdmins(next, a.logger))
}

// rejectAdmins rejects requests of Renku administrators, so their elevated privileges cannot be
// exercised through an MCP client. This is a 403 without a challenge rather than a 401, since
// authenticating again would yield a token with the same role.
func rejectAdmins(next http.Handler, logger *slog.Logger) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		info := sdkauth.TokenInfoFromContext(r.Context())
		if admin, _ := info.Extra[adminKey].(bool); admin {
			logger.Info("rejected access token of an administrator", "subject", info.UserID)
			http.Error(w, "administrators cannot use the MCP server", http.StatusForbidden)
			return
		}
		next.ServeHTTP(w, r)
	})
}

// Ready reports whether the signing keys can be fetched, so the server can validate tokens.
func (a *Authenticator) Ready(ctx context.Context) bool {
	if a.ready.Load() {
		return true
	}
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, a.cfg.JWKSURL, nil)
	if err != nil {
		return false
	}
	res, err := http.DefaultClient.Do(req)
	if err != nil {
		a.logger.Warn("signing keys are not reachable", "url", a.cfg.JWKSURL, "error", err)
		return false
	}
	defer func() { _ = res.Body.Close() }()
	if res.StatusCode != http.StatusOK {
		a.logger.Warn("signing keys are not reachable", "url", a.cfg.JWKSURL, "status", res.StatusCode)
		return false
	}
	a.ready.Store(true)
	return true
}
