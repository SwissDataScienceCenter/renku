import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts

config = pulumi.Config()

def gitlab(config, global_config):
    gitlab_config = pulumi.Config('gitlab')
    values = gitlab_config.require_object('values')

    global_config = pulumi.Config('global')
    global_values = global_config.require_object('values')

    values['global'] = global_values
    return Chart(
        '{}-gitlab'.format(pulumi.get_stack()),
        config=ChartOpts(
            chart='gitlab',
            repo='stable',
            version='0.4.1-9418599',
            values=values
        )
    )
