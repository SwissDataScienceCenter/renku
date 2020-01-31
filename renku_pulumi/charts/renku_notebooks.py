import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts
from deepmerge import always_merger

default_chart_values = {
    "securityContext": {
        "enabled": False
    },
    "resources": {
        "requests": {
            "cpu": "100m",
            "memory": "512Mi"
        }
    },
    "serverOptions": {
        "defaultUrl": {
            "order": 1,
            "displayName": "Default Environment",
            "type": "enum",
            "default": "/lab",
            "options": [
                "/lab",
                "/rstudio"
            ]
        },
        "cpu_request": {
            "order": 2,
            "displayName": "Number of CPUs",
            "type": "enum",
            "default": 0.1,
            "options": [
                0.1,
                0.5
            ]
        },
        "mem_request": {
            "order": 3,
            "displayName": "Amount of Memory",
            "type": "enum",
            "default": "1G",
            "options": [
                "1G",
                "2G"
            ]
        },
        "gpu_request": {
            "order": 4,
            "displayName": "Number of GPUs",
            "type": "enum",
            "default": 0,
            "options": [
                0
            ]
        },
        "lfs_auto_fetch": {
            "order": 5,
            "displayName": "Automatically fetch LFS data",
            "type": "boolean",
            "default": False
        }
    },
    "jupyterhub": {
        "rbac": {
            "enabled": True
        },
        "scheduling": {
            "userPlaceholder": {
                "enabled": False
            },
            "userPods": {
                "nodeAffinity": {
                    "matchNodePurpose": "require"
                }
            },
            "userScheduler": {
                "pdb": {
                    "enabled": False
                }
            }
        },
        "singleuser": {
            "image": {
                "name": "renku/singleuser",
                "tag": "0.3.7-renku0.5.2"
            }
        },
        "proxy": {
            "service": {
                "type": "ClusterIP"
            },
            "https": {
                "enabled": False
            },
            "chp": {
                "resources": {
                    "requests": {
                        "cpu": "100m",
                        "memory": "512Mi"
                    }
                }
            }
        }
    }
}


def delete_before_replace_resources(obj, opts):
    """change some resources to be deleted before upgrade to fix deployment errors."""
    types = ['PodDisruptionBudget', 'RoleBinding', 'Role', 'ServiceAccount']

    if obj['kind'] in types:
        opts.delete_before_replace = True


def renku_notebooks(config, global_config, dependencies=[]):
    notebooks_config = pulumi.Config('notebooks')
    values = notebooks_config.get_object('values') or {}

    values = always_merger.merge(default_chart_values, values)

    sentry = config.get('sentry_dsn')

    if sentry:
        hub = values.setdefault('jupyterhub', {})
        singleuser = hub.setdefault('singleuser', {})
        singleuser.setdefault('sentryDsn', sentry)

    values['global'] = global_config['global']
    return Chart(
        '{}-notebooks'.format(pulumi.get_stack()),
        config=ChartOpts(
            chart='renku-notebooks',
            version='0.6.2',
            fetch_opts=FetchOpts(
                repo='https://swissdatasciencecenter.github.io/helm-charts/'
            ),
            values=values,
            transformations=[delete_before_replace_resources]
        ),
        opts=pulumi.ResourceOptions(depends_on=dependencies)
    )
