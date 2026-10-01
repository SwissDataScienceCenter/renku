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


if __name__ == "__main__":
    unittest.main()
