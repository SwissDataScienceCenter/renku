FROM python:3.10-alpine

COPY requirements.txt init-realm.py utils.py /app/
WORKDIR /app
RUN pip install -r requirements.txt
USER 1000:1000
