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
	sdkmcp "github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/mcp"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools"
)

// MCPPath is where the streamable HTTP transport is served.
const MCPPath = "/mcp"

const maxRequestBytes = 1 << 20

// instructions holds only rules that apply across tools; guidance for one tool is in its description.
const instructions = "Tools to work with Renku on behalf of the logged in user: projects, groups, data connectors, " +
	"session launchers, sessions, jobs and apps.\n\n" +
	"- If a tool fails with status 401, the user's login is no longer valid. Stop and ask the user to " +
	"reconnect the Renku MCP server in their client; do not retry.\n" +
	"- Call list_resource_pools before creating a session launcher or starting a session or job, and use " +
	"the smallest resource class that matches.\n" +
	"- Confirm with the user before deleting anything, stopping a session or changing the visibility of a project or data connector. " +
	"Some clients ask the user themselves; if the user declines there, do not retry.\n" +
	"- To wait for a session, job, build or app, call wait_for_session, wait_for_build or wait_for_app " +
	"rather than polling the get_ tools. They return after about a minute; call them again while the status " +
	"has not changed.\n" +
	"- Never pass credentials (passwords, access keys, tokens) as tool arguments, and do not ask the user for " +
	"them: the tools refuse them. The user adds them to a data connector in the Renku UI."

// Options configures the MCP server.
type Options struct {
	// APIURL is the base URL of the Renku data API, e.g. http://renku-gateway/api/data.
	APIURL       string
	Version      string
	Logger       *slog.Logger
	LogArguments bool
	// RateLimit enables limiting the tool calls of every user to ToolCallsPerMinute with bursts of
	// ToolCallBurst, see mcp.RateLimit.
	RateLimit          bool
	ToolCallsPerMinute int
	ToolCallBurst      int
}

// NewMCPServer creates the MCP server with all tools registered.
func NewMCPServer(opts Options) (*sdkmcp.Server, error) {
	if u, err := url.Parse(opts.APIURL); err != nil || (u.Scheme != "http" && u.Scheme != "https") || u.Host == "" {
		return nil, fmt.Errorf("invalid API URL %q, expected e.g. https://renku.example.org/api/data", opts.APIURL)
	}
	if opts.RateLimit && (opts.ToolCallsPerMinute <= 0 || opts.ToolCallBurst <= 0) {
		return nil, fmt.Errorf("invalid rate limit of %d tool calls per minute with bursts of %d, both must be positive", opts.ToolCallsPerMinute, opts.ToolCallBurst)
	}
	s := sdkmcp.NewServer(&sdkmcp.Implementation{Name: "renku", Version: opts.Version}, &sdkmcp.ServerOptions{
		Instructions: instructions,
		// The tool list never changes and the server does not log to the client.
		Capabilities: &sdkmcp.ServerCapabilities{Tools: &sdkmcp.ToolCapabilities{}},
	})
	// Each call wraps the middleware added before, so the rate limit runs first and rejected calls are
	// only logged by it.
	s.AddReceivingMiddleware(mcp.LogToolCalls(mcp.LogOptions{Logger: opts.Logger, LogArguments: opts.LogArguments}))
	if opts.RateLimit {
		s.AddReceivingMiddleware(mcp.RateLimit(mcp.RateLimitOptions{PerMinute: opts.ToolCallsPerMinute, Burst: opts.ToolCallBurst, Logger: opts.Logger}))
	}
	api, err := renkuapi.NewClients(opts.APIURL, &http.Client{Timeout: 60 * time.Second})
	if err != nil {
		return nil, err
	}
	tools.Register(s, api)
	return s, nil
}

// mcpCORS lets MCP clients running in a browser, such as the MCP Inspector, call the MCP endpoint.
// It answers preflights before authentication, since browsers send them without the Authorization
// header. Any origin is allowed: requests are authenticated with a bearer token rather than cookies,
// so another site cannot act as the user without having their token.
var mcpCORS = middleware.CORSWithConfig(middleware.CORSConfig{
	AllowOrigins: []string{"*"},
	AllowMethods: []string{http.MethodGet, http.MethodPost, http.MethodDelete},
	AllowHeaders: []string{"Authorization", "Content-Type", "Accept", "Last-Event-ID", "Mcp-Protocol-Version", "Mcp-Session-Id"},
	// WWW-Authenticate points browser clients to the protected resource metadata after a 401.
	ExposeHeaders: []string{"WWW-Authenticate", "Mcp-Protocol-Version", "Mcp-Session-Id"},
	MaxAge:        int((24 * time.Hour).Seconds()),
})

// NewHTTPHandler serves the MCP endpoint behind authentication, the protected resource metadata and health checks.
func NewHTTPHandler(s *sdkmcp.Server, authenticator *auth.Authenticator, logger *slog.Logger) http.Handler {
	// Without JSONResponse, responses are SSE streams, which can carry notifications about the call
	// before its result. Stateless servers cannot send requests: tools ask the user with input requests
	// in their result instead, see toolkit.Confirm.
	mcpHandler := sdkmcp.NewStreamableHTTPHandler(func(*http.Request) *sdkmcp.Server { return s }, &sdkmcp.StreamableHTTPOptions{
		Stateless:           true,
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
		// It does not cover tool handlers, which the SDK runs in their own goroutines, see mcp.LogToolCalls.
		middleware.RecoverWithConfig(middleware.RecoverConfig{DisableStackAll: true}),
	)
	e.Any(MCPPath, echo.WrapHandler(authenticator.Middleware(mcpHandler)), mcpCORS)
	// The SDK handler answers preflights and sets the CORS headers itself.
	e.Match([]string{http.MethodGet, http.MethodOptions}, authenticator.MetadataPath(), echo.WrapHandler(authenticator.MetadataHandler()))
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
