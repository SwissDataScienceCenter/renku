pub mod buildinfo;
pub(crate) mod config;
pub(crate) mod data_services;
pub(crate) mod keycloak;
mod proxy_server;
pub use config::Settings;
pub use config::generate_completions;
pub use proxy_server::serve_proxy;
