use color_eyre::Result;
/// Module for interacting with data services.
use reqwest::Url;
use ssh_key::PublicKey;

pub struct Client {
    base_url: Url,
    client: reqwest::Client,
}

#[derive(serde::Deserialize, serde::Serialize, Clone, Debug)]
#[serde(deny_unknown_fields)]
struct SessionAuthorizeRequest {
    pub public_key: ::std::string::String,
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

    pub async fn authorize_session(
        &self,
        public_key: &PublicKey,
        session_name: &str,
    ) -> Result<bool> {
        let url = self
            .base_url
            .join("internal")?
            .join("sessions")?
            .join(session_name)?
            .join("authorize")?;
        let openssh_key = public_key.to_openssh()?;
        let payload = SessionAuthorizeRequest {
            public_key: openssh_key,
        };
        let resp = self.client.post(url).json(&payload).send().await?;
        let success = resp.status().is_success();
        if success {
            log::debug!(
                "Call to {} authorized session {session_name}",
                self.base_url
            );
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
