FROM python:alpine

RUN apk add --no-cache jq && \
    pip install yq && \
    wget --quiet https://dl.min.io/client/mc/release/linux-amd64/mc -O /usr/local/bin/mc && \
    chmod 0755 /usr/local/bin/mc

COPY entrypoint.sh /
ENTRYPOINT /entrypoint.sh
