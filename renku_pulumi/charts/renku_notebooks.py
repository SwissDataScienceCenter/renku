import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts

config = pulumi.Config()

def delete_before_replace_resources(obj, opts):
    """change some resources to be deleted before upgrade to fix deployment errors."""
    types = ['PodDisruptionBudget', 'RoleBinding', 'Role', 'ServiceAccount']

    if obj['kind'] in types:
        opts.delete_before_replace = True

def renku_notebooks(config, global_config, dependencies=[]):
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
            values=values,
            transformations=[delete_before_replace_resources]
        ),
        opts=pulumi.ResourceOptions(depends_on=dependencies)
    )
