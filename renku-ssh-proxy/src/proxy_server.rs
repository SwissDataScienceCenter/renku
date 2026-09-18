use std::collections::HashMap;
use std::sync::Arc;

use crate::Settings;
use crate::data_services::Client;
use crate::keycloak::TokenProvider;
use color_eyre::eyre::{OptionExt, Result};
use russh::keys::ssh_key::{HashAlg, PublicKey};
use russh::keys::{PrivateKey, PrivateKeyWithHashAlg};
use russh::server::Server as _;
use russh::{Channel, ChannelId, ChannelMsg, Pty, Sig};
use russh::{client, server};
use tokio::net::TcpListener;
use tokio::sync::mpsc;

/// Creates and runs a proxy server
pub async fn serve_proxy(settings: &Settings) -> Result<()> {
    let tokens = Arc::new(TokenProvider::new(
        &settings.keycloak,
        &settings.data_services_timeout,
    )?);
    let client = Client::new(
        &settings.data_services_url,
        &settings.data_services_timeout,
        tokens,
    )?;
    let mut ph = ProxyHandler::new(client)
        .with_target(settings.target.clone())
        .with_auth_key(settings.session_auth_key.0.clone());
    let socket = TcpListener::bind(settings.listen).await?;
    let server = ph.run_on_socket(settings.ssh_server_config.clone(), &socket);
    server.await?;
    Ok(())
}

#[derive(Clone, Debug)]
pub struct Target {
    pub host: String,
    pub port: u16,
    pub user: String,
    pub expected_host_key: Option<PublicKey>,
}

struct TargetHandler {
    expected_host_key: Option<PublicKey>,
}

impl client::Handler for TargetHandler {
    type Error = color_eyre::eyre::Error;

    async fn check_server_key(
        &mut self,
        server_public_key: &PublicKey,
    ) -> Result<bool, Self::Error> {
        match &self.expected_host_key {
            // compare key data only: the wire key carries no comment and the pinned key may
            Some(pk) => Ok(server_public_key.key_data() == pk.key_data()),
            None => Ok(false),
        }
    }
}

#[derive(Debug, Clone)]
pub enum SshMsg {
    Data(Vec<u8>),
    Signal(Sig),
    Pty {
        term: String,
        cols: u32,
        rows: u32,
        pixw: u32,
        pixh: u32,
        modes: Vec<(Pty, u32)>,
    },
    Shell,
    Eof,
    Exec(Vec<u8>),
    WindowChange {
        cols: u32,
        rows: u32,
        pixw: u32,
        pixh: u32,
    },
    Subsystem(String),
}

#[derive(Clone)]
pub struct ProxyHandler {
    pub target: Option<Target>,
    upstream: Option<Arc<client::Handle<TargetHandler>>>,
    channels: HashMap<ChannelId, mpsc::Sender<SshMsg>>,
    client: Arc<Client>,
    auth_key: Option<Arc<PrivateKey>>,
}

impl ProxyHandler {
    pub fn new(client: Client) -> Self {
        Self {
            target: None,
            upstream: None,
            channels: HashMap::new(),
            client: Arc::new(client),
            auth_key: None,
        }
    }

    pub fn with_target(mut self, target: Target) -> Self {
        self.target = Some(target);
        self
    }

    pub fn with_auth_key(mut self, key: Arc<PrivateKey>) -> Self {
        self.auth_key = Some(key);
        self
    }

    fn set_target_host<T: AsRef<str>>(&mut self, host: T) {
        if let Some(t) = &mut self.target {
            t.host = host.as_ref().to_string();
        }
    }

    async fn connect_target(&mut self) -> Result<Arc<client::Handle<TargetHandler>>> {
        if let Some(h) = &self.upstream {
            return Ok(h.clone());
        }
        log::debug!("Connecting to target host: {:?}", self.target);
        let target = self
            .target
            .clone()
            .ok_or_eyre("No session target host available")?;
        let config = Arc::new(client::Config::default());
        let mut handle = russh::client::connect(
            config,
            (target.host.as_str(), target.port),
            TargetHandler {
                expected_host_key: target.expected_host_key,
            },
        )
        .await?;
        let auth_key = self
            .auth_key
            .clone()
            .ok_or_eyre("no session auth key configured")?;
        let hash_alg = handle.best_supported_rsa_hash().await?.flatten();
        let auth = handle
            .authenticate_publickey(&target.user, PrivateKeyWithHashAlg::new(auth_key, hash_alg))
            .await?;
        if !auth.success() {
            color_eyre::eyre::bail!("proxy failed to authenticate to session host: {:?}", &auth);
        }

        let handle = Arc::new(handle);
        self.upstream = Some(handle.clone());
        Ok(handle)
    }

    async fn forward(&self, channel: &ChannelId, msg: SshMsg) -> Result<()> {
        if let Some(tx) = self.channels.get(channel) {
            let result = tx.send(msg).await;
            if let Err(err) = result {
                log::error!("Error forwarding ssh message: {:?}", err);
            }
        } else {
            log::warn!("No channel available for {:?}", channel);
        }
        Ok(())
    }

    fn is_allowed_forward_target(host: &str) -> bool {
        host.eq_ignore_ascii_case("localhost")
            || host
                .parse::<std::net::IpAddr>()
                .is_ok_and(|ip| ip.is_loopback())
    }
}

impl server::Handler for ProxyHandler {
    type Error = color_eyre::eyre::Error;

    async fn auth_publickey(&mut self, user: &str, public_key: &PublicKey) -> Result<server::Auth> {
        if self.target.is_none() || user.trim().is_empty() {
            log::warn!("No target host set!");
            Ok(server::Auth::reject())
        } else {
            // The username is the session hostname
            log::debug!("Setting target host to {user}");
            self.set_target_host(user);
            match self.client.authorize_session(public_key, user).await {
                Ok(true) => {
                    log::info!("Auth successful");
                    Ok(server::Auth::Accept)
                }
                Ok(false) => {
                    log::warn!(
                        "Auth failed for {}",
                        public_key.fingerprint(HashAlg::Sha256)
                    );
                    Ok(server::Auth::reject())
                }
                Err(err) => {
                    log::error!("Error obtaining authorization from data-services: {}", err);
                    Ok(server::Auth::reject())
                }
            }
        }
    }

    async fn channel_open_session(
        &mut self,
        channel: Channel<server::Msg>,
        reply: server::ChannelOpenHandle,
        session: &mut server::Session,
    ) -> Result<()> {
        log::debug!("Entering channel_open_session");
        let client_id = channel.id();
        let upstream = self.connect_target().await?;
        let up_channel = upstream.channel_open_session().await?;
        let (tx, rx) = mpsc::channel::<SshMsg>(64);
        self.channels.insert(client_id, tx);
        log::debug!(
            "Connected. Open ssh session to target host: {:?} on channel {}",
            self.target,
            client_id
        );

        let serve_handle = session.handle();
        tokio::spawn(proxy_channel(up_channel, rx, serve_handle, client_id));
        reply.accept().await;

        Ok(())
    }

    async fn data(
        &mut self,
        channel: ChannelId,
        data: &[u8],
        _session: &mut server::Session,
    ) -> Result<()> {
        log::debug!("Sending data...");
        self.forward(&channel, SshMsg::Data(data.to_vec())).await
    }

    async fn shell_request(
        &mut self,
        channel: ChannelId,
        session: &mut server::Session,
    ) -> Result<(), Self::Error> {
        log::debug!("Requesting shell...");
        let _ = self.forward(&channel, SshMsg::Shell).await;
        session.channel_success(channel)?;
        Ok(())
    }

    async fn channel_eof(
        &mut self,
        channel: ChannelId,
        _session: &mut server::Session,
    ) -> Result<()> {
        log::debug!("Sending eof...");
        self.forward(&channel, SshMsg::Eof).await
    }

    async fn channel_close(
        &mut self,
        channel: ChannelId,
        _session: &mut server::Session,
    ) -> Result<()> {
        log::debug!("Remove channel {} on channel_close...", channel);
        self.channels.remove(&channel);
        Ok(())
    }

    async fn signal(
        &mut self,
        channel: ChannelId,
        sig: Sig,
        _session: &mut server::Session,
    ) -> Result<(), Self::Error> {
        log::debug!("Sending signal {:?}...", sig);
        self.forward(&channel, SshMsg::Signal(sig)).await
    }

    async fn exec_request(
        &mut self,
        channel: ChannelId,
        data: &[u8],
        session: &mut server::Session,
    ) -> Result<()> {
        log::debug!("Sending exec...");
        self.forward(&channel, SshMsg::Exec(data.to_vec())).await?;
        session.channel_success(channel)?;
        Ok(())
    }

    async fn pty_request(
        &mut self,
        channel: ChannelId,
        term: &str,
        col_width: u32,
        row_height: u32,
        pix_width: u32,
        pix_height: u32,
        modes: &[(russh::Pty, u32)],
        _session: &mut server::Session,
    ) -> Result<()> {
        log::debug!("Sending pty request...");
        self.forward(
            &channel,
            SshMsg::Pty {
                term: term.to_string(),
                cols: col_width,
                rows: row_height,
                pixw: pix_width,
                pixh: pix_height,
                modes: modes.to_vec(),
            },
        )
        .await
    }

    async fn window_change_request(
        &mut self,
        channel: ChannelId,
        col_width: u32,
        row_height: u32,
        pix_width: u32,
        pix_height: u32,
        session: &mut server::Session,
    ) -> Result<()> {
        log::debug!("Sending window_change request...");
        self.forward(
            &channel,
            SshMsg::WindowChange {
                cols: col_width,
                rows: row_height,
                pixw: pix_width,
                pixh: pix_height,
            },
        )
        .await?;
        session.channel_success(channel)?;
        Ok(())
    }

    async fn subsystem_request(
        &mut self,
        channel: ChannelId,
        name: &str,
        session: &mut server::Session,
    ) -> Result<()> {
        log::debug!("Sending subsystem request...");
        self.forward(&channel, SshMsg::Subsystem(name.to_string()))
            .await?;
        session.channel_success(channel)?;
        Ok(())
    }

    async fn channel_open_direct_tcpip(
        &mut self,
        channel: Channel<server::Msg>,
        host_to_connect: &str,
        port_to_connect: u32,
        originator_address: &str,
        originator_port: u32,
        reply: server::ChannelOpenHandle,
        session: &mut server::Session,
    ) -> Result<()> {
        if ProxyHandler::is_allowed_forward_target(host_to_connect) {
            let client_id = channel.id();
            log::debug!(
                "Open direct tcpip channel ({}) to {}:{}...",
                client_id,
                host_to_connect,
                port_to_connect
            );
            let upstream = self.connect_target().await?;
            let up_channel_r = upstream
                .channel_open_direct_tcpip(
                    host_to_connect,
                    port_to_connect,
                    originator_address,
                    originator_port,
                )
                .await;
            match up_channel_r {
                Ok(up_channel) => {
                    let (tx, rx) = mpsc::channel::<SshMsg>(64);
                    self.channels.insert(client_id, tx);

                    let serve_handle = session.handle();
                    tokio::spawn(proxy_channel(up_channel, rx, serve_handle, client_id));

                    reply.accept().await;
                }
                Err(russh::Error::ChannelOpenFailure(cause)) => {
                    log::info!(
                        "Attempt to forward to port {} failed: {:?}",
                        port_to_connect,
                        cause
                    );
                    reply.reject(cause).await;
                }
                Err(err) => {
                    log::info!(
                        "Attempt to forward to port {} failed: {}",
                        port_to_connect,
                        err
                    );
                    reply.reject(russh::ChannelOpenFailure::ConnectFailed).await;
                }
            }
        } else {
            log::info!(
                "Don't allow forwarding to non-local host {}",
                host_to_connect
            );
            reply
                .reject(russh::ChannelOpenFailure::AdministrativelyProhibited)
                .await;
        }
        Ok(())
    }
}

/// Forwards ssh messages received from the channel to the client handler.
async fn proxy_channel(
    mut up: Channel<client::Msg>,
    mut rx: mpsc::Receiver<SshMsg>,
    client: server::Handle,
    client_id: ChannelId,
) {
    loop {
        tokio::select! {
            cmd = rx.recv() => {
                log::trace!("Received ssh-msg {:?}, sending upstream...", cmd);
                match cmd {
                    Some(SshMsg::Data(data)) => {
                        let _ = up.data(&data[..]).await;
                    }
                    Some(SshMsg::Pty{term, cols, rows, pixh, pixw, modes}) => {
                        let _ = up.request_pty(true, &term, cols, rows, pixw, pixh, &modes).await;
                    }
                    Some(SshMsg::Signal(sig)) => {
                        let _ = up.signal(sig).await;
                    }
                    Some(SshMsg::Shell) => {
                        let _ = up.request_shell(true).await;
                    }
                    Some(SshMsg::Eof) => {
                        let _ = up.eof().await;
                    }
                    Some(SshMsg::Exec(cmd)) => {
                        let _ = up.exec(true, cmd).await;
                    }
                    Some(SshMsg::WindowChange { cols, rows, pixw, pixh }) => {
                        let _ = up.window_change(cols, rows, pixw, pixh).await;
                    }
                    Some(SshMsg::Subsystem(name)) => {
                        let _ = up.request_subsystem(true, name).await;
                    }
                    None => {
                        let _ = up.eof().await;
                        let _ = up.close().await;
                        break;
                    }
                }
            },

            msg = up.wait() => {
                log::trace!("Received msg from upstream, send to local client: {:?}", msg);
                match msg {
                    Some(ChannelMsg::Data { data }) => {
                        let _ = client.data(client_id, data).await;
                    }
                    Some(ChannelMsg::ExtendedData { data, ext }) => {
                        let _ = client.extended_data(client_id,ext, data).await;
                    },
                    Some(ChannelMsg::Eof) => {
                        let _ = client.eof(client_id).await;
                    },
                    Some(ChannelMsg::ExitStatus { exit_status }) => {
                        let _ = client.exit_status_request(client_id, exit_status).await;
                    },
                    Some(ChannelMsg::Close) => {
                        let _ = client.close(client_id).await;
                        break;
                    },
                    None => {
                        let _ = client.close(client_id).await;
                        break;
                    },
                    Some(msg) => {
                        log::warn!("No upstream message handler available for {:?}", msg);
                    }
                }
            },
        }
    }
}

impl server::Server for ProxyHandler {
    type Handler = Self;
    fn new_client(&mut self, _: Option<std::net::SocketAddr>) -> Self {
        self.clone()
    }
    fn handle_session_error(&mut self, error: <Self::Handler as russh::server::Handler>::Error) {
        let cause = error.downcast_ref::<russh::Error>();
        if let Some(russh::Error::Disconnect) = cause {
            log::debug!("Client disconnected: {error:#?}");
        } else {
            log::error!("Session error: {error:#?}");
        }
    }
}

impl Drop for ProxyHandler {
    fn drop(&mut self) {
        log::debug!("Closing client. Clear all channels");
        self.channels.clear();
    }
}

#[test]
fn test_forward_allow_host() {
    assert!(ProxyHandler::is_allowed_forward_target("localhost"));
    assert!(ProxyHandler::is_allowed_forward_target("127.0.0.1"));
    assert!(ProxyHandler::is_allowed_forward_target("::1"));

    assert!(!ProxyHandler::is_allowed_forward_target("google.com"));
    assert!(!ProxyHandler::is_allowed_forward_target("80.17.21.131"));
}

#[tokio::test]
async fn test_check_server_key_pins_expected_and_rejects_other() {
    use russh::client::Handler as _;
    use russh::keys::ssh_key::PublicKey;
    // the pinned key is loaded from a .pub file and carries a comment; the key presented on the
    // wire has none, so the comparison must ignore comments
    let pinned = PublicKey::from_openssh(
        "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIHSWqeaS0b8NVxqu8dzb3fXmQzH/Kd5ClsYNMrXA9E+I pinned-comment",
    )
    .unwrap();
    let presented = PublicKey::from_openssh(
        "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIHSWqeaS0b8NVxqu8dzb3fXmQzH/Kd5ClsYNMrXA9E+I",
    )
    .unwrap();
    let other = PublicKey::from_openssh(
        "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAII+9SqY9JGLCxxsjnsNJKtwwNVYjjfd1VTHXHNZ/truJ t2",
    )
    .unwrap();
    let mut handler = TargetHandler {
        expected_host_key: Some(pinned),
    };
    assert!(handler.check_server_key(&presented).await.unwrap());
    assert!(!handler.check_server_key(&other).await.unwrap());
}

#[tokio::test]
async fn test_check_server_key_fails_closed_without_pin() {
    use russh::client::Handler as _;
    use russh::keys::ssh_key::PublicKey;
    let key = PublicKey::from_openssh(
        "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIHSWqeaS0b8NVxqu8dzb3fXmQzH/Kd5ClsYNMrXA9E+I t1",
    )
    .unwrap();
    let mut handler = TargetHandler {
        expected_host_key: None,
    };
    assert!(!handler.check_server_key(&key).await.unwrap());
}
