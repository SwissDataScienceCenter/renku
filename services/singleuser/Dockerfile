# Build as renku/singleuser
# Run with the DockerSpawner in JupyterHub

ARG BASE_IMAGE=jupyterhub/singleuser:0.8.1
FROM $BASE_IMAGE
MAINTAINER Swiss Data Science Center <info@datascience.ch>

ARG RENKU_VERSION=master
ARG EXTRA_PACKAGES="build-essential emacs git inkscape jed libsm6 libxext-dev libxrender1 lmodern netcat python3-dev unzip nano"

ENV PIP_INSTALL "python3 -m pip install --no-cache --upgrade --upgrade-strategy only-if-needed"

# Install Git LFS.
USER 0
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends git curl ca-certificates $EXTRA_PACKAGES && \
    curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | bash && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends git-lfs && \
    rm -r /var/lib/apt/lists/*
USER $NB_UID

# install pinned jupyterhub and ensure notebook is installed
RUN python3 -m pip install --no-cache -U pip && \
    $PIP_INSTALL git+https://github.com/SwissDataScienceCenter/renku-python.git@${RENKU_VERSION}#egg=renku[all] && \
    $PIP_INSTALL notebook jupyter_contrib_nbextensions widgetsnbextension && \
    jupyter contrib nbextension install --user

ADD setup.py gitlab_commit_push.py /tmp/gitlab_commit_push/
RUN $PIP_INSTALL /tmp/gitlab_commit_push/ && \
    jupyter serverextension enable --py gitlab_commit_push

ADD notebook.json /home/jovyan/.jupyter/nbconfig/
ADD gitlab-commit-push.js /home/jovyan/.local/share/jupyter/nbextensions/

CMD ["jupyterhub-singleuser"]
