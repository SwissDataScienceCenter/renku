import pulumi
from pulumi_kubernetes.extensions.v1beta1 import Ingress

def ingress(global_config):
    config = pulumi.Config()

    if not config.get_bool('ingress_enabled'):
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

    annotations = config.get_object("ingress_annotations")

    if annotations:
        metadata['annotations'] = annotations

    spec = {}

    tls = config.get_object('ingress_tls')

    if tls:
        spec['tls'] = [{
            'hosts': t['hosts'],
            'secretName': t['secretName']
        } for t in tls]

    hosts = config.get_object('ingress_hosts')

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
                                'serviceName':'{}-gateway'.format(stack),
                                'servicePort':global_config['gateway']['service']['port']
                            }
                        },
                        {
                            'path': '/',
                            'backend':{
                                'serviceName':'{}-ui'.format(stack),
                                'servicePort':global_config['ui']['service']['port']
                            }
                        }
                    ]
                }
            }

            if pulumi.get_bool('keycloak_enabled'):
                h['http']['paths'].append({
                    'path': '/auth',
                    'backend': {
                        'serviceName':'{}-keycloak'.format(stack),
                        'servicePort':global_config['keycloak']['service']['port']
                    }
                })

            if pulumi.get_bool('gitlab_enabled'):
                h['http']['paths'].append({
                    'path': '/gitlab',
                    'backend': {
                        'serviceName':'{}-gitlab'.format(stack),
                        'servicePort': 80
                    }
                })

            if pulumi.get_bool('graph_enabled'):
                h['http']['paths'].append({
                    'path': '/webhook/events',
                    'backend': {
                        'serviceName':'{}-webhook-service'.format(stack),
                        'servicePort': 80
                    }
                })

                h['http']['paths'].append({
                    'path': '/knowledge-graph',
                    'backend': {
                        'serviceName':'{}-knowledge-graph'.format(stack),
                        'servicePort': 80
                    }
                })

        spec['rules'] = [{
            'host': h,

        } for h in hosts]

    return Ingress(
        "{}-{}".format(stack, pulumi.get_project()),
        metadata=metadata,
        spec=spec
    )
