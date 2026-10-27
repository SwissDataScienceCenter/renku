// Package app wires the MCP server, the tools and authentication together.
package app

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"
	"net/url"
	"time"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
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
func NewMCPServer(opts Options) (*mcp.Server, error) {
	if u, err := url.Parse(opts.APIURL); err != nil || (u.Scheme != "http" && u.Scheme != "https") || u.Host == "" {
		return nil, fmt.Errorf("invalid API URL %q, expected e.g. https://renku.example.org/api/data", opts.APIURL)
	}
	s := mcp.NewServer(&mcp.Implementation{Name: "renku", Version: opts.Version}, &mcp.ServerOptions{
		Instructions: "Tools to explore Renku projects, groups and data connectors and to start, inspect and stop Renku sessions on behalf of the logged in user.",
		// The tool list never changes and the server does not log to the client.
		Capabilities: &mcp.ServerCapabilities{Tools: &mcp.ToolCapabilities{}},
	})
	toolOpts := tools.Options{
		APIURL:       opts.APIURL,
		HTTPClient:   &http.Client{Timeout: 60 * time.Second},
		Logger:       opts.Logger,
		LogArguments: opts.LogArguments,
	}
	if err := tools.Register(s, toolOpts); err != nil {
		return nil, err
	}
	return s, nil
}

// NewHTTPHandler serves the MCP endpoint behind authentication, the protected resource metadata and health checks.
func NewHTTPHandler(s *mcp.Server, authenticator *auth.Authenticator, logger *slog.Logger) http.Handler {
	mcpHandler := mcp.NewStreamableHTTPHandler(func(*http.Request) *mcp.Server { return s }, &mcp.StreamableHTTPOptions{
		Stateless:           true,
		JSONResponse:        true,
		Logger:              logger,
		MaxRequestBodyBytes: maxRequestBytes,
	})

	mux := http.NewServeMux()
	mux.Handle(MCPPath, authenticator.Middleware(mcpHandler))
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
