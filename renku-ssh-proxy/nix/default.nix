{
  system ? builtins.currentSystem,
  inputs ? import ../.tack,
  pkgs ? import inputs.nixpkgs {
    inherit system;
    overlays = [ (import inputs.rust-overlay) ];
  },
}:
let
  inherit (pkgs) pkgsStatic lib;

  rustPlatform = pkgsStatic.makeRustPlatform {
    cargo = pkgsStatic.rust-bin.stable.latest.default;
    rustc = pkgsStatic.rust-bin.stable.latest.default;
  };

  src = lib.fileset.toSource {
    root = ../.;
    fileset = lib.fileset.unions [
      ../Cargo.toml
      ../Cargo.lock
      ../README.md
      ../build.rs
      ../src
    ];
  };

  commonArgs = {
    inherit src;
    __structuredAttrs = true;
    strictDeps = true;
    cargoLock.lockFile = ../Cargo.lock;

    nativeBuildInputs = [
      pkgsStatic.cmake
      pkgsStatic.installShellFiles
      pkgsStatic.perl
    ]
    ++ lib.optionals (pkgsStatic.stdenv.hostPlatform.isDarwin) [ pkgsStatic.lld ];

    buildInputs = lib.optionals (pkgsStatic.stdenv.hostPlatform.isDarwin) [ pkgsStatic.libiconv ];
  };

  renku-ssh-proxy = rustPlatform.buildRustPackage (
    commonArgs
    // {
      pname = (lib.importTOML ../Cargo.toml).package.name;
      version = (lib.importTOML ../Cargo.toml).package.version;
      doCheck = false;
      postInstall = lib.optionalString (pkgsStatic.stdenv.buildPlatform.canExecute pkgsStatic.stdenv.hostPlatform) ''
          for shell in fish zsh bash; do
              COMPLETE=$shell $out/bin/renku-ssh-proxy > renku-ssh-proxy.$shell
              installShellCompletion --$shell renku-ssh-proxy.$shell
          done
      '';

      env.NIX_GIT_SHA = inputs.self.rev or inputs.self.dirtyRev or "unknown";
      meta = {
        name = "renku-ssh-proxy";
        homepage = "https://renkulab.io";
        license = lib.licenses.asl20;
        mainProgram = "renku-ssh-proxy";
        platforms = lib.platforms.linux ++ lib.platforms.darwin;
      };
    }
  );

  renku-ssh-proxy-no_default_features = renku-ssh-proxy.overrideAttrs { buildNoDefaultFeatures = true; };

in
{
  inherit renku-ssh-proxy renku-ssh-proxy-no_default_features;

  packages = import ./packages.nix {
    inherit
      pkgs
      renku-ssh-proxy
      renku-ssh-proxy-no_default_features
      ;
  };

  checks = import ./checks.nix {
    inherit
      pkgs
      renku-ssh-proxy
      ;
  };

  shell = pkgs.mkShell {
    packages = [
      # General dependencies
      pkgs.rust-bin.stable.latest.default
      pkgs.just
      pkgs.tack

      # Dev dependencies
      pkgs.cargo-flamegraph
      pkgs.rust-analyzer

      # Test dependencies
      pkgs.cargo-nextest
    ];
  };
}
