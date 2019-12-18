from ..utils import b64encode

import pulumi
from pulumi_kubernetes.core.v1 import Secret

def secret(global_config):
    config = pulumi.Config('gateway')
    gateway_values = config.require_object('values')

    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    k8s_config = pulumi.Config('kubernetes')

    stack = pulumi.get_stack()

    gateway_name = "{}-{}-gateway".format(stack, pulumi.get_project())

    gateway_metadata = {
        'labels':
            {
                'app': gateway_name,
                'release': stack
            }
    }

    if 'oidcClientSecret' not in gateway_values:
        gateway_values['oidcClientSecret'] = global_values['gateway']['clientSecret']

    if 'gitlabClientSecret' not in gateway_values:
        gateway_values['gitlabClientSecret'] = global_values['gateway']['gitlabClientSecret']

    return Secret(
        gateway_name,
        metadata=gateway_metadata,
        type='Opaque',
        data={
            'oidcClientSecret': b64encode(gateway_values['oidcClientSecret']),
            'gitlabClientSecret': b64encode(gateway_values['gitlabClientSecret']),
            'jupyterhubClientSecret': b64encode(gateway_values['jupyterhub']['clientSecret']),
            'gatewaySecret': b64encode(gateway_values['secretKey'])
        }
    )
