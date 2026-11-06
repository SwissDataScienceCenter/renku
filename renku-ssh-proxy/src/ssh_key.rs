//! Module to deal with ssh keys
use color_eyre::eyre::{Result, WrapErr};
use std::path::Path;
use std::sync::Arc;

pub fn load_private_key_from_file<P>(file: P) -> Result<russh::keys::PrivateKey>
where
    P: AsRef<Path>,
{
    russh::keys::load_secret_key(&file, None)
        .with_context(|| format!("reading private key {}", file.as_ref().display()))
}

pub fn load_public_key_from_file<P>(file: P) -> Result<russh::keys::PublicKey>
where
    P: AsRef<Path>,
{
    russh::keys::load_public_key(&file)
        .with_context(|| format!("reading public key {}", file.as_ref().display()))
}

/// The private key the proxy uses to authenticate to session hosts.
/// Its `Debug` redacts the key material.
#[derive(Clone)]
pub struct SessionAuthKey(pub Arc<russh::keys::PrivateKey>);

impl SessionAuthKey {
    pub fn load_from_file<P>(file: P) -> Result<SessionAuthKey>
    where
        P: AsRef<Path>,
    {
        Ok(SessionAuthKey(Arc::new(load_private_key_from_file(file)?)))
    }
}

impl std::fmt::Debug for SessionAuthKey {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        f.write_str("SessionAuthKey(***)")
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_load_public_key_from_file() {
        let path =
            std::env::temp_dir().join(format!("session-host-pub-{}.pub", std::process::id()));
        std::fs::write(
            &path,
            "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIHSWqeaS0b8NVxqu8dzb3fXmQzH/Kd5ClsYNMrXA9E+I t1\n",
        )
        .unwrap();
        let key = load_public_key_from_file(&path).unwrap();
        std::fs::remove_file(&path).ok();
        assert_eq!(key.algorithm(), russh::keys::Algorithm::Ed25519);
    }
}
