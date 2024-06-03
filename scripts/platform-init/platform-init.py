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


@dataclass
class Config:
    k8s_namespace: str
    renku_fullname: str
    secret_service_private_key: str | None = field(repr=False)
    encryption_key: str | None = field(repr=False)
    previous_secret_service_private_key: str | None = field(repr=False)

    @classmethod
    def from_env(cls):
        config_map = yaml.load(
            os.environ.get("PLATFORM_INIT_CONFIG", "{}"), Loader=yaml.Loader
        )
        return cls(
            k8s_namespace=os.environ["K8S_NAMESPACE"],
            renku_fullname=os.environ["RENKU_FULLNAME"],
            secret_service_private_key=config_map.get("secretServicePrivateKey"),
            encryption_key=config_map.get("dataServiceEncryptionKey"),
            previous_secret_service_private_key=config_map.get(
                "secretServicePreviousPrivateKey"
            ),
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


def init_secret_service_secret(config: Config):
    """Initialize private and public key for secrets storage service."""
    logging.info("Initializing secret service secret")
    v1 = k8s_client.CoreV1Api()

    private_key_secret = f"{config.renku_fullname}-secret-service-private-key"
    private_key_entry_name = "privateKey"
    existing_private_key = _get_k8s_secret(
        config.k8s_namespace, private_key_secret, private_key_entry_name
    )

    public_key_secret = f"{config.renku_fullname}-secret-service-public-key"
    public_key_entry_name = "publicKey"
    existing_public_key = _get_k8s_secret(
        config.k8s_namespace, public_key_secret, public_key_entry_name
    )

    previous_private_key_entry_name = "previousPrivateKey"

    secret_data = {previous_private_key_entry_name: ""}

    if config.previous_secret_service_private_key is not None:
        secret_data[previous_private_key_entry_name] = b64encode(
            config.previous_secret_service_private_key.encode()
        ).decode()

    if existing_private_key is None and config.secret_service_private_key is None:
        # generate new secret
        logging.info("Generating new private key")
        private_key = rsa.generate_private_key(public_exponent=65537, key_size=4096)
        private_key_pem = private_key.private_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PrivateFormat.PKCS8,
            encryption_algorithm=serialization.NoEncryption(),
        )
        secret_data[private_key_entry_name] = b64encode(private_key_pem).decode()
        v1.create_namespaced_secret(
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data=secret_data,
                kind="Secret",
                metadata={
                    "name": private_key_secret,
                    "namespace": config.k8s_namespace,
                },
                type="Opaque",
            ),
        )
    elif config.secret_service_private_key is not None:
        # create private key from config
        logging.info("Setting private key from values")
        if existing_private_key:
            # delete old private key
            logging.info("deleting old private key secret")
            v1.delete_namespaced_secret(private_key_secret, config.k8s_namespace)
        private_key = serialization.load_pem_private_key(
            config.secret_service_private_key.encode(), password=None
        )
        secret_data[private_key_entry_name] = b64encode(
            config.secret_service_private_key.encode()
        ).decode()
        v1.create_namespaced_secret(
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data=secret_data,
                kind="Secret",
                metadata={
                    "name": private_key_secret,
                    "namespace": config.k8s_namespace,
                },
                type="Opaque",
            ),
        )
    else:
        # just load key to create public key from and unset previous secret if set
        logging.info("Leaving private key unchanged")
        private_key = serialization.load_pem_private_key(
            existing_private_key.encode(), password=None
        )
        v1.patch_namespaced_secret(
            private_key_secret,
            config.k8s_namespace,
            {"data": {previous_private_key_entry_name: ""}},
        )

    # generate public key
    public_key_pem = private_key.public_key().public_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PublicFormat.SubjectPublicKeyInfo,
    )
    if existing_public_key is None:
        v1.create_namespaced_secret(
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={public_key_entry_name: b64encode(public_key_pem).decode()},
                kind="Secret",
                metadata={"name": public_key_secret, "namespace": config.k8s_namespace},
                type="Opaque",
            ),
        )
    else:
        v1.patch_namespaced_secret(
            public_key_secret,
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={public_key_entry_name: b64encode(public_key_pem).decode()},
                kind="Secret",
                metadata={"name": public_key_secret, "namespace": config.k8s_namespace},
                type="Opaque",
            ),
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
    init_secret_service_secret(config)
    init_secret_and_data_service_encryption(config)


if __name__ == "__main__":
    main()
