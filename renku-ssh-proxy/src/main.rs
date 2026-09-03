use renku_ssh_proxy::Settings;
use renku_ssh_proxy::buildinfo;
use renku_ssh_proxy::echoserver;

#[tokio::main]
async fn main() -> color_eyre::Result<()> {
    color_eyre::install()?;

    let args: Vec<String> = std::env::args().collect();

    // Intercept before clap parses → skips all validation
    if args.iter().any(|a| a == "--version" || a == "-V") {
        let info = buildinfo::BuildInfo::default();
        println!("{}", info);
        std::process::exit(0);
    }

    let settings = Settings::create()?;

    env_logger::builder()
        .filter_level(settings.log_level.log_level_filter())
        .init();

    log::info!("Hello! Try to connect to port 2222, like ssh -p 2222 <the-host>");
    let _ = echoserver::do_proxy(&settings).await;
    Ok(())
}
