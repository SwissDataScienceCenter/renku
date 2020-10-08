#!/usr/bin/env python

import argparse
import os
import random
import re
import secrets
import subprocess
import sys

import kubernetes as k8s
import ruamel.yaml

yaml = ruamel.yaml.YAML()
yaml.preserve_quotes = True

# Note: We hardcode "notebooks.fullname" / the service
# name here. These secondary deployments MUST run in their
# own dedicated namespace anyway.
TEMPORARY_NOTEBOOKS_SERVICE_NAME = "notebooks-tmp"


JUPYTERHUB_POSTGRES_SECRET_NAME = "renku-jupyterhub-tmp-postgres"


def random_hex_seeded(length, seed):
    """Get a random hex string of a given lenth with a specific seed."""
    random.seed(seed)
    return bytearray(random.getrandbits(8) for _ in range(length)).hex()


def get_values(release_name, kube_context, renku_namespace):
    """Get the values file from an existing helm release."""
    helm_version = subprocess.run(
        [
            "helm",
            "version",
        ],
        stdout=subprocess.PIPE,
    ).stdout.decode("utf-8")
    sys.stdout.write(f"Helm version {helm_version} \n")
    values = subprocess.run(
        [
            "helm",
            "get",
            "values",
            f"{release_name}",
            "--kube-context",
            f"{kube_context}",
            "--namespace",
            f"{renku_namespace}",
        ],
        check=True,
        stdout=subprocess.PIPE,
    ).stdout.decode("utf-8")
    return yaml.load(values)


def make_tmp_values(values, renku_namespace, out_path=None):
    """Create a values file for a secondary notebooks service helm deployment
    (inluding a secondary Jupyterhub) which handles temporary sessions for
    logged-out users."""

    # Copy over all the necessary stuff from the original values file
    new_values = values["notebooks"]

    # We don't know the release name here, so we override the name of the service
    new_values["fullnameOverride"] = TEMPORARY_NOTEBOOKS_SERVICE_NAME

    new_values["global"] = {
        "renku": {"domain": values["global"]["renku"]["domain"]},
        "useHTTPS": values["global"]["useHTTPS"],
        "gitlab": {"urlPrefix": values["global"]["gitlab"]["urlPrefix"]},
    }

    hub_section = new_values["jupyterhub"]["hub"]
    hub_section["cookieSecret"] = random_hex_seeded(32, hub_section["cookieSecret"])
    hub_section["baseUrl"] = "{}-tmp/".format(
        hub_section.get("baseUrl", "/jupyterhub/").rstrip("/")
    )
    hub_section["db"]["url"] = (
        hub_section["db"]["url"]
        .replace("jupyterhub", "jupyterhub-tmp")
        .replace("postgresql:", "postgresql.{}.svc:".format(renku_namespace))
    )
    hub_section["extraEnv"] = [
        env
        for env in hub_section["extraEnv"]
        if env["name"] not in ["PGPASSWORD", "JUPYTERHUB_AUTHENTICATOR"]
    ]
    hub_section["extraEnv"].append(
        {
            "name": "PGPASSWORD",
            "valueFrom": {
                "secretKeyRef": {
                    "name": "renku-jupyterhub-tmp-postgres",
                    "key": "jupyterhub-tmp-postgres-password",
                }
            },
        }
    )
    hub_section["extraEnv"].append({"name": "JUPYTERHUB_AUTHENTICATOR", "value": "tmp"})
    hub_section["services"]["notebooks"]["url"] = "http://{}".format(
        TEMPORARY_NOTEBOOKS_SERVICE_NAME
    )
    hub_section["services"]["notebooks"]["apiToken"] = random_hex_seeded(
        32, hub_section["services"]["notebooks"]["apiToken"]
    )
    del hub_section["services"]["gateway"]

    auth_section = new_values["jupyterhub"]["auth"]
    auth_section["state"]["cryptoKey"] = random_hex_seeded(
        32, auth_section["state"]["cryptoKey"]
    )
    auth_section["type"] = "tmp"
    del auth_section["gitlab"]

    new_values["jupyterhub"]["proxy"]["secretToken"] = random_hex_seeded(
        32, new_values["jupyterhub"]["proxy"]["secretToken"]
    )

    # Add some reasonably short defaults for server culling
    new_values["jupyterhub"]["cull"] = {"enabled": True, "timeout": 3600, "every": 60}

    # Disable user userScheduler
    new_values["jupyterhub"]["scheduling"]["userScheduler"] = {"enabled": False }
    new_values["jupyterhub"]["scheduling"]["userPlaceholder"] = {"enabled": False }

    # Configure ingress rule for /jupyterhub-tmp/
    new_values["ingress"] = values["ingress"]
    new_values["ingress"]["jupyterhubPath"] = new_values["jupyterhub"]["hub"]["baseUrl"]

    if not out_path:
        out_path = "tmp_notebooks_values_{}.yaml".format(secrets.token_hex(4))
    with open(out_path, "w") as f:
        yaml.dump(new_values, f)
    sys.stdout.write(f"Successfully created {out_path}\n")

    return out_path


def create_tmp_namespace(k8s_api_client, tmp_namespace):
    """Create the namespace tmp_namepspace"""

    try:
        k8s_api_client.create_namespace(
            k8s.client.V1Namespace(metadata=k8s.client.V1ObjectMeta(name=tmp_namespace))
        )
    except k8s.client.rest.ApiException as e:
        if e.status == 409:
            sys.stdout.write(f"Namespace {tmp_namespace} exists, skipping...\n")
        else:
            raise e


def copy_secret(k8s_api_client, renku_namespace, tmp_namespace):
    """Copy the JH postgres secret from renku_namepace to tmp_namespace"""

    secret_list = k8s_api_client.list_namespaced_secret(renku_namespace).items
    secret_dict = {secret.metadata.name: secret for secret in secret_list}
    jh_postgres_secret = secret_dict[JUPYTERHUB_POSTGRES_SECRET_NAME]

    jh_postgres_secret.metadata = k8s.client.V1ObjectMeta(
        namespace=tmp_namespace, name=JUPYTERHUB_POSTGRES_SECRET_NAME
    )

    # Delete the secret if it already exists
    try:
        k8s_api_client.delete_namespaced_secret(
            JUPYTERHUB_POSTGRES_SECRET_NAME, tmp_namespace
        )
    except k8s.client.rest.ApiException as e:
        if e.status == 404:
            pass
        else:
            raise e

    k8s_api_client.create_namespaced_secret(tmp_namespace, jh_postgres_secret)


def check_notebooks_chart_version():
    """Check the version of the renku-notebooks chart inside ./renku """

    chartdir = os.path.abspath(os.path.dirname(__file__))
    chart_dependencies = (
        subprocess.run(
            ["helm", "dependency", "list", f"{chartdir}/renku"],
            stdout=subprocess.PIPE,
            check=True,
        )
        .stdout.decode("utf-8")
        .split("\n")
    )
    for dependency in chart_dependencies:
        if "renku-notebooks" in dependency:
            [_, renku_notebooks_version, renku_chart_repo, _] = dependency.split("\t")
    return renku_notebooks_version


def main():
    """Script that will handle all necessary parts to for creating a secondary
    renku notebooks deployment from a primary Renku deployment which has anonymous
    sessions enabled. """

    argparser = argparse.ArgumentParser(description=main.__doc__)
    argparser.add_argument(
        "--release-name",
        help="Name of the primary Renku helm release. REQUIRED.",
        required=True,
    )
    argparser.add_argument(
        "--renku-namespace",
        help="Namespace of the primary Renku deployment. REQUIRED.",
        required=True,
    )
    argparser.add_argument(
        "--tmp-namespace",
        help="Namespace for the secondary renku-notebooks deployment. OPTIONAL.",
        required=False,
    )
    argparser.add_argument(
        "--kube-context", help="Kubernetes context to use. OPTIONAL.", required=False
    )
    argparser.add_argument(
        "--extra-values",
        help="""Path to an additional values file for the secondary notebooks-deployment.
These values will override the ones automatically derived from primary Renku deployment. OPTIONAL.""",
        required=False,
    )
    argparser.add_argument(
        "--no-cleanup",
        dest="cleanup",
        action="store_false",
        help="Do not remove the generated values file after deployment.",
    )
    argparser.add_argument(
        "--no-deploy",
        dest="deploy",
        action="store_false",
        help="Only display the helm command for deployment, do not execute it.",
    )
    argparser.set_defaults(cleanup=True, deploy=True)
    args = argparser.parse_args()

    renku_namespace = args.renku_namespace
    if args.tmp_namespace:
        tmp_namespace = args.tmp_namespace
    else:
        tmp_namespace = f"{renku_namespace}-tmp"
    if args.kube_context:
        kube_context = args.kube_context
    else:
        # Get the name of the current-context
        kube_context = k8s.config.list_kube_config_contexts()[1]["name"]

    # Get the chart version from the renku requirements.yaml
    renku_notebooks_version = check_notebooks_chart_version()
    sys.stdout.write(f"Found renku-notebooks version {renku_notebooks_version}\n")

    # Get an k8s api client from the specified local context
    k8s_api_client = k8s.client.CoreV1Api(
        k8s.client.ApiClient(
            k8s.config.load_kube_config(
                config_file=os.environ["KUBECONFIG"], context=kube_context
            )
        )
    )

    # Create secondary namespace using the k8s python client,
    # skip if it already exists.
    create_tmp_namespace(k8s_api_client, tmp_namespace)

    # Copy the secret using the k8s python client,
    # skip if it already exists.
    copy_secret(k8s_api_client, renku_namespace, tmp_namespace)

    # Create a values file for the secondary renku-notebooks deployment
    renku_values = get_values(args.release_name, kube_context, renku_namespace)
    tmp_values_path = make_tmp_values(renku_values, renku_namespace)

    # Deploy through the helm CLI
    helm_command = [
        "helm",
        "upgrade",
        "--install",
        f"{renku_namespace}-tmp-notebooks",
        "renku/renku-notebooks",
        "--version",
        f"{renku_notebooks_version}",
        "--namespace",
        f"{tmp_namespace}",
        "-f",
        f"{tmp_values_path}",
        "--timeout",
        "1800s",
        "--kube-context",
        f"{kube_context}",
    ]
    if args.extra_values:
        helm_command += ["-f", args.extra_values]

    command_string = " ".join(helm_command)
    sys.stdout.write(f"\nHelm command for deployment: \n{command_string}\n\n")
    if args.deploy:
        subprocess.run(helm_command, check=True)
        sys.stdout.write(f"Successfully deployed {renku_namespace}-tmp-notebooks.\n")

    # Finally, clean up the autogenerated values file
    if args.cleanup:
        sys.stdout.write(
            f'Deleting the temporary values file {tmp_values_path}, use the "--no-cleanup" option to avoid this.\n'
        )
        os.remove(tmp_values_path)


if __name__ == "__main__":
    main()
