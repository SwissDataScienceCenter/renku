import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts

config = pulumi.Config()

def postgresql(config, global_config):
    postgres_config = pulumi.Config('postgres')
    values = postgres_config.require_object('values')

    global_config['postgres'] = values

    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    values['global'] = global_values
    return Chart(
        '{}-postgresql'.format(pulumi.get_stack()),
        config=ChartOpts(
            chart='postgresql',
            repo='stable',
            version='0.14.4',
            values=values
        )
    )
