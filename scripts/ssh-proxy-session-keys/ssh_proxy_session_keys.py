#!/usr/bin/env python3
"""Generate the two static proxy-to-session (hop 2) SSH keypairs as Kubernetes Secrets.

Idempotent: existing secrets are left untouched so chart upgrades never rotate keys.

See docs/superpowers/specs/2026-09-18-ssh-proxy-session-authn-design.md.
"""

import argparse
import logging
import os
import subprocess
import sys
import tempfile
from pathlib import Path

logger = logging.getLogger("ssh-proxy-session-keys")

# Interface contract. The secret name is {fullname}{suffix}.
KEYPAIRS: tuple[dict[str, str], ...] = (
    {
        "suffix": "-ssh-session-host-key",
        "private_key": "hostKey",
        "public_key": "hostKeyPub",
        "comment": "renku-session-host-key",
    },
    {
        "suffix": "-ssh-proxy-session-auth-key",
        "private_key": "authKey",
        "public_key": "authKeyPub",
        "comment": "renku-ssh-proxy-session-auth",
    },
)


def secret_name(fullname: str, suffix: str) -> str:
    return f"{fullname}{suffix}"


def generate_keypair(workdir: Path, name: str, comment: str) -> tuple[str, str]:
    """Generate an ed25519 keypair; return (private, public) file contents."""
    key_path = workdir / name
    subprocess.run(
        ["ssh-keygen", "-t", "ed25519", "-N", "", "-C", comment, "-f", str(key_path)],
        check=True,
        capture_output=True,
        text=True,
    )
    public_path = key_path.with_name(key_path.name + ".pub")
    return key_path.read_text(), public_path.read_text()


def ensure_secret(api, namespace: str, name: str, data: dict[str, str]) -> bool:
    """Create the secret if it does not exist. Returns True when created."""
    from kubernetes.client.exceptions import ApiException

    try:
        api.read_namespaced_secret(name=name, namespace=namespace)
        logger.info("secret %s already exists, leaving it untouched", name)
        return False
    except ApiException as err:
        if err.status != 404:
            raise

    api.create_namespaced_secret(
        namespace=namespace,
        body={
            "apiVersion": "v1",
            "kind": "Secret",
            "metadata": {"name": name},
            "stringData": data,
        },
    )
    logger.info("created secret %s", name)
    return True


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--namespace", default=os.environ.get("K8S_NAMESPACE"))
    parser.add_argument("--fullname", default=os.environ.get("RENKU_FULLNAME"))
    args = parser.parse_args()
    if not args.namespace or not args.fullname:
        logger.error("missing --namespace/--fullname (or K8S_NAMESPACE/RENKU_FULLNAME)")
        return 1

    from kubernetes import client, config

    config.load_incluster_config()
    api = client.CoreV1Api()

    with tempfile.TemporaryDirectory() as tmp:
        workdir = Path(tmp)
        for keypair in KEYPAIRS:
            name = secret_name(args.fullname, keypair["suffix"])
            private, public = generate_keypair(workdir, name, keypair["comment"])
            ensure_secret(
                api,
                args.namespace,
                name,
                {keypair["private_key"]: private, keypair["public_key"]: public},
            )
    return 0


if __name__ == "__main__":
    sys.exit(main())
