import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts

from charts import (renku_ui, renku_graph, renku_notebooks, postgresql,
                    minio, keycloak, gitlab, redis)

from resources.main import (configmap, ingress, postgres_postinstall_job,
    gitlab_postinstall_job, keycloak_postinstall_job, gitlab_secrets,
    graph_secrets, jupyterhub_secrets, keycloak_secrets, renku_secret)

from resources import gateway

def deploy():
    config = pulumi.Config()

    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    # global config is used to propagate dynamic values
    global_config = {}

    global_config['http'] = 'https' if global_values['useHTTPS'] else 'http'

    configmap(global_config)

    ingress(global_config)

    renku_secret()

    renku_ui(config, global_config)

    jupyterhub_secrets()
    renku_notebooks(config, global_config)

    #gateway
    redis(global_config)
    sc = gateway.secret(global_config)
    cfg = gateway.configmaps(global_config)
    gateway.deployments(global_config, sc, cfg)
    gateway.ingress(global_config)
    gateway.services(global_config)

    if config.require_bool('graph_enabled'):
        pg, tok = graph_secrets()
        renku_graph(config, global_config, pg, tok)

    if config.require_bool('gitlab_enabled'):
        gps, gss = gitlab_secrets()
        g = gitlab(config, global_config)
        gitlab_postinstall_job(global_config, gps, [g])

    if config.require_bool('postgres_enabled'):
        p = postgresql(config, global_config)
        postgres_postinstall_job(global_config, [p])

    if config.require_bool('keycloak_enabled'):
        keycloak_secrets()
        k = keycloak(config, global_config)
        keycloak_postinstall_job(global_config, [k])

    if config.require_bool('minio_enabled'):
        minio(config, global_config)

if __name__ == "__main__":
    deploy()
