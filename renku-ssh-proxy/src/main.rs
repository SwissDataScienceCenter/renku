use renku_ssh_proxy::Settings;
use renku_ssh_proxy::buildinfo;

#[tokio::main]
async fn main() -> color_eyre::Result<()> {
    color_eyre::install()?;
    renku_ssh_proxy::generate_completions();

    let args: Vec<String> = std::env::args().collect();

    // Intercept before clap parses to skip validation and exit early
    if args.iter().any(|a| a == "--version" || a == "-V") {
        let info = buildinfo::BuildInfo::default();
        println!("{}", info);
        std::process::exit(0);
    }

    let settings = Settings::create()?;

    env_logger::builder()
        .filter_level(settings.log_level.log_level_filter())
        .init();

    println!(
        "Proxy server running. Try to connect to {}",
        settings.listen
    );
    let _ = renku_ssh_proxy::serve_proxy(&settings).await;
    Ok(())
}
