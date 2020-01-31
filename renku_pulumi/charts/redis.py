import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts
from deepmerge import always_merger

default_chart_values = {
    "nameOverride": "gw-redis",
    "cluster": {
        "enabled": False
    },
    "usePassword": False,
    "networkPolicy": {
        "enabled": True,
        "allowExternal": False
    },
    "master": {
        "persistence": {
            "enabled": False
        },
        "resources": {
            "requests": {
                "cpu": "100m",
                "memory": "512Mi"
            }
        }
    }
}


def redis(global_config):
    redis_config = pulumi.Config('redis')
    values = redis_config.get_object('values') or {}

    values = always_merger.merge(default_chart_values, values)

    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    global_config['redis'] = {
        'fullname': '{}-redis'.format(pulumi.get_stack())
    }

    values['global'] = global_values
    return Chart(
        global_config['redis']['fullname'],
        config=ChartOpts(
            chart='redis',
            repo='stable',
            version='3.7.2',
            values=values
        )
    )
