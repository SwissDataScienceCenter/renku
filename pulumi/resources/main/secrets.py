from base64 import b64encode

import pulumi
from pulumi_kubernetes.core.v1 import Secret
from pulumi_random.random_password import RandomPassword

def gitlab_secrets():
    config = pulumi.Config()

    k8s_config = pulumi.Config('kubernetes')

    gitlab_db_password = config.get('gitlab_db_password')

    if not gitlab_db_password:
        gitlab_db_password = RandomPassword(
            'gitlab_db_password',
            length=64,
            special=True,
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
                gitlab_db_password.result.apply(
                    lambda p: b64encode(p.encode()).decode('ascii'))
        }
    )

    gitlab_sudo_token = config.get('gitlab_sudo_token')

    if not gitlab_sudo_token:
        gitlab_sudo_token = RandomPassword(
            'gitlab_sudo_token',
            length=64,
            special=True,
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
                gitlab_sudo_token.result.apply(
                    lambda p: b64encode(p.encode()).decode('ascii'))
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
            special=True,
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
                graph_db_password.result.apply(
                    lambda p: b64encode(p.encode()).decode('ascii'))
        }
    )

    graph_token_password = config.get('graph_token_postgres_password')

    if not graph_token_password:
        graph_token_password = RandomPassword(
            'graph_token_password',
            length=64,
            special=True,
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
                graph_token_password.result.apply(
                    lambda p: b64encode(p.encode()).decode('ascii'))
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
            special=True,
            number=True,
            upper=True)

    return Secret(
        'renku-jupyterhub-postgres',
        metadata={
            'labels':
                {
                    "app": pulumi.get_project(),
                    "release": pulumi.get_stack()
                }
        },
        type='Opaque',
        data={
            'jupyterhub-postgres-password':
                jupyterhub_db_password.result.apply(
                    lambda p: b64encode(p.encode()).decode('ascii'))
        }
    )

def keycloak_secrets():
    config = pulumi.Config()

    k8s_config = pulumi.Config('kubernetes')

    keycloak_db_password = config.get('keycloak_db_password')

    if not keycloak_db_password:
        keycloak_db_password = RandomPassword(
            'keycloak_db_password',
            length=64,
            special=True,
            number=True,
            upper=True)

    keycloak_postgres_secret = Secret(
        'renku-keycloak-postgres',
        metadata={
            'labels':
                {
                    "app": pulumi.get_project(),
                    "release": pulumi.get_stack()
                }
        },
        type='Opaque',
        data={
            'keycloak-postgres-password':
                keycloak_db_password.result.apply(
                    lambda p: b64encode(p.encode()).decode('ascii'))
        }
    )

    keycloak_password = config.get('keycloak_password')

    if not keycloak_password:
        keycloak_password = RandomPassword(
            'keycloak_password',
            length=64,
            special=True,
            number=True,
            upper=True)

    keycloak_password_secret = Secret(
        'keycloak-password-secret',
        metadata={
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
                    lambda p: b64encode(p.encode()).decode('ascii'))
        }
    )

    return keycloak_postgres_secret, keycloak_password_secret
