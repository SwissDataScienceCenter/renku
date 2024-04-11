from base64 import b64decode, b64encode
import json
import logging
from typing import cast
from kubernetes import client as k8s_client, config as k8s_config
from dataclasses import dataclass, field
import os
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography.hazmat.primitives import serialization


@dataclass
class Config:
    k8s_namespace: str
    renku_fullname: str
    secret_service_public_key: str | None
    secret_service_private_key: str | None = field(repr=False)

    @classmethod
    def from_env(cls):
        config_map = json.loads(os.environ.get("PLATFORM_INIT_CONFIG", "{}"))
        return cls(
            k8s_namespace=os.environ["K8S_NAMESPACE"],
            renku_fullname=os.environ["RENKU_FULLNAME"],
            secret_service_public_key=config_map.get("SECRET_SERVICE_PUBLIC_KEY"),
            secret_service_private_key=config_map.get("SECRET_SERVICE_SECRET_KEY"),
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

    if existing_private_key is None and config.secret_service_private_key is None:
        # generate new secret
        private_key = rsa.generate_private_key(public_exponent=65537, key_size=4096)
        private_key_pem = private_key.private_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PrivateFormat.PKCS8,
            encryption_algorithm=serialization.NoEncryption(),
        )
        v1.create_namespaced_secret(
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={private_key_entry_name: b64encode(private_key_pem).decode()},
                kind="Secret",
                metadata={
                    "name": private_key_secret,
                    "namespace": config.k8s_namespace,
                },
                type="Opaque",
            ),
        )
    elif existing_private_key is None and config.secret_service_private_key is not None:
        # create private key from config
        private_key = serialization.load_pem_private_key(
            config.secret_service_private_key.encode(), password=None
        )
        v1.create_namespaced_secret(
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={
                    private_key_entry_name: b64encode(
                        config.secret_service_private_key.encode()
                    ).decode()
                },
                kind="Secret",
                metadata={
                    "name": private_key_secret,
                    "namespace": config.k8s_namespace,
                },
                type="Opaque",
            ),
        )

    elif (
        existing_private_key != config.secret_service_private_key
        and config.secret_service_private_key is not None
    ):
        # update private key
        private_key = serialization.load_pem_private_key(
            config.secret_service_private_key.encode(), password=None
        )
        v1.patch_namespaced_secret(
            private_key_secret,
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={
                    private_key_entry_name: b64encode(
                        config.secret_service_private_key.encode()
                    ).decode()
                },
                kind="Secret",
                metadata={
                    "name": private_key_secret,
                    "namespace": config.k8s_namespace,
                },
                type="Opaque",
            ),
        )
    else:
        # just load key in case public key needs to be generated
        private_key = serialization.load_pem_private_key(
            existing_private_key.encode(), password=None
        )

    if existing_public_key is None and config.secret_service_public_key is None:
        # generate public key
        public_key_pem = private_key.public_key().public_bytes(
            encoding=serialization.Encoding.PEM,
            format=serialization.PublicFormat.SubjectPublicKeyInfo,
        )
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
    elif existing_public_key is None and config.secret_service_public_key is not None:
        # create public key from config
        v1.create_namespaced_secret(
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={
                    public_key_entry_name: b64encode(
                        config.secret_service_public_key.encode()
                    ).decode()
                },
                kind="Secret",
                metadata={"name": public_key_secret, "namespace": config.k8s_namespace},
                type="Opaque",
            ),
        )
    elif (
        existing_public_key != config.secret_service_public_key
        and config.secret_service_public_key is not None
    ):
        # update public key from config
        v1.patch_namespaced_secret(
            public_key_secret,
            config.k8s_namespace,
            k8s_client.V1Secret(
                api_version="v1",
                data={
                    public_key_entry_name: b64encode(
                        config.secret_service_public_key.encode()
                    ).decode()
                },
                kind="Secret",
                metadata={"name": public_key_secret, "namespace": config.k8s_namespace},
                type="Opaque",
            ),
        )


def main():
    config = Config.from_env()
    k8s_config.load_incluster_config()
    logging.info("Initializing Renku platform")
    init_secret_service_secret(config)


if __name__ == "__main__":
    main()
