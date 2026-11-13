---
title: MCP Server
---

The Renku MCP server exposes platform operations as typed tools that AI agents can call
via the [Model Context Protocol](https://modelcontextprotocol.io). Once enabled, users can
connect AI assistants (Claude Code, pi, Claude.ai) to their Renku deployment and manage
projects, sessions, data connectors, and compute resources conversationally.

## Enabling the MCP Server

The MCP server is disabled by default. Add the following to your Helm values to enable it:

```yaml
mcpServer:
  enabled: true
  resources:
    requests:
      memory: 64Mi
      cpu: 50m
    limits:
      memory: 256Mi
```

The server will be available at `https://<your-deployment>/mcp`. It calls the Renku API
through the gateway inside the cluster, with the access token of the user.

Other settings, with their defaults:

```yaml
mcpServer:
  logLevel: info
  # Log the arguments of every tool call. Credentials are redacted.
  logToolArguments: false
  # Rate limit of tool calls per user
  rateLimit:
    enabled: true
    callsPerMinute: 60
    burst: 20
  oauth:
    # Fetch the realm signing keys from here instead of the public Keycloak URL.
    jwksUrl: ""
  ingress:
    # Added to the annotations of `ingress.annotations`
    annotations:
      nginx.ingress.kubernetes.io/proxy-read-timeout: "120"
      nginx.ingress.kubernetes.io/proxy-buffering: "off"
```

Tools that wait for a session, build, or app to be ready return after 50 seconds at most, and
responses are streamed as server-sent events. If you do not use ingress-nginx, make sure your
ingress controller does not time out such requests sooner and does not buffer the responses.

## Keycloak Client

Enabling the MCP server requires a `renku-mcp` Keycloak client. This is created automatically
by the Renku realm setup job — no manual Keycloak configuration is needed.

The client is a public OAuth 2.0 client using authorization code flow with PKCE (S256), and users
are asked to consent before an MCP client gets access. Its access tokens carry the `renku-mcp`
audience, which the MCP server requires. By default, it accepts these redirect URIs:

- `https://claude.ai/api/mcp/auth_callback` — Claude.ai
- `http://localhost:8484`, `http://127.0.0.1:8484` and their `/callback` variants — Claude Code,
  Codex and pi, set up with callback port 8484 as in the user documentation

To allow other MCP clients or another callback port, set the whole list in
`mcpServer.oauth.redirectUris`, for example:

```yaml
mcpServer:
  oauth:
    redirectUris:
      - https://claude.ai/api/mcp/auth_callback
      - http://localhost:9090
      - http://localhost:9090/callback
      - http://127.0.0.1:9090
      - http://127.0.0.1:9090/callback
```

The realm setup job updates the `renku-mcp` client on the next deployment. If you change the port,
tell your users, since the client setup in the user documentation uses port 8484.

## Security

- **Token validation.** The MCP server validates the signature, issuer, and expiry of every
  access token, and requires the `renku-mcp` audience and authorized party, so tokens issued
  to other Renku clients are rejected. It forwards the token to the Renku API, which checks it
  again.
- **Admin accounts are blocked.** The server rejects requests of users with the Renku
  administrator role (`renku-admin`), so their privileges cannot be used through an AI agent.
- **Credentials** (S3 keys, passwords) are never accepted as tool parameters. Users must
  add storage credentials through the Renku UI.
- **Rate limiting.** Tool calls are rate limited per user, see `mcpServer.rateLimit`.

## OAuth Discovery

When a client connects without a token, the server answers with a `WWW-Authenticate` challenge
that points to its protected resource metadata (RFC 9728), served at
`/.well-known/oauth-protected-resource/mcp`. That document names the Renku Keycloak realm as the
authorization server, and clients fetch its metadata from Keycloak directly.

Standards-compliant MCP clients (Claude.ai, Claude Code) discover these automatically and
complete the OAuth flow without any manual token configuration.

## Health Checks

The server answers `/healthz` once it runs and `/readyz` once it can fetch the signing keys of
the Keycloak realm. The Helm chart uses them for the liveness and readiness probes.
