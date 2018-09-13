FROM python:3.6

# RUN apk add --update openssl
RUN wget https://github.com/mozilla/geckodriver/releases/download/v0.21.0/geckodriver-v0.21.0-linux64.tar.gz && \
    tar -xvzf geckodriver* && \
    chmod +x geckodriver && \
    mv geckodriver /usr/local/bin && \
    apt-get update -y && \
    apt-get install -y firefox-esr xvfb enchant shellcheck

COPY requirements.txt /tests/requirements.txt

WORKDIR /tests
RUN pip install -r requirements.txt

COPY . /tests

CMD xvfb-run pytest -v --reruns 3
