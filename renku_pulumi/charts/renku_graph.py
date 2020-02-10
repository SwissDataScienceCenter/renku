from base64 import b64encode

import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts
from pulumi_random.random_password import RandomPassword
from pulumi_random.random_id import RandomId
from deepmerge import always_merger

default_chart_values = {
    "jena": {
        "users": {
            "admin": {},
            "renku": {}
        },
        "resources": {
            "requests": {
                "cpu": "200m",
                "memory": "1Gi"
            }
        },
        "persistence": {
            "storageClass": "temporary"
        }
    },
    "webhookService": {
        "hookToken": {},
        "eventsSynchronization": {
            "initialDelay": "2 minutes",
            "interval": "1 hour"
        }
    },
    "tokenRepository": {
        "tokenEncryption": {}
    },
    "resources": {
        "requests": {
            "cpu": "100m",
            "memory": "2Gi"
        }
    }
}


def renku_graph(config, global_config, postgres_secret, token_secret, postgres_chart):
    graph_config = pulumi.Config('graph')
    values = graph_config.get_object('values') or {}

    if config.get_bool('dev'):
        default_chart_values['jena']['users']['admin']['password'] = RandomPassword(
            'graph_jena_admin_password',
            length=8,
            special=False,
            number=True,
            upper=False,
            lower=True).result.apply(lambda r: b64encode(r.encode()).decode('ascii'))

        default_chart_values['jena']['users']['renku']['password'] = RandomPassword(
            'graph_jena_renku_password',
            length=8,
            special=False,
            number=True,
            upper=False,
            lower=True).result.apply(lambda r: b64encode(r.encode()).decode('ascii'))

        default_chart_values['webhookService']['hookToken']['secret'] = RandomId(
            'graph_webhookservice_secret',
            byte_length=8).hex.apply(lambda r: b64encode(r.encode()).decode('ascii'))

        default_chart_values['tokenRepository']['tokenEncryption']['secret'] = RandomId(
            'graph_tokenrepository_secret',
            byte_length=8).hex.apply(lambda r: b64encode(r.encode()).decode('ascii'))

    values = always_merger.merge(default_chart_values, values)

    k8s_config = pulumi.Config("kubernetes")

    values['global'] = global_config['global']

    sentry = config.get('sentry_dsn')

    if sentry:
        sent = values.setdefault('sentry', {})
        sent.setdefault('sentryDsnRenkuPython', sentry)

    stack = pulumi.get_stack()

    if postgres_chart and 'postgresqlHost' not in values:
        values['postgresqlHost'] = postgres_chart.resources.apply(
            lambda r: next(s for k, s in r.items() if k.startswith('v1/Service'))
            ).metadata['name']

    #generate passwords
    # admin_password = RandomPassword("admin_password", length=8, special=True, number=True, upper=True)
    # values.jena.users.admin['password'] = admin_password.result.apply(lambda p: b64encode(p.encode()).decode('ascii'))

    # renku_password = RandomPassword("renku_password", length=8, special=True, number=True, upper=True)
    # values.jena.users.renku['password'] = renku_password.result.apply(lambda p: b64encode(p.encode()).decode('ascii'))

    # hook_token = RandomPassword("hook_token", length=8, special=True, number=True, upper=True)
    # values.webhookService.hookToken.secret = hook_token.result.apply(lambda p: b64encode(p.encode()).decode('ascii'))

    # encryption_token = RandomPassword("encraption_token", length=8, special=True, number=True, upper=True)
    # values.tokenRepository.tokenEncryption.secret = encryption_token.result.apply(lambda p: b64encode(p.encode()).decode('ascii'))

    # values.sentry.environmentName = values.sentry.environmentName.format(namespace=k8s_config.require("namespace"))
    # values.sentry.sentryDsnRenkuPython = global_config['sentryDsnRenkuPython']

    values['global']['graph']['dbEventLog']['existingSecret'] = postgres_secret.metadata['name']
    values['global']['graph']['tokenRepository']['existingSecret'] = token_secret.metadata['name']

    global_config['graph'] = values

    return Chart(
        '{}-graph'.format(stack),
        config=ChartOpts(
            chart='renku-graph',
            version=graph_config.require('version'),
            fetch_opts=FetchOpts(
                repo=graph_config.get('repository') or 'https://swissdatasciencecenter.github.io/helm-charts/'
            ),
            values=values
        )
    )
