import pulumi
from pulumi_kubernetes.extensions.v1beta1 import Ingress

def ingress(global_config):
    config = pulumi.Config('gateway')
    gateway_values = config.require_object('values')

    if not gateway_values['ingress']['enabled']:
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

    for h in gateway_values['ingress']['hosts']:
        spec['rules'].append({
            'host': h,
            'http': {
                'paths':[{
                    'path': gateway_values['ingress']['path'],
                    'backend': {
                        'serviceName': gateway_name,
                        'servicePort': 'http'
                    }
                }]
            }
        })

    if gateway_values['ingress']['tls']:
        spec['tls'] = []

        for t in gateway_values['ingress']['tls']:
            spec['tls'].append({
                'hosts': t['hosts'],
                'secretName': t['secretName']
            })

    return Ingress(
        gateway_name,
        metadata=gateway_metadata,
        spec=spec
    )
