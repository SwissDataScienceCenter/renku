from base64 import b64decode, b64encode
import yaml
import logging
from typing import cast
from kubernetes import client as k8s_client, config as k8s_config
from dataclasses import dataclass, field
import os
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives import serialization
from cryptography.fernet import Fernet

PRIVATE_KEY_ENTRY_NAME = "privateKey"
PUBLIC_KEY_ENTRY_NAME = "publicKey"
PREVIOUS_PRIVATE_KEY_ENTRY_NAME = "previousPrivateKey"


@dataclass
class Config:
    k8s_namespace: str
    renku_fullname: str
    secret_service_private_key_secret_name: str
    secret_service_public_key_secret_name: str
    secret_service_private_key: str | None = field(repr=False)
    encryption_key: str | None = field(repr=False)
    previous_secret_service_private_key: str | None = field(repr=False)

    @classmethod
    def from_env(cls):
        config_map = yaml.load(
            os.environ.get("PLATFORM_INIT_CONFIG", "{}"), Loader=yaml.Loader
        )
        renku_fullname = os.environ["RENKU_FULLNAME"]
        return cls(
            k8s_namespace=os.environ["K8S_NAMESPACE"],
            renku_fullname=renku_fullname,
            secret_service_private_key=config_map.get("secretServicePrivateKey"),
            encryption_key=config_map.get("dataServiceEncryptionKey"),
            previous_secret_service_private_key=config_map.get(
                "secretServicePreviousPrivateKey"
            ),
            secret_service_private_key_secret_name=f"{renku_fullname}-secret-service-private-key",
            secret_service_public_key_secret_name=f"{renku_fullname}-secret-service-public-key",
        )


def _get_k8s_secret(namespace: str, secret_name: str, secret_key: str) -> str | None:
    v1 = k8s_client.CoreV1Api()
    try:
        secret = cast(
            k8s_client.V1Secret,
            v1.read_namespaced_secret(secret_name, namespace),
        )
        if not secret.data:
            return None
        secret_data = secret.data.get(secret_key)
        if secret_data is not None:
            secret_data = b64decode(secret_data).decode()
        return secret_data

    except k8s_client.ApiException:
        return None


def set_public_key(config: Config):
    """Set the secrets storage public key."""
    v1 = k8s_client.CoreV1Api()

    existing_public_key = _get_k8s_secret(
        config.k8s_namespace,
        config.secret_service_public_key_secret_name,
        PUBLIC_KEY_ENTRY_NAME,
    )

    private_key_rsa = serialization.load_pem_private_key(
        config.secret_service_private_key.encode(), password=None
    )
    public_key_pem = private_key_rsa.public_key().public_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PublicFormat.SubjectPublicKeyInfo,
    )
    if existing_public_key is None:
        v1.create_namespaced_secret(
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={PUBLIC_KEY_ENTRY_NAME: b64encode(public_key_pem).decode()},
                kind="Secret",
                metadata={
                    "name": config.secret_service_public_key_secret_name,
                    "namespace": config.k8s_namespace,
                },
                type="Opaque",
            ),
        )
    else:
        v1.patch_namespaced_secret(
            config.secret_service_public_key_secret_name,
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={PUBLIC_KEY_ENTRY_NAME: b64encode(public_key_pem).decode()},
                kind="Secret",
                metadata={
                    "name": config.secret_service_public_key_secret_name,
                    "namespace": config.k8s_namespace,
                },
                type="Opaque",
            ),
        )


def setup_private_key_rotation(config: Config, existing_key: str | None):
    """Setup secrets storage secret for private key rotation.

    Note: In theory, wy don't need a config value for the previous secrets at all, we could just rotate secrets if the secret in config
    doesn't match the one in kubernetes. But since secrets rotation is an expensive and destructive operation, we require it set in config
    and do some sanity checking here.
    """
    if existing_key is None:
        raise ValueError(
            "Cannot set up secrets key rotation without existing private key"
        )
    if existing_key == config.secret_service_private_key:
        raise ValueError(
            "Can't set up secrets key rotation, new key matches existing k8s key"
        )
    if config.previous_secret_service_private_key != existing_key:
        raise ValueError(
            "secretServicePreviousPrivateKey does not match existing k8s secrets key, did you misconfigure secrets rotation?"
        )

    v1 = k8s_client.CoreV1Api()

    v1.patch_namespaced_secret(
        config.secret_service_private_key_secret_name,
        config.k8s_namespace,
        k8s_client.V1Secret(
            api_version="v1",
            data={
                PRIVATE_KEY_ENTRY_NAME: b64encode(
                    config.secret_service_private_key.encode()
                ).decode(),
                PREVIOUS_PRIVATE_KEY_ENTRY_NAME: b64encode(
                    config.previous_secret_service_private_key.encode()
                ).decode(),
            },
            kind="Secret",
            metadata={
                "name": config.secret_service_private_key_secret_name,
                "namespace": config.k8s_namespace,
            },
            type="Opaque",
        ),
    )

    set_public_key(config)


def initialize_private_key(config: Config):
    """Set the secret service private key."""

    if not config.secret_service_private_key:
        # autogenerate a key. This is mostly for convenience in CI deployments.
        private_key_rsa = rsa.generate_private_key(public_exponent=65537, key_size=4096)
        private_key_pem = private_key_rsa.private_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PrivateFormat.PKCS8,
            encryption_algorithm=serialization.NoEncryption(),
        )
        config.secret_service_private_key = b64encode(private_key_pem).decode()

    v1 = k8s_client.CoreV1Api()

    v1.create_namespaced_secret(
        config.k8s_namespace,
        k8s_client.V1Secret(
            api_version="v1",
            data={PRIVATE_KEY_ENTRY_NAME: config.secret_service_private_key},
            kind="Secret",
            metadata={
                "name": config.secret_service_private_key_secret_name,
                "namespace": config.k8s_namespace,
            },
            type="Opaque",
        ),
    )
    set_public_key(config)


def set_secret_service_secrets(config: Config):
    """Initialize private and public key for secrets storage service."""
    logging.info("Initializing secret service secret")

    private_key_secret = f"{config.renku_fullname}-secret-service-private-key"
    existing_private_key = _get_k8s_secret(
        config.k8s_namespace, private_key_secret, PRIVATE_KEY_ENTRY_NAME
    )

    if config.previous_secret_service_private_key:
        setup_private_key_rotation(config, existing_private_key)
    elif not existing_private_key:
        initialize_private_key(config)
    elif (
        existing_private_key is not None
        and config.secret_service_private_key is not None
        and existing_private_key != config.secret_service_private_key
    ):
        raise ValueError(
            "Private key in config does not match private key in k8s and not performing key rotation"
        )


def init_secret_and_data_service_encryption(config: Config):
    """Initialize symmetric encryption key for encryption at rest in data service."""
    logging.info("Initializing data service encryption")
    v1 = k8s_client.CoreV1Api()

    encryption_key = f"{config.renku_fullname}-secrets-storage"
    encryption_key_name = "encryptionKey"
    existing_encryption_key = _get_k8s_secret(
        config.k8s_namespace, encryption_key, encryption_key_name
    )

    if existing_encryption_key is None and config.encryption_key is None:
        # generate a symmetric encryption key
        key = Fernet.generate_key()
        v1.create_namespaced_secret(
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={encryption_key_name: b64encode(key).decode()},
                kind="Secret",
                metadata={
                    "name": encryption_key,
                    "namespace": config.k8s_namespace,
                },
                type="Opaque",
            ),
        )
    elif existing_encryption_key is None and config.encryption_key is not None:
        key = config.encryption_key.encode()
        v1.create_namespaced_secret(
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={encryption_key_name: b64encode(key).decode()},
                kind="Secret",
                metadata={
                    "name": encryption_key,
                    "namespace": config.k8s_namespace,
                },
                type="Opaque",
            ),
        )


def main():
    config = Config.from_env()
    k8s_config.load_incluster_config()
    logging.basicConfig(level=logging.INFO)
    logging.info("Initializing Renku platform")
    set_secret_service_secrets(config)
    init_secret_and_data_service_encryption(config)


if __name__ == "__main__":
    main()
