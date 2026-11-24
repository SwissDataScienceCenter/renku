#!/usr/bin/env python3
"""Entrypoint for the renku-remote chart's ssh-proxy session-keys Job.

Generates the proxy<->session SSH keypairs for this cluster (authkey-S, hostkey-S) and
composes the Secret that gets mounted into every session, reusing the same idempotent
logic the main Renku chart uses for its own (local) sessions.
"""

import logging
import os

from kubernetes import client as k8s_client, config as k8s_config

from ssh_proxy_session_keys import ensure_session_mount_secret, ensure_ssh_proxy_session_keys


def main():
    logging.basicConfig(level=logging.INFO)
    k8s_config.load_incluster_config()

    namespace = os.environ["K8S_NAMESPACE"]
    fullname = os.environ["RENKU_FULLNAME"]
    mount_secret_name = os.environ.get("SESSION_KEYS_SECRET_NAME", "renku-ssh-session-keys")

    api = k8s_client.CoreV1Api()
    logging.info("Ensuring ssh-proxy session keys for %s in %s", fullname, namespace)
    ensure_ssh_proxy_session_keys(api, namespace, fullname)
    ensure_session_mount_secret(api, namespace, fullname, mount_secret_name)


if __name__ == "__main__":
    main()
