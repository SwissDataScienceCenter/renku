import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts

config = pulumi.Config()

def renku_ui(config, global_config):
    ui_config = pulumi.Config('ui')
    values = ui_config.require_object('values')

    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    values['global'] = global_values

    global_config['ui'] = values
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
