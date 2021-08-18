FROM python:3.7-alpine3.10

# install dependencies
RUN apk add --no-cache git bash && \
    apk add --no-cache --virtual .build-deps gcc g++ make && \
    wget -O /usr/bin/yq "https://github.com/mikefarah/yq/releases/download/3.1.1/yq_linux_amd64" && \
    chmod a+x /usr/bin/yq && \
    pip install -U pip chartpress==0.3.2 six==1.16.0 && \
    apk del .build-deps

COPY update-upstream.sh /
ENTRYPOINT [ "/update-upstream.sh" ]
