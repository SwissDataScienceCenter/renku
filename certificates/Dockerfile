# Taken from https://gitlab.com/gitlab-org/build/CNG/-/blob/master/alpine-certificates/Dockerfile
ARG FROM_IMAGE=alpine
ARG ALPINE_VERSION=3.14
FROM $FROM_IMAGE:$ALPINE_VERSION

ARG CA_PKG_VERSION=20211220-r0
RUN apk --update --no-cache add ca-certificates=${CA_PKG_VERSION} java-cacerts=1.0-r1 && \
    adduser -u 1000 -D -H renku

COPY scripts/bundle-certificates.sh /scripts/

VOLUME /etc/ssl/certs /usr/local/share/ca-certificates

CMD ["sh", "/scripts/bundle-certificates.sh"]
