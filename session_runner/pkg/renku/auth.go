package renku

import (
	"context"
	"fmt"
	"log"
	"net/http"
	"net/url"
	"sync"
	"time"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/renku/api/auth"
	"github.com/golang-jwt/jwt/v5"
)

const (
	expiryMargin        = 3 * time.Second
	refreshExpiryMargin = 3 * time.Minute
)

type RenkuAuth struct {
	accessToken      string
	expiresAt        time.Time
	refreshToken     string
	refreshExpiresAt time.Time
	tokenType        string
	mutex            sync.RWMutex

	refreshTicker *time.Ticker

	authClient auth.ClientWithResponsesInterface
}

func NewRenkuAuth(serverURL *url.URL, accessToken string, refreshToken string) (ra *RenkuAuth, err error) {
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

	apiURL := serverURL.ResolveReference(&url.URL{Path: "/api/data"})
	apiURLStr := apiURL.String()

	authClient, err := auth.NewClientWithResponses(apiURLStr)
	if err != nil {
		return nil, err
	}
	renkuAuth.authClient = authClient

	renkuAuth.refreshTicker = time.NewTicker(time.Minute)
	go renkuAuth.periodicTokenRefresh()

	return &renkuAuth, nil
}

func (ra *RenkuAuth) RequestEditor() RequestEditorFn {
	return func(ctx context.Context, req *http.Request) error {
		if req.Header.Get("Authorization") != "" {
			return nil
		}
		token, err := ra.GetAccessToken(ctx)
		if err != nil {
			return err
		}
		req.Header.Add("Authorization", fmt.Sprintf("%s %s", ra.tokenType, token))
		return nil
	}
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
			message = resJSONDefault.Error.Message
			if resJSONDefault.Error.Detail != nil {
				message += fmt.Sprintf(", detail: %s", *resJSONDefault.Error.Detail)
			}
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

// periodicTokenRefresh keeps the refresh token valid
func (ra *RenkuAuth) periodicTokenRefresh() {
	for {
		<-ra.refreshTicker.C
		ra.mutex.RLock()
		refreshExpiresAt := ra.refreshExpiresAt
		ra.mutex.RUnlock()

		if !IsNotExpired(refreshExpiresAt, refreshExpiryMargin, true /*=required*/) {
			log.Println("Getting a new renku refresh token from automatic checks")
			ctx, cancel := context.WithTimeout(context.Background(), time.Minute)
			err := ra.refreshTokens(ctx)
			cancel()
			if err != nil {
				log.Printf("Could not refresh renku token: %s\n", err.Error())
			}
		}
	}

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
