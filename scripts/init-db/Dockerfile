FROM python:3.11-slim-bullseye

RUN apt-get update && apt-get install -y \
    postgresql-client \
    tini && \
    rm -rf /var/lib/apt/lists/*
COPY . .
RUN pip3 install -r requirements.txt 
USER 1000:1000
ENTRYPOINT [ "tini", "-g", "--", "python" ]
