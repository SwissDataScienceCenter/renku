// Package app wires the MCP server, the tools and authentication together.
package app

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"
	"net/url"
	"time"

	"github.com/labstack/echo/v5"
	"github.com/labstack/echo/v5/middleware"
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

	e := echo.New()
	e.Use(
		middleware.RequestIDWithConfig(middleware.RequestIDConfig{
			// Tool handlers read the ID from the request headers to log it and pass it on to the Renku API.
			RequestIDHandler: func(c *echo.Context, requestID string) { c.Request().Header.Set(echo.HeaderXRequestID, requestID) },
		}),
		middleware.RequestLoggerWithConfig(middleware.RequestLoggerConfig{
			// Probes would flood the logs.
			Skipper: func(c *echo.Context) bool {
				p := c.Request().URL.Path
				return p == "/healthz" || p == "/readyz"
			},
			LogRequestID: true,
			LogMethod:    true,
			LogURIPath:   true,
			LogStatus:    true,
			LogLatency:   true,
			LogRemoteIP:  true,
			HandleError:  true,
			LogValuesFunc: func(c *echo.Context, v middleware.RequestLoggerValues) error {
				attrs := []any{"request_id", v.RequestID, "method", v.Method, "path", v.URIPath, "status", v.Status, "duration", v.Latency, "remote_ip", v.RemoteIP}
				if v.Error != nil {
					logger.ErrorContext(c.Request().Context(), "http request", append(attrs, "error", v.Error)...)
					return nil
				}
				logger.InfoContext(c.Request().Context(), "http request", attrs...)
				return nil
			},
		}),
		// After the request logger, so that recovered panics are logged with a stack trace and status 500.
		// It does not cover tool handlers, which the SDK runs in their own goroutines, see internal/tools.
		middleware.RecoverWithConfig(middleware.RecoverConfig{DisableStackAll: true}),
	)
	e.Any(MCPPath, echo.WrapHandler(authenticator.Middleware(mcpHandler)))
	e.GET(authenticator.MetadataPath(), echo.WrapHandler(authenticator.MetadataHandler()))
	e.GET("/healthz", func(c *echo.Context) error { return c.NoContent(http.StatusOK) })
	e.GET("/readyz", func(c *echo.Context) error {
		ctx, cancel := context.WithTimeout(c.Request().Context(), 5*time.Second)
		defer cancel()
		if !authenticator.Ready(ctx) {
			return c.String(http.StatusServiceUnavailable, "signing keys not available")
		}
		return c.NoContent(http.StatusOK)
	})
	return e
}
