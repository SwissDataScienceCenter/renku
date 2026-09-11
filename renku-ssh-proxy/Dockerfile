FROM rust:trixie AS builder
RUN apt-get update && apt-get install -y --no-install-recommends git

WORKDIR /app
COPY renku-ssh-proxy ./renku-ssh-proxy

RUN --mount=type=bind,source=.git,target=/app/.git \
    cd renku-ssh-proxy && \
    cargo build --release --bin renku-ssh-proxy --target-dir /build

FROM gcr.io/distroless/cc-debian13
COPY --from=builder /build/release/renku-ssh-proxy /renku-ssh-proxy
ENTRYPOINT ["/renku-ssh-proxy"]
