// Reading the configuration from a file and allow override via CLI
// options and environment variables

use clap::Parser;
use clap_verbosity_flag::{Verbosity, VerbosityFilter};
use color_eyre::Help;
use color_eyre::eyre::{OptionExt, Result, WrapErr};
use directories::ProjectDirs;
use russh::Preferred;
use russh::server::Config as SshServerConfig;
use serde::{Deserialize, Deserializer};
use std::net::SocketAddr;
use std::path::{Path, PathBuf};
use std::sync::Arc;
use std::time::Duration;

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

    /// Path to the public key to authenticate at the target ssh
    #[arg(long, value_name = "FILE", env = "RENKU_SSH_PROXY_TARGET_KEY")]
    target_key: Option<PathBuf>,

    /// Be more verbose when logging. Verbosity increases with each occurrence.
    #[command(flatten)]
    log_level: Option<Verbosity>,

    /// Client inactivity timeout.
    #[arg(long)]
    inactivity_timeout: Option<humantime::Duration>,
}

/// Config-file schema. Every field is optional so partial files work.
#[derive(Debug, Default, Deserialize)]
#[serde(deny_unknown_fields)]
struct FileConfig {
    listen: Option<SocketAddr>,
    host_key: Option<PathBuf>,
    target_key: Option<PathBuf>,
    #[serde(deserialize_with = "deserialize_verbosity")]
    log_level: Option<Verbosity>,
    #[serde(with = "humantime_serde")]
    inactivity_timeout: Option<Duration>,
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
    pub target_key_file: PathBuf,
    pub log_level: Verbosity,
    pub ssh_server_config: Arc<SshServerConfig>,
}

impl Settings {
    fn resolve(cli: Cli, file: FileConfig) -> Result<Self> {
        let listen = cli
            .listen
            .or(file.listen)
            .unwrap_or_else(|| "127.0.0.1:2222".parse().unwrap());

        let log_level = cli
            .log_level
            .or(file.log_level)
            .unwrap_or_else(|| Verbosity::from(VerbosityFilter::Warn));

        let host_key_file = cli
            .host_key
            .or(file.host_key)
            .ok_or_eyre("missing `host_key`")
            .suggestion("pass --host-key or set `host_key` in the config file")?;

        let inactivity_timeout = cli
            .inactivity_timeout
            .map(|e| e.into())
            .or(file.inactivity_timeout)
            .unwrap_or_else(|| Duration::from_mins(30));

        let ssh_server_config = russh::server::Config {
            inactivity_timeout: Some(inactivity_timeout),
            auth_rejection_time: Duration::from_secs(3),
            auth_rejection_time_initial: Some(Duration::from_secs(0)),
            keys: vec![russh::keys::load_secret_key(&host_key_file, None)?],
            preferred: Preferred::default(),
            ..Default::default()
        };
        let ssh_server_config = Arc::new(ssh_server_config);

        let target_key_file = cli.target_key
            .or(file.target_key)
            .ok_or_eyre("missing `target_key`")
            .suggestion("Pass --target-key, set `target_key` in the config file or env var RENKU_SSH_PROXY_TARGET_KEY")?;

        Ok(Settings {
            listen,
            host_key_file,
            log_level,
            ssh_server_config,
            target_key_file,
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
    ProjectDirs::from("io", "renku", "ssh-proxy").map(|dirs| dirs.config_dir().join("config.toml"))
}
