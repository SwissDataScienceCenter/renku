FROM python:3.12.0a1-alpine

COPY requirements.txt init-realm.py /app/
WORKDIR /app
RUN pip install -r requirements.txt
USER 1000:1000
