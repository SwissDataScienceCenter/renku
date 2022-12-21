FROM golang:1.19.3-alpine3.16 as builder
COPY . /src
WORKDIR /src
RUN go build -o /init-rabbitmq

FROM alpine:3.16
COPY --from=builder /init-rabbitmq /init-rabbitmq
USER 1000:1000
ENTRYPOINT ["/init-rabbitmq"]
