import pulumi
from pulumi_random.random_id import RandomId

from charts import (
    renku_ui,
    GraphChart,
    renku_notebooks,
    postgresql,
    minio,
    keycloak,
    keycloak_secrets,
    gitlab,
    redis,
)

from resources.main import (
    configmap,
    ingress,
    postgres_postinstall_job,
    gitlab_postinstall_job,
    keycloak_postinstall_job,
    gitlab_secrets,
    graph_secrets,
    jupyterhub_secrets,
    renku_secret,
)

from resources import gateway
from deepmerge import always_merger

from configparser import ConfigParser

default_global_values = {
    "tests": {"image": {"repository": "renku/tests", "tag": "latest"}},
    "gateway": {},
    "gitlab": {},
    "graph": {
        "dbEventLog": {
            "postgresPassword": {"value": None, "overwriteOnHelmUpgrade": False},
            "existingSecret": None,
        },
        "tokenRepository": {
            "postgresPassword": {"value": None, "overwriteOnHelmUpgrade": False},
            "existingSecret": None,
        },
        "jena": {},
    },
    "renku": {},
    "useHTTPS": True,
}


def get_global_values(config):
    global_values = pulumi.Config("global")
    global_values = global_values.get_object("values") or {}

    stack = pulumi.get_stack()

    if config.get_bool("dev"):
        # set values for dev deployment
        default_global_values["gateway"]["clientSecret"] = RandomId(
            "gateway_client_secret", byte_length=32
        ).hex

        default_global_values["gateway"]["gitlabClientSecret"] = RandomId(
            "gateway_gitlab_client_secret", byte_length=32
        ).hex

        default_global_values["gitlab"]["clientSecret"] = RandomId(
            "gitlab_client_secret", byte_length=32
        ).hex

        baseurl = config.get("baseurl")
        k8s_config = pulumi.Config("kubernetes")

        if baseurl:
            default_global_values["renku"]["domain"] = "{}.{}".format(
                k8s_config.require("namespace"), baseurl
            )

    default_global_values["graph"]["jena"]["dataset"] = stack
    default_global_values["gitlab"]["fullname"] = "{}-gitlab".format(stack)

    global_values = always_merger.merge(default_global_values, global_values)

    return global_values


def deploy():
    config = pulumi.Config()

    chart_reqs = ConfigParser()
    chart_reqs.read("chart_requirements.ini")

    global_values = get_global_values(config)

    # global config is used to propagate dynamic values
    global_config = {}

    global_config["http"] = "https" if global_values["useHTTPS"] else "http"
    global_config["global"] = global_values

    rs = renku_secret(global_config)

    # ui
    ui = renku_ui(config, global_config, chart_reqs)

    postgres = None
    postgres_job = None
    graph = None
    keycloak_chart = None
    gitlab_chart = None
    gitlab_job = None
    gitlab_postgres_secret = None

    graph_postgres, graph_token = graph_secrets()

    if config.require_bool("postgres_enabled"):
        postgres = postgresql(config, global_config, chart_reqs)

    jupyterhub_secret = jupyterhub_secrets()
    notebooks = renku_notebooks(
        config, global_config, chart_reqs, jupyterhub_secret, postgres, dependencies=[postgres_job, gitlab_job]
    )

    config_map = configmap(global_config)

    if config.require_bool("gitlab_enabled"):
        gitlab_postgres_secret = gitlab_secrets()

    postgres_job = postgres_postinstall_job(global_config, graph_postgres, graph_token, config_map, gitlab_postgres_secret=gitlab_postgres_secret, dependencies=[postgres, gitlab_postgres_secret])

    if config.require_bool("keycloak_enabled"):
        keycloak_postgres, keycloak_password, keycloak_users = keycloak_secrets(global_config)
        keycloak_chart = keycloak(config, global_config, chart_reqs, postgres, [postgres_job])

    if config.require_bool("gitlab_enabled"):
        gitlab_chart = gitlab(config, global_config, chart_reqs, postgres, gitlab_postgres_secret, [postgres_job, keycloak_chart])
        gitlab_job = gitlab_postinstall_job(global_config, config_map, rs, gitlab_postgres_secret, gitlab_chart)

    if config.require_bool("graph_enabled"):
        graph = GraphChart(config, graph_postgres, graph_token, postgres, global_config=global_config, dependencies=[gitlab_job])

    if config.require_bool("minio_enabled"):
        minio(config, global_config)

    gateway_values = gateway.gateway_values(global_config)

    # gateway
    redis_chart = redis(global_config, chart_reqs)
    gateway_secret = gateway.secret(global_config, gateway_values)
    gateway_config = gateway.configmaps(global_config, gateway_values)
    gateway.ingress(global_config, gateway_values)
    gateway_deployment = gateway.gateway_deployment(global_config, gateway_values, gateway_secret, gateway_config)
    gateway.gateway_service(global_config, gateway_values, gateway_secret, gateway_config, gateway_deployment)

    renku_ingress = ingress(global_config, dependencies=[gateway_deployment, notebooks, ui, graph, keycloak_chart, gitlab_job])
    gateway_auth = gateway.gateway_auth_deployment(
        global_config, gateway_values, gateway_secret, gateway_config, redis_chart, dependencies=[renku_ingress]
    )
    gateway.gateway_auth_service(global_config, gateway_values, gateway_secret, gateway_config, gateway_auth)

    if config.require_bool("keycloak_enabled"):
        keycloak_postinstall_job(
            global_config, keycloak_users, [keycloak_chart, keycloak_postgres, keycloak_password, renku_ingress, gitlab_job]
        )

    pulumi.export('global_config', pulumi.Output.secret(global_config))


if __name__ == "__main__":
    deploy()
