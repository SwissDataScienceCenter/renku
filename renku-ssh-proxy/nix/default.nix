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

  devshellPkgs = lib.optionals (pkgsStatic.stdenv.hostPlatform.isLinux) (builtins.attrValues inputs.devshell-tools.legacyPackages.${system}.vm-scripts);
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
    ] ++ devshellPkgs;
    env = {
      DEV_VM = "rpsdevvm";
      VM_SSH_PORT = "10022";
      SSH_KEY_PRIV = "${inputs.devshell-tools}/internal/dev-vm-key";
      SSH_KEY_PUB = "${inputs.devshell-tools}/internal/dev-vm-key.pub";

      RENKU_SSH_PROXY_TARGET_HOST = "localhost";
      RENKU_SSH_PROXY_TARGET_USER = "renku";
      RENKU_SSH_PROXY_TARGET_PORT = "10022";
      RUST_LOG = "info,renku_ssh_proxy=debug";
    };
  };

  rpsdevvm = inputs.devshell-tools.lib.mkVm {
    inherit system;
    modules = [
      {
        virtualisation.memorySize = 2048;
        networking.hostName = "rpsdev";
        services.openssh.settings = {
          PermitEmptyPasswords = true;
          PasswordAuthentication = true;
        };
        security.pam.services.sshd.allowNullPassword = true;
        users.users.renku = {
          password = "";
          isNormalUser = true;
          group = "nogroup";
        };
      }
    ];
  };
}
