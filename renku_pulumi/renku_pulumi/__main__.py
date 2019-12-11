import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts

from renku_pulumi.charts import (renku_ui, renku_graph, renku_notebooks, renku_gateway,
    postgresql, minio, keycloak, gitlab)

from renku_pulumi.resources.main import (configmap, ingress, postgres_postinstall_job,
    gitlab_postinstall_job, keycloak_postinstall_job, gitlab_secrets,
    graph_secrets, jupyterhub_secrets, keycloak_secrets)

def deploy():
    config = pulumi.Config()

    # global config is used to propagate dynamic values
    global_config = {}

    confmap = configmap(global_config)
    ing = ingress(global_config)


    renku_ui(config, global_config)

    jupyterhub_secrets()
    renku_notebooks(config, global_config)

    renku_gateway(config, global_config)

    # if config.require_bool('graph_enabled'):
    #     graph_secrets()
    #     renku_graph(config, global_config)

    if config.require_bool('gitlab_enabled'):
        g = gitlab(config, global_config)
        gitlab_secrets()
        gitlab_postinstall_job(global_config, [g])

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
