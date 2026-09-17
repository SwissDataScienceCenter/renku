use vergen_gix::{Build, Cargo, Emitter, Gix, Rustc};

pub fn main() {
    if let Ok(val) = std::env::var("RSP_RELEASE_VERSION") {
        println!("cargo:rustc-env=CARGO_PKG_VERSION={}", val);
    }
    println!("cargo:rerun-if-env-changed=RSP_RELEASE_VERSION");

    let mut binding = Emitter::default();
    let emitter = binding
        .add_instructions(&Build::all_build())
        .unwrap()
        .add_instructions(&Cargo::all_cargo())
        .unwrap()
        .add_instructions(&Rustc::all_rustc())
        .unwrap();

    if let Ok(sha) = std::env::var("NIX_GIT_SHA") {
        emitter.emit().unwrap();
        println!("cargo:rustc-env=VERGEN_GIT_SHA={sha}");
    } else {
        emitter
            .add_instructions(&Gix::all_git())
            .unwrap()
            .emit()
            .unwrap();
    }
}
