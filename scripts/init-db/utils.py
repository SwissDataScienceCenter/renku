import base64
import logging

import requests
from kubernetes import client, config
from kubernetes.config.config_exception import ConfigException
from kubernetes.config.incluster_config import (
    SERVICE_CERT_FILENAME,
    SERVICE_TOKEN_FILENAME,
    InClusterConfigLoader,
)
from psycopg2 import connect
from tenacity import (
    before_sleep_log, 
    retry, stop_after_attempt,
    stop_after_delay, 
    wait_fixed,
)


@retry(
    stop=(stop_after_attempt(200) | stop_after_delay(600)),
    wait=wait_fixed(3),
    reraise=True,
    before_sleep=before_sleep_log(logging, logging.WARNING),
)
def get_db_connection(*args, **kwargs):
    logging.info("Trying to connect to postgress...")
    return connect(*args, **kwargs, connect_timeout=10)


@retry(stop=(stop_after_attempt(200) | stop_after_delay(600)), wait=wait_fixed(3), reraise=True)
def gitlab_is_online(url: str) -> int:
    logging.info("Waiting for gitlab to come online...")
    res = requests.get(url, timeout=10)
    if res.status_code != 200:
        raise ValueError(f"Gitlab is not available at {url}, status code is {res.status_code}")
    return res.status_code
