import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts
from deepmerge import always_merger

default_chart_values = {
    "ingress": {"enabled": False},
    "service": {"type": "ClusterIP", "port": 80},
    "welcomePage": {
        "text": "## Welcome to Renku!\nRenku is software for collaborative data science.\nWith Renku you can share code and data, discuss problems and solutions, and coordinate data-science projects.\n## Template\nI am templateable, so deployment specific information can be put here!\n"
    },
    "resources": {"requests": {"cpu": "100m", "memory": "128Mi"}},
    "templatesRepository": {
        "url": "https://github.com/SwissDataScienceCenter/renku-project-template",
        "ref": "master",
    },
}


def renku_ui(config, global_config):
    ui_config = pulumi.Config("ui")
    values = ui_config.get_object("values") or {}

    if config.get_bool("dev"):
        k8s_config = pulumi.Config("kubernetes")

        baseurl = config.get("baseurl")

        if baseurl:
            default_chart_values["baseUrl"] = "https://{}.{}".format(
                k8s_config.require("namespace"), baseurl
            )
            default_chart_values["gitlabUrl"] = "https//{}/gitlab".format(baseurl)
            default_chart_values["gatewayUrl"] = "https://{}.{}/api".format(
                k8s_config.require("namespace"), baseurl
            )
            default_chart_values["jupyterhubUrl"] = "https://{}.{}/jupyterhub".format(
                k8s_config.require("namespace"), baseurl
            )

    values = always_merger.merge(default_chart_values, values)

    values["global"] = global_config["global"]

    global_config["ui"] = values
    return Chart(
        "{}-ui".format(pulumi.get_stack()),
        config=ChartOpts(
            chart="renku-ui",
            version=ui_config.require("version"),
            fetch_opts=FetchOpts(
                repo=ui_config.get("repository")
                or "https://swissdatasciencecenter.github.io/helm-charts/"
            ),
            values=values,
        ),
    )
