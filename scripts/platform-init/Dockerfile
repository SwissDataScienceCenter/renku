FROM python:3.12-alpine

COPY requirements.txt platform-init.py /app/
WORKDIR /app
RUN pip install -r requirements.txt
USER 1000:1000

