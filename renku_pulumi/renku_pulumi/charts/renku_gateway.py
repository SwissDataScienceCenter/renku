import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts

config = pulumi.Config()

def renku_gateway(config, global_config):
    gateway_config = pulumi.Config('gateway')
    values = gateway_config.require_object('values')

    global_config = pulumi.Config('global')
    global_values = global_config.require_object('values')

    values['global'] = global_values
    return Chart(
        'gateway',
        config=ChartOpts(
            chart='renku-gateway',
            version='0.6.0',
            fetch_opts=FetchOpts(
                repo='https://swissdatasciencecenter.github.io/helm-charts/'
            ),
            values=values
        )
    )
