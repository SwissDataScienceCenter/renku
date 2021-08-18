FROM python:3.7-slim

RUN apt-get update && \
    apt-get install -y \
    chromium-driver \
    gcc \
    git \
    git-lfs \
    gnupg \
    nodejs \
    wget

# This 'mkdir' is just to make the openjdk-11-jdk installation to pass
RUN mkdir -p /usr/share/man/man1 && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    wget --quiet -O - "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt-get update && \
    apt-get install -y openjdk-11-jdk sbt && \
    rm -rf /var/cache/apt && \
    wget --quiet https://dl.min.io/client/mc/release/linux-amd64/mc -O /usr/local/bin/mc && \
    chmod 0755 /usr/local/bin/mc

RUN python3 -m pip install \
    'papermill==2.1.3' \
    'requests>=2.20.0' \
    'jupyterhub==1.1.0' \
    'nbresuse==0.3.3' \
    'jupyterlab-git==0.20.0' \
    'pipx>=0.15.0.0' \
    'pandas==1.3.0' \
    'seaborn==0.11.1'

ENV DOCKER="1"
COPY . /tests
ENTRYPOINT ["/tests/docker-run-tests.sh"]
