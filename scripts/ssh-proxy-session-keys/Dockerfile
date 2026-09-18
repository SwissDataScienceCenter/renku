FROM python:3.12-slim-bookworm
RUN apt-get update && apt-get install -y --no-install-recommends \
    openssh-client \
    tini && \
    rm -rf /var/lib/apt/lists/*
COPY requirements.txt ssh_proxy_session_keys.py /app/
WORKDIR /app
RUN pip3 install -r requirements.txt
# ssh-keygen needs a writable HOME; the pod runs as an arbitrary non-root uid
ENV HOME=/tmp
USER 1000:1000
ENTRYPOINT [ "tini", "-g", "--", "python" ]
