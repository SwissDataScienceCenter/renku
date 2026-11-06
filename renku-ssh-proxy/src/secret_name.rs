//! Module defining a type for a secret name
use crate::ssh_key;
use color_eyre::Result;
use color_eyre::eyre::eyre;
use std::fmt;
use std::path::{Path, PathBuf};

#[derive(Clone, Debug)]
pub struct SecretName {
    pub name: String,
    pub key: String,
}

const DEFAULT_KEY: &str = "key";

impl SecretName {
    fn make_path(&self) -> PathBuf {
        Path::new("/secrets").join(&self.name).join(&self.key)
    }

    pub fn load_as_secret_key(&self) -> Result<ssh_key::SessionAuthKey> {
        let f = self.make_path();
        if !f.exists() {
            Err(eyre!(
                "The path '{:?}' derived from secret name {} doesn't exist.",
                &f,
                self
            ))
        } else {
            ssh_key::SessionAuthKey::load_from_file(&f)
        }
    }

    pub fn load_as_public_key(&self) -> Result<russh::keys::PublicKey> {
        let f = self.make_path();
        if !f.exists() {
            Err(eyre!(
                "The path '{:?}' derived from secret name {} doesn't exist.",
                &f,
                self
            ))
        } else {
            ssh_key::load_public_key_from_file(&f)
        }
    }

    pub fn render(&self) -> String {
        format!("{}/{}", self.name, self.key)
    }

    pub fn parse<I>(s: I) -> Result<SecretName>
    where
        I: AsRef<str>,
    {
        let parts: Vec<&str> = s.as_ref().split("/").collect();
        match parts.len() {
            1 => {
                let c = parts[0].trim();
                if c.is_empty() {
                    Err(eyre!("Empty secret name"))
                } else {
                    Ok(SecretName {
                        name: c.to_string(),
                        key: DEFAULT_KEY.to_string(),
                    })
                }
            }
            2 => {
                let n = parts[0].trim();
                let k = parts[1].trim();
                if n.is_empty() {
                    Err(eyre!("Empty secret name"))
                } else {
                    let kk = if k.is_empty() { DEFAULT_KEY } else { k };
                    Ok(SecretName {
                        name: n.to_string(),
                        key: kk.to_string(),
                    })
                }
            }
            _ => Err(eyre!("Invalid secret name, too many parts: {:?}", &parts)),
        }
    }
}
impl serde::Serialize for SecretName {
    fn serialize<S: serde::Serializer>(&self, serializer: S) -> Result<S::Ok, S::Error> {
        let s = &self.render();
        serializer.serialize_str(s)
    }
}

impl<'de> serde::Deserialize<'de> for SecretName {
    fn deserialize<D: serde::Deserializer<'de>>(deserializer: D) -> Result<Self, D::Error> {
        let s = String::deserialize(deserializer)?;
        Self::parse(s).map_err(serde::de::Error::custom)
    }
}
impl fmt::Display for SecretName {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}", self.render())
    }
}
