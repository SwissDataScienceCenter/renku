//! Module for obtaining Keycloak access tokens.

use color_eyre::Result;
use color_eyre::eyre::eyre;
use form_urlencoded::Serializer;
use reqwest::Url;
use std::fmt;
use std::time::{Duration, Instant};
use tokio::sync::RwLock;

#[cfg(test)]
use wiremock::matchers::{body_string_contains, method, path};
#[cfg(test)]
use wiremock::{Mock, MockServer, ResponseTemplate};

/// Keycloak client-credentials settings.
pub struct KeycloakSettings {
    pub url: String,
    pub realm: String,
    pub client_id: String,
    pub client_secret: String,
}

impl fmt::Debug for KeycloakSettings {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.debug_struct("KeycloakSettings")
            .field("url", &self.url)
            .field("realm", &self.realm)
            .field("client_id", &self.client_id)
            .field("client_secret", &"***")
            .finish()
    }
}

/// Minimum remaining lifetime before a cached token is refreshed.
const REFRESH_MARGIN: Duration = Duration::from_secs(30);
const DEFAULT_EXPIRES_IN: u64 = 60;

struct CachedToken {
    access_token: String,
    expires_at: Instant,
}

#[derive(serde::Deserialize)]
struct TokenResponse {
    access_token: String,
    expires_in: Option<u64>,
}

/// Obtains and caches Keycloak access tokens via the client-credentials grant.
pub struct TokenProvider {
    client: reqwest::Client,
    token_url: Url,
    client_id: String,
    client_secret: String,
    cache: RwLock<Option<CachedToken>>,
}

impl TokenProvider {
    pub fn new(settings: &KeycloakSettings, timeout: &Duration) -> Result<Self> {
        let base = Url::parse(&settings.url)?;
        let mut token_url = base.clone();
        {
            let mut path = token_url
                .path_segments_mut()
                .map_err(|_| eyre!("Cannot extend base url: {}", settings.url))?;
            // The chart's Keycloak URL ends with `/auth/`; without this the
            // appended segments would start with a second slash.
            path.pop_if_empty();
            path.extend([
                "realms",
                settings.realm.as_str(),
                "protocol",
                "openid-connect",
                "token",
            ]);
        }
        let client = reqwest::Client::builder().timeout(*timeout).build()?;
        Ok(Self {
            client,
            token_url,
            client_id: settings.client_id.clone(),
            client_secret: settings.client_secret.clone(),
            cache: RwLock::new(None),
        })
    }

    /// Returns a valid access token, using the cache when it has enough life left.
    pub async fn token(&self) -> Result<String> {
        if let Some(cached) = self.cache.read().await.as_ref()
            && cached.expires_at.saturating_duration_since(Instant::now()) > REFRESH_MARGIN
        {
            return Ok(cached.access_token.clone());
        }
        self.refresh().await
    }

    /// Fetches a new access token and replaces the cached one.
    pub async fn refresh(&self) -> Result<String> {
        let cached = self.fetch().await?;
        let token = cached.access_token.clone();
        *self.cache.write().await = Some(cached);
        Ok(token)
    }

    async fn fetch(&self) -> Result<CachedToken> {
        let form = Serializer::new(String::new())
            .append_pair("grant_type", "client_credentials")
            .append_pair("client_id", &self.client_id)
            .append_pair("client_secret", &self.client_secret)
            .finish();
        let resp = self
            .client
            .post(self.token_url.clone())
            .header("content-type", "application/x-www-form-urlencoded")
            .body(form)
            .send()
            .await?;
        let status = resp.status();
        if !status.is_success() {
            return Err(eyre!("Keycloak token request failed with status {status}"));
        }
        let body: TokenResponse = resp.json().await?;
        let expires_in = body.expires_in.unwrap_or(DEFAULT_EXPIRES_IN);
        Ok(CachedToken {
            access_token: body.access_token,
            // A bad `expires_in` must not panic; fall back to an immediately-expired token.
            expires_at: Instant::now()
                .checked_add(Duration::from_secs(expires_in))
                .unwrap_or_else(Instant::now),
        })
    }
}

#[cfg(test)]
fn test_provider(server: &MockServer) -> TokenProvider {
    let settings = KeycloakSettings {
        url: server.uri(),
        realm: "test".to_string(),
        client_id: "ssh-proxy".to_string(),
        client_secret: "secret".to_string(),
    };
    TokenProvider::new(&settings, &Duration::from_secs(5)).unwrap()
}

#[cfg(test)]
fn token_mock(expires_in: u64) -> Mock {
    Mock::given(method("POST"))
        .and(path("/realms/test/protocol/openid-connect/token"))
        .and(body_string_contains("grant_type=client_credentials"))
        .respond_with(ResponseTemplate::new(200).set_body_raw(
            format!(r#"{{"access_token":"abc","expires_in":{expires_in}}}"#),
            "application/json",
        ))
}

#[tokio::test]
async fn test_token_is_cached() {
    let server = MockServer::start().await;
    token_mock(300).expect(1).mount(&server).await;

    let provider = test_provider(&server);
    assert_eq!(provider.token().await.unwrap(), "abc");
    assert_eq!(provider.token().await.unwrap(), "abc");
}

#[tokio::test]
async fn test_token_within_refresh_margin_is_refetched() {
    let server = MockServer::start().await;
    token_mock(10).expect(2).mount(&server).await;

    let provider = test_provider(&server);
    assert_eq!(provider.token().await.unwrap(), "abc");
    assert_eq!(provider.token().await.unwrap(), "abc");
}

#[tokio::test]
async fn test_missing_expires_in_uses_the_default() {
    let server = MockServer::start().await;
    Mock::given(method("POST"))
        .and(path("/realms/test/protocol/openid-connect/token"))
        .respond_with(
            ResponseTemplate::new(200)
                .set_body_raw(r#"{"access_token":"abc"}"#, "application/json"),
        )
        .expect(1)
        .mount(&server)
        .await;

    let provider = test_provider(&server);
    assert_eq!(provider.token().await.unwrap(), "abc");
    assert_eq!(provider.token().await.unwrap(), "abc");
}

#[tokio::test]
async fn test_absurd_expires_in_does_not_cache_forever() {
    let server = MockServer::start().await;
    Mock::given(method("POST"))
        .and(path("/realms/test/protocol/openid-connect/token"))
        .respond_with(ResponseTemplate::new(200).set_body_raw(
            format!(r#"{{"access_token":"abc","expires_in":{}}}"#, u64::MAX),
            "application/json",
        ))
        .expect(2)
        .mount(&server)
        .await;

    let provider = test_provider(&server);
    assert_eq!(provider.token().await.unwrap(), "abc");
    assert_eq!(provider.token().await.unwrap(), "abc");
}

#[tokio::test]
async fn test_token_error_on_non_success() {
    let server = MockServer::start().await;
    Mock::given(method("POST"))
        .and(path("/realms/test/protocol/openid-connect/token"))
        .respond_with(ResponseTemplate::new(401))
        .mount(&server)
        .await;

    let provider = test_provider(&server);
    assert!(provider.token().await.is_err());
}

#[test]
fn test_settings_debug_redacts_secret() {
    let settings = KeycloakSettings {
        url: "http://localhost:8080".to_string(),
        realm: "renku".to_string(),
        client_id: "ssh-proxy".to_string(),
        client_secret: "super-secret".to_string(),
    };
    let debug = format!("{settings:?}");
    assert!(!debug.contains("super-secret"));
    assert!(debug.contains("***"));
}

#[test]
fn test_token_url_from_base_with_trailing_slash() {
    let settings = KeycloakSettings {
        url: "https://renku.example/auth/".to_string(),
        realm: "Renku".to_string(),
        client_id: "ssh-proxy".to_string(),
        client_secret: "secret".to_string(),
    };
    let provider = TokenProvider::new(&settings, &Duration::from_secs(5)).unwrap();
    assert_eq!(
        provider.token_url.as_str(),
        "https://renku.example/auth/realms/Renku/protocol/openid-connect/token"
    );
}

#[test]
fn test_token_url_from_base_without_trailing_slash() {
    let settings = KeycloakSettings {
        url: "https://renku.example/auth".to_string(),
        realm: "Renku".to_string(),
        client_id: "ssh-proxy".to_string(),
        client_secret: "secret".to_string(),
    };
    let provider = TokenProvider::new(&settings, &Duration::from_secs(5)).unwrap();
    assert_eq!(
        provider.token_url.as_str(),
        "https://renku.example/auth/realms/Renku/protocol/openid-connect/token"
    );
}
