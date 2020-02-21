FROM docker:19.03.2

# install dependencies
RUN apk add git python3 && \
    wget -O /usr/bin/yq "https://github.com/mikefarah/yq/releases/download/3.1.1/yq_linux_amd64" && \
    chmod a+x /usr/bin/yq && \
    pip3 install -U pip chartpress==0.3.2

COPY publish-chart.sh /
ENTRYPOINT [ "/publish-chart.sh" ]
