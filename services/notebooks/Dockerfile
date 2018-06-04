FROM python:3.6-alpine

RUN apk update && apk add curl

WORKDIR /code
COPY requirements.txt /tmp/requirements.txt

RUN pip install -r /tmp/requirements.txt

COPY . /code

ENV FLASK_APP=/code/notebooks_service.py

RUN addgroup -g 1000 kyaku && \
    adduser -S -u 1000 -G kyaku kyaku
USER kyaku

CMD ["flask", "run", "-p" ,"8000", "-h", "0.0.0.0"]

HEALTHCHECK --interval=20s --timeout=10s --retries=5 CMD curl -f http://localhost:8000/health
