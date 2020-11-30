#!/bin/env python
#
# Usage: Specify versions of components and other deployment details via environment variables.
#
# Example:
#
# RENKU_VALUES_FILE=<path-to-values-file> \
# RENKU_RELEASE=<namespace>-renku \
# RENKU_RELEASE_NAMESPACE=<namespace> \
# renku_core="@<git-ref>" renku_ui="0.11.0" python3 deploy_dev_renku.py
#
#


#%%
import os
import pprint
import re
import tempfile

from pathlib import Path
from subprocess import check_call

import yaml

components = [
    "renku-core",
    "renku-gateway",
    "renku-graph",
    "renku-notebooks",
    "renku-ui",
]


class RenkuRequirement(object):
    """Class for handling custom renku requirements."""

    def __init__(self, component, version, tempdir):
        self.component = component
        self.tempdir = tempdir

        self.version_ = version
        self.ref = None
        self.is_git_ref = False
        if version.startswith("@"):
            # this is a git ref
            self.ref = version
            self.is_git_ref = True

    @property
    def version(self):
        if self.is_git_ref:
            self.clone()
            self.chartpress(skip_build=True)
            with open(
                self.repo_dir / "helm-chart" / self.component / "Chart.yaml"
            ) as f:
                chart = yaml.load(f)
            return chart.get("version")
        return self.version_

    @property
    def helm_repo(self):
        if self.ref:
            return f"file://{self.tempdir}/{self.repo}/helm-chart/{self.component}"
        return "https://swissdatasciencecenter.github.io/helm-charts/"

    # handle the special case of renku-python
    @property
    def repo(self):
        if self.component == "renku-core":
            return "renku-python"
        return self.component

    @property
    def repo_url(self):
        if self.component == "renku-core":
            return f"https://github.com/SwissDataScienceCenter/renku-python.git"
        return f"https://github.com/SwissDataScienceCenter/{self.component}.git"

    @property
    def repo_dir(self):
        return Path(f"{self.tempdir}/{self.repo}")

    def clone(self):
        """Clone repo and reset to ref."""
        if not self.repo_dir.exists():
            check_call(
                [
                    "git",
                    "clone",
                    self.repo_url,
                    self.repo_dir,
                ]
            )
        check_call(["git", "checkout", self.ref.strip("@")], cwd=self.repo_dir)

    def chartpress(self, skip_build=False):
        """Run chartpress."""
        check_call(
            ["helm", "dep", "update", f"helm-chart/{self.component}"], cwd=self.repo_dir
        )
        cmd = ["chartpress", "--push"]
        if skip_build:
            cmd.append("--skip-build")
        check_call(cmd, cwd=self.repo_dir)

    def setup(self):
        """Checkout the repo and run chartpress."""
        self.clone()
        self.chartpress()


def configure_requirements(tempdir, reqs, components):
    """
    Reads versions from environment variables and renders the requirements.yaml file.

    If any of the requested versions reference a git ref, the chart is rendered and
    images built and pushed to dockerhub.
    """
    for component in components:
        if os.environ.get(component.replace("-", "_")):
            # we have specified a component to override
            version = os.environ.get(component.replace("-", "_"))

            # form and setup the requirement
            req = RenkuRequirement(component, version, tempdir)
            if req.ref:
                req.setup()
                # replace the requirement
            for dep in reqs["dependencies"]:
                if dep["name"] == component.replace("_", "-"):
                    dep["version"] = req.version
                    dep["repository"] = req.helm_repo
                    continue
    return reqs


tempdir_ = tempfile.TemporaryDirectory()
tempdir = Path(tempdir_.name)

renku_dir = os.environ.get("RENKU_DIR", tempdir / "renku")
reqs_path = renku_dir / "charts/renku/requirements.yaml"

## 1. clone the renku repo
check_call(["git", "clone", "https://github.com/SwissDataScienceCenter/renku.git", renku_dir])

with open(reqs_path) as f:
    reqs = yaml.load(f)

## 2. set the chosen versions in the requirements.yaml file
reqs = configure_requirements(tempdir, reqs, components)

with open(reqs_path, "w") as f:
    yaml.dump(reqs, f)

## 3. run helm dep update renku
check_call(["helm3", "dep", "update", "renku"], cwd=renku_dir / "charts")

## 4. deploy
values_file = os.environ.get("RENKU_VALUES_FILE")
release = os.environ.get("RENKU_RELEASE")
release_namespace = os.environ.get("RENKU_RELEASE_NAMESPACE")

print(f"*** Dependencies for release {release} under namespace {release_namespace} ***")
pprint.pp(reqs)

check_call(
    [
        "helm3",
        "upgrade",
        "--install",
        release,
        "./renku",
        "-f",
        values_file,
        "--namespace",
        release_namespace,
    ],
    cwd=renku_dir / "charts",
)

tempdir_.cleanup()
