import pulumi
from pulumi_kubernetes.extensions.v1beta1 import Ingress

def ingress(global_config):
    config = pulumi.Config()
    ingress_config = pulumi.Config('ingress')

    if not ingress_config.get_bool('enabled'):
        return

    k8s_config = pulumi.Config('kubernetes')

    stack = pulumi.get_stack()

    metadata = {
        'labels':
            {
                'app': pulumi.get_project(),
                'release': stack
            }
    }

    annotations = ingress_config.get_object("annotations")

    if annotations:
        metadata['annotations'] = annotations

    spec = {}

    tls = ingress_config.get_object('tls')

    if tls:
        spec['tls'] = [{
            'hosts': t['hosts'],
            'secretName': t['secretName']
        } for t in tls]

    hosts = ingress_config.get_object('hosts')


    if hosts:
        spec['rules'] = []
        for host in hosts:
            h = {
                'host': host,
                'http': {
                    'paths': [
                        {
                            'path': '/jupyterhub',
                            'backend':{
                                'serviceName': 'proxy-public',
                                'servicePort': 8000
                            }
                        },
                        {
                            'path': '/api',
                            'backend':{
                                'serviceName':global_config['gateway']['name'],
                                'servicePort': global_config['gateway']['service']['port']
                            }
                        },
                        {
                            'path': '/',
                            'backend':{
                                'serviceName':'{}-ui-renku-ui'.format(stack),
                                'servicePort':global_config['ui']['service']['port']
                            }
                        }
                    ]
                }
            }

            if config.get_bool('keycloak_enabled'):
                h['http']['paths'].append({
                    'path': '/auth',
                    'backend': {
                        'serviceName':'{}-keycloak-http'.format(stack),
                        'servicePort': global_config['keycloak']['keycloak']['service']['port']
                    }
                })

            if config.get_bool('gitlab_enabled'):
                h['http']['paths'].append({
                    'path': '/gitlab',
                    'backend': {
                        'serviceName':'{}-gitlab'.format(stack),
                        'servicePort': 80
                    }
                })

            if config.get_bool('graph_enabled'):
                h['http']['paths'].append({
                    'path': '/webhook/events',
                    'backend': {
                        'serviceName':'{}-graph-webhook-service'.format(stack),
                        'servicePort': 80
                    }
                })

                h['http']['paths'].append({
                    'path': '/knowledge-graph',
                    'backend': {
                        'serviceName':'{}-graph-knowledge-graph'.format(stack),
                        'servicePort': 80
                    }
                })

            spec['rules'].append(h)

    return Ingress(
        "{}-{}".format(stack, pulumi.get_project()),
        metadata=metadata,
        spec=spec
    )
