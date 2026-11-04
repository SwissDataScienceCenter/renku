pub(crate) mod activity;
pub mod buildinfo;
pub(crate) mod config;
pub(crate) mod data_services;

pub(crate) mod key_source;
pub(crate) mod keycloak;
mod proxy_server;
pub(crate) mod secret_name;
pub(crate) mod ssh_key;
pub(crate) mod timer;
pub(crate) mod warning;
pub use config::Settings;
pub use config::generate_completions;
pub use proxy_server::serve_proxy;
