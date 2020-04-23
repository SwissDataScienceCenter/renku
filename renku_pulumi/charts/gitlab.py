import pulumi
from pulumi.resource import CustomTimeouts
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts
from pulumi_random.random_password import RandomPassword
from deepmerge import always_merger

default_chart_values = {
    "password": "gitlabadmin",
    "oauth": {"autoSignIn": True},
    "demoUserIsAdmin": False,
}


def gitlab(config, global_config, chart_reqs, postgres_chart, gitlab_postgres_secret, dependencies=[]):
    gitlab_config = pulumi.Config("gitlab")
    values = gitlab_config.get_object("values") or {}

    if postgres_chart:
        default_chart_values["postgresqlHost"] = postgres_chart.resources.apply(
            lambda r: next(s for k, s in r.items() if k.startswith("v1/Service"))
        ).metadata["name"]

    if gitlab_postgres_secret:
        default_chart_values['postgresSecret'] = gitlab_postgres_secret.metadata['name']

    values = always_merger.merge(default_chart_values, values)

    global_config['gitlab'] = values

    values["global"] = global_config["global"]

    chart_repo = chart_reqs.get("gitlab", "repository")
    if chart_repo.startswith("http"):
        repo = None
        fetchopts = FetchOpts(repo=chart_repo)
    else:
        repo = chart_repo
        fetchopts = None

    dependencies = [d for d in dependencies if d]

    return Chart(
        pulumi.get_stack(),
        config=ChartOpts(
            chart="gitlab",
            repo=repo,
            fetch_opts=fetchopts,
            version=chart_reqs.get("gitlab", "version"),
            values=values,
        ),
        opts=pulumi.ResourceOptions(depends_on=dependencies, custom_timeouts=CustomTimeouts(create="30m")),
    )
