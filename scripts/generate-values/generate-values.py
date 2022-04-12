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
    """Run dev deploy."""

    argparser = argparse.ArgumentParser(description=__doc__)

    argparser.add_argument("--values-file", help="Input values file")
    argparser.add_argument(
        "--namespace", help="Namespace for the deployment"
    )
    argparser.add_argument(
        "--gitlab-url", help="Gitlab URL", default="https://dev.renku.ch/gitlab"
    )
    argparser.add_argument(
        "--gitlab-registry",
        help="Gitlab Image Registry URL",
        default="registry.dev.renku.ch",
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

    namespace = args.namespace or prompt("Namespace: ")

    if args.values_file:
        with open(args.values_file) as f:
            values = yaml.load(f.read())
    else:
        values = {}

    # merge values with the template
    with open(args.template) as f:
        t = f.read().format(namespace=namespace, renku_domain=args.renku_domain, gitlab_registry=args.gitlab_registry)
        template = yaml.load(t)

    values = merge_values(values, template)

    # import ipdb; ipdb.set_trace()

    # check if gitlab is being deployed - if not make sure we have the client configuration
    if not values.get("gitlab", {}).get("enabled", False):
        gateway_config = values.get("gateway", {})
        gitlab_url = (
            args.gitlab_url or gateway_config.get("gitlabUrl") or prompt("GitLab URL: ")
        )
        gitlab_client_id = (
            args.gitlab_client_id
            or gateway_config.get("gitlabClientId")
            or prompt("GitLab client id: ")
        )
        gitlab_client_secret = (
            args.gitlab_client_secret
            or gateway_config.get("gitlabClientSecret")
            or prompt("GitLab client secret: ")
        )

        if not (gitlab_url and gitlab_client_id and gitlab_client_secret):
            raise RuntimeError(
                "If not deploying own GitLab, you must specify the GitLab URL, client id and client secret."
            )

        # set the gitlab client id everywhere
        values["gateway"]["gitlabClientId"] = gitlab_client_id

        # set the gitlab client secret everywhere
        values["gateway"]["gitlabClientSecret"] = gitlab_client_secret

    # if a key is set to '<use `openssl rand -hex 32`>' automatically generate the secret
    values = recurse_dict_secrets(values)

    #
    # value mappings
    #

    # set the gitlab URL everywhere
    values["ui"]["gitlabUrl"] = gitlab_url
    values["notebooks"]["gitlab"]["url"] = gitlab_url
    values["graph"]["gitlab"]["url"] = gitlab_url
    values["gateway"]["gitlabUrl"] = gitlab_url
    values["notebooks"]["gitlab"]["registry"]["host"] = args.gitlab_registry

    if args.output:
        with open(args.output, "w") as f:
            yaml.dump(values, f)
        return 0

    print("{}".format(yaml.dump(values, sys.stdout)))



if __name__ == "__main__":
    main()
