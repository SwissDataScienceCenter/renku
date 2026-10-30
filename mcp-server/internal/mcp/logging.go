// Package mcp holds the receiving middleware of the MCP server: logging, panic recovery and rate limiting of tool calls.
package mcp

import (
	"context"
	"errors"
	"log/slog"
	"runtime/debug"
	"time"

	sdkmcp "github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

// LogOptions configures LogToolCalls.
type LogOptions struct {
	Logger *slog.Logger
	// LogArguments adds the tool arguments to the per-call log line. They can contain free text entered by users.
	LogArguments bool
}

// LogToolCalls returns a receiving middleware that logs every tool call with its outcome and turns panics
// of tool handlers into tool errors. The SDK does not recover them, and they would take down the whole server.
// It stores the caller in the context with toolkit.WithCaller, so that the tool's API requests carry the
// request ID of the log line.
func LogToolCalls(opts LogOptions) sdkmcp.Middleware {
	return func(next sdkmcp.MethodHandler) sdkmcp.MethodHandler {
		return func(ctx context.Context, method string, req sdkmcp.Request) (result sdkmcp.Result, err error) {
			call, ok := req.(*sdkmcp.CallToolRequest)
			if !ok {
				return next(ctx, method, req)
			}
			start := time.Now()
			c := toolkit.CallerOf(ctx, call)
			ctx = toolkit.WithCaller(ctx, c)
			attrs := []any{"tool", call.Params.Name, "sub", c.Principal.Subject, "request_id", c.RequestID}
			if opts.LogArguments {
				attrs = append(attrs, "arguments", call.Params.Arguments)
			}
			defer func() {
				if r := recover(); r != nil {
					opts.Logger.Error("tool call", append(attrs, "outcome", "panic", "error", r, "stack", string(debug.Stack()), "duration", time.Since(start))...)
					result, err = toolError("The tool failed unexpectedly. Try again later."), nil
				}
			}()

			result, err = next(ctx, method, req)
			attrs = append(attrs, "duration", time.Since(start))
			if err != nil {
				// A protocol error, e.g. an unknown tool.
				opts.Logger.Info("tool call", append(attrs, "outcome", "rejected", "error", err)...)
				return result, err
			}
			res, _ := result.(*sdkmcp.CallToolResult)
			if res == nil || !res.IsError {
				opts.Logger.Info("tool call", append(attrs, "outcome", "ok")...)
				return result, err
			}
			var apiErr *toolkit.Error
			if !errors.As(res.GetError(), &apiErr) {
				// Arguments that do not match the input schema, or an error of the tool itself.
				opts.Logger.Info("tool call", append(attrs, "outcome", "tool_error", "error", res.GetError())...)
				return result, err
			}
			attrs = append(attrs, "outcome", apiErr.Outcome)
			if apiErr.Status != 0 {
				attrs = append(attrs, "status", apiErr.Status)
			}
			switch apiErr.Outcome {
			case toolkit.OutcomeUpstreamError:
				opts.Logger.Error("tool call", append(attrs, "body", toolkit.Truncate(string(apiErr.Body), 1000))...)
			case toolkit.OutcomeUnreachable:
				opts.Logger.Error("tool call", append(attrs, "error", apiErr.Err)...)
			case toolkit.OutcomeResponseTooLarge:
				opts.Logger.Warn("tool call", attrs...)
			default:
				opts.Logger.Info("tool call", attrs...)
			}
			return result, err
		}
	}
}

func toolError(message string) *sdkmcp.CallToolResult {
	return &sdkmcp.CallToolResult{IsError: true, Content: []sdkmcp.Content{&sdkmcp.TextContent{Text: message}}}
}
