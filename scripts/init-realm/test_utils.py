"""Unit checks for the Keycloak client bootstrap helpers."""

import os
import unittest
from unittest import mock

from utils import OIDCClient, OIDCClientsConfig, OAuthFlow

# Every existing client is required by OIDCClientsConfig.from_env(), and every
# non-public client needs a secret, so a cleared environment must still carry
# all six.
BASE_ENV = {
    "RENKU_BASE_URL": "https://renku.example",
    "RENKU_KC_CLIENT_ID": "renku",
    "RENKU_KC_CLIENT_SECRET": "secret",
    "RENKU_KC_CLIENT_OAUTH_FLOW": "authorization_code",
    "CLI_KC_CLIENT_ID": "renku-cli",
    "CLI_KC_CLIENT_OAUTH_FLOW": "device",
    "CLI_KC_CLIENT_PUBLIC": "true",
    "UI_KC_CLIENT_ID": "renku-ui",
    "UI_KC_CLIENT_SECRET": "secret",
    "UI_KC_CLIENT_OAUTH_FLOW": "authorization_code",
    "NOTEBOOKS_KC_CLIENT_ID": "notebooks",
    "NOTEBOOKS_KC_CLIENT_SECRET": "secret",
    "NOTEBOOKS_KC_CLIENT_OAUTH_FLOW": "authorization_code",
    "SWAGGER_KC_CLIENT_ID": "swagger",
    "SWAGGER_KC_CLIENT_PUBLIC": "true",
    "SWAGGER_KC_CLIENT_OAUTH_FLOW": "authorization_code",
    "DATASERVICE_KC_CLIENT_ID": "data-service",
    "DATASERVICE_KC_CLIENT_SECRET": "secret",
    "DATASERVICE_KC_CLIENT_OAUTH_FLOW": "client_credentials",
}


class TestSSHProxyClient(unittest.TestCase):
    def _client(self, **overrides):
        kwargs = dict(
            id="ssh-proxy",
            base_url="https://renku.example",
            oauth_flow=OAuthFlow.client_credentials,
            secret="secret",
        )
        kwargs.update(overrides)
        return OIDCClient(**kwargs)

    def test_direct_access_grants_omitted_by_default(self):
        self.assertNotIn("directAccessGrantsEnabled", self._client().to_dict())

    def test_direct_access_grants_disabled_is_emitted(self):
        payload = self._client(direct_access_grants_enabled=False).to_dict()
        self.assertIs(payload["directAccessGrantsEnabled"], False)

    def test_service_account_enabled(self):
        payload = self._client(direct_access_grants_enabled=False).to_dict()
        self.assertIs(payload["serviceAccountsEnabled"], True)

    def test_renku_audience_is_mapped(self):
        # data-services validates `aud`; the shared payload maps it for every client.
        mappers = self._client().to_dict()["protocolMappers"]
        audiences = [m["config"].get("included.client.audience") for m in mappers]
        self.assertIn("renku", audiences)

    def test_client_is_absent_without_env(self):
        with mock.patch.dict(os.environ, BASE_ENV, clear=True):
            config = OIDCClientsConfig.from_env()
        self.assertIsNone(config.ssh_proxy)
        self.assertEqual(len(config.to_list()), 6)

    def test_client_is_present_with_env(self):
        env = {
            **BASE_ENV,
            "SSHPROXY_KC_CLIENT_ID": "ssh-proxy",
            "SSHPROXY_KC_CLIENT_SECRET": "secret",
            "SSHPROXY_KC_CLIENT_OAUTH_FLOW": "client_credentials",
        }
        with mock.patch.dict(os.environ, env, clear=True):
            config = OIDCClientsConfig.from_env()
        self.assertIsNotNone(config.ssh_proxy)
        self.assertEqual(len(config.to_list()), 7)
        self.assertEqual(config.to_list()[-1].id, "ssh-proxy")


if __name__ == "__main__":
    unittest.main()
