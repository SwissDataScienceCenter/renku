package renku

import (
	"context"
	"fmt"
	"sync"
	"time"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/auth"
	"github.com/golang-jwt/jwt/v5"
)

const (
	expiryMargin = 3 * time.Second
)

type RenkuAuth struct {
	accessToken      string
	expiresAt        time.Time
	refreshToken     string
	refreshExpiresAt time.Time
	tokenType        string
	mutex            sync.RWMutex

	authClient auth.ClientWithResponsesInterface
}

func NewRenkuAuth(baseURL string, accessToken string, refreshToken string) (ra *RenkuAuth, err error) {
	renkuAuth := RenkuAuth{
		accessToken:  accessToken,
		refreshToken: refreshToken,
		tokenType:    "Bearer",
		mutex:        sync.RWMutex{},
	}

	parser := jwt.NewParser()
	claims := jwt.RegisteredClaims{}
	_, _, err = parser.ParseUnverified(renkuAuth.accessToken, &claims)
	if err == nil {
		renkuAuth.expiresAt = claims.ExpiresAt.Time
	}
	_, _, err = parser.ParseUnverified(renkuAuth.refreshToken, &claims)
	if err == nil {
		renkuAuth.refreshExpiresAt = claims.ExpiresAt.Time
	}

	authClient, err := auth.NewClientWithResponses(baseURL)
	if err != nil {
		return nil, err
	}
	renkuAuth.authClient = authClient

	return &renkuAuth, nil
}

func (ra *RenkuAuth) GetAccessToken(ctx context.Context) (token string, err error) {
	ra.mutex.RLock()
	accessToken := ra.accessToken
	expiresAt := ra.expiresAt
	ra.mutex.RUnlock()

	if IsNotExpired(expiresAt, expiryMargin, true /*=required*/) {
		return accessToken, nil
	}

	if err := ra.refreshTokens(ctx); err != nil {
		return "", nil
	}
	ra.mutex.RLock()
	accessToken = ra.accessToken
	ra.mutex.RUnlock()
	return accessToken, nil
}

func (ra *RenkuAuth) refreshTokens(ctx context.Context) error {
	ra.mutex.Lock()
	defer ra.mutex.Unlock()

	body := auth.PostTokenRequest{
		GrantType:    auth.RefreshToken,
		RefreshToken: ra.refreshToken,
	}
	res, err := ra.authClient.PostInternalAuthenticationTokenWithFormdataBodyWithResponse(ctx, body)
	if err != nil {
		return err
	}
	resJSON200 := res.GetJSON200()
	if resJSON200 == nil {
		message := ""
		resJSONDefault := res.GetJSONDefault()
		if resJSONDefault != nil {
			message = fmt.Sprintf("%s, detail: %s", resJSONDefault.Error.Message, *resJSONDefault.Error.Detail)
		} else {
			message = res.HTTPResponse.Status
		}
		return fmt.Errorf("failed to refresh Renku auth tokens: %s", message)
	}

	ra.accessToken = resJSON200.AccessToken
	ra.refreshToken = resJSON200.RefreshToken
	ra.tokenType = resJSON200.TokenType
	if ra.tokenType == "" {
		ra.tokenType = "Bearer"
	}

	parser := jwt.NewParser()
	claims := jwt.RegisteredClaims{}
	_, _, err = parser.ParseUnverified(ra.accessToken, &claims)
	if err == nil {
		ra.expiresAt = claims.ExpiresAt.Time
	}
	_, _, err = parser.ParseUnverified(ra.refreshToken, &claims)
	if err == nil {
		ra.refreshExpiresAt = claims.ExpiresAt.Time
	}

	return nil
}

// IsNotExpired returns true if expiresAt is still in the future, with a given margin.
// When expiresAt is zero, returns !required.
func IsNotExpired(expiresAt time.Time, margin time.Duration, required bool) bool {
	now := time.Now()
	deadline := now.Add(margin)
	if expiresAt.IsZero() {
		return !required
	}
	return deadline.Before(expiresAt)
}
