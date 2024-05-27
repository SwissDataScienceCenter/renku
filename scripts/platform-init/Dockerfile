FROM python:3.12-slim-bookworm
RUN apt-get update && apt-get install -y \
    tini && \
    rm -rf /var/lib/apt/lists/*
COPY requirements.txt platform-init.py /app/
WORKDIR /app
RUN pip3 install -r requirements.txt
USER 1000:1000

ENTRYPOINT [ "tini", "-g", "--", "python" ]
