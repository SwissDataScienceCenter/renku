FROM alpine/k8s:1.16.8

# install dependencies
RUN apk add python3 docker && \
    wget -O /usr/bin/yq "https://github.com/mikefarah/yq/releases/download/3.1.1/yq_linux_amd64" && \
    chmod a+x /usr/bin/yq && \
    pip3 install -U pip chartpress==0.3.2

COPY deploy_dev_renku.py entrypoint.sh /
ENTRYPOINT /entrypoint.sh
