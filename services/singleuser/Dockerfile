# Build as renku/singleuser
# Run with the DockerSpawner in JupyterHub

ARG BASE_IMAGE=renku/minimal-notebook:latest
FROM $BASE_IMAGE
MAINTAINER Swiss Data Science Center <info@datascience.ch>

ADD install /tmp/install
ARG JUPYTERHUB_VERSION=0.8.1

# install pinned jupyterhub and ensure notebook is installed
RUN python3 -m pip install -U pip && \
    python3 /tmp/install && \
    python3 -m pip install notebook jupyter_contrib_nbextensions widgetsnbextension && \
    jupyter contrib nbextension install --user

ADD setup.py gitlab_commit_push.py /tmp/gitlab_commit_push/
RUN python3 -m pip install /tmp/gitlab_commit_push/ && \
    jupyter serverextension enable --py gitlab_commit_push

ADD notebook.json /home/jovyan/.jupyter/nbconfig/
ADD gitlab-commit-push.js /home/jovyan/.local/share/jupyter/nbextensions/

CMD '/usr/local/bin/start-singleuser.sh'
