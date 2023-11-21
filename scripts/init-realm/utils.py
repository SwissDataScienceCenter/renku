import json
import os
from copy import deepcopy
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

    def get_keycloak_payload(
        self,
        existing_payload: Dict[str, Any] | None = None,
        disable_other_flows: bool = True
    ) -> Dict[str, Any]:
        output = deepcopy(existing_payload) if existing_payload else {}
        if disable_other_flows:
            output.update(
                serviceAccountsEnabled=False,
                standardFlowEnabled=False,
            )
        match self:
            case OAuthFlow.authorization_code:
                output["standardFlowEnabled"] = True
            case OAuthFlow.device:
                if isinstance(output.get("attributes"), dict):
                    output["attributes"]["oauth2.device.authorization.grant.enabled"] = True
                else:
                    output["attributes"] = {"oauth2.device.authorization.grant.enabled": True}
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
        default_protocol_mappers = []
        if self.oauth_flow == OAuthFlow.client_credentials:
            default_protocol_mappers.extend([
                {
                    "name": "Client ID",
                    "protocol": "openid-connect",
                    "protocolMapper": "oidc-usersessionmodel-note-mapper",
                    "consentRequired": False,
                    "config": {
                        "user.session.note": "clientId",
                        "id.token.claim": True,
                        "access.token.claim": True,
                        "claim.name": "clientId",
                        "jsonType.label": "String"
                    }
                },
                {
                    "name": "Client Host",
                    "protocol": "openid-connect",
                    "protocolMapper": "oidc-usersessionmodel-note-mapper",
                    "consentRequired": False,
                    "config": {
                        "user.session.note": "clientHost",
                        "id.token.claim": True,
                        "access.token.claim": True,
                        "claim.name": "clientHost",
                        "jsonType.label": "String"
                    }
                },
                {
                    "name": "Client IP Address",
                    "protocol": "openid-connect",
                    "protocolMapper": "oidc-usersessionmodel-note-mapper",
                    "consentRequired": False,
                    "config": {
                        "user.session.note": "clientAddress",
                        "id.token.claim": True,
                        "access.token.claim": True,
                        "claim.name": "clientAddress",
                        "jsonType.label": "String"
                    }
                }
            ])
        output = {
            "clientId": self.id,
            "baseUrl": self.base_url,
            "publicClient": self.public_client,
            "attributes": self.attributes,
            "redirectUris": [self.base_url + "/*"],
            "webOrigins": [self.base_url + "/*"],
            "protocolMappers": default_protocol_mappers + [
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
        output = self.oauth_flow.get_keycloak_payload(output, self.disable_other_oauth_flows)
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
class OIDCGitlabClient(OIDCClient):
    """A Keycloak OIDC client used by the internal Renku Gitlab deployment."""

    @classmethod
    def from_env(cls, prefix: str = "INTERNAL_GITLAB_") -> "OIDCGitlabClient":
        return cls(
            secret=os.environ.get(f"{prefix}OIDC_CLIENT_SECRET"),
            id=os.environ.get(f"{prefix}OIDC_CLIENT_ID", "gitlab"),
            base_url=os.environ.get("RENKU_BASE_URL"),
            oauth_flow=OAuthFlow.authorization_code,
        )

    def to_dict(self) -> Optional[Dict[str, Any]]:
        return {
            "clientId": self.id,
            "baseUrl": f"{self.base_url}",
            "secret": self.secret,
            "redirectUris": [
                f"{self.base_url}/users/auth/oauth2_generic/callback",
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

    def to_list(self) -> List[OIDCClient]:
        return [
            self.renku,
            self.cli,
            self.ui,
            self.notebooks,
            self.swagger,
            self.data_service,
        ]
