FROM python:3.7-alpine3.10

# install dependencies
RUN apk add git bash && \
    wget -O /usr/bin/yq "https://github.com/mikefarah/yq/releases/download/3.1.1/yq_linux_amd64" && \
    chmod a+x /usr/bin/yq && \
    pip install -U pip chartpress==0.3.2

COPY update-upstream.sh /
ENTRYPOINT [ "/update-upstream.sh" ]
