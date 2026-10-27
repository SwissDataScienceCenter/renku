// Package app wires the MCP server, the tools and authentication together.
package app

import (
	"context"
	"log/slog"
	"net/http"
	"time"

	"github.com/mark3labs/mcp-go/server"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/gen/apiclient"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools"
)

// MCPPath is where the streamable HTTP transport is served.
const MCPPath = "/mcp"

const maxRequestBytes = 1 << 20

// Options configures the MCP server.
type Options struct {
	// APIURL is the base URL of the Renku data API, e.g. http://renku-gateway/api/data.
	APIURL       string
	Version      string
	Logger       *slog.Logger
	LogArguments bool
}

// NewMCPServer creates the MCP server with all tools registered.
func NewMCPServer(opts Options) (*server.MCPServer, error) {
	client, err := apiclient.NewClient(opts.APIURL, apiclient.WithHTTPClient(&http.Client{Timeout: 60 * time.Second}))
	if err != nil {
		return nil, err
	}
	s := server.NewMCPServer("renku", opts.Version,
		server.WithToolCapabilities(false),
		server.WithRecovery(),
		server.WithInstructions("Tools to explore Renku projects, groups and data connectors and to start, inspect and stop Renku sessions on behalf of the logged in user."),
	)
	tools.Register(s, tools.Options{Client: client, Logger: opts.Logger, LogArguments: opts.LogArguments})
	return s, nil
}

// NewHTTPHandler serves the MCP endpoint behind authentication, the protected resource metadata and health checks.
func NewHTTPHandler(s *server.MCPServer, authenticator *auth.Authenticator, logger *slog.Logger) http.Handler {
	mcpHandler := server.NewStreamableHTTPServer(s,
		server.WithStateLess(true),
		server.WithDisableStreaming(true),
		server.WithStreamableHTTPLogger(logger),
		server.WithHTTPContextFunc(func(ctx context.Context, r *http.Request) context.Context {
			if p, ok := auth.PrincipalFrom(r.Context()); ok {
				ctx = auth.WithPrincipal(ctx, p)
			}
			return tools.WithClientIP(ctx, r.Header.Get("X-Real-IP"))
		}),
	)

	mux := http.NewServeMux()
	mux.Handle(MCPPath, authenticator.Middleware(http.MaxBytesHandler(mcpHandler, maxRequestBytes)))
	mux.Handle(authenticator.MetadataPath(), authenticator.MetadataHandler())
	mux.HandleFunc("GET /healthz", func(w http.ResponseWriter, _ *http.Request) { w.WriteHeader(http.StatusOK) })
	mux.HandleFunc("GET /readyz", func(w http.ResponseWriter, r *http.Request) {
		ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
		defer cancel()
		if !authenticator.Ready(ctx) {
			http.Error(w, "signing keys not available", http.StatusServiceUnavailable)
			return
		}
		w.WriteHeader(http.StatusOK)
	})
	return mux
}
