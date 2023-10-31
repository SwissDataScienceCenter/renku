#!/usr/bin/env python3

import argparse
import re
import subprocess
import sys

import ruamel.yaml
from prompt_toolkit import prompt

yaml = ruamel.yaml.YAML()
yaml.preserve_quotes = True


def merge_values(v1, v2):
    for k, v in v2.items():
        if isinstance(v, dict):
            node = v1.setdefault(k, {})
            merge_values(node, v)
        v1.setdefault(k, v)
    return v1


def recurse_dict_secrets(d, path=""):
    """Recursively populate values."""
    secret_regex = re.compile("<use \`(.*)\`>")
    for k, v in d.items():
        if isinstance(v, dict):
            d[k] = recurse_dict_secrets(v, path="{path}.{k}".format(path=path, k=k))
        else:
            if isinstance(v, str):
                if secret_regex.match(v):
                    command = secret_regex.findall(v)[0]
                    d[k] = subprocess.check_output(command, shell=True).decode().strip()
                    print("Adding secret {path}.{k}".format(path=path, k=k))
    return d


def main():
    """Render a basic Renku values file."""

    print("----------------------------")
    print("| Configuring Renku values |")
    print("----------------------------")

    argparser = argparse.ArgumentParser(description=__doc__)

    argparser.add_argument(
        "--namespace", help="Namespace for the deployment"
    )
    argparser.add_argument("--gitlab-url", help="Gitlab URL")
    argparser.add_argument(
        "--gitlab-registry",
        help="Gitlab Image Registry URL"
    )
    argparser.add_argument("--gitlab-client-id", help="Gitlab client ID")
    argparser.add_argument(
        "--gitlab-client-secret", help="Gitlab client secret"
    )
    argparser.add_argument("--renku-domain", help="Renku domain")
    argparser.add_argument(
        "--template", help="Values template to use", default="base-renku-values.yaml.template"
    )
    argparser.add_argument(
        "--output", "-o", help="Output file"
    )
    args = argparser.parse_args()

    namespace = args.namespace or prompt("Namespace: ", default="renku")

    # we must have a renku domain
    renku_domain = args.renku_domain or prompt("Renku domain: ", default="renku.example.com")

    # ask for information about Gitlab
    gitlab_url = (
        args.gitlab_url or prompt("GitLab URL: ")
    )
    gitlab_client_id = (
        args.gitlab_client_id or prompt("GitLab client id: ")
    )
    gitlab_client_secret = (
        args.gitlab_client_secret or prompt("GitLab client secret: ")
    )
    gitlab_registry = (
        args.gitlab_registry or prompt("Gitlab registry hostname: ", default=f"registry.{renku_domain}")
    )

    if not (gitlab_url and gitlab_client_id and gitlab_client_secret and gitlab_registry):
        raise RuntimeError(
            "You must specify the GitLab URL, client id and client secret."
        )

    # read in the template and set the values
    with open(args.template) as f:
        t = f.read().format(
            namespace=namespace,
            renku_domain=renku_domain,
            gitlab_registry=gitlab_registry,
            gitlab_url=gitlab_url
        )
        values = yaml.load(t)

    # if a key is set to '<use `openssl rand -hex 32`>' automatically generate the secret
    values = recurse_dict_secrets(values)

    values["global"]["gateway"]["gitlabClientId"] = gitlab_client_id
    values["global"]["gateway"]["gitlabClientSecret"] = gitlab_client_secret
    values["gitlab"] = {"enabled": False}

    warning = """
# This is an automatically generated values file to deploy Renku.
# Please scrutinize it carefully before trying to deploy.
"""

    if args.output:
        with open(args.output, "w") as f:
            f.write(warning)
            yaml.dump(values, f)
        return 0

    print("")
    print("--------------------------")
    print("| Auto-generated values: |")
    print("--------------------------")

    print(warning)
    yaml.dump(values, sys.stdout)

if __name__ == "__main__":
    main()
