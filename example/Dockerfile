# curl + jq

FROM buildpack-deps:curl

RUN apt-get update && apt-get install -y \
        jq \
    && rm -rf /var/lib/apt/lists/*

ADD . /

CMD ["bash", "/example.sh"]
