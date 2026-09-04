// Reading the configuration from a file and allow override via CLI
// options and environment variables

use crate::proxy_server::Target;
use base64::{Engine as _, engine::general_purpose::STANDARD};
use clap::CommandFactory;
use clap::Parser;
use clap_complete::CompleteEnv;
use clap_verbosity_flag::{Verbosity, VerbosityFilter};
use color_eyre::Help;
use color_eyre::eyre::{OptionExt, Result, WrapErr};
use directories::ProjectDirs;
use russh::Preferred;
use russh::server::Config as SshServerConfig;
use serde::{Deserialize, Deserializer};
use std::fs;
use std::net::SocketAddr;
use std::path::{Path, PathBuf};
use std::sync::Arc;
use std::time::Duration;

/// Generates the cli completions and exits.
pub fn generate_completions() {
    CompleteEnv::with_factory(Cli::command).complete();
}

fn load_private_key_from_file<P>(file: P) -> Result<russh::keys::PrivateKey>
where
    P: AsRef<Path>,
{
    match russh::keys::load_secret_key(&file, None) {
        Ok(pk) => Ok(pk),
        Err(_) => {
            // try base64 decoding the contents
            let contents = fs::read_to_string(&file)?;
            let decoded = STANDARD.decode(contents.trim())?;
            let decoded = &String::from_utf8_lossy(&decoded);
            let pk = russh::keys::decode_secret_key(decoded, None)?;
            Ok(pk)
        }
    }
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

    /// Path to the public key to authenticate at the target ssh
    #[arg(long, value_name = "FILE", env = "RENKU_SSH_PROXY_TARGET_KEY")]
    target_key: Option<PathBuf>,

    /// Host name of the target server
    #[arg(long, env = "RENKU_SSH_PROXY_TARGET_HOST")]
    target_host: String,

    /// The ssh port of the target server
    #[arg(long, default_value_t = 22, env = "RENKU_SSH_PROXY_TARGET_PORT")]
    target_port: u16,

    /// username of the target user
    #[arg(long, env = "RENKU_SSH_PROXY_TARGET_USER")]
    target_user: String,

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
    pub log_level: Verbosity,
    pub ssh_server_config: Arc<SshServerConfig>,
    pub target: Target, // temporarily use a fixed target host
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
            keys: vec![load_private_key_from_file(&host_key_file)?],
            preferred: Preferred::default(),
            ..Default::default()
        };
        let ssh_server_config = Arc::new(ssh_server_config);

        let target_key_file = cli.target_key
            .or(file.target_key)
            .ok_or_eyre("missing `target_key`")
            .suggestion("Pass --target-key, set `target_key` in the config file or env var RENKU_SSH_PROXY_TARGET_KEY")?;

        let target = Target {
            host: cli.target_host,
            port: cli.target_port,
            user: cli.target_user,
            key_path: target_key_file,
            expected_host_key: None,
        };
        Ok(Settings {
            listen,
            host_key_file,
            log_level,
            ssh_server_config,
            target,
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
