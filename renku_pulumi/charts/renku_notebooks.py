import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts
from pulumi_random.random_id import RandomId

from deepmerge import always_merger

default_chart_values = {
    "gitlab": {
        "registry": {}
    },
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
        "hub": {
            "services": {
                "notebooks": {},
                "gateway": {}
            },
            "extraEnv": []
        },
        "auth": {
            "state": {},
            "gitlab": {}
        },
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

    if config.get_bool('dev'):
        default_chart_values['jupyterhub']['hub']['cookieSecret'] = RandomId(
            'notebook_jupyterhub_cookie_secret',
            byte_length=32).hex

        default_chart_values['jupyterhub']['hub']['services']['notebooks']['apiToken'] = RandomId(
            'notebook_jupyterhub_notebooks_api_token',
            byte_length=32).hex

        default_chart_values['jupyterhub']['auth']['state']['cryptoKey'] = RandomId(
            'notebook_jupyterhub_auth_crypto_key',
            byte_length=32).hex

        default_chart_values['jupyterhub']['proxy']['secretToken'] = RandomId(
            'notebook_jupyterhub_proxy_secret_token',
            byte_length=32).hex

        baseurl = config.get('baseurl')
        k8s_config = pulumi.Config("kubernetes")

        if baseurl:
            default_chart_values['gitlab']['registry']['host'] = "registry.{}".format(baseurl)
            gitlab_url = "https://{}/gitlab".format(baseurl)
            default_chart_values['gitlab']['url'] = gitlab_url
            default_chart_values['jupyterhub']['hub']['extraEnv'].append({'name': 'GITLAB_URL', 'value': gitlab_url})
            default_chart_values['jupyterhub']['hub']['services']['gateway']['oauth_redirect_url'] = "https://{}.{}/api/auth/jupyterhub/token".format(
                k8s_config.require("namespace"), baseurl)
            default_chart_values['jupyterhub']['auth']['gitlab']['callbackUrl'] = "https://{}.{}/jupyterhub/hub/oauth_callback".format(
                k8s_config.require("namespace"), baseurl)

    if 'gateway' in global_config and 'jupyterhub' in global_config['gateway'] and 'clientSecret' in global_config['gateway']['jupyterhub']:
        default_chart_values['jupyterhub']['hub']['services']['gateway']['apiToken'] = global_config['gateway']['jupyterhub']['clientSecret']

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
            version=notebooks_config.require('version'),
            fetch_opts=FetchOpts(
                repo=notebooks_config.get('repository') or 'https://swissdatasciencecenter.github.io/helm-charts/'
            ),
            values=values,
            transformations=[delete_before_replace_resources]
        ),
        opts=pulumi.ResourceOptions(depends_on=dependencies)
    )
