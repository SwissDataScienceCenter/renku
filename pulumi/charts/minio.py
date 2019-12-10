import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts

config = pulumi.Config()

def minio(config, global_config):
    minio_config = pulumi.Config('minio')
    values = minio_config.require_object('values')

    global_config = pulumi.Config('global')
    global_values = global_config.require_object('values')

    values['global'] = global_values
    return Chart(
        'minio',
        config=ChartOpts(
            chart='minio',
            repo='stable',
            version='1.6.0',
            values=values
        )
    )
