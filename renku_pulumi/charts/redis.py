import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts

config = pulumi.Config()


def redis(global_config):
    redis_config = pulumi.Config('redis')
    values = redis_config.require_object('values')

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
