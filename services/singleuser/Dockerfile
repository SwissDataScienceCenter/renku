# Build as renku/singleuser
# Run with the DockerSpawner in JupyterHub

ARG JUPYTERHUB_VERSION=0.9
ARG BASE_IMAGE=jupyterhub/singleuser:$JUPYTERHUB_VERSION

FROM $BASE_IMAGE
ARG JUPYTERHUB_VERSION
MAINTAINER Swiss Data Science Center <info@datascience.ch>

USER root
# Copied from jupyter-minimal-notebook
# Install all OS dependencies for fully functional notebook server
RUN apt-get update && apt-get install -yq --no-install-recommends \
    build-essential \
    curl \
    gnupg \
    git \
    libsm6 \
    libxext-dev \
    libxrender1 \
    lmodern \
    netcat \
    python-dev \
    unzip \
    nano \
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | sudo bash && sudo apt-get install git-lfs=2.4.2
USER jovyan

# install renku-python, jupyterlab and git extension
RUN python3 -m pip install -U pip && \
    python3 -m pip install renku && \
    pip install -U jupyterlab && \
    jupyter labextension update @jupyterlab/hub-extension --no-build && \
    jupyter labextension install @jupyterlab/git --no-build && \
    jupyter lab build && \
    jupyter labextension list && \
    python3 -m pip install -e git+https://github.com/SwissDataScienceCenter/jupyterlab-git.git@fix-git-current-dir#egg=jupyterlab_git && \
    jupyter serverextension enable --py jupyterlab_git

COPY git-config.bashrc /home/jovyan/
RUN cat /home/jovyan/git-config.bashrc >> /home/jovyan/.bashrc && rm /home/jovyan/git-config.bashrc

# Add user `jovyan` to sudoers (passwordless)
USER root
RUN echo "jovyan ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers
USER jovyan

CMD '/usr/local/bin/start-singleuser.sh'
