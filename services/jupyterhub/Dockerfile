FROM jupyterhub/jupyterhub:0.9.2

RUN curl -fsSL get.docker.com | sh

COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt

COPY jupyterhub_config.py spawners.py /srv/jupyterhub/

HEALTHCHECK --interval=20s --timeout=10s --retries=5 CMD curl -f http://localhost:8000/hub/api
