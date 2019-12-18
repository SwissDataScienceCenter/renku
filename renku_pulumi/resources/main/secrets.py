import json

from jinja2 import Template
import pulumi
from pulumi import Output
from pulumi_kubernetes.core.v1 import Secret
from pulumi_random.random_password import RandomPassword

from ..utils import b64encode

KEYCLOAK_CLIENTS_TEMPLATE = Template("""
[
  {
    "clientId": "renku",
    "baseUrl": "{{ global.http }}://{{ global.renku.domain }}",
    "secret": "{{ global.gateway.clientSecret }}",
    "redirectUris": [
        "{{ global.http}}://{{ global.renku.domain }}/*"
    ],
    "webOrigins": [
        "{{ global.http }}://{{ global.renku.domain }}/*"
    ],
    "protocolMappers": [{
      "name": "audience for renku",
      "protocol": "openid-connect",
      "protocolMapper": "oidc-audience-mapper",
      "consentRequired": false,
      "config": {
        "included.client.audience": "renku",
        "id.token.claim": false,
        "access.token.claim": true,
        "userinfo.token.claim": false
      }
    }]
  }

  {% if gitlab_enabled %}
  ,{
    "clientId": "gitlab",
    "baseUrl": "{{ global.http }}://{{ global.renku.domain }}/gitlab",
    "secret": "{{ global.gitlab.clientSecret }}",
    "redirectUris": [
      "{{ global.http }}://{{ global.renku.domain }}/gitlab/users/auth/oauth2_generic/callback"
    ],
    "webOrigins": []
  }
  {% endif %}
]""", trim_blocks=True, lstrip_blocks=True)

KEYCLOAK_USERS_TEMPLATE = Template("""
[{
    "username": "demo",
    "password": "{{ password}}",
    "enabled": true,
    "emailVerified": true,
    "firstName": "John",
    "lastName": "Doe",
    "email": "demo@datascience.ch"
  }]""", trim_blocks=True, lstrip_blocks=True)

def gitlab_secrets():
    config = pulumi.Config()

    k8s_config = pulumi.Config('kubernetes')

    gitlab_db_password = config.get('gitlab_db_password')

    if not gitlab_db_password:
        gitlab_db_password = RandomPassword(
            'gitlab_db_password',
            length=64,
            special=False,
            number=True,
            upper=True)

    gitlab_postgres_secret = Secret(
        '{}-gitlab-postgres'.format(pulumi.get_stack()),
        metadata={
            'labels':
                {
                    'app': pulumi.get_project(),
                    'release': pulumi.get_stack()
                }
        },
        type='Opaque',
        data={
            'gitlab-postgres-password':
                Output.from_input(gitlab_db_password).result.apply(
                    lambda p: b64encode(p))
        }
    )

    gitlab_sudo_token = config.get('gitlab_sudo_token')

    if not gitlab_sudo_token:
        gitlab_sudo_token = RandomPassword(
            'gitlab_sudo_token',
            length=64,
            special=False,
            number=True,
            upper=True)

    gitlab_sudo_secret = Secret(
        '{}-gitlab-sudo'.format(pulumi.get_stack()),
        metadata={
            'labels':
                {
                    'app': pulumi.get_project(),
                    'release': pulumi.get_stack()
                }
        },
        type='Opaque',
        data={
            'gitlab-sudo-token':
                Output.from_input(gitlab_sudo_token).result.apply(
                    lambda p: b64encode(p))
        }
    )

    return gitlab_postgres_secret, gitlab_sudo_secret

def graph_secrets():
    config = pulumi.Config()

    k8s_config = pulumi.Config('kubernetes')

    graph_db_password = config.get('graph_db_password')

    if not graph_db_password:
        graph_db_password = RandomPassword(
            'graph_db_password',
            length=64,
            special=False,
            number=True,
            upper=True)

    graph_db_postgres_secret = Secret(
        '{}-graph-db-postgres'.format(pulumi.get_stack()),
        metadata={
            'labels':
                {
                    "app": pulumi.get_project(),
                    "release": pulumi.get_stack()
                }
        },
        type='Opaque',
        data={
            'graph-dbEventLog-postgresPassword':
                Output.from_input(graph_db_password).result.apply(
                    lambda p: b64encode(p))
        }
    )

    graph_token_password = config.get('graph_token_postgres_password')

    if not graph_token_password:
        graph_token_password = RandomPassword(
            'graph_token_password',
            length=64,
            special=False,
            number=True,
            upper=True)

    graph_token_postgres_secret = Secret(
        '{}-graph-token-postgres'.format(pulumi.get_stack()),
        metadata={
            'labels':
                {
                    "app": pulumi.get_project(),
                    "release": pulumi.get_stack()
                }
        },
        type='Opaque',
        data={
            'graph-tokenRepository-postgresPassword':
                Output.from_input(graph_token_password).result.apply(
                    lambda p: b64encode(p))
        }
    )

    return graph_db_postgres_secret, graph_token_postgres_secret

def jupyterhub_secrets():
    config = pulumi.Config()

    k8s_config = pulumi.Config('kubernetes')

    jupyterhub_db_password = config.get('jupyterhub_db_password')

    if not jupyterhub_db_password:
        jupyterhub_db_password = RandomPassword(
            'jupyterhub_db_password',
            length=64,
            special=False,
            number=True,
            upper=True)

    return Secret(
        'renku-jupyterhub-postgres',
        metadata={
            'name': 'renku-jupyterhub-postgres',
            'labels':
                {
                    "app": pulumi.get_project(),
                    "release": pulumi.get_stack()
                }
        },
        type='Opaque',
        data={
            'jupyterhub-postgres-password':
                Output.from_input(jupyterhub_db_password).result.apply(
                    lambda p: b64encode(p))
        },
        opts=pulumi.ResourceOptions(delete_before_replace=True)
    )

def keycloak_secrets():
    config = pulumi.Config()

    k8s_config = pulumi.Config('kubernetes')

    keycloak_db_password = config.get('keycloak_db_password')

    if not keycloak_db_password:
        keycloak_db_password = RandomPassword(
            'keycloak_db_password',
            length=64,
            special=False,
            number=True,
            upper=True)

    keycloak_postgres_secret = Secret(
        'renku-keycloak-postgres',
        metadata={
            'name': 'renku-keycloak-postgres',
            'labels':
                {
                    "app": pulumi.get_project(),
                    "release": pulumi.get_stack()
                }
        },
        type='Opaque',
        data={
            'keycloak-postgres-password':
                Output.from_input(keycloak_db_password).result.apply(
                    lambda p: b64encode(p))
        },
        opts=pulumi.ResourceOptions(delete_before_replace=True)
    )

    keycloak_password = config.get('keycloak_password')

    if not keycloak_password:
        keycloak_password = RandomPassword(
            'keycloak_password',
            length=64,
            special=False,
            number=True,
            upper=True)

    keycloak_password = Output.from_input(keycloak_password)

    pulumi.export('keycloak-admin-password', keycloak_password.result)

    keycloak_password_secret = Secret(
        'keycloak-password-secret',
        metadata={
            'name': 'keycloak-password-secret',
            'labels':
                {
                    "app": pulumi.get_project(),
                    "release": pulumi.get_stack()
                }
        },
        type='Opaque',
        data={
            'keycloak-password':
                keycloak_password.result.apply(
                    lambda p: b64encode(p))
        },
        opts=pulumi.ResourceOptions(delete_before_replace=True)
    )

    return keycloak_postgres_secret, keycloak_password_secret


def renku_secret(global_config):
    config = pulumi.Config()

    notebooks_values = pulumi.Config('notebooks')
    notebooks_values = notebooks_values.require_object('values')

    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    k8s_config = pulumi.Config('kubernetes')

    gitlab_client_secret = notebooks_values['jupyterhub']['auth']['gitlab']['clientSecret']

    if not gitlab_client_secret:
        gitlab_client_secret = RandomPassword(
            'keycloak_db_password',
            length=64,
            special=True,
            number=True,
            upper=True)

    gateway_gitlab_client_secret = global_values['gateway']['gitlabClientSecret']

    if not gateway_gitlab_client_secret:
        gateway_gitlab_client_secret = RandomPassword(
            'keycloak_db_password',
            length=64,
            special=True,
            number=True,
            upper=True)

    data = {
            'jupyterhub-auth-gitlab-client-secret': Output.from_input(gitlab_client_secret).apply(
                    lambda p: b64encode(p)),
            'gateway-gitlab-client-secret': Output.from_input(gateway_gitlab_client_secret).apply(
                    lambda p: b64encode(p))
        }

    if config.get_bool('keycloak_enabled'):
        keycloak_values = pulumi.Config('keycloak')
        keycloak_values = keycloak_values.require_object('values')

        data['keycloak-username'] = b64encode(keycloak_values['keycloak']['username'])

        pulumi.export('keycloak-admin-username', keycloak_values['keycloak']['username'])

        users_data = {
            'global': {**global_config, **global_values},
            'gitlab_enabled': config.get_bool('gitlab_enabled') or False
        }
        data['clients'] = b64encode(KEYCLOAK_CLIENTS_TEMPLATE.render(**users_data))
        data['users'] = b64encode("[]")

        if 'createDemoUser' in keycloak_values['keycloak'] and keycloak_values['keycloak']['createDemoUser']:
            client_password = RandomPassword(
                'keycloak_demo_password',
                length=32,
                special=True,
                number=True,
                upper=True)

            data['clients'] = b64encode(KEYCLOAK_USERS_TEMPLATE.render(password=client_password))

    if 'users_json' in global_values['tests']:
        data['users.json'] = b64encode(json.dumps(global_values['tests']['users_json']))

    secret_name = "{}-{}".format(pulumi.get_stack(), pulumi.get_project())

    return Secret(
        secret_name,
        metadata={
            'name': secret_name,
            'labels':
                {
                    "app": pulumi.get_project(),
                    "release": pulumi.get_stack()
                }
        },
        type='Opaque',
        data=data,
        opts=pulumi.ResourceOptions(delete_before_replace=True)
    )
