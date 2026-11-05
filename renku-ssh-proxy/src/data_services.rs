//! Module for interacting with the secret service.

use color_eyre::Result;
use color_eyre::eyre::eyre;
use reqwest::Url;
use ssh_key::PublicKey;
use std::time::Duration;

pub struct Client {
    base_url: UrlType,
    client: reqwest::Client,
}

#[derive(Clone, Debug)]
enum UrlType {
    Base(Url),
    Pattern(String),
}

#[derive(serde::Serialize, Clone, Debug)]
#[serde(deny_unknown_fields)]
struct SessionAuthorizeRequest {
    pub public_key: String,
}

#[derive(serde::Serialize, serde::Deserialize, Clone, Debug)]
struct SessionAuthorizeResponse {
    pub target_host: String,
    pub target_port: Option<u16>,
}

fn extend_url<I>(base_url: &Url, items: I) -> Result<Url>
where
    I: IntoIterator,
    I::Item: AsRef<str>,
{
    let mut url = base_url.clone();
    {
        let mut path = url
            .path_segments_mut()
            .map_err(|_| eyre!("Cannot extend base url: {}", base_url))?;
        path.extend(items);
    }
    Ok(url)
}

#[derive(Clone, Debug)]
pub struct AuthResult {
    pub success: bool,
    pub target_host: Option<String>,
    pub target_port: Option<u16>,
}

impl Client {
    pub fn new(base_url: &str, timeout: &Duration) -> Result<Client> {
        let uri = if base_url.contains("{session_name}") {
            UrlType::Pattern(base_url.to_string())
        } else {
            UrlType::Base(Url::parse(base_url)?)
        };
        let client = reqwest::Client::builder().timeout(*timeout).build()?;
        Ok(Client {
            base_url: uri,
            client,
        })
    }

    fn make_authorize_url(&self, session_name: &str) -> Result<Url> {
        match &self.base_url {
            UrlType::Base(base) => {
                extend_url(base, &["internal", "sessions", session_name, "authorize"])
            }
            UrlType::Pattern(patt) => {
                let full_url = patt.replace("{session_name}", session_name);
                Ok(Url::parse(&full_url)?)
            }
        }
    }

    pub async fn authorize_session(
        &self,
        public_key: &PublicKey,
        session_name: &str,
    ) -> Result<AuthResult> {
        let url = self.make_authorize_url(session_name)?;
        log::debug!("Call to: {}", url);
        let openssh_key = public_key.to_openssh()?;
        let payload = SessionAuthorizeRequest {
            public_key: openssh_key,
        };
        let resp = self.client.post(url).json(&payload).send().await?;
        let success = resp.status().is_success();
        let mut target_host: Option<String> = None;
        let mut target_port: Option<u16> = None;
        if success {
            log::debug!("Authorized session {session_name} via public-key");
            if resp.status() == 200 {
                let session_resp: SessionAuthorizeResponse = resp.json().await?;
                target_host = Some(session_resp.target_host);
                target_port = session_resp.target_port;
            }
        } else {
            log::debug!("Failed to authorize public key: {}", payload.public_key);
            log::info!(
                "Call to {:?} denied access to session {session_name}: {}",
                self.base_url,
                resp.status()
            );
        }
        Ok(AuthResult {
            success,
            target_host,
            target_port,
        })
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
