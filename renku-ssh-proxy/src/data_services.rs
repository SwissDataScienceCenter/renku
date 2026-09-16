//! Module for interacting with data services.

use color_eyre::Result;
use reqwest::Url;
use ssh_key::PublicKey;

pub struct Client {
    base_url: Url,
    client: reqwest::Client,
}

#[derive(serde::Serialize, Clone, Debug)]
#[serde(deny_unknown_fields)]
struct SessionAuthorizeRequest {
    pub public_key: String,
}

impl Client {
    pub fn new(base_url: &str) -> Result<Client> {
        let uri = Url::parse(base_url)?;
        let client = reqwest::Client::builder().build()?;
        Ok(Client {
            base_url: uri,
            client,
        })
    }

    fn make_url<I>(&self, items: I) -> Url
    where
        I: IntoIterator,
        I::Item: AsRef<str>,
    {
        let mut url = self.base_url.clone();
        {
            let mut path = url.path_segments_mut().unwrap();
            path.extend(items);
        }
        url
    }

    pub async fn authorize_session(
        &self,
        public_key: &PublicKey,
        session_name: &str,
    ) -> Result<bool> {
        let url = self.make_url(&["internal", "sessions", session_name, "authorize"]);
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
