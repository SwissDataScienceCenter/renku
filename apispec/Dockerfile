FROM python:alpine

RUN apk update && apk add build-base python-dev linux-headers

COPY requirements.txt /code/requirements.txt
WORKDIR /code

RUN pip install -r requirements.txt

COPY swagger.py /code/swagger.py

CMD ["uwsgi", "--http", ":5000", "--wsgi-file", "swagger.py"]

EXPOSE 5000
