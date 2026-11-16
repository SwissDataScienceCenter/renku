//! Module for interacting with the secret service.

use crate::keycloak::TokenProvider;
use color_eyre::Result;
use color_eyre::eyre::eyre;
use reqwest::Url;
use ssh_key::PublicKey;
use std::future::Future;
use std::sync::Arc;
use std::time::Duration;

pub struct Client {
    base_url: Url,
    data_services_url: Url,
    client: reqwest::Client,
    token_provider: Arc<TokenProvider>,
}

/// One read of a session's cull clock. A `None` is a timestamp data-services could not use.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct SessionClock {
    pub remaining: Option<Duration>,
    pub since_input: Option<Duration>,
    pub alive: bool,
}

/// A clock call that failed. `SessionGone` is final; `Failed` is retried next window.
#[derive(Debug)]
pub enum ClockError {
    SessionGone,
    Failed(color_eyre::Report),
}

pub type ClockResult<T> = std::result::Result<T, ClockError>;

/// What the clock loop needs from the data-services client, so tests can fake it.
/// Written with explicit `impl Future + Send` because `async fn` bounds cannot
/// prove `Send` for `tokio::spawn`.
pub(crate) trait ClockClient: Send + Sync {
    fn touch_session(&self, session: &str) -> impl Future<Output = ClockResult<()>> + Send;
    fn session_clock(
        &self,
        session: &str,
    ) -> impl Future<Output = ClockResult<SessionClock>> + Send;
}

#[derive(serde::Deserialize)]
struct ClockResponse {
    seconds_until_hibernate: Option<u64>,
    seconds_since_last_interaction: Option<u64>,
    alive: bool,
}

#[derive(serde::Serialize, Clone, Debug)]
#[serde(deny_unknown_fields)]
struct SessionAuthorizeRequest {
    pub public_key: String,
}

fn make_url<I>(base: &Url, items: I) -> Result<Url>
where
    I: IntoIterator,
    I::Item: AsRef<str>,
{
    let mut url = base.clone();
    {
        let mut path = url
            .path_segments_mut()
            .map_err(|_| eyre!("Cannot extends base url: {base}"))?;
        // A base ending in `/` would otherwise produce a double slash, which the route
        // does not serve.
        path.pop_if_empty();
        path.extend(items);
    }
    Ok(url)
}

impl Client {
    pub fn new(
        secret_service_url: &str,
        data_services_url: &str,
        token_provider: Arc<TokenProvider>,
        timeout: &Duration,
    ) -> Result<Client> {
        let uri = Url::parse(secret_service_url)?;
        let data_services_uri = Url::parse(data_services_url)?;
        let client = reqwest::Client::builder().timeout(*timeout).build()?;
        Ok(Client {
            base_url: uri,
            data_services_url: data_services_uri,
            client,
            token_provider,
        })
    }

    pub async fn authorize_session(
        &self,
        public_key: &PublicKey,
        session_name: &str,
    ) -> Result<bool> {
        let url = make_url(
            &self.base_url,
            &["internal", "sessions", session_name, "authorize"],
        )?;
        log::debug!("Call to: {}", url);
        let openssh_key = public_key.to_openssh()?;
        let payload = SessionAuthorizeRequest {
            public_key: openssh_key,
        };
        let resp = self.client.post(url).json(&payload).send().await?;
        let success = resp.status().is_success();
        if success {
            log::debug!("Authorized session {session_name} via public-key");
        } else {
            log::debug!("Failed to authorize public key: {}", payload.public_key);
            log::info!(
                "Call to {} denied access to session {session_name}: {}",
                self.base_url,
                resp.status()
            );
        }
        Ok(success)
    }

    /// Records user input on the session. Only 2xx counts as a touch.
    pub async fn touch_session(&self, session: &str) -> ClockResult<()> {
        let url = make_url(
            &self.data_services_url,
            ["internal", "sessions", session, "interaction"],
        )
        .map_err(ClockError::Failed)?;
        log::debug!("Touch session clock: {}", url);
        let resp = self
            .with_token(|token| self.client.post(url.clone()).bearer_auth(token))
            .await?;
        if resp.status().is_success() {
            Ok(())
        } else {
            Err(ClockError::Failed(eyre!(
                "touch for session {session} failed: {}",
                resp.status()
            )))
        }
    }

    /// Reads how long the session has until hibernation. A 404 means it is gone.
    pub async fn session_clock(&self, session: &str) -> ClockResult<SessionClock> {
        let url = make_url(
            &self.data_services_url,
            ["internal", "sessions", session, "interaction"],
        )
        .map_err(ClockError::Failed)?;
        log::debug!("Read session clock: {}", url);
        let resp = self
            .with_token(|token| self.client.get(url.clone()).bearer_auth(token))
            .await?;
        let status = resp.status();
        if status == reqwest::StatusCode::NOT_FOUND {
            return Err(ClockError::SessionGone);
        }
        if !status.is_success() {
            return Err(ClockError::Failed(eyre!(
                "clock read for session {session} failed: {status}"
            )));
        }
        let body: ClockResponse = resp.json().await.map_err(|e| {
            ClockError::Failed(color_eyre::Report::new(e).wrap_err("parsing session clock"))
        })?;
        Ok(SessionClock {
            remaining: body.seconds_until_hibernate.map(Duration::from_secs),
            since_input: body.seconds_since_last_interaction.map(Duration::from_secs),
            alive: body.alive,
        })
    }

    /// Sends a request with the service token, refreshing and retrying once on 401.
    async fn with_token<F>(&self, build: F) -> ClockResult<reqwest::Response>
    where
        F: Fn(&str) -> reqwest::RequestBuilder,
    {
        let token = self
            .token_provider
            .token()
            .await
            .map_err(ClockError::Failed)?;
        let resp = build(&token)
            .send()
            .await
            .map_err(|e| ClockError::Failed(e.into()))?;
        if resp.status() != reqwest::StatusCode::UNAUTHORIZED {
            return Ok(resp);
        }
        let token = self
            .token_provider
            .refresh()
            .await
            .map_err(ClockError::Failed)?;
        let resp = build(&token)
            .send()
            .await
            .map_err(|e| ClockError::Failed(e.into()))?;
        if resp.status() == reqwest::StatusCode::UNAUTHORIZED {
            return Err(ClockError::Failed(eyre!(
                "data-services rejected the service token twice"
            )));
        }
        Ok(resp)
    }
}

#[expect(
    clippy::manual_async_fn,
    reason = "`async fn` in a trait cannot state the `Send` bound `tokio::spawn` needs"
)]
impl ClockClient for Client {
    fn touch_session(&self, session: &str) -> impl Future<Output = ClockResult<()>> + Send {
        async move { Client::touch_session(self, session).await }
    }

    fn session_clock(
        &self,
        session: &str,
    ) -> impl Future<Output = ClockResult<SessionClock>> + Send {
        async move { Client::session_clock(self, session).await }
    }
}

#[test]
fn test_encode_session_authorize_request() {
    let req = SessionAuthorizeRequest {
        public_key: "blablabla".into(),
    };
    let x = serde_json::to_vec(&req).unwrap();
    let y = String::from_utf8_lossy(&x);
    assert_eq!(y, "{\"public_key\":\"blablabla\"}");
}

#[cfg(test)]
mod tests {
    use super::*;
    use crate::keycloak::{KeycloakSettings, TokenProvider};
    use std::sync::Arc;
    use wiremock::matchers::{body_string, header, method, path};
    use wiremock::{Mock, MockServer, ResponseTemplate};

    #[test]
    fn test_make_url_ignores_a_trailing_slash() {
        let base = Url::parse("http://ds/api/data/").unwrap();
        let url = make_url(&base, ["internal", "sessions", "s1", "interaction"]).unwrap();
        assert_eq!(
            url.as_str(),
            "http://ds/api/data/internal/sessions/s1/interaction"
        );
    }

    const TOKEN_PATH: &str = "/auth/realms/test/protocol/openid-connect/token";
    const INTERACTION_PATH: &str = "/api/data/internal/sessions/s1/interaction";

    async fn token_server(requests: u64, status: u16) -> MockServer {
        let server = MockServer::start().await;
        let body = if status == 200 {
            r#"{"access_token":"abc","expires_in":300}"#
        } else {
            r#"{"error":"invalid_client"}"#
        };
        Mock::given(method("POST"))
            .and(path(TOKEN_PATH))
            .respond_with(ResponseTemplate::new(status).set_body_raw(body, "application/json"))
            .expect(requests)
            .mount(&server)
            .await;
        server
    }

    fn client_for(kc: &MockServer, ds: &MockServer) -> Client {
        let provider = Arc::new(
            TokenProvider::new(
                &KeycloakSettings {
                    url: format!("{}/auth/", kc.uri()),
                    realm: "test".to_string(),
                    client_id: "ssh-proxy".to_string(),
                    client_secret: "s".to_string(),
                },
                &Duration::from_secs(5),
            )
            .unwrap(),
        );
        Client::new(
            "http://127.0.0.1:1",
            &format!("{}/api/data", ds.uri()),
            provider,
            &Duration::from_secs(5),
        )
        .unwrap()
    }

    #[tokio::test]
    async fn test_touch_sends_bearer_and_no_body() {
        let kc = token_server(1, 200).await;
        let ds = MockServer::start().await;
        Mock::given(method("POST"))
            .and(path(INTERACTION_PATH))
            .and(header("authorization", "Bearer abc"))
            .and(body_string(""))
            .respond_with(ResponseTemplate::new(204))
            .expect(1)
            .mount(&ds)
            .await;

        assert!(client_for(&kc, &ds).touch_session("s1").await.is_ok());
    }

    #[tokio::test]
    async fn test_clock_parses_values_and_nulls() {
        let kc = token_server(1, 200).await;
        let ds = MockServer::start().await;
        Mock::given(method("GET"))
            .and(path(INTERACTION_PATH))
            .respond_with(ResponseTemplate::new(200).set_body_raw(
                r#"{"seconds_until_hibernate":300,"seconds_since_last_interaction":null,"alive":true}"#,
                "application/json",
            ))
            .expect(1)
            .mount(&ds)
            .await;

        let clock = client_for(&kc, &ds).session_clock("s1").await.unwrap();
        assert_eq!(clock.remaining, Some(Duration::from_secs(300)));
        assert_eq!(clock.since_input, None);
        assert!(clock.alive);
    }

    #[tokio::test]
    async fn test_clock_reports_gone_on_404() {
        let kc = token_server(1, 200).await;
        let ds = MockServer::start().await;
        Mock::given(method("GET"))
            .and(path(INTERACTION_PATH))
            .respond_with(ResponseTemplate::new(404))
            .expect(1)
            .mount(&ds)
            .await;

        assert!(matches!(
            client_for(&kc, &ds).session_clock("s1").await,
            Err(ClockError::SessionGone)
        ));
    }

    #[tokio::test]
    async fn test_touch_refreshes_token_once_on_401() {
        let kc = token_server(2, 200).await;
        let ds = MockServer::start().await;
        Mock::given(method("POST"))
            .and(path(INTERACTION_PATH))
            .respond_with(ResponseTemplate::new(401))
            .up_to_n_times(1)
            .mount(&ds)
            .await;
        Mock::given(method("POST"))
            .and(path(INTERACTION_PATH))
            .respond_with(ResponseTemplate::new(204))
            .expect(1)
            .mount(&ds)
            .await;

        assert!(client_for(&kc, &ds).touch_session("s1").await.is_ok());
    }

    #[tokio::test]
    async fn test_second_401_is_a_failure() {
        let kc = token_server(2, 200).await;
        let ds = MockServer::start().await;
        Mock::given(method("POST"))
            .and(path(INTERACTION_PATH))
            .respond_with(ResponseTemplate::new(401))
            .expect(2)
            .mount(&ds)
            .await;

        assert!(matches!(
            client_for(&kc, &ds).touch_session("s1").await,
            Err(ClockError::Failed(_))
        ));
    }

    #[tokio::test]
    async fn test_server_error_is_a_failure_and_not_retried() {
        let kc = token_server(1, 200).await;
        let ds = MockServer::start().await;
        Mock::given(method("POST"))
            .and(path(INTERACTION_PATH))
            .respond_with(ResponseTemplate::new(500))
            .expect(1)
            .mount(&ds)
            .await;

        assert!(matches!(
            client_for(&kc, &ds).touch_session("s1").await,
            Err(ClockError::Failed(_))
        ));
    }

    #[tokio::test]
    async fn test_token_failure_is_a_failure() {
        let kc = token_server(1, 401).await;
        let ds = MockServer::start().await;

        assert!(matches!(
            client_for(&kc, &ds).touch_session("s1").await,
            Err(ClockError::Failed(_))
        ));
        assert!(ds.received_requests().await.unwrap().is_empty());
    }
}
