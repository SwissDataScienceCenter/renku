//! Module for interacting with data services.

use crate::keycloak::TokenProvider;
use color_eyre::Result;
use color_eyre::eyre::eyre;
use reqwest::Url;
use ssh_key::PublicKey;
use std::sync::Arc;
use std::time::Duration;

pub struct Client {
    base_url: Url,
    client: reqwest::Client,
    tokens: Arc<TokenProvider>,
}

#[derive(serde::Serialize, Clone, Debug)]
#[serde(deny_unknown_fields)]
struct SessionAuthorizeRequest {
    pub public_key: String,
}

impl Client {
    pub fn new(base_url: &str, timeout: &Duration, tokens: Arc<TokenProvider>) -> Result<Client> {
        let uri = Url::parse(base_url)?;
        let client = reqwest::Client::builder().timeout(*timeout).build()?;
        Ok(Client {
            base_url: uri,
            client,
            tokens,
        })
    }

    fn make_url<I>(&self, items: I) -> Result<Url>
    where
        I: IntoIterator,
        I::Item: AsRef<str>,
    {
        let mut url = self.base_url.clone();
        {
            let mut path = url
                .path_segments_mut()
                .map_err(|_| eyre!("Cannot extends base url: {}", self.base_url))?;
            path.extend(items);
        }
        Ok(url)
    }

    pub async fn authorize_session(
        &self,
        public_key: &PublicKey,
        session_name: &str,
    ) -> Result<bool> {
        let url = self.make_url(&["internal", "sessions", session_name, "authorize"])?;
        log::debug!("Call to: {}", url);
        let openssh_key = public_key.to_openssh()?;
        let payload = SessionAuthorizeRequest {
            public_key: openssh_key,
        };
        let mut resp = self.post_authorize(&url, &payload).await?;
        if resp.status() == reqwest::StatusCode::UNAUTHORIZED {
            // The cached token may have expired between the cache check and the request.
            self.tokens.refresh().await?;
            resp = self.post_authorize(&url, &payload).await?;
        }
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

    async fn post_authorize(
        &self,
        url: &Url,
        payload: &SessionAuthorizeRequest,
    ) -> Result<reqwest::Response> {
        let token = self.tokens.token().await?;
        let resp = self
            .client
            .post(url.clone())
            .bearer_auth(&token)
            .json(payload)
            .send()
            .await?;
        Ok(resp)
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
use crate::keycloak::KeycloakSettings;
#[cfg(test)]
use wiremock::matchers::{header, method, path};
#[cfg(test)]
use wiremock::{Mock, MockServer, ResponseTemplate};

#[cfg(test)]
const TEST_KEY: &str =
    "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIPXhsNCQyI4HlAkaUIujCoGv3isiGoDR/MpS2yKlMfPY";

#[cfg(test)]
async fn test_client(server: &MockServer) -> Client {
    let settings = KeycloakSettings {
        url: server.uri(),
        realm: "test".to_string(),
        client_id: "ssh-proxy".to_string(),
        client_secret: "secret".to_string(),
    };
    let tokens = Arc::new(TokenProvider::new(&settings, &Duration::from_secs(5)).unwrap());
    Client::new(&server.uri(), &Duration::from_secs(5), tokens).unwrap()
}

#[cfg(test)]
fn token_response(token: &str) -> ResponseTemplate {
    ResponseTemplate::new(200).set_body_raw(
        format!(r#"{{"access_token":"{token}","expires_in":300}}"#),
        "application/json",
    )
}

#[cfg(test)]
async fn mount_token(server: &MockServer) {
    Mock::given(method("POST"))
        .and(path("/realms/test/protocol/openid-connect/token"))
        .respond_with(token_response("abc"))
        .expect(1)
        .up_to_n_times(1)
        .mount(server)
        .await;
    Mock::given(method("POST"))
        .and(path("/realms/test/protocol/openid-connect/token"))
        .respond_with(token_response("xyz"))
        .expect(1)
        .mount(server)
        .await;
}

#[tokio::test]
async fn test_authorize_retries_once_on_unauthorized() {
    let server = MockServer::start().await;
    mount_token(&server).await;
    Mock::given(method("POST"))
        .and(path("/internal/sessions/s1/authorize"))
        .and(header("authorization", "Bearer abc"))
        .respond_with(ResponseTemplate::new(401))
        .expect(1)
        .up_to_n_times(1)
        .mount(&server)
        .await;
    Mock::given(method("POST"))
        .and(path("/internal/sessions/s1/authorize"))
        .and(header("authorization", "Bearer xyz"))
        .respond_with(ResponseTemplate::new(204))
        .expect(1)
        .mount(&server)
        .await;

    let client = test_client(&server).await;
    let key = PublicKey::from_openssh(TEST_KEY).unwrap();
    assert!(client.authorize_session(&key, "s1").await.unwrap());
}

#[tokio::test]
async fn test_authorize_fails_closed_when_unauthorized_twice() {
    let server = MockServer::start().await;
    mount_token(&server).await;
    Mock::given(method("POST"))
        .and(path("/internal/sessions/s1/authorize"))
        .and(header("authorization", "Bearer abc"))
        .respond_with(ResponseTemplate::new(401))
        .expect(1)
        .up_to_n_times(1)
        .mount(&server)
        .await;
    Mock::given(method("POST"))
        .and(path("/internal/sessions/s1/authorize"))
        .and(header("authorization", "Bearer xyz"))
        .respond_with(ResponseTemplate::new(401))
        .expect(1)
        .mount(&server)
        .await;

    let client = test_client(&server).await;
    let key = PublicKey::from_openssh(TEST_KEY).unwrap();
    assert!(!client.authorize_session(&key, "s1").await.unwrap());
}
