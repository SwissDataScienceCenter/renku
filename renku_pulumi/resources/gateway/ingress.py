import pulumi
from pulumi_kubernetes.extensions.v1beta1 import Ingress

from .values import gateway_values

def ingress(global_config):
    config = pulumi.Config('gateway')
    values = gateway_values()

    if not values['ingress']['enabled']:
        return

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

    spec = {
        'rules': []
    }

    for h in values['ingress']['hosts']:
        spec['rules'].append({
            'host': h,
            'http': {
                'paths': [{
                    'path': values['ingress']['path'],
                    'backend': {
                        'serviceName': gateway_name,
                        'servicePort': 'http'
                    }
                }]
            }
        })
    print(spec)

    if values['ingress']['tls']:
        spec['tls'] = []

        for t in values['ingress']['tls']:
            spec['tls'].append({
                'hosts': t['hosts'],
                'secretName': t['secretName']
            })

    return Ingress(
        gateway_name,
        metadata=gateway_metadata,
        spec=spec
    )
