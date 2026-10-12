FROM golang:1.26 AS build
WORKDIR /app
COPY go.mod go.sum /app/
RUN go mod download
COPY pkg pkg
COPY cmd cmd
RUN CGO_ENABLED=0 go build -a -o renku_runner cmd/runner/main.go

FROM debian:trixie
WORKDIR /app
COPY --from=build --chown=root:root /app/renku_runner /app/renku_runner
