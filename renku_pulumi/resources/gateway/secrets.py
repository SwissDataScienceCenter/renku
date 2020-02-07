from ..utils import b64encode

import pulumi
from pulumi_kubernetes.core.v1 import Secret

from .values import gateway_values

def secret(global_config):
    config = pulumi.Config('gateway')
    values = gateway_values()

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

    if 'oidcClientSecret' not in values:
        values['oidcClientSecret'] = global_values['gateway']['clientSecret']

    if 'gitlabClientSecret' not in values:
        values['gitlabClientSecret'] = global_values['gateway']['gitlabClientSecret']

    return Secret(
        gateway_name,
        metadata=gateway_metadata,
        type='Opaque',
        data={
            'oidcClientSecret': b64encode(values['oidcClientSecret']),
            'gitlabClientSecret': b64encode(values['gitlabClientSecret']),
            'jupyterhubClientSecret': b64encode(values['jupyterhub']['clientSecret']),
            'gatewaySecret': b64encode(values['secretKey'])
        }
    )
