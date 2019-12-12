import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts

config = pulumi.Config()

def renku_ui(config, global_config):
    graph_config = pulumi.Config('graph')
    values = graph_config.require_object('values')

    global_config = pulumi.Config('global')
    global_values = global_config.require_object('values')

    values['global'] = global_values
    return Chart(
        '{}-ui'.format(pulumi.get_stack()),
        config=ChartOpts(
            chart='renku-ui',
            version='0.7.2',
            fetch_opts=FetchOpts(
                repo='https://swissdatasciencecenter.github.io/helm-charts/'
            ),
            values=values
        )
    )
