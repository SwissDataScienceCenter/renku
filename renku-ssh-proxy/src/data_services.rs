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

/// One read of a session's hibernation deadline. A `None` is a timestamp data-services could not use.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct SessionDeadline {
    pub remaining: Option<Duration>,
    pub since_input: Option<Duration>,
    pub alive: bool,
}

/// A data-services call that failed. `SessionGone` is final; `Failed` is retried next window.
#[derive(Debug)]
pub enum SessionError {
    SessionGone,
    Failed(color_eyre::Report),
}

pub type SessionResult<T> = std::result::Result<T, SessionError>;

/// What the timer needs from the data-services client, so tests can fake it.
/// Written with explicit `impl Future + Send` because `async fn` bounds cannot
/// prove `Send` for `tokio::spawn`.
pub(crate) trait SessionClient: Send + Sync {
    fn touch_session(&self, session: &str) -> impl Future<Output = SessionResult<()>> + Send;
    fn session_deadline(
        &self,
        session: &str,
    ) -> impl Future<Output = SessionResult<SessionDeadline>> + Send;
}

#[derive(serde::Deserialize)]
struct SessionResponse {
    #[serde(rename = "lastInteraction")]
    last_interaction: Option<String>,
    status: SessionStatus,
}

#[derive(serde::Deserialize)]
struct SessionStatus {
    state: String,
    will_hibernate_at: Option<String>,
}

/// A timestamp data-services could not use is a `None`, never an error.
fn parse_timestamp(value: Option<String>) -> Option<jiff::Timestamp> {
    value.and_then(|s| s.parse::<jiff::Timestamp>().ok())
}

fn non_negative_seconds(seconds: i64) -> Duration {
    Duration::from_secs(seconds.max(0) as u64)
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
    pub async fn touch_session(&self, session: &str) -> SessionResult<()> {
        let url = make_url(&self.data_services_url, ["sessions", session])
            .map_err(SessionError::Failed)?;
        log::debug!("Touch session interaction: {}", url);
        let resp = self
            .with_token(|token| {
                self.client
                    .patch(url.clone())
                    .bearer_auth(token)
                    .json(&serde_json::json!({"lastInteraction": "now"}))
            })
            .await?;
        if resp.status().is_success() {
            Ok(())
        } else {
            Err(SessionError::Failed(eyre!(
                "touch for session {session} failed: {}",
                resp.status()
            )))
        }
    }

    /// Reads how long the session has until hibernation. A 404 means it is gone.
    pub async fn session_deadline(&self, session: &str) -> SessionResult<SessionDeadline> {
        let url = make_url(&self.data_services_url, ["sessions", session])
            .map_err(SessionError::Failed)?;
        log::debug!("Read session deadline: {}", url);
        let resp = self
            .with_token(|token| self.client.get(url.clone()).bearer_auth(token))
            .await?;
        let status = resp.status();
        if status == reqwest::StatusCode::NOT_FOUND {
            return Err(SessionError::SessionGone);
        }
        if !status.is_success() {
            return Err(SessionError::Failed(eyre!(
                "deadline read for session {session} failed: {status}"
            )));
        }
        let body: SessionResponse = resp.json().await.map_err(|e| {
            SessionError::Failed(
                color_eyre::Report::new(e).wrap_err("parsing the session deadline"),
            )
        })?;
        let now = jiff::Timestamp::now();
        let deadline = parse_timestamp(body.status.will_hibernate_at);
        let interaction = parse_timestamp(body.last_interaction);
        Ok(SessionDeadline {
            remaining: deadline.map(|t| non_negative_seconds(t.as_second() - now.as_second())),
            since_input: interaction.map(|t| non_negative_seconds(now.as_second() - t.as_second())),
            // Only a terminal session is not alive: a readiness blip or a future
            // Amalthea state must not stop the proxy's per-connection timer.
            alive: !matches!(
                body.status.state.as_str(),
                "hibernated" | "failed" | "succeeded"
            ),
        })
    }

    /// Sends a request with the service token, refreshing and retrying once on 401.
    async fn with_token<F>(&self, build: F) -> SessionResult<reqwest::Response>
    where
        F: Fn(&str) -> reqwest::RequestBuilder,
    {
        let token = self
            .token_provider
            .token()
            .await
            .map_err(SessionError::Failed)?;
        let resp = build(&token)
            .send()
            .await
            .map_err(|e| SessionError::Failed(e.into()))?;
        if resp.status() != reqwest::StatusCode::UNAUTHORIZED {
            return Ok(resp);
        }
        let token = self
            .token_provider
            .refresh()
            .await
            .map_err(SessionError::Failed)?;
        let resp = build(&token)
            .send()
            .await
            .map_err(|e| SessionError::Failed(e.into()))?;
        if resp.status() == reqwest::StatusCode::UNAUTHORIZED {
            return Err(SessionError::Failed(eyre!(
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
impl SessionClient for Client {
    fn touch_session(&self, session: &str) -> impl Future<Output = SessionResult<()>> + Send {
        async move { Client::touch_session(self, session).await }
    }

    fn session_deadline(
        &self,
        session: &str,
    ) -> impl Future<Output = SessionResult<SessionDeadline>> + Send {
        async move { Client::session_deadline(self, session).await }
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
        let url = make_url(&base, ["sessions", "s1"]).unwrap();
        assert_eq!(url.as_str(), "http://ds/api/data/sessions/s1");
    }

    const TOKEN_PATH: &str = "/auth/realms/test/protocol/openid-connect/token";
    const SESSION_PATH: &str = "/api/data/sessions/s1";

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
    async fn test_touch_patches_last_interaction_now() {
        let kc = token_server(1, 200).await;
        let ds = MockServer::start().await;
        Mock::given(method("PATCH"))
            .and(path(SESSION_PATH))
            .and(header("authorization", "Bearer abc"))
            .and(body_string(r#"{"lastInteraction":"now"}"#))
            .respond_with(ResponseTemplate::new(200))
            .expect(1)
            .mount(&ds)
            .await;

        assert!(client_for(&kc, &ds).touch_session("s1").await.is_ok());
    }

    async fn deadline_response(body: String, status: u16) -> SessionResult<SessionDeadline> {
        let kc = token_server(1, 200).await;
        let ds = MockServer::start().await;
        Mock::given(method("GET"))
            .and(path(SESSION_PATH))
            .respond_with(ResponseTemplate::new(status).set_body_raw(body, "application/json"))
            .expect(1)
            .mount(&ds)
            .await;
        client_for(&kc, &ds).session_deadline("s1").await
    }

    #[tokio::test]
    async fn test_deadline_computes_remaining_and_since_input() {
        let now = jiff::Timestamp::now();
        let interaction = now - jiff::SignedDuration::from_secs(300);
        let hibernate = now + jiff::SignedDuration::from_secs(600);
        let body = format!(
            r#"{{"lastInteraction":"{interaction}","status":{{"state":"running","will_hibernate_at":"{hibernate}"}}}}"#
        );

        let deadline = deadline_response(body, 200).await.unwrap();
        let remaining = deadline.remaining.unwrap().as_secs();
        assert!((595..=605).contains(&remaining), "remaining={remaining}");
        let since_input = deadline.since_input.unwrap().as_secs();
        assert!(
            (295..=305).contains(&since_input),
            "since_input={since_input}"
        );
        assert!(deadline.alive);
    }

    #[tokio::test]
    async fn test_deadline_null_timestamps_are_none() {
        let body =
            r#"{"lastInteraction":null,"status":{"state":"running","will_hibernate_at":null}}"#
                .to_string();
        let deadline = deadline_response(body, 200).await.unwrap();
        assert_eq!(deadline.remaining, None);
        assert_eq!(deadline.since_input, None);
        assert!(deadline.alive);
    }

    #[tokio::test]
    async fn test_deadline_unparseable_timestamps_are_none() {
        let body = r#"{"lastInteraction":"not-a-date","status":{"state":"running","will_hibernate_at":"13 o'clock"}}"#
            .to_string();
        let deadline = deadline_response(body, 200).await.unwrap();
        assert_eq!(deadline.remaining, None);
        assert_eq!(deadline.since_input, None);
        assert!(deadline.alive);
    }

    #[tokio::test]
    async fn test_deadline_alive_unless_terminal() {
        for (state, expected) in [
            ("hibernated", false),
            ("failed", false),
            ("succeeded", false),
            ("running", true),
            ("starting", true),
            ("stopping", true),
            ("not-ready", true),
            ("some-future-state", true),
        ] {
            let body = format!(
                r#"{{"lastInteraction":null,"status":{{"state":"{state}","will_hibernate_at":null}}}}"#
            );
            let deadline = deadline_response(body, 200).await.unwrap();
            assert_eq!(deadline.alive, expected, "state={state}");
        }
    }

    #[tokio::test]
    async fn test_deadline_reports_gone_on_404() {
        assert!(matches!(
            deadline_response(String::new(), 404).await,
            Err(SessionError::SessionGone)
        ));
    }

    #[tokio::test]
    async fn test_deadline_server_error_is_failed_not_gone() {
        assert!(matches!(
            deadline_response(String::new(), 500).await,
            Err(SessionError::Failed(_))
        ));
    }

    #[tokio::test]
    async fn test_touch_refreshes_token_once_on_401() {
        let kc = token_server(2, 200).await;
        let ds = MockServer::start().await;
        Mock::given(method("PATCH"))
            .and(path(SESSION_PATH))
            .respond_with(ResponseTemplate::new(401))
            .up_to_n_times(1)
            .mount(&ds)
            .await;
        Mock::given(method("PATCH"))
            .and(path(SESSION_PATH))
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
        Mock::given(method("PATCH"))
            .and(path(SESSION_PATH))
            .respond_with(ResponseTemplate::new(401))
            .expect(2)
            .mount(&ds)
            .await;

        assert!(matches!(
            client_for(&kc, &ds).touch_session("s1").await,
            Err(SessionError::Failed(_))
        ));
    }

    #[tokio::test]
    async fn test_server_error_is_a_failure_and_not_retried() {
        let kc = token_server(1, 200).await;
        let ds = MockServer::start().await;
        Mock::given(method("PATCH"))
            .and(path(SESSION_PATH))
            .respond_with(ResponseTemplate::new(500))
            .expect(1)
            .mount(&ds)
            .await;

        assert!(matches!(
            client_for(&kc, &ds).touch_session("s1").await,
            Err(SessionError::Failed(_))
        ));
    }

    #[tokio::test]
    async fn test_token_failure_is_a_failure() {
        let kc = token_server(1, 401).await;
        let ds = MockServer::start().await;

        assert!(matches!(
            client_for(&kc, &ds).touch_session("s1").await,
            Err(SessionError::Failed(_))
        ));
        assert!(ds.received_requests().await.unwrap().is_empty());
    }
}
