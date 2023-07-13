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
    if not (res.status_code >= 200 and res.status_code < 400):
        raise ValueError(f"Gitlab is not available at {url}, status code is {res.status_code}")
    return res.status_code


@retry(stop=(stop_after_attempt(200) | stop_after_delay(600)), wait=wait_fixed(3), reraise=True)
def get_k8s_secret(namespace: str, name: str, key: str) -> str:
    logging.info(f"Reading k8s secret {name} in namesapce {namespace} with key {key}...")
    try:
        InClusterConfigLoader(
            token_filename=SERVICE_TOKEN_FILENAME,
            cert_filename=SERVICE_CERT_FILENAME,
        ).load_and_set()
    except ConfigException:
        config.load_config()
    core_v1 = client.CoreV1Api()
    secret = core_v1.read_namespaced_secret(name, namespace)
    if secret is None:
        raise ValueError(f"Cannot find k8s secret {name} in namesapce {namespace}.")
    val = secret.data.get(key)
    if val is None:
        raise ValueError(f"Cannot find the key {key} in k8s secret {name} in namesapce {namespace}.")
    val_decoded = base64.standard_b64decode(val).decode("utf-8")
    return val_decoded
