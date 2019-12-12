import pulumi
from pulumi_kubernetes.core.v1 import Service

def services(global_config):
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

    servicePort = config.get_int('port') or 80

    gateway_service = Service(
        gateway_name,
        metadata=gateway_metadata,
        spec={
            'type': gateway_values['service']['type'],
            'ports':[
                {
                    'port': servicePort,
                    'targetPort': 'http',
                    'protocol': 'TCP',
                    'name': 'http'
                },
                {
                    'port': 8000,
                    'targetPort': 'http',
                    'protocol': 'TCP',
                    'name': 'admin'
                }
            ],
            'selector': gateway_metadata['labels']
        }
    )

    auth_name = '{}-auth'.format(gateway_name)
    auth_metadata = {
        'labels':
            {
                'app': auth_name,
                'release': stack
            }
    }

    auth_service = Service(
        auth_name,
        metadata=auth_metadata,
        spec={
            'type': gateway_values['service']['type'],
            'ports':[
                {
                    'port': 80,
                    'targetPort': 'http',
                    'protocol': 'TCP',
                    'name': 'http'
                }
            ],
            'selector': auth_metadata['labels']
        }
    )

    return gateway_service, auth_service
