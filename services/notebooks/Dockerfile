FROM python:3.6-alpine

COPY . /code
WORKDIR /code
RUN pip install -r requirements.txt

ENV FLASK_APP=/code/notebooks_service.py

CMD ["flask", "run", "-p" ,"80", "-h", "0.0.0.0"]
