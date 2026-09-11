use std::collections::HashMap;
use std::sync::Arc;

use crate::Settings;
use color_eyre::eyre::{OptionExt, Result};
use russh::keys::ssh_key::{HashAlg, PublicKey};
use russh::server::Server as _;
use russh::{Channel, ChannelId, ChannelMsg, Pty, Sig};
use russh::{client, server};
use tokio::net::TcpListener;
use tokio::sync::mpsc;

/// Creates and runs a proxy server
pub async fn serve_proxy(settings: &Settings) -> Result<()> {
    let mut ph = ProxyHandler::new().with_target(settings.target.clone());
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
        if let Some(pk) = &self.expected_host_key {
            Ok(server_public_key == pk)
        } else {
            Ok(true) //todo: don't allow that irl
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
    fixed_key: PublicKey,
}

static FIXED_KEY_STR: &str = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDizMVvJFeooFSwv6qovyXn8SyoFkzKXZFwZFyzAgEmwK8h+OOLrI/Wcea9XuMKIh1QOUdi7i9bmTkUUoMAbK4iFGvRP+ScrmZ9y/8asOj25Vvj484ZhRKv6edVCb4PZyebB2+bvURx7fgaxwzTphxz6ilaJ0bR7PEEWeXKAFLoPPY24ClVGc+MnsBXcyaOO7/ekpDRdrsYBxDltMM9fBOvDISdB8UITGM6tdycm6Lb0qwotdtEK8ly58s6BlWyLtQyIAtKpp7Z6ubCGi74wRBiXPe7GaIaGr8GteiG7NQwPnraNtZdyTQ3/KFqULCa9jrIDuCHhPkaS60AiEGJ6qhZWottRgTDSLk1JpWDfXBjC0ISg2cc1PyCsW2lrooIV3Fvfo/048IMH8x6T5oB37AaR6icKzLaW6RaXLm+/bHNv3tFUl+DGfKAVahThEw9al86JTz7npXhV+0Dlx0vxMKSZO+kMCcaqSXmkh5pp8WH5NoxQL3e9ZsSvexGCo2f0Fk=";

impl ProxyHandler {
    pub fn new() -> Self {
        Self {
            target: None,
            upstream: None,
            channels: HashMap::new(),
            fixed_key: PublicKey::from_openssh(FIXED_KEY_STR).unwrap(),
        }
    }

    pub fn with_target(mut self, target: Target) -> Self {
        self.target = Some(target);
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
        // let halg = client::Handle::best_supported_rsa_hash(&handle)
        //     .await?
        //     .unwrap_or(Some(ssh_key::HashAlg::Sha256));
        // log::debug!("Using hash-alg with target host: {:?}", halg);
        let auth = handle.authenticate_none(&target.user).await?;
        if !auth.success() {
            let auth = handle.authenticate_password(&target.user, "").await?;
            if !auth.success() {
                color_eyre::eyre::bail!(
                    "proxy failed to authenticate to session host: {:?}",
                    &auth
                );
            }
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
}

impl server::Handler for ProxyHandler {
    type Error = color_eyre::eyre::Error;

    async fn auth_publickey(
        &mut self,
        user: &str,
        public_key: &PublicKey,
    ) -> std::prelude::v1::Result<server::Auth, Self::Error> {
        if self.target.is_none() || user.trim().is_empty() {
            log::warn!("No target host set!");
            Ok(server::Auth::reject())
        } else {
            // The username is the session hostname
            log::debug!("Setting target host to {user}");
            self.set_target_host(user);
            //let pk = public_key.to_openssh();
            // todo: reach out to data_services

            if self.fixed_key.key_data() == public_key.key_data() {
                log::info!("Auth successful");
                Ok(server::Auth::Accept)
            } else {
                log::warn!(
                    "Auth failed due to wrong public key: {}",
                    public_key.fingerprint(HashAlg::Sha256)
                );
                Ok(server::Auth::reject())
            }
        }
    }

    async fn channel_open_session(
        &mut self,
        channel: Channel<server::Msg>,
        reply: server::ChannelOpenHandle,
        session: &mut server::Session,
    ) -> std::prelude::v1::Result<(), Self::Error> {
        log::debug!("Entering channel_open_session");
        let client_id = channel.id();
        let upstream = self.connect_target().await?;
        let up_channel = upstream.channel_open_session().await?;
        let (tx, rx) = mpsc::channel::<SshMsg>(64);
        self.channels.insert(client_id, tx);
        log::debug!(
            "Connected. Open ssh session to target host: {:?}",
            self.target
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
    ) -> std::prelude::v1::Result<(), Self::Error> {
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
    ) -> std::prelude::v1::Result<(), Self::Error> {
        log::debug!("Sending eof...");
        self.forward(&channel, SshMsg::Eof).await
    }

    async fn channel_close(
        &mut self,
        channel: ChannelId,
        _session: &mut server::Session,
    ) -> std::prelude::v1::Result<(), Self::Error> {
        log::debug!("Sending channel_close...");
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
    ) -> std::prelude::v1::Result<(), Self::Error> {
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
    ) -> std::prelude::v1::Result<(), Self::Error> {
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
    ) -> std::prelude::v1::Result<(), Self::Error> {
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
    ) -> std::prelude::v1::Result<(), Self::Error> {
        log::debug!("Sending subsystem request...");
        self.forward(&channel, SshMsg::Subsystem(name.to_string()))
            .await?;
        session.channel_success(channel)?;
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
        log::debug!("Closing client");
        self.channels.clear();
    }
}
