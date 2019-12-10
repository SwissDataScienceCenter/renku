import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts

config = pulumi.Config()

def postgresql(config, global_config):
    postgres_config = pulumi.Config('postgres')
    values = postgres_config.require_object('values')

    global_config = pulumi.Config('global')
    global_values = global_config.require_object('values')

    values['global'] = global_values
    return Chart(
        'postgresql',
        config=ChartOpts(
            chart='postgresql',
            repo='stable',
            version='0.14.4',
            values=values
        )
    )
