import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts

config = pulumi.Config()

def keycloak(config, global_config, dependencies=[]):
    keycloak_config = pulumi.Config('keycloak')
    values = keycloak_config.require_object('values')

    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    global_config['keycloak'] = values

    values['global'] = global_values

    dependencies = [d for d in dependencies if d]
    return Chart(
        '{}-keycloak'.format(pulumi.get_stack()),
        config=ChartOpts(
            chart='keycloak',
            repo='stable',
            version='4.10.1',
            values=values
        ),
        opts=pulumi.ResourceOptions(depends_on=dependencies)
    )
