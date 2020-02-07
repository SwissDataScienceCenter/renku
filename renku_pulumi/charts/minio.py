import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts
from deepmerge import always_merger

default_chart_values = {}


def minio(config, global_config):
    minio_config = pulumi.Config('minio')
    values = minio_config.get_object('values') or {}

    values = always_merger.merge(default_chart_values, values)

    values['global'] = global_config['global']
    return Chart(
        '{}-minio'.format(pulumi.get_stack()),
        config=ChartOpts(
            chart='minio',
            repo='stable',
            version=minio_config.require('version'),
            values=values
        )
    )
