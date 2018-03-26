FROM jupyterhub/jupyterhub:latest

RUN curl -fsSL get.docker.com | sh
RUN pip install dockerspawner==0.9.1 nullauthenticator oauthenticator flask python-gitlab requests-oauthlib
