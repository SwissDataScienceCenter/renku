{
  pkgs,
  renku-ssh-proxy,
  src,
  advisory-db,
  ...
}: let
  # Reuses the same cargo setup as the build above, just swapping the build/install
  # phases for another (cargo) command instead of a real build.
  cargoRun = name: extraNativeBuildInputs: cmd:
    renku-ssh-proxy.overrideAttrs (old: {
      pname = "${old.pname}-${name}";
      nativeBuildInputs = (old.nativeBuildInputs or []) ++ extraNativeBuildInputs;
      buildPhase = ''
        runHook preBuild
        ${cmd}
        runHook postBuild
      '';
      doCheck = false;
      installPhase = "touch $out";
    });
in {
  # Build the crate as part of the checks
  build = renku-ssh-proxy;

  clippy = cargoRun "clippy" [] ''
    cargo clippy --all-targets --all-features --offline -- -D warnings
  '';

  # cargo-audit has no --config flag; it only auto-discovers .cargo/audit.toml
  audit = cargoRun "audit" [pkgs.cargo-audit] ''
    cargo audit --no-fetch --db ${advisory-db} --file Cargo.lock
  '';

  format = cargoRun "format" [] ''
    cargo fmt --check
  '';

  # advisories are already covered by the `audit` check above against the
  # same pinned advisory-db; cargo-deny manages its own advisory-db cache
  # (keyed by URL under db-path) which isn't populated here, so skip that
  # category and only check what doesn't need a network-fetched db.
  deny = cargoRun "deny" [pkgs.cargo-deny] ''
    cargo deny --offline check bans licenses sources
  '';
}
