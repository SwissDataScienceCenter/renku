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
  image:
    tag: "latest" # pin to a specific version in production
  resources:
    requests:
      memory: 256Mi
      cpu: 100m
    limits:
      memory: 512Mi
```

The server will be available at `https://<your-deployment>/mcp`.

## Keycloak Client

Enabling the MCP server requires a `renku-mcp` Keycloak client. This is created automatically
by the Renku realm setup job — no manual Keycloak configuration is needed.

The client is a public OAuth 2.0 client using authorization code flow with PKCE (S256). It
supports the following redirect URIs out of the box:

- `https://claude.ai/api/mcp/auth_callback` — Claude.ai
- `http://localhost:8484` and `/callback` variants — Claude Code and pi

If users need additional redirect URIs (for other MCP clients or custom ports), add them to
`mcpServer.keycloak.extraRedirectUris` in your values:

```yaml
mcpServer:
  keycloak:
    extraRedirectUris: '["http://localhost:9090", "http://localhost:9090/callback"]'
```

## Security

- **Admin accounts are blocked by default.** The server refuses all tool calls if the
  authenticated user has the Renku admin role. This prevents accidental platform-wide
  operations from an AI agent. Set `RENKU_MCP_ALLOW_ADMIN=1` in the deployment environment
  to override.
- **Token validation** is handled by the ingress proxy (same as all other Renku services).
  The MCP server forwards Bearer tokens to the data API; it does not validate JWTs itself.
- **Credentials** (S3 keys, passwords) are never accepted as tool parameters. Users must
  add storage credentials through the Renku UI.

## OAuth Discovery

The MCP server exposes two standard OAuth discovery endpoints:

| Endpoint                                  | RFC      | Purpose                                    |
| ----------------------------------------- | -------- | ------------------------------------------ |
| `/.well-known/oauth-protected-resource`   | RFC 9728 | Points clients to the authorization server |
| `/.well-known/oauth-authorization-server` | RFC 8414 | Proxies Keycloak's OIDC metadata           |

Standards-compliant MCP clients (Claude.ai, Claude Code) discover these automatically and
complete the OAuth flow without any manual token configuration.
