FROM python:3.6-alpine

RUN apk update && apk add curl

COPY . /code
WORKDIR /code
RUN pip install -r requirements.txt

ENV FLASK_APP=/code/notebooks_service.py

CMD ["flask", "run", "-p" ,"80", "-h", "0.0.0.0"]

HEALTHCHECK --interval=20s --timeout=10s --retries=5 CMD curl -f http://localhost/health
