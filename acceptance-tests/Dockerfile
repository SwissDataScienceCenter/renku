FROM python:3.9-slim

RUN apt-get update && \
    apt-get install -y \
    chromium-driver \
    gcc \
    git \
    git-lfs \
    gnupg \
    nodejs \
    wget \
    curl

# This 'mkdir' is just to make the openjdk installation to pass
RUN mkdir -p /usr/share/man/man1 && \
    apt-get update && \
    apt-get install -y openjdk-21-jdk

# Install sbt
RUN echo "deb [signed-by=/usr/share/keyrings/scala.gpg] https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb [signed-by=/usr/share/keyrings/scala.gpg] https://repo.scala-sbt.org/scalasbt/debian /" |  tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | gpg --dearmor -o /usr/share/keyrings/scala.gpg && \
    apt-get update &&  apt-get install sbt && \
    rm -rf /var/cache/apt && \
    wget --quiet https://dl.min.io/client/mc/release/linux-amd64/mc -O /usr/local/bin/mc && \
    chmod 0755 /usr/local/bin/mc

COPY . /tests

RUN addgroup -gid 1001 attests && \
    useradd -ms /bin/bash -u 1001 -g attests attests && \
    chmod 777 -R /tests && \
    chown -R attests:attests /tests

USER attests

ENV PATH=/home/attests/.local/bin:/home/attests/.local/lib/python3.8/site-packages:$PATH

RUN python3 -m pip install --upgrade 'pip==23.0' && \
    python3 -m pip install -r /tests/requirements.txt

ENV DOCKER="1"
ENTRYPOINT ["/tests/docker-run-tests.sh"]
