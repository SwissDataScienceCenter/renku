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
