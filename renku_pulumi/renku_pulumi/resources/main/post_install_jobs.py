import pulumi

from pulumi_kubernetes.batch.v1 import Job

def gitlab_postinstall_job(global_config, dependencies=[]):
    config = pulumi.Config()
    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    k8s_config = pulumi.Config('kubernetes')

    stack = pulumi.get_stack()
    project = pulumi.get_project()

    renku_name = '{}-{}'.format(stack, project)

    name = '{}-post-install-gitlab'.format(stack)

    return Job(
        name,
        metadata={
            'labels':
                {
                    'app': pulumi.get_project(),
                    'release': stack
                }
        },
        spec={
            'template':{
                'metadata': {
                    'name': name,
                    'labels':
                        {
                            'app': pulumi.get_project(),
                            'release': stack
                        }
                },
                'spec': {
                    'restartPolicy': 'Never',
                    'containers': [
                        {
                            'name': 'configure-gitlab',
                            'image': 'postgres:9.6',
                            'command': [
                                "/bin/bash",
                                "/scripts/init-gitlab.sh"],
                            'volumeMounts': [{
                                'name': 'init',
                                'mountPath': '/scripts',
                                'readOnly': True
                            }],
                            'env':[
                                {
                                    'name': 'PGDATABASE',
                                    'value': global_values['gitlab']['postgresDatabase']
                                },
                                {
                                    'name': 'PGUSER',
                                    'value': global_values['gitlab']['postgresUser']
                                },
                                {
                                    'name': 'PGHOST',
                                    'value': '{}-postgresql'.format(stack)
                                },
                                {
                                    'name': 'PGPASSWORD',
                                    'valueFrom': {
                                        'secretKeyRef': {
                                            'name': '{}-{}-gitlab-postgres'.format(stack, project),
                                            'key': 'gitlab-postgres-password'
                                        }
                                    }
                                },
                                {
                                    'name': 'JUPYTERHUB_AUTH_GITLAB_CLIENT_SECRET',
                                    'valueFrom': {
                                        'secretKeyRef': {
                                            'name': renku_name,
                                            'key': 'jupyterhub-auth-gitlab-client-secret'
                                        }
                                    }
                                },
                                {
                                    'name': 'GATEWAY_GITLAB_CLIENT_SECRET',
                                    'valueFrom': {
                                        'secretKeyRef': {
                                            'name': renku_name,
                                            'key': 'gateway-gitlab-client-secret'
                                        }
                                    }
                                }
                            ]
                        }
                    ],
                    'volumes':[{
                        'name': 'init',
                        'configMap':{
                            'name': renku_name
                        }
                    }]
                }
            }
        },
        opts=pulumi.ResourceOptions(depends_on=dependencies)
    )


def keycloak_postinstall_job(global_config, dependencies=[]):
    config = pulumi.Config()
    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    k8s_config = pulumi.Config('kubernetes')

    stack = pulumi.get_stack()
    project = pulumi.get_project()

    renku_name = '{}-{}'.format(stack, project)

    name = '{}-post-install-keycloak'.format(stack)

    return Job(
        name,
        metadata={
            'labels':
                {
                    'app': pulumi.get_project(),
                    'release': stack
                }
        },
        spec={
            'template':{
                'metadata': {
                    'name': name,
                    'labels':
                        {
                            'app': pulumi.get_project(),
                            'release': stack
                        }
                },
                'spec':{
                    'restartPolicy':'Never',
                    'containers':[{
                        'name': 'init-keycloak',
                        'image': '{}:{}'.format(
                            global_config['keycloak']['initRealm']['image']['repository'],
                            global_config['keycloak']['initRealm']['image']['tag']),
                        'command': ['python'],
                        'args': [
                            "/app/init-realm.py",
                            "--admin-user=$(KEYCLOAK_ADMIN_USER)",
                            "--admin-password=$(KEYCLOAK_ADMIN_PASSWORD)",
                            "--keycloak-url=$(KEYCLOAK_URL)",
                            "--users-file=/app/data/users",
                            "--clients-file=/app/data/clients"
                        ],
                        'volumeMounts':[{
                            'name': 'realm-data',
                            'mountPath': '/app/data',
                            'readOnly': True
                        }],
                        'env':[
                            {
                                'name': 'KEYCLOAK_URL',
                                'value': '{}://{}/auth/'.format('https', global_values['renku']['domain']) # TODO: where does this value come from?
                            },
                            {
                                'name': 'KEYCLOAK_ADMIN_USER',
                                'valueFrom':{
                                    'secretKeyRef':{
                                        'name': renku_name,
                                        'key': 'keycloak-username'
                                    }
                                }
                            },
                            {
                                'name': 'KEYCLOAK_ADMIN_PASSWORD',
                                'valueFrom':{
                                    'secretKeyRef':{
                                        'name': 'keycloak-password-secret',
                                        'key': 'keycloak-password'
                                    }
                                }
                            },
                            {
                                'name': 'PYTHONUNBUFFERED',
                                'value': '0'
                            }
                        ]
                    }],
                    'volumes': [{
                        'name': 'realm-data',
                        'secret': {
                            'secretName': renku_name
                        }
                    }]
                }
            }
        },
        opts=pulumi.ResourceOptions(depends_on=dependencies)
    )

def postgres_postinstall_job(global_config, dependencies=[]):
    config = pulumi.Config()
    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    k8s_config = pulumi.Config('kubernetes')

    stack = pulumi.get_stack()
    project = pulumi.get_project()

    renku_name = '{}-{}'.format(stack, project)

    name = '{}-post-install-postgres'.format(stack)

    volume_mounts = [
        {
            'name': 'scripts',
            'mountPath': '/scripts',
            'readOnly': True
        },
        {
            'name': 'passwords',
            'mountPath': '/passwords',
            'readOnly': True
        },
        {
            'name': 'keycloak-postgres',
            'mountPath': '/keycloak-postgres',
            'readOnly': True
        },
        {
            'name': 'jupyterhub-postgres',
            'mountPath': '/jupyterhub-postgres',
            'readOnly': True
        },
        {
            'name': 'graph-db-postgres',
            'mountPath': '/graph-db-postgres',
            'readOnly': True
        },
        {
            'name': 'graph-token-postgres',
            'mountPath': '/graph-token-postgres',
            'readOnly': True
        }
    ]

    volumes = [
        {
            'name': 'scripts',
            'configMap': {
                'name': renku_name
            }
        },
        {
            'name': 'passwords',
            'secret': {
                'secretName': renku_name
            }
        },
        {
            'name': 'keycloak-postgres',
            'secret': {
                'secretName': 'renku-keycloak-postgres'
            }
        },
        {
            'name': 'jupyterhub-postgres',
            'secret': {
                'secretName': 'renku-jupyterhub-postgres'
            }
        },
        {
            'name': 'graph-db-postgres',
            'secret': {
                'secretName': '{}-graph-db-postgres'.format(renku_name)
            }
        },
        {
            'name': 'graph-token-postgres',
            'secret': {
                'secretName': '{}-graph-token-postgres'.format(renku_name)
            }
        }
    ]

    if config.get_bool('gitlab_enabled'):
        volume_mounts.extend([
            {
                'name': 'gitlab-sudo',
                'mountPath': '/gitlab-sudo',
                'readOnly': True
            },
            {
                'name': 'gitlab-postgres',
                'mountPath': '/gitlab-postgres',
                'readOnly': True
            }
        ])

        volumes.extend([
            {
                'name': 'gitlab-sudo',
                'secret': {
                    'secretName': '{}-gitlab-sudo'.format(renku_name)
                }
            },
            {
                'name': 'gitlab-postgres',
                'secret': {
                    'secretName': '{}-gitlab-postgres'.format(renku_name)
                }
            }
        ])

    return Job(
        name,
        metadata={
            'labels':
                {
                    'app': pulumi.get_project(),
                    'release': stack
                }
        },
        spec={
            'template':{
                'metadata': {
                    'name': name,
                    'labels':
                        {
                            'app': pulumi.get_project(),
                            'release': stack
                        }
                },
                'spec':{
                    'restartPolicy':'Never',
                    'containers':[{
                        'name': 'init-databases',
                        'image': '{}:{}'.format(
                            config.get('postgresql_image'),
                            config.get('postgresql_imagetag')),
                        'command': ["/bin/bash", "/scripts/init-postgres.sh"],
                        'volumeMounts':volume_mounts,
                        'env':[
                            {
                                'name': 'PGDATABASE',
                                'value': config.get('postgres_database')
                            },
                            {
                                'name': 'PGUSER',
                                'value': config.get('postgres_user')
                            },
                            {
                                'name': 'PGHOST',
                                'value': '{}-postgresql'.format(stack)
                            },
                            {
                                'name': 'PGPASSWORD',
                                'valueForm':{
                                    'secretKeyRef':{
                                        'name': '{}-postgresql'.format(stack),
                                        'key': 'postgres-password'
                                    }
                                }
                            }
                        ]
                    }],
                    'volumes': volumes
                }
            }
        },
        opts=pulumi.ResourceOptions(depends_on=dependencies)
    )
