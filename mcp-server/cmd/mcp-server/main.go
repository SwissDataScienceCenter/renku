// Command mcp-server serves the Renku MCP server.
//
// By default it serves the streamable HTTP transport and requires Keycloak
// access tokens. With --stdio it serves a single local client over stdio and
// uses the token from RENKU_ACCESS_TOKEN, which is meant for development.
package main

import (
	"context"
	"errors"
	"flag"
	"fmt"
	"log/slog"
	"net/http"
	"os"
	"os/signal"
	"strconv"
	"syscall"
	"time"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/app"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/auth"
)

// version is set at build time with -ldflags "-X main.version=...".
var version = "dev"

func main() {
	stdio := flag.Bool("stdio", false, "serve over stdio instead of HTTP (development only)")
	flag.Parse()

	logger := slog.New(slog.NewJSONHandler(os.Stderr, &slog.HandlerOptions{Level: logLevel()}))
	slog.SetDefault(logger)

	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

	if err := run(ctx, logger, *stdio); err != nil {
		logger.Error("server failed", "error", err)
		os.Exit(1)
	}
}

func run(ctx context.Context, logger *slog.Logger, stdio bool) error {
	apiURL := os.Getenv("RENKU_API_URL")
	if apiURL == "" {
		return errors.New("RENKU_API_URL is required, e.g. https://renku.example.org/api/data")
	}
	s, err := app.NewMCPServer(app.Options{
		APIURL:       apiURL,
		Version:      version,
		Logger:       logger,
		LogArguments: boolEnv("MCP_LOG_TOOL_ARGUMENTS"),
	})
	if err != nil {
		return err
	}
	if stdio {
		return runStdio(ctx, logger, s)
	}
	return runHTTP(ctx, logger, s)
}

func runStdio(ctx context.Context, logger *slog.Logger, s *mcp.Server) error {
	token := os.Getenv("RENKU_ACCESS_TOKEN")
	if token == "" {
		logger.Warn("RENKU_ACCESS_TOKEN is not set, only public data is accessible")
	} else if admin, err := auth.IsAdminToken(token); err != nil {
		return fmt.Errorf("RENKU_ACCESS_TOKEN: %w", err)
	} else if admin {
		return errors.New("RENKU_ACCESS_TOKEN belongs to an administrator, who cannot use the MCP server")
	}
	// Tool handlers run with contexts derived from the one passed to Run.
	ctx = auth.WithPrincipal(ctx, auth.Principal{Subject: "stdio", Token: auth.AccessToken(token)})
	if err := s.Run(ctx, &mcp.StdioTransport{}); err != nil && !errors.Is(err, context.Canceled) {
		return err
	}
	return nil
}

func runHTTP(ctx context.Context, logger *slog.Logger, s *mcp.Server) error {
	authenticator, err := auth.New(ctx, auth.Config{
		Issuer:          os.Getenv("KEYCLOAK_ISSUER"),
		JWKSURL:         os.Getenv("KEYCLOAK_JWKS_URL"),
		Audience:        envOr("MCP_AUDIENCE", "renku"),
		AuthorizedParty: envOr("MCP_AUTHORIZED_PARTY", "renku-mcp"),
		ResourceURL:     os.Getenv("MCP_RESOURCE_URL"),
	}, logger)
	if err != nil {
		return fmt.Errorf("configuring authentication (KEYCLOAK_ISSUER, MCP_RESOURCE_URL): %w", err)
	}

	srv := &http.Server{
		Addr:              envOr("MCP_LISTEN_ADDR", ":8080"),
		Handler:           app.NewHTTPHandler(s, authenticator, logger),
		ReadHeaderTimeout: 10 * time.Second,
		ReadTimeout:       30 * time.Second,
		WriteTimeout:      90 * time.Second,
		IdleTimeout:       120 * time.Second,
	}
	errs := make(chan error, 1)
	go func() {
		logger.Info("listening", "addr", srv.Addr, "version", version)
		errs <- srv.ListenAndServe()
	}()
	select {
	case err := <-errs:
		return err
	case <-ctx.Done():
	}
	shutdownCtx, cancel := context.WithTimeout(context.Background(), 20*time.Second)
	defer cancel()
	return srv.Shutdown(shutdownCtx)
}

func envOr(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}

func boolEnv(key string) bool {
	v, _ := strconv.ParseBool(os.Getenv(key))
	return v
}

func logLevel() slog.Level {
	var level slog.Level
	if err := level.UnmarshalText([]byte(envOr("LOG_LEVEL", "info"))); err != nil {
		return slog.LevelInfo
	}
	return level
}
