// Reading the configuration from a file and allow override via CLI
// options and environment variables

use crate::keycloak::KeycloakSettings;
use crate::proxy_server::Target;
use crate::ssh_key;
use crate::ssh_key::SessionAuthKey;
use clap::CommandFactory;
use clap::Parser;
use clap_complete::CompleteEnv;
use clap_verbosity_flag::{Verbosity, VerbosityFilter};
use color_eyre::Help;
use color_eyre::eyre::{OptionExt, Result, WrapErr};
use russh::Preferred;
use russh::server::Config as SshServerConfig;
use serde::{Deserialize, Deserializer};
use std::net::SocketAddr;
use std::path::{Path, PathBuf};
use std::sync::Arc;
use std::time::Duration;

/// Generates the cli completions and exits.
pub fn generate_completions() {
    CompleteEnv::with_factory(Cli::command).complete();
}

/// Renku SSH proxy service.
#[derive(Debug, Parser)]
#[command(name = "renku-ssh-proxy", about)]
struct Cli {
    /// Path to a config file (overrides the default location).
    #[arg(short, long, value_name = "FILE", env = "RENKU_SSH_PROXY_CONFIG_FILE")]
    config: Option<PathBuf>,

    /// Address to listen on, e.g. 127.0.0.1:2222
    #[arg(short, long, env = "RENKU_SSH_PROXY_LISTEN")]
    listen: Option<SocketAddr>,

    /// Path to the server host key.
    #[arg(long, value_name = "FILE", env = "RENKU_SSH_PROXY_HOST_KEY")]
    host_key: Option<PathBuf>,

    /// Path to the pinned session host public key.
    #[arg(long, value_name = "FILE", env = "RENKU_SSH_PROXY_SESSION_HOST_KEY")]
    session_host_key: Option<PathBuf>,

    /// Path to the private key used to authenticate to session hosts.
    #[arg(long, value_name = "FILE", env = "RENKU_SSH_PROXY_SESSION_AUTH_KEY")]
    session_auth_key: Option<PathBuf>,

    /// The ssh port of the target server
    #[arg(long, env = "RENKU_SSH_PROXY_TARGET_PORT")]
    target_port: Option<u16>,

    /// username of the target user
    #[arg(long, env = "RENKU_SSH_PROXY_TARGET_USER")]
    target_user: Option<String>,

    #[arg(long, env = "RENKU_SSH_PROXY_SECRET_SERVICE_URL")]
    secret_service_url: Option<String>,

    /// Keycloak base URL, e.g. https://keycloak.example.com/auth/
    #[arg(long, env = "RENKU_SSH_PROXY_KEYCLOAK_URL")]
    keycloak_url: Option<String>,

    /// Keycloak realm that holds the ssh-proxy client.
    #[arg(long, env = "RENKU_SSH_PROXY_KEYCLOAK_REALM")]
    keycloak_realm: Option<String>,

    /// Keycloak client id for the client-credentials grant.
    #[arg(long, env = "RENKU_SSH_PROXY_KEYCLOAK_CLIENT_ID")]
    keycloak_client_id: Option<String>,

    /// Keycloak client secret for the client-credentials grant.
    #[arg(long, env = "RENKU_SSH_PROXY_KEYCLOAK_CLIENT_SECRET")]
    keycloak_client_secret: Option<String>,

    /// Base URL of the data-services API, ending in /api/data.
    #[arg(long, env = "RENKU_SSH_PROXY_DATA_SERVICES_URL")]
    data_services_url: Option<String>,

    #[arg(long)]
    data_services_timeout: Option<HumanDuration>,

    /// Be more verbose when logging. Verbosity increases with each occurrence.
    #[command(flatten)]
    log_level: Option<Verbosity>,

    /// Client inactivity timeout.
    #[arg(long)]
    inactivity_timeout: Option<HumanDuration>,
}

/// Config-file schema. Every field is optional so partial files work.
#[derive(Debug, Deserialize)]
#[serde(deny_unknown_fields)]
#[serde(default)]
struct FileConfig {
    listen: Option<SocketAddr>,
    host_key: Option<PathBuf>,
    session_host_key: Option<PathBuf>,
    session_auth_key: Option<PathBuf>,
    #[serde(deserialize_with = "deserialize_verbosity")]
    log_level: Option<Verbosity>,
    target_port: Option<u16>,
    target_user: Option<String>,
    inactivity_timeout: Option<HumanDuration>,
    secret_service_url: Option<String>,
    keycloak_url: Option<String>,
    keycloak_realm: Option<String>,
    keycloak_client_id: Option<String>,
    keycloak_client_secret: Option<String>,
    data_services_url: Option<String>,
    data_services_timeout: Option<HumanDuration>,
}

impl Default for FileConfig {
    fn default() -> Self {
        Self {
            listen: "127.0.0.1:2222".parse().ok(),
            host_key: None,
            session_host_key: None,
            session_auth_key: None,
            log_level: Some(Verbosity::from(VerbosityFilter::Warn)),
            target_port: Some(80),
            target_user: Some("renku".to_string()),
            inactivity_timeout: Some(HumanDuration(Duration::from_mins(30))),
            secret_service_url: None,
            keycloak_url: None,
            keycloak_realm: None,
            keycloak_client_id: None,
            keycloak_client_secret: None,
            data_services_url: None,
            data_services_timeout: Some(HumanDuration(Duration::from_mins(1))),
        }
    }
}

fn deserialize_verbosity<'de, D>(
    deserializer: D,
) -> core::result::Result<Option<Verbosity>, D::Error>
where
    D: Deserializer<'de>,
{
    let s = String::deserialize(deserializer)?.to_lowercase();
    match s.as_str() {
        "off" => Ok(Some(Verbosity::from(VerbosityFilter::Off))),
        "error" => Ok(Some(Verbosity::from(VerbosityFilter::Error))),
        "warn" => Ok(Some(Verbosity::from(VerbosityFilter::Warn))),
        "info" => Ok(Some(Verbosity::from(VerbosityFilter::Info))),
        "debug" => Ok(Some(Verbosity::from(VerbosityFilter::Debug))),
        "trace" => Ok(Some(Verbosity::from(VerbosityFilter::Trace))),
        _ => Ok(None),
    }
}

/// A non-negative duration parsed from a human-friendly string such as
/// `30m`, `1h 30m`, `90s`, `2 minutes` or ISO 8601 (`PT30M`).
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
struct HumanDuration(Duration);

impl std::str::FromStr for HumanDuration {
    type Err = String;

    fn from_str(s: &str) -> core::result::Result<Self, Self::Err> {
        let sd: jiff::SignedDuration = s
            .trim()
            .parse()
            .map_err(|e| format!("invalid duration `{s}`: {e}"))?;
        Duration::try_from(sd)
            .map(HumanDuration)
            .map_err(|_| format!("duration `{s}` must not be negative"))
    }
}

impl<'de> Deserialize<'de> for HumanDuration {
    fn deserialize<D: Deserializer<'de>>(deserializer: D) -> core::result::Result<Self, D::Error> {
        String::deserialize(deserializer)?
            .parse()
            .map_err(serde::de::Error::custom)
    }
}

impl FileConfig {
    pub fn load(path: &Path) -> Result<FileConfig> {
        let text = std::fs::read_to_string(path)
            .with_context(|| format!("reading config file {}", path.display()))?;
        let cfg = toml::from_str(&text)
            .with_context(|| format!("parsing config file {}", path.display()))?;
        Ok(cfg)
    }
}

/// Fully resolved settings.
#[derive(Debug)]
pub struct Settings {
    pub listen: SocketAddr,
    pub host_key_file: PathBuf,
    pub(crate) session_auth_key: SessionAuthKey,
    pub log_level: Verbosity,
    pub ssh_server_config: Arc<SshServerConfig>,
    pub target: Target,
    pub secret_service_url: String,
    pub data_services_timeout: Duration,
    pub keycloak: KeycloakSettings,
    pub data_services_url: String,
}

/// Keycloak and data-services values for the session deadline, resolved together so
/// tests do not need the SSH key files `Settings::resolve` loads.
fn resolve_data_services(cli: &Cli, file: &FileConfig) -> Result<(KeycloakSettings, String)> {
    let keycloak_url = cli
        .keycloak_url
        .clone()
        .or_else(|| file.keycloak_url.clone())
        .ok_or_eyre("missing `keycloak_url`")
        .suggestion("pass --keycloak-url or set RENKU_SSH_PROXY_KEYCLOAK_URL")?;
    let keycloak_realm = cli
        .keycloak_realm
        .clone()
        .or_else(|| file.keycloak_realm.clone())
        .ok_or_eyre("missing `keycloak_realm`")
        .suggestion("pass --keycloak-realm or set RENKU_SSH_PROXY_KEYCLOAK_REALM")?;
    let keycloak_client_id = cli
        .keycloak_client_id
        .clone()
        .or_else(|| file.keycloak_client_id.clone())
        .ok_or_eyre("missing `keycloak_client_id`")
        .suggestion("pass --keycloak-client-id or set RENKU_SSH_PROXY_KEYCLOAK_CLIENT_ID")?;
    let keycloak_client_secret = cli
        .keycloak_client_secret
        .clone()
        .or_else(|| file.keycloak_client_secret.clone())
        .ok_or_eyre("missing `keycloak_client_secret`")
        .suggestion(
            "pass --keycloak-client-secret or set RENKU_SSH_PROXY_KEYCLOAK_CLIENT_SECRET",
        )?;
    let data_services_url = cli
        .data_services_url
        .clone()
        .or_else(|| file.data_services_url.clone())
        .ok_or_eyre("missing `data_services_url`")
        .suggestion("pass --data-services-url or set RENKU_SSH_PROXY_DATA_SERVICES_URL")?;

    let keycloak = KeycloakSettings {
        url: keycloak_url,
        realm: keycloak_realm,
        client_id: keycloak_client_id,
        client_secret: keycloak_client_secret,
    };
    Ok((keycloak, data_services_url))
}

impl Settings {
    fn resolve(cli: Cli, file: FileConfig) -> Result<Self> {
        let (keycloak, data_services_url) = resolve_data_services(&cli, &file)?;

        let listen = cli.listen.or(file.listen).unwrap();

        let log_level = cli.log_level.or(file.log_level).unwrap();

        let host_key_file = cli
            .host_key
            .or(file.host_key)
            .ok_or_eyre("missing `host_key`")
            .suggestion("pass --host-key or set `host_key` in the config file")?;

        let session_host_key_file = cli
            .session_host_key
            .or(file.session_host_key)
            .ok_or_eyre("missing `session_host_key`")
            .suggestion("pass --session-host-key or set `session_host_key` in the config file")?;
        let session_auth_key_file = cli
            .session_auth_key
            .or(file.session_auth_key)
            .ok_or_eyre("missing `session_auth_key`")
            .suggestion("pass --session-auth-key or set `session_auth_key` in the config file")?;
        let session_host_key = ssh_key::load_public_key_from_file(&session_host_key_file)?;
        let session_auth_key = SessionAuthKey::load_from_file(&session_auth_key_file)?;

        let inactivity_timeout = cli
            .inactivity_timeout
            .or(file.inactivity_timeout)
            .unwrap()
            .0;

        // expose as config if ops ever need tuning.
        // Keepalives reap half-open clients and keep NAT/LB idle-timeouts from
        // silently dropping long-lived interactive sessions.
        const MAX_AUTH_ATTEMPTS: usize = 3;
        const KEEPALIVE_INTERVAL: Duration = Duration::from_secs(30);
        const KEEPALIVE_MAX: usize = 3;

        let ssh_server_config = russh::server::Config {
            inactivity_timeout: Some(inactivity_timeout),
            auth_rejection_time: Duration::from_secs(3),
            auth_rejection_time_initial: Some(Duration::from_secs(0)),
            max_auth_attempts: MAX_AUTH_ATTEMPTS,
            keepalive_interval: Some(KEEPALIVE_INTERVAL),
            keepalive_max: KEEPALIVE_MAX,
            keys: vec![ssh_key::load_private_key_from_file(&host_key_file)?],
            preferred: Preferred::default(),
            ..Default::default()
        };
        let ssh_server_config = Arc::new(ssh_server_config);

        let target_user = cli.target_user.or(file.target_user).unwrap();
        let target_port = cli.target_port.or(file.target_port).unwrap();
        let target = Target {
            host: "".to_string(),
            port: target_port,
            user: target_user,
            expected_host_key: Some(session_host_key),
            auth_key: None,
        };
        let secret_service_url = cli
            .secret_service_url
            .or(file.secret_service_url)
            .ok_or_eyre("missing `secret_service_url`")
            .suggestion(
                "pass --secret-service-url or set `secret_service_url` in the config file",
            )?;

        let data_services_timeout = cli
            .data_services_timeout
            .or(file.data_services_timeout)
            .unwrap()
            .0;

        Ok(Settings {
            listen,
            host_key_file,
            session_auth_key,
            log_level,
            ssh_server_config,
            target,
            secret_service_url,
            data_services_timeout,
            keycloak,
            data_services_url,
        })
    }

    pub fn create() -> Result<Settings> {
        let cli = Cli::parse();
        let file = match &cli.config {
            Some(path) => FileConfig::load(path)?,
            None => match default_config_path() {
                Some(path) if path.exists() => FileConfig::load(&path)?,
                _ => FileConfig::default(),
            },
        };
        Settings::resolve(cli, file)
    }
}

fn default_config_path() -> Option<PathBuf> {
    "/config/renku/ssh_proxy".parse().ok()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_data_services_config_rejects_missing_values() {
        let cli = Cli::try_parse_from([
            "renku-ssh-proxy",
            "--data-services-url",
            "http://ds/api/data",
        ])
        .unwrap();
        let err = resolve_data_services(&cli, &FileConfig::default()).unwrap_err();
        assert!(format!("{err:#}").contains("keycloak_url"), "{err:#}");
    }

    #[test]
    fn test_data_services_config_accepts_all_values() {
        let cli = Cli::try_parse_from([
            "renku-ssh-proxy",
            "--keycloak-url",
            "http://kc/auth/",
            "--keycloak-realm",
            "Renku",
            "--keycloak-client-id",
            "ssh-proxy",
            "--keycloak-client-secret",
            "s",
            "--data-services-url",
            "http://ds/api/data",
        ])
        .unwrap();
        let (keycloak, data_services_url) =
            resolve_data_services(&cli, &FileConfig::default()).unwrap();
        assert_eq!(keycloak.client_id, "ssh-proxy");
        assert_eq!(data_services_url, "http://ds/api/data");
    }

    fn d(s: &str) -> Duration {
        s.parse::<HumanDuration>().unwrap().0
    }

    #[test]
    fn parses_human_durations() {
        assert_eq!(d("30m"), Duration::from_mins(30));
        assert_eq!(d("1h 30m"), Duration::from_mins(90));
        assert_eq!(d("1h30m"), Duration::from_mins(90));
        assert_eq!(d("90s"), Duration::from_secs(90));
        assert_eq!(d("500ms"), Duration::from_millis(500));
        assert_eq!(d("2 minutes"), Duration::from_mins(2));
        assert_eq!(d("PT30M"), Duration::from_mins(30));
    }

    #[test]
    fn rejects_bad_durations() {
        assert!("-5s".parse::<HumanDuration>().is_err());
        assert!("soon".parse::<HumanDuration>().is_err());
        assert!("".parse::<HumanDuration>().is_err());
    }

    #[test]
    fn file_config_reads_duration_strings() {
        let cfg: FileConfig =
            toml::from_str("inactivity_timeout = \"45m\"\ndata_services_timeout = \"10s\"")
                .unwrap();
        assert_eq!(
            cfg.inactivity_timeout,
            Some(HumanDuration(Duration::from_mins(45)))
        );
        assert_eq!(
            cfg.data_services_timeout,
            Some(HumanDuration(Duration::from_secs(10)))
        );
    }
}
