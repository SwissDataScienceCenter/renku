FROM jupyterhub/jupyterhub:latest

RUN curl -fsSL get.docker.com | sh
RUN pip install git+https://github.com/jupyterhub/dockerspawner#egg=dockerspawner nullauthenticator oauthenticator flask python-gitlab requests-oauthlib

COPY jupyterhub_config.py /srv/jupyterhub/jupyterhub_config.py
COPY project_service.py /srv/jupyterhub/project_service.py
