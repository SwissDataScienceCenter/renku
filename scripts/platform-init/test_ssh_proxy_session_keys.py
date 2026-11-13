import importlib.util
import shutil
import tempfile
import unittest
from pathlib import Path

import ssh_proxy_session_keys


class TestContract(unittest.TestCase):
    def test_secret_names(self):
        self.assertEqual(
            ssh_proxy_session_keys.secret_name(
                "renku", ssh_proxy_session_keys.KEYPAIRS[0]["suffix"]
            ),
            "renku-ssh-session-host-key",
        )
        self.assertEqual(
            ssh_proxy_session_keys.secret_name(
                "renku", ssh_proxy_session_keys.KEYPAIRS[1]["suffix"]
            ),
            "renku-ssh-proxy-session-auth-key",
        )

    def test_secret_data_keys(self):
        self.assertEqual(
            [kp["private_key"] for kp in ssh_proxy_session_keys.KEYPAIRS],
            ["hostKey", "authKey"],
        )
        self.assertEqual(
            [kp["public_key"] for kp in ssh_proxy_session_keys.KEYPAIRS],
            ["hostKeyPub", "authKeyPub"],
        )

    def test_generators(self):
        self.assertEqual(
            [kp["generator"] for kp in ssh_proxy_session_keys.KEYPAIRS],
            ["dropbearkey", "ssh-keygen"],
        )


@unittest.skipUnless(importlib.util.find_spec("cryptography"), "cryptography not installed")
class TestOpensshKeypair(unittest.TestCase):
    def test_openssh_ed25519(self):
        private, public = ssh_proxy_session_keys.generate_ssh_keypair(Path("."), "k", "renku-comment")
        self.assertIn(b"BEGIN OPENSSH PRIVATE KEY", private)
        self.assertTrue(public.startswith(b"ssh-ed25519 "), public)
        self.assertTrue(public.strip().endswith(b"renku-comment"))


@unittest.skipUnless(shutil.which("dropbearkey"), "dropbearkey not installed")
class TestDropbearKeypair(unittest.TestCase):
    def test_native_private_and_openssh_public(self):
        with tempfile.TemporaryDirectory() as tmp:
            private, public = ssh_proxy_session_keys.generate_dropbear_keypair(
                Path(tmp), "k"
            )
        # dropbear's native format is binary and not PEM/OpenSSH
        self.assertNotIn(b"BEGIN", private)
        self.assertTrue(public.startswith(b"ssh-ed25519 "), public)


class FakeSecretsApi:
    """Minimal stand-in for CoreV1Api, secrets keyed by name."""

    def __init__(self):
        self.secrets: dict[str, dict[str, bytes]] = {}

    def read_namespaced_secret(self, name, namespace):
        from kubernetes.client.exceptions import ApiException

        if name not in self.secrets:
            raise ApiException(status=404)
        from base64 import b64encode
        from types import SimpleNamespace

        data = {k: b64encode(v).decode() for k, v in self.secrets[name].items()}
        return SimpleNamespace(data=data)

    def create_namespaced_secret(self, namespace, body):
        from base64 import b64decode

        name = body["metadata"]["name"]
        self.secrets[name] = {k: b64decode(v) for k, v in body["data"].items()}


@unittest.skipUnless(shutil.which("dropbearkey"), "dropbearkey not installed")
class TestSessionMountSecret(unittest.TestCase):
    def test_composes_mount_secret_from_hop2_keypairs(self):
        api = FakeSecretsApi()
        ssh_proxy_session_keys.ensure_ssh_proxy_session_keys(api, "ns", "renku-remote")
        ssh_proxy_session_keys.ensure_session_mount_secret(
            api, "ns", "renku-remote", "renku-ssh-session-keys"
        )

        mount_secret = api.secrets["renku-ssh-session-keys"]
        host_key_secret = api.secrets["renku-remote-ssh-session-host-key"]
        auth_key_secret = api.secrets["renku-remote-ssh-proxy-session-auth-key"]

        self.assertEqual(mount_secret["ssh_host_ed25519_key"], host_key_secret["hostKey"])
        self.assertEqual(mount_secret["authorized_keys"], auth_key_secret["authKeyPub"])


if __name__ == "__main__":
    unittest.main()
