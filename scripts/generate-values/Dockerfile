FROM python:3.9-slim

COPY requirements.txt /code/

RUN pip install --no-cache -r /code/requirements.txt

COPY generate-values.py base-renku-values.yaml.template /code/

WORKDIR /work
ENTRYPOINT ["/code/generate-values.py", "--template", "/code/base-renku-values.yaml.template"]
