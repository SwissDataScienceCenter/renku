---
title: AI Agents
---

Renku supports AI agents through the [Model Context Protocol (MCP)](https://modelcontextprotocol.io).
Once connected, an AI assistant can manage your projects, launch sessions, link data
connectors, and run jobs on your behalf — directly from your conversation.

## Connecting an AI agent

### Claude Code

Run this once in your terminal (replace the URL with your Renku deployment):

```bash
claude mcp add --transport http \
  --client-id renku-mcp \
  --callback-port 8484 \
  renku https://renkulab.io/mcp
```

Claude Code will open a browser to complete the login.

### pi

Add the following to your `.pi/mcp.json`:

```json
{
  "mcpServers": {
    "renku": {
      "type": "http",
      "url": "https://renkulab.io/mcp",
      "oauth": {
        "clientId": "renku-mcp",
        "redirectUri": "http://localhost:8484"
      }
    }
  }
}
```

pi will prompt you to log in the first time it connects.

### Claude.ai

Remote MCP server support in Claude.ai is being rolled out gradually. When available
on your plan, you can add `https://renkulab.io/mcp` as a remote MCP server in your
Claude.ai settings and authentication will be handled automatically.

## What the agent can do

Once connected, the agent has access to the full Renku platform:

- **Projects** — list, create, update, and delete projects; manage Git repositories and documentation
- **Sessions** — launch interactive sessions, wait for them to start, view logs, stop them
- **Jobs** — run non-interactive compute jobs and wait for results
- **Data connectors** — create and link connectors for S3, WebDAV, DOI/Zenodo, Polybox, and other storage backends
- **Session launchers** — create launchers with custom environments (built from code or a container image)
- **Resource classes** — query available compute resources and select the right class for a task

## Example: start a session

Tell the agent what you need — for example:

> "Start a session for my project `myuser/analysis` with at least 8 GB RAM"

The agent will:

1. Look up your project
2. Query available resource classes for one with at least 8 GB
3. Find or create a session launcher
4. Launch the session and wait for it to be ready
5. Give you the URL to open it

## Security

- The agent cannot perform operations if you are logged in as a Renku administrator. Log out and back in as a regular user first.
- **Never share credentials with the agent.** If a data connector requires a password or API key, add it through the Renku UI — the agent will prompt you to do this rather than asking for the credentials directly.
- The agent acts on your behalf using your own Renku account and respects all existing permissions.
