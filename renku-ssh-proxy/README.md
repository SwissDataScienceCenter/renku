# renku-ssh-proxy

## Building the container image

1. with `docker` it must build from the root directory
   ```
   docker build -f renku-ssh-proxy/Dockerfile -t <the-tag> .
   ```

2. with `nix` it must run in the source root (`renku-ssh-proxy`):
   ```
   just image
   ```

## Dependency auditing

This crate parses untrusted SSH traffic, so dependencies are audited and
updated on a standing cadence rather than only when touched.

- `cargo audit` runs in CI on every pull request that changes
  `renku-ssh-proxy/**` and weekly (see `.github/workflows/renku-ssh-proxy-audit.yml`).
  Run it locally with `cd renku-ssh-proxy && cargo install cargo-audit --locked && cargo audit`.
- Dependabot opens weekly cargo update PRs for this directory.
- `russh` is the highest-risk dependency. It is required as `russh = "0.62.6"`
  and currently locks to `0.62.7`. Keep it current: prefer accepting Dependabot
  bumps within `0.62.x`, and read the upstream release notes before crossing a
  minor version. To force a refresh explicitly:
  `cargo update -p russh && cargo test && cargo audit`.
