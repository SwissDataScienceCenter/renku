package mcp

import (
	"context"
	"log/slog"
	"sync"
	"time"

	sdkmcp "github.com/modelcontextprotocol/go-sdk/mcp"
	"golang.org/x/time/rate"
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
			principal, _, requestID := caller(ctx, call)
			if l.allow(principal.Subject) {
				return next(ctx, method, req)
			}
			opts.Logger.Warn("tool call", "tool", call.Params.Name, "sub", principal.Subject, "request_id", requestID, "outcome", "rate_limited")
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
	return limiter.AllowN(now, 1)
}
