# Built from the repository root, see chartpress.yaml:
#   docker build -f mcp-server/Dockerfile -t <the-tag> .
FROM golang:1.27.3-trixie@sha256:fbb7ae40b62ff8afe31662df570d39b4f5052ac32b90dae8e60d7657a20a74f4 AS builder
COPY --from=ghcr.io/casey/just:1.58.0 /just /usr/local/bin/

WORKDIR /app/mcp-server
COPY mcp-server/go.mod mcp-server/go.sum ./
RUN go mod download
COPY mcp-server ./
ARG VERSION=dev
RUN just build /build ${VERSION}

FROM gcr.io/distroless/static-debian13:nonroot@sha256:18f4cc26c9ffa7f20a359298be7deadac5c607c8e03a02b8119f1a5fc2f82c99
COPY --from=builder /build/mcp-server /mcp-server
USER nonroot:nonroot
EXPOSE 8080
ENTRYPOINT ["/mcp-server"]
