import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts
from deepmerge import always_merger

default_chart_values = {
    "password": "gitlabadmin",
    "image": {
        "repository": "gitlab/gitlab-ce",
        "tag": "11.8.10-ce.0"
    },
    "oauth":{
      "autoSignIn": True
    },
    "demoUserIsAdmin": False,
    "persistence": {
        "size": "30Gi"
    },
    "registry": {
        "enabled": False
    }
}


def gitlab(config, global_config):
    gitlab_config = pulumi.Config('gitlab')
    values = gitlab_config.get_object('values') or {}

    values = always_merger.merge(default_chart_values, values)

    values['global'] = global_config['global']
    return Chart(
        '{}-gitlab'.format(pulumi.get_stack()),
        config=ChartOpts(
            chart='gitlab',
            repo='stable',
            version='0.4.1-9418599',
            values=values
        )
    )
