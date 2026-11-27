use color_eyre::Result;

use crate::secret_name::SecretName;
use crate::ssh_key;
use russh::keys::PublicKey;
use std::fmt;

#[derive(Clone, Debug)]
pub enum KeySource {
    Name(SecretName),
    Inline(PublicKey),
}

// Algorithm names ssh-key understands: ssh-ed25519, ssh-rsa, ssh-dss,
// ecdsa-sha2-nistp*, sk-ssh-ed25519@openssh.com, sk-ecdsa-sha2-nistp256@openssh.com
const KEY_PREFIXES: &[&str] = &["ssh-", "ecdsa-sha2-", "sk-ssh-", "sk-ecdsa-"];

impl KeySource {
    pub fn parse<I>(input: I) -> Result<KeySource>
    where
        I: AsRef<str>,
    {
        let s = input.as_ref().trim();

        if KEY_PREFIXES.iter().any(|p| s.starts_with(p)) {
            log::debug!("Read public key as inline openssh key");
            let key = ssh_key::read_public_key_from_openssh(s)?;
            Ok(KeySource::Inline(key))
        } else {
            let sn = SecretName::parse(s)?;
            log::debug!("Read public key as secret name {}", sn);
            Ok(KeySource::Name(sn))
        }
    }

    pub fn load_as_public_key(&self) -> Result<PublicKey> {
        match self {
            KeySource::Inline(pk) => Ok(pk.clone()),
            KeySource::Name(sn) => sn.load_as_public_key(),
        }
    }

    pub fn render(&self) -> String {
        match self {
            KeySource::Inline(pk) => pk.to_openssh().unwrap(),
            KeySource::Name(sn) => sn.render(),
        }
    }
}
impl serde::Serialize for KeySource {
    fn serialize<S: serde::Serializer>(&self, serializer: S) -> Result<S::Ok, S::Error> {
        let s = &self.render();
        serializer.serialize_str(s)
    }
}

impl<'de> serde::Deserialize<'de> for KeySource {
    fn deserialize<D: serde::Deserializer<'de>>(deserializer: D) -> Result<Self, D::Error> {
        let s = String::deserialize(deserializer)?;
        Self::parse(s).map_err(serde::de::Error::custom)
    }
}
impl fmt::Display for KeySource {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.render())
    }
}
