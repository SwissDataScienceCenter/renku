#!/usr/bin/env python3
"""Generate the two static proxy-to-session (hop 2) SSH keypairs as Kubernetes Secrets.

The session host keypair is generated with `dropbearkey`: dropbear's `-r` reads only dropbear's
native private-key format. The proxy auth keypair uses `ssh-keygen` (OpenSSH), which russh reads
and dropbear accepts in `authorized_keys`.

Idempotent: existing secrets are left untouched so chart upgrades never rotate keys.

See docs/superpowers/specs/2026-09-18-ssh-proxy-session-authn-design.md.
"""

import argparse
import base64
import logging
import os
import subprocess
import sys
import tempfile
from pathlib import Path

logger = logging.getLogger("ssh-proxy-session-keys")


def _run(cmd: list[str]) -> subprocess.CompletedProcess[str]:
    """Run a command, surfacing its stderr on failure."""
    result = subprocess.run(cmd, capture_output=True, text=True)
    if result.returncode != 0:
        raise RuntimeError(f"{cmd[0]} failed ({result.returncode}): {result.stderr.strip()}")
    return result

# Interface contract. The secret name is {fullname}{suffix}.
KEYPAIRS: tuple[dict[str, str], ...] = (
    {
        "suffix": "-ssh-session-host-key",
        "private_key": "hostKey",
        "public_key": "hostKeyPub",
        "generator": "dropbearkey",
        "comment": "renku-session-host-key",
    },
    {
        "suffix": "-ssh-proxy-session-auth-key",
        "private_key": "authKey",
        "public_key": "authKeyPub",
        "generator": "ssh-keygen",
        "comment": "renku-ssh-proxy-session-auth",
    },
)


def secret_name(fullname: str, suffix: str) -> str:
    return f"{fullname}{suffix}"


def generate_ssh_keypair(workdir: Path, name: str, comment: str) -> tuple[bytes, bytes]:
    """Generate an Ed25519 keypair in OpenSSH format; return (private, public) bytes.

    Uses the `cryptography` library instead of the `ssh-keygen` binary: ssh-keygen calls getpwuid()
    and aborts with "No user exists for uid N" when the runtime uid has no passwd entry, which a
    minimal image cannot guarantee.
    """
    from cryptography.hazmat.primitives import serialization
    from cryptography.hazmat.primitives.asymmetric.ed25519 import Ed25519PrivateKey

    key = Ed25519PrivateKey.generate()
    private = key.private_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PrivateFormat.OpenSSH,
        encryption_algorithm=serialization.NoEncryption(),
    )
    public = (
        key.public_key().public_bytes(
            encoding=serialization.Encoding.OpenSSH,
            format=serialization.PublicFormat.OpenSSH,
        )
        + f" {comment}\n".encode()
    )
    return private, public


def generate_dropbear_keypair(workdir: Path, name: str) -> tuple[bytes, bytes]:
    """Generate an ed25519 keypair with dropbearkey (native format); return bytes.

    Older `dropbearkey` (e.g. Debian's) does not write a `.pub` file, it prints the public key to
    stdout. The comment is ignored because the proxy pin compares key data only.
    """
    key_path = workdir / name
    result = _run(["dropbearkey", "-t", "ed25519", "-f", str(key_path)])
    public = next(
        line for line in result.stdout.splitlines() if line.startswith("ssh-ed25519 ")
    )
    return key_path.read_bytes(), f"{public}\n".encode()


def generate_keypair(
    workdir: Path, name: str, keypair: dict[str, str]
) -> tuple[bytes, bytes]:
    if keypair["generator"] == "dropbearkey":
        return generate_dropbear_keypair(workdir, name)
    return generate_ssh_keypair(workdir, name, keypair["comment"])


def ensure_secret(api, namespace: str, name: str, data: dict[str, bytes]) -> bool:
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
            # binary safe: the dropbear native key is not valid UTF-8
            "data": {k: base64.b64encode(v).decode("ascii") for k, v in data.items()},
        },
    )
    logger.info("created secret %s", name)
    return True


def main() -> int:
    logging.basicConfig(level=logging.INFO)
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
            private, public = generate_keypair(workdir, name, keypair)
            ensure_secret(
                api,
                args.namespace,
                name,
                {keypair["private_key"]: private, keypair["public_key"]: public},
            )
    return 0


if __name__ == "__main__":
    sys.exit(main())
