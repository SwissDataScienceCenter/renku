FROM jupyterhub/jupyterhub:latest

RUN curl -fsSL get.docker.com | sh
RUN pip install git+https://github.com/jupyterhub/dockerspawner#egg=dockerspawner nullauthenticator oauthenticator flask python-gitlab requests-oauthlib
