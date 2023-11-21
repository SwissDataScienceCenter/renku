import json
import os
from dataclasses import dataclass, field
from enum import Enum
from typing import Any, Dict, List, Optional


@dataclass
class DemoUserConfig:
    """Stores the configuration needed to create a demo Renku user in Keycloak."""

    create_demo_user: bool
    demo_user_password: Optional[str] = field(default=None, repr=False)

    def __post_init__(self):
        if self.create_demo_user and not self.demo_user_password:
            raise ValueError("A demo user is required to be created but its password is not defined.")

    @classmethod
    def from_env(cls) -> "DemoUserConfig":
        return cls(
            create_demo_user=os.environ.get("CREATE_DEMO_USER", "false").lower() == "true",
            demo_user_password=os.environ.get("DEMO_USER_PASSWORD"),
        )

    def to_dict(self) -> Optional[Dict[str, Any]]:
        if not self.create_demo_user:
            return None
        return {
            "username": "demo@datascience.ch",
            "password": self.demo_user_password,
            "enabled": True,
            "emailVerified": True,
            "firstName": "John",
            "lastName": "Doe",
            "email": "demo@datascience.ch",
        }


class OAuthFlow(Enum):
    device: str = "device"
    authorization_code: str = "authorization_code"
    client_credentials: str = "client_credentials"

    def get_keycloak_payload(self, disable_other_flows: bool = True) -> Dict[str, Any]:
        output = {
            "oauth2DeviceAuthorizationGrantEnabled": False,
            "serviceAccountsEnabled": False,
            "standardFlowEnabled": False,
        } if disable_other_flows else {}
        match self:
            case OAuthFlow.authorization_code:
                output["standardFlowEnabled"] = True
            case OAuthFlow.device:
                output["oauth2DeviceAuthorizationGrantEnabled"] = True
            case OAuthFlow.client_credentials:
                output["serviceAccountsEnabled"] = True
        return output

    @classmethod
    def from_env(cls, prefix: str = ""):
        return cls(os.environ.get(f"{prefix}OAUTH_FLOW"))


@dataclass
class OIDCClient:
    """Stores the configuration needed to create an OIDC client application in Keycloak. These
    clients are utilized by different Renku backend or frontend services to authenticate any
    Renku user with Keycloak."""

    id: str
    base_url: str
    oauth_flow: OAuthFlow
    disable_other_oauth_flows: bool = True
    secret: Optional[str] = field(default=None, repr=False)
    attributes: Dict[str, Any] = field(default_factory=lambda: {})
    service_account_roles: List[str] = field(default_factory=list)
    public_client: bool = False

    def __post_init__(self):
        self.base_url = self.base_url.rstrip("/")
        if not self.public_client and not self.secret:
            raise ValueError(
                f"The OIDC client configuration for client {self.id} is not valid, "
                "the client is marked as not public but a secret is not provided."
            )
        if self.oauth_flow != OAuthFlow.client_credentials and len(self.service_account_roles) > 0:
            raise ValueError(
                f"Service account roles can only be specified for the {OAuthFlow.client_credentials.value} flow"
            )

    def to_dict(self) -> Dict[str, Any]:
        output = {
            "clientId": self.id,
            "baseUrl": self.base_url,
            "publicClient": self.public_client,
            "attributes": self.attributes,
            "redirectUris": [self.base_url + "/*"],
            "webOrigins": [self.base_url + "/*"],
            "protocolMappers": [
                {
                    "name": "renku audience for renku cli",
                    "protocol": "openid-connect",
                    "protocolMapper": "oidc-audience-mapper",
                    "consentRequired": False,
                    "config": {
                        "included.client.audience": "renku",
                        "id.token.claim": False,
                        "access.token.claim": True,
                        "userinfo.token.claim": False,
                    },
                }
            ],
        }
        if self.secret is not None:
            output["secret"] = self.secret
        output.update(**self.oauth_flow.get_keycloak_payload(self.disable_other_oauth_flows))
        return output

    @classmethod
    def from_env(cls, prefix: str = "RENKU_KC_CLIENT_") -> "OIDCClient":
        return cls(
            id=os.environ[f"{prefix}ID"],
            secret=os.environ.get(f"{prefix}SECRET"),
            base_url=os.environ.get(f"{prefix}BASE_URL", os.environ["RENKU_BASE_URL"]),
            attributes=json.loads(os.environ.get(f"{prefix}ATTRIBUTES", "{}")),
            public_client=os.environ.get(f"{prefix}PUBLIC", "false").lower() == "true",
            oauth_flow=OAuthFlow.from_env(prefix),
            disable_other_oauth_flows=os.environ.get(
                f"{prefix}DISABLE_OTHER_OAUTH_FLOWS", "true"
            ).lower() == "true",
            service_account_roles=json.loads(os.environ.get(f"{prefix}SERVICE_ACCOUNT_ROLES", "[]")),
        )


@dataclass
class OIDCGitlabClient:
    """A Keycloak OIDC client used by the internal Renku Gitlab deployment (if this deployment is enabled)."""

    internal_gitlab_enabled: bool = False
    oidc_client_secret: Optional[str] = field(default=None, repr=False)
    oidc_client_id: str = "gitlab"
    renku_base_url: Optional[str] = None

    def __post_init__(self):
        if self.internal_gitlab_enabled and not (self.oidc_client_secret or self.renku_base_url):
            raise ValueError(
                "The internal Gitlab is enabled, but the Renku base URL and/or the Keycloak OIDC client secret are not defined."
            )
        self.renku_base_url = self.renku_base_url.rstrip("/")

    @classmethod
    def from_env(cls, prefix: str = "INTERNAL_GITLAB_") -> "OIDCGitlabClient":
        return cls(
            internal_gitlab_enabled=os.environ.get(f"{prefix}ENABLED", "false").lower() == "true",
            oidc_client_secret=os.environ.get(f"{prefix}OIDC_CLIENT_SECRET"),
            oidc_client_id=os.environ.get(f"{prefix}OIDC_CLIENT_ID", "gitlab"),
            renku_base_url=os.environ.get(f"RENKU_BASE_URL"),
        )

    def to_dict(self) -> Optional[Dict[str, Any]]:
        if not self.internal_gitlab_enabled:
            return None
        return {
            "clientId": self.oidc_client_id,
            "baseUrl": f"{self.renku_base_url}/gitlab",
            "secret": self.oidc_client_secret,
            "redirectUris": [
                f"{self.renku_base_url}/gitlab/users/auth/oauth2_generic/callback",
            ],
            "webOrigins": [],
        }


@dataclass
class OIDCClientsConfig:
    renku: OIDCClient
    cli: OIDCClient
    ui: OIDCClient
    notebooks: OIDCClient
    swagger: OIDCClient
    data_service: OIDCClient

    @classmethod
    def from_env(cls) -> "OIDCClientsConfig":
        return cls(
            renku=OIDCClient.from_env(prefix="RENKU_KC_CLIENT_"),
            cli=OIDCClient.from_env(prefix="CLI_KC_CLIENT_"),
            ui=OIDCClient.from_env(prefix="UI_KC_CLIENT_"),
            notebooks=OIDCClient.from_env(prefix="NOTEBOOKS_KC_CLIENT_"),
            swagger=OIDCClient.from_env(prefix="SWAGGER_KC_CLIENT_"),
            data_service=OIDCClient.from_env(prefix="DATASERVICE_KC_CLIENT_"),
        )

    def to_list(self) -> List[Dict[str, Any]]:
        return [
            self.renku.to_dict(),
            self.cli.to_dict(),
            self.ui.to_dict(),
            self.notebooks.to_dict(),
            self.swagger.to_dict(),
            self.data_service.to_dict(),
        ]
