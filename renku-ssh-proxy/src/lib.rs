pub mod buildinfo;
pub(crate) mod config;
mod proxy_server;
pub(crate) mod data_services;
pub use config::Settings;
pub use config::generate_completions;
pub use proxy_server::serve_proxy;
