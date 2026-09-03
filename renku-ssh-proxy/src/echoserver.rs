use std::collections::HashMap;
use std::sync::Arc;

use crate::config::Settings;
use russh::keys::{Certificate, *};
use russh::server::{Msg, Server as _, Session};
use russh::*;
use tokio::net::TcpListener;
use tokio::sync::Mutex;

pub async fn do_run(settings: &Settings) {
    let mut sh = Server {
        clients: Arc::new(Mutex::new(HashMap::new())),
        id: 0,
    };

    let socket = TcpListener::bind(settings.listen).await.unwrap();
    let server = sh.run_on_socket(settings.ssh_server_config.clone(), &socket);
    // let handle = server.handle();

    // tokio::spawn(async move {
    //     tokio::time::sleep(std::time::Duration::from_secs(600)).await;
    //     handle.shutdown("Server shutting down after 10 minutes".into());
    // });

    server.await.unwrap()
}

#[derive(Clone)]
struct Server {
    clients: Arc<Mutex<HashMap<usize, (ChannelId, russh::server::Handle)>>>,
    id: usize,
}

impl Server {
    async fn post(&mut self, data: Vec<u8>) {
        let mut clients = self.clients.lock().await;
        for (id, (channel, s)) in clients.iter_mut() {
            if *id != self.id {
                let _ = s.data(*channel, data.clone()).await;
            }
        }
    }
}

impl server::Server for Server {
    type Handler = Self;
    fn new_client(&mut self, _: Option<std::net::SocketAddr>) -> Self {
        let s = self.clone();
        self.id += 1;
        s
    }
    fn handle_session_error(&mut self, _error: <Self::Handler as russh::server::Handler>::Error) {
        eprintln!("Session error: {_error:#?}");
    }
}

impl server::Handler for Server {
    type Error = russh::Error;

    async fn channel_open_session(
        &mut self,
        channel: Channel<Msg>,
        reply: server::ChannelOpenHandle,
        session: &mut Session,
    ) -> Result<(), Self::Error> {
        {
            let mut clients = self.clients.lock().await;
            clients.insert(self.id, (channel.id(), session.handle()));
        }
        reply.accept().await;
        Ok(())
    }

    async fn auth_publickey(
        &mut self,
        _: &str,
        _key: &ssh_key::PublicKey,
    ) -> Result<server::Auth, Self::Error> {
        Ok(server::Auth::Accept)
    }

    async fn auth_openssh_certificate(
        &mut self,
        _user: &str,
        _certificate: &Certificate,
    ) -> Result<server::Auth, Self::Error> {
        Ok(server::Auth::Accept)
    }

    async fn data(
        &mut self,
        channel: ChannelId,
        data: &[u8],
        session: &mut Session,
    ) -> Result<(), Self::Error> {
        // Sending Ctrl+C ends the session and disconnects the client
        if data == [3] {
            return Err(russh::Error::Disconnect);
        }
        let input = String::from_utf8_lossy(data);
        let data = format!("Got data: {}\r\n", input).into_bytes();
        log::debug!("Sending: {}", input);
        self.post(data.clone()).await;
        session.data(channel, data)?;
        Ok(())
    }

    async fn tcpip_forward(
        &mut self,
        address: &str,
        port: &mut u32,
        session: &mut Session,
    ) -> Result<bool, Self::Error> {
        let handle = session.handle();
        let address = address.to_string();
        let port = *port;
        tokio::spawn(async move {
            let channel = handle
                .channel_open_forwarded_tcpip(address, port, "1.2.3.4", 1234)
                .await
                .unwrap();
            let _ = channel.data(&b"Hello from a forwarded port"[..]).await;
            let _ = channel.eof().await;
        });
        Ok(true)
    }
}

impl Drop for Server {
    fn drop(&mut self) {
        let id = self.id;
        let clients = self.clients.clone();
        tokio::spawn(async move {
            let mut clients = clients.lock().await;
            clients.remove(&id);
        });
    }
}
