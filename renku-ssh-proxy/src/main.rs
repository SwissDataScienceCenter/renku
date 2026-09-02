mod echoserver;


#[tokio::main]
async fn main() -> color_eyre::Result<()> {
    env_logger::builder()
        .filter_level(log::LevelFilter::Debug)
        .init();

    println!("Hello! Try to connect to port 2222, like ssh -p 2222 <the-host>");
    echoserver::do_run().await;
    Ok(())
}
