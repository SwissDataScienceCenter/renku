FROM python:3.6-alpine

RUN apk --no-cache add --update \
    build-base \
    linux-headers \
    python-dev

COPY requirements.txt /code/requirements.txt
WORKDIR /code

RUN pip install  --no-cache-dir -r requirements.txt

COPY swagger.py /code/swagger.py

CMD ["uwsgi", "--http", ":5000", "--wsgi-file", "swagger.py"]

EXPOSE 5000
