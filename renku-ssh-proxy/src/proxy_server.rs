use std::collections::HashMap;
use std::sync::Arc;

use color_eyre::eyre::{OptionExt, Result};
use russh::client;
use russh::keys::ssh_key::PublicKey;
use russh::keys::{PrivateKeyWithHashAlg, load_secret_key};
use russh::server;
use russh::{Channel, ChannelId, Pty, Sig};
use std::path::PathBuf;
use tokio::sync::mpsc;

#[derive(Clone, Debug)]
pub struct Target {
    pub host: String,
    pub port: u16,
    pub user: String,
    pub key_path: PathBuf,
    pub expected_host_key: Option<PublicKey>,
}

struct UpstreamHandler {
    expected_host_key: Option<PublicKey>,
}

impl client::Handler for UpstreamHandler {
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

pub enum Cmd {
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
}

#[derive(Clone)]
pub struct ProxyHandler {
    pub target: Option<Target>,
    upstream: Option<Arc<client::Handle<UpstreamHandler>>>,
    channels: HashMap<ChannelId, mpsc::Sender<Cmd>>,
}

impl ProxyHandler {
    pub fn new() -> Self {
        Self {
            target: None,
            upstream: None,
            channels: HashMap::new(),
        }
    }

    async fn connect_target(&mut self) -> Result<Arc<client::Handle<UpstreamHandler>>> {
        if let Some(h) = &self.upstream {
            return Ok(h.clone());
        }
        log::debug!("Connecting to target host: {:?}", &self.target);
        let target = self
            .target
            .clone()
            .ok_or_eyre("No session target host available")?;
        let config = Arc::new(client::Config::default());
        let mut handle = russh::client::connect(
            config,
            (target.host.as_str(), target.port),
            UpstreamHandler {
                expected_host_key: target.expected_host_key,
            },
        )
        .await?;
        log::info!(
            "Created client handle. Loading key from {:?}",
            &target.key_path
        );
        let key = load_secret_key(&target.key_path, None)?;
        let halg = client::Handle::best_supported_rsa_hash(&handle)
            .await?
            .unwrap_or_else(|| Some(ssh_key::HashAlg::Sha256));
        log::info!("Using hash-alg: {:?}", halg);
        let auth = handle
            .authenticate_publickey(
                &target.user,
                PrivateKeyWithHashAlg::new(Arc::new(key), halg),
            )
            .await?;
        if !auth.success() {
            color_eyre::eyre::bail!("proxy failed to authenticate to session host");
        }

        let handle = Arc::new(handle);
        self.upstream = Some(handle.clone());
        Ok(handle)
    }
}

impl server::Handler for ProxyHandler {
    type Error = color_eyre::eyre::Error;

    async fn auth_publickey(
        &mut self,
        _user: &str,
        _public_key: &ssh_key::PublicKey,
    ) -> std::prelude::v1::Result<server::Auth, Self::Error> {
        self.target = Some(Target {
            host: "eknet.org".to_string(),
            port: 22,
            user: "eike".to_string(),
            key_path: PathBuf::from("/home/sdsc/workspace/renku/renku-ssh-proxy/id_rsa_key"),
            expected_host_key: None,
        });
        Ok(server::Auth::Accept)
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
        let (tx, rx) = mpsc::channel::<Cmd>(64);
        self.channels.insert(client_id, tx);
        log::debug!(
            "Connected. Open ssh session to target host: {:?}",
            &self.target
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
        if let Some(tx) = self.channels.get(&channel) {
            let _ = tx.send(Cmd::Data(data.to_vec())).await;
        }
        Ok(())
    }

    async fn shell_request(
        &mut self,
        channel: ChannelId,
        session: &mut server::Session,
    ) -> Result<(), Self::Error> {
        log::debug!("Requesting shell...");
        if let Some(tx) = self.channels.get(&channel) {
            let _ = tx.send(Cmd::Shell).await;
        }
        session.channel_success(channel)?;
        Ok(())
    }

    async fn channel_eof(
        &mut self,
        channel: ChannelId,
        _session: &mut server::Session,
    ) -> std::prelude::v1::Result<(), Self::Error> {
        log::debug!("Sending eof...");
        if let Some(tx) = self.channels.get(&channel) {
            let _ = tx.send(Cmd::Eof).await;
        }
        Ok(())
    }

    async fn signal(
        &mut self,
        channel: ChannelId,
        sig: Sig,
        _session: &mut server::Session,
    ) -> Result<(), Self::Error> {
        log::debug!("Sending signal {:?}...", &sig);
        if let Some(tx) = self.channels.get(&channel) {
            let _ = tx.send(Cmd::Signal(sig)).await;
        }
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
        if let Some(tx) = self.channels.get(&channel) {
            let _ = tx
                .send(Cmd::Pty {
                    term: term.to_string(),
                    cols: col_width,
                    rows: row_height,
                    pixw: pix_width,
                    pixh: pix_height,
                    modes: modes.to_vec(),
                })
                .await?;
        }
        Ok(())
    }
}

async fn proxy_channel(
    up: Channel<client::Msg>,
    mut rx: mpsc::Receiver<Cmd>,
    _client: server::Handle,
    _client_id: ChannelId,
) {
    loop {
        tokio::select! {
            cmd = rx.recv() => match cmd {
                Some(Cmd::Data(data)) => {
                    let _ = up.data(&data[..]).await;
                }
                Some(Cmd::Pty{term, cols, rows, pixh, pixw, modes}) => {
                    let _ = up.request_pty(true, &term, cols, rows, pixw, pixh, &modes).await;
                }
                Some(Cmd::Signal(sig)) => {
                    let _ = up.signal(sig).await;
                }
                Some(Cmd::Shell) => {
                    let _ = up.request_shell(true).await;
                }
                Some(Cmd::Eof) => {
                    let _ = up.eof().await;
                }
                None => {
                    let _ = up.eof().await;
                    let _ = up.close().await;
                    break;
                }
            }
        }
    }
}

impl server::Server for ProxyHandler {
    type Handler = Self;
    fn new_client(&mut self, _: Option<std::net::SocketAddr>) -> Self {
        self.clone()
    }
    fn handle_session_error(&mut self, error: <Self::Handler as russh::server::Handler>::Error) {
        log::error!("Session error: {error:#?}");
    }
}
