package mcp

import (
	"context"
	"log/slog"
	"sync"
	"time"

	sdkmcp "github.com/modelcontextprotocol/go-sdk/mcp"
	"golang.org/x/time/rate"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

// limiterScanInterval is how often the limiters of idle users are dropped.
const limiterScanInterval = time.Minute

// RateLimitOptions configures RateLimit.
type RateLimitOptions struct {
	// PerMinute is the sustained number of tool calls a user may make per minute.
	PerMinute int
	// Burst is the number of tool calls a user may make at once.
	Burst  int
	Logger *slog.Logger
}

// RateLimit returns a receiving middleware that limits the tool calls of every user with a token bucket.
// Calls over the limit are returned to the model as tool errors so that it can back off. The limit is per
// server process, so with several replicas the effective limit is multiplied by their number.
func RateLimit(opts RateLimitOptions) sdkmcp.Middleware {
	l := &limiters{
		limit:    rate.Limit(float64(opts.PerMinute) / 60),
		burst:    opts.Burst,
		byUser:   map[string]*rate.Limiter{},
		now:      time.Now,
		lastScan: time.Now(),
	}
	return func(next sdkmcp.MethodHandler) sdkmcp.MethodHandler {
		return func(ctx context.Context, method string, req sdkmcp.Request) (sdkmcp.Result, error) {
			call, ok := req.(*sdkmcp.CallToolRequest)
			if !ok {
				return next(ctx, method, req)
			}
			c := toolkit.CallerOf(ctx, call)
			if l.allow(c.Principal.Subject) {
				return next(ctx, method, req)
			}
			opts.Logger.Warn("tool call", "tool", call.Params.Name, "sub", c.Principal.Subject, "request_id", c.RequestID, "outcome", "rate_limited")
			return toolError("Too many tool calls. Wait a few seconds before trying again."), nil
		}
	}
}

// limiters holds a token bucket per user. Full buckets are dropped, since a new one behaves the same.
type limiters struct {
	limit    rate.Limit
	burst    int
	now      func() time.Time
	mu       sync.Mutex
	byUser   map[string]*rate.Limiter
	lastScan time.Time
}

func (l *limiters) allow(subject string) bool {
	l.mu.Lock()
	defer l.mu.Unlock()
	now := l.now()
	if now.Sub(l.lastScan) > limiterScanInterval {
		// Check and purge the limiters from users who are idle.
		// If you have not made any requests your tokens are full and the user is "idle".
		// This is to prevent the byUser map from growing too large.
		// When a user comes back we create a new token bucket for them.
		for s, limiter := range l.byUser {
			if limiter.TokensAt(now) >= float64(l.burst) {
				delete(l.byUser, s)
			}
		}
		l.lastScan = now
	}
	limiter, ok := l.byUser[subject]
	if !ok {
		limiter = rate.NewLimiter(l.limit, l.burst)
		l.byUser[subject] = limiter
	}
	// You need one token to make a tool call, when the tokens run out you cannot make tool calls.
	return limiter.AllowN(now, 1)
}
