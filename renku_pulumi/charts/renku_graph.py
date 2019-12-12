from base64 import b64encode

import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts
from pulumi_random.random_password import RandomPassword

config = pulumi.Config()

def renku_graph(config, global_config, postgres_secret, token_secret):
    graph_config = pulumi.Config('graph')
    values = graph_config.require_object('values')
    k8s_config = pulumi.Config("kubernetes")

    global_config = pulumi.Config('global')
    global_values = global_config.require_object('values')

    values['global'] = global_values

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

    return Chart(
        '{}-graph'.format(pulumi.get_stack()),
        config=ChartOpts(
            chart='renku-graph',
            version='0.32.0-913601f',
            fetch_opts=FetchOpts(
                #repo='https://swissdatasciencecenter.github.io/helm-charts/'
                repo='http://127.0.0.1:8879/'
            ),
            values=values
        )
    )
