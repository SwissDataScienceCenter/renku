FROM python:3.6-alpine

RUN apk update && apk add curl

COPY . /code
WORKDIR /code
RUN pip install -r requirements.txt

ENV FLASK_APP=/code/notebooks_service.py

RUN addgroup -g 1000 kyaku && \
    adduser -S -u 1000 -G kyaku kyaku
USER kyaku

CMD ["flask", "run", "-p" ,"8000", "-h", "0.0.0.0"]

HEALTHCHECK --interval=20s --timeout=10s --retries=5 CMD curl -f http://localhost/health
