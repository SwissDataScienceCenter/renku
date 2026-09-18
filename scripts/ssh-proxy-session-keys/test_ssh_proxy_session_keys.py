import shutil
import tempfile
import unittest
from pathlib import Path

import ssh_proxy_session_keys


class TestContract(unittest.TestCase):
    def test_secret_names(self):
        self.assertEqual(
            ssh_proxy_session_keys.secret_name("renku", ssh_proxy_session_keys.KEYPAIRS[0]["suffix"]),
            "renku-ssh-session-host-key",
        )
        self.assertEqual(
            ssh_proxy_session_keys.secret_name("renku", ssh_proxy_session_keys.KEYPAIRS[1]["suffix"]),
            "renku-ssh-proxy-session-auth-key",
        )

    def test_secret_data_keys(self):
        self.assertEqual(
            [kp["private_key"] for kp in ssh_proxy_session_keys.KEYPAIRS], ["hostKey", "authKey"]
        )
        self.assertEqual(
            [kp["public_key"] for kp in ssh_proxy_session_keys.KEYPAIRS], ["hostKeyPub", "authKeyPub"]
        )


@unittest.skipUnless(shutil.which("ssh-keygen"), "ssh-keygen not installed")
class TestGenerateKeypair(unittest.TestCase):
    def test_public_key_is_openssh_ed25519(self):
        with tempfile.TemporaryDirectory() as tmp:
            private, public = ssh_proxy_session_keys.generate_keypair(Path(tmp), "k", "renku-comment")
        self.assertIn("BEGIN OPENSSH PRIVATE KEY", private)
        self.assertTrue(public.startswith("ssh-ed25519 "), public)
        self.assertTrue(public.strip().endswith("renku-comment"))


if __name__ == "__main__":
    unittest.main()
