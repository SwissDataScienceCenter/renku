import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts

from charts import (renku_ui, renku_graph, renku_notebooks, postgresql,
                    minio, keycloak, keycloak_secrets, gitlab, redis)

from resources.main import (configmap, ingress, postgres_postinstall_job,
    gitlab_postinstall_job, keycloak_postinstall_job, gitlab_secrets,
    graph_secrets, jupyterhub_secrets, renku_secret)

from resources import gateway

def deploy():
    config = pulumi.Config()

    global_values = pulumi.Config('global')
    global_values = global_values.require_object('values')

    # global config is used to propagate dynamic values
    global_config = {}

    global_config['http'] = 'https' if global_values['useHTTPS'] else 'http'
    global_config['global'] = global_values

    if config.require_bool('gitlab_enabled'):
        gps, gss = gitlab_secrets()
        g = gitlab(config, global_config)
        gitlab_postinstall_job(global_config, gps, [g, gss])

    renku_secret(global_config)

    # ui
    ui = renku_ui(config, global_config)

    cfg_map = configmap(global_config)

    pg = None
    tok = None
    pg_job = None
    graph = None
    kc_job = None

    if config.require_bool('graph_enabled'):
        pg, tok = graph_secrets()
        graph = renku_graph(config, global_config, pg, tok)

    if config.require_bool('postgres_enabled'):
        p = postgresql(config, global_config)
        pg_job = postgres_postinstall_job(global_config, pg, tok, cfg_map, [p])

    if config.require_bool('keycloak_enabled'):
        kp, ks, kus = keycloak_secrets(global_config)
        keycloak_chart = keycloak(config, global_config, [pg_job])

    if config.require_bool('minio_enabled'):
        minio(config, global_config)

    jupyterhub_secrets()
    nb = renku_notebooks(config, global_config, dependencies=[pg_job])

    # gateway
    redis_chart = redis(global_config)
    sc = gateway.secret(global_config)
    cfg = gateway.configmaps(global_config)
    gateway.ingress(global_config)
    gd = gateway.gateway_deployment(global_config, sc, cfg)
    gs = gateway.gateway_service(global_config, sc, cfg, gd)

    ing = ingress(global_config, dependencies=[gd, nb, ui, graph, keycloak_chart])
    gad = gateway.gateway_auth_deployment(global_config, sc, cfg, redis_chart, dependencies=[ing])
    gas = gateway.gateway_auth_service(global_config, sc, cfg, gad)

    if config.require_bool('keycloak_enabled'):
        kc_job = keycloak_postinstall_job(global_config, kus, [keycloak_chart, kp, ks, ing])

if __name__ == "__main__":
    deploy()
