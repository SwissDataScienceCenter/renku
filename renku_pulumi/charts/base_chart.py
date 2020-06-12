from base64 import b64encode

import yaml
import pulumi
from pulumi_kubernetes.helm.v2 import Chart, ChartOpts, FetchOpts
from pulumi_random.random_password import RandomPassword
from pulumi_random.random_id import RandomId
from deepmerge import always_merger


class BaseChart(Chart):
    chart_name = None
    default_values_template = {}

    def __init__(self, config, global_config=None, dependencies=[]):
        self.global_config = global_config

        stack = pulumi.get_stack()

        self.release_name = "{}-graph".format(stack)
        opts = None

        if dependencies:
            opts = pulumi.ResourceOptions(depends_on=dependencies)

        super().__init__(self.release_name, self.chart_opts, opts=opts)

    @property
    def pulumi_config(self):
        """Base pulumi config."""
        return pulumi.Config()

    @property
    def chart_requirements(self):
        """Get chart requirements."""

        with open('chart_requirements.yml', 'r') as f:
            chart_reqs = yaml.load(f, Loader=yaml.FullLoader)

        return chart_reqs.get(self.chart_name, {})

    @property
    def chart_opts(self):
        """Get the Chart config."""
        return ChartOpts(
            chart="renku-graph",
            version=self.chart_version,
            repo=self.chart_repo,
            fetch_opts=self.fetch_opts,
            values=self.values,
        )

    @property
    def fetch_opts(self):
        """Get chart fetch_opts."""
        chart_repo = self.chart_requirements["repository"]
        if chart_repo.startswith("http"):
            return FetchOpts(repo=chart_repo)
        return None

    @property
    def chart_repo(self):
        """Get chart fetch_opts."""
        chart_repo = self.chart_requirements["repository"]
        if chart_repo.startswith("http"):
            return None

        return chart_repo

    @property
    def chart_version(self):
        """Get the version of the chart to deploy."""
        self.chart_requirements["version"]

    @property
    def default_values(self):
        """Get chart default values."""
        return self.default_values_template

    @property
    def values(self):
        """Get chart values."""
        chart_config = pulumi.Config(self.chart_name)
        values = chart_config.get_object("values") or {}

        values = always_merger.merge(self.default_values, values)

        values = self.values_post_process(values)

        values["global"] = self.global_config["global"]
        self.global_config[self.chart_name] = values

        return values

    def values_post_process(self, values):
        """Postprocessing of values."""
        return values

    def generated_random_password(self, name, length):
        """Helper method to generated a random password."""
        return RandomPassword(
                name,
                length=length,
                special=False,
                number=True,
                upper=False,
                lower=True,
            ).result.apply(lambda r: b64encode(r.encode()).decode("ascii"))

    def generate_random_id(self, name, length=8):
        """Helper method to generated a random id."""
        return RandomId(
                name, byte_length=length
            ).hex.apply(lambda r: b64encode(r.encode()).decode("ascii"))
