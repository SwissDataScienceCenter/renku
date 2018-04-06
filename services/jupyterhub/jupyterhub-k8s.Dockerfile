FROM jupyterhub/k8s-hub:v0.6

USER root

COPY requirements-k8s.txt /tmp/requirements.renga.txt
RUN pip3 install -r /tmp/requirements.renga.txt --no-cache-dir

COPY project_service.py spawners.py /usr/local/lib/python3.6/dist-packages/

USER ${NB_USER}
