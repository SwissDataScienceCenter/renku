import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts

config = pulumi.Config()

def renku_notebooks(config, global_config):
    notebooks_config = pulumi.Config('notebooks')
    values = notebooks_config.require_object('values')

    global_config = pulumi.Config('global')
    global_values = global_config.require_object('values')

    values['global'] = global_values
    return Chart(
        '{}-notebooks'.format(pulumi.get_stack()),
        config=ChartOpts(
            chart='renku-notebooks',
            version='0.6.2',
            fetch_opts=FetchOpts(
                repo='https://swissdatasciencecenter.github.io/helm-charts/'
            ),
            values=values
        )
    )
