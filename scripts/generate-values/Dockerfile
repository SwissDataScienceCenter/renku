FROM python:3.9-slim

WORKDIR /work
COPY requirements.txt /work/

RUN cd /work && \
    pip install --no-cache -r requirements.txt

COPY generate-values.py /work/

ENTRYPOINT ["/work/generate-values.py"]
