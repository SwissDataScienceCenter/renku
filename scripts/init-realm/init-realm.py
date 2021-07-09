# -*- coding: utf-8 -*-
#
# Copyright 2017-2018 - Swiss Data Science Center (SDSC)
# A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
# Eidgenössische Technische Hochschule Zürich (ETHZ).
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import sys
import argparse
import warnings
import getpass
import json
import time

from keycloak import KeycloakAdmin
from keycloak.exceptions import KeycloakConnectionError, KeycloakGetError


# Helper functions which are called by the script.


def _check_existing(existing_object, new_object, case, idKey):
    """
    Compare the new object to the existing one, warn
    about mismatches.
    """
    for key in new_object.keys():
        if key not in existing_object:
            warning = "Found missing key '{}' at {} '{}'!".format(
                key, case, new_object[idKey]
            )
            warnings.warn(warning)

        elif new_object[key] != existing_object[key]:
            warning = "Found mismatch for key '{}' at {} '{}'!".format(
                key, case, new_object[idKey]
            )
            warnings.warn(warning)
            warnings.warn("To be created: \n{}".format(json.dumps(new_object[key])))
            warnings.warn("Existing: \n{}".format(json.dumps(existing_object[key])))


def _check_and_create_client(keycloak_admin, new_client):
    """
    Check if a client exists. Create it if not. Alert if
    it exists but with different details than what is provided.
    """

    sys.stdout.write("Checking if {} client exists...".format(new_client["clientId"]))
    realm_clients = keycloak_admin.get_clients()
    clientIds = [c["clientId"] for c in realm_clients]
    if new_client["clientId"] in clientIds:
        sys.stdout.write("found\n")
        realm_client = realm_clients[clientIds.index(new_client["clientId"])]

        # We have to separately query the secret as it is not part of
        # the original respone
        secret = keycloak_admin.get_client_secrets(realm_client["id"])
        realm_client["secret"] = secret["value"]

        # We have to remove the auto-generated IDs of the protocol mapper(s)
        # before comparing to the to-be-created client.
        # Also, we have to fix quoted booleans in the JSON document from
        # the Keycloak API.
        if "protocolMappers" in realm_client:
            for mapper in realm_client["protocolMappers"]:
                del mapper["id"]
                mapper["config"] = json.loads(
                    json.dumps(mapper["config"])
                    .replace('"true"', "true")
                    .replace('"false"', "false")
                )

        _check_existing(realm_client, new_client, "client", "clientId")

        # If we're dealing with the gateway client which is missing the
        # necessary protocol mapper to add the client-id to the audience
        # claim, we add it specifically.
        mappers_client_ids = ["gateway"]
        if realm_client["clientId"] in mappers_client_ids:
            mappers_missing = (
                "protocolMappers" not in realm_client
                and "protocolMappers" in new_client
            )
            audience_mapper_missing = "audience for gateway" not in [
                mapper["name"] for mapper in realm_client.get("protocolMappers", [])
            ]
            if mappers_missing or audience_mapper_missing:
                sys.stdout.write(
                    "found, but without the necessary protocol mapper. Adding it now..."
                )
                realm_client["protocolMappers"] = new_client[
                    "protocolMappers"
                ] + realm_client.get("protocolMappers", [])

                # We use DELETE followed by POST since PUT does not add the
                # provided protocolMappers to the client and PATCH is not
                # supported by the API.
                keycloak_admin.delete_client(realm_client["id"])
                keycloak_admin.create_client(realm_client)

                sys.stdout.write("done\n")

    else:
        sys.stdout.write("not found\n")
        sys.stdout.write("Creating {} client...".format(new_client["clientId"]))
        keycloak_admin.create_client(new_client)
        sys.stdout.write("done\n")


def _check_and_create_user(keycloak_admin, new_user):
    """
    Check if a user exists. Create it if not. Alert if
    it exists but with different details than what is provided.
    """

    sys.stdout.write("Checking if {} user exists...".format(new_user["username"]))
    realm_users = keycloak_admin.get_users(query={})
    usernames = [u["username"] for u in realm_users]

    if new_user["username"] in usernames:
        del new_user["password"]
        sys.stdout.write("found\n")
        realm_user = realm_users[usernames.index(new_user["username"])]
        _check_existing(realm_user, new_user, "user", "username")

    else:
        new_user_password = new_user["password"]
        del new_user["password"]
        sys.stdout.write("not found\n")
        sys.stdout.write("Creating user {} ...".format(new_user["username"]))
        keycloak_admin.create_user(payload=new_user)
        new_user_id = keycloak_admin.get_user_id(new_user["username"])
        keycloak_admin.set_user_password(
            new_user_id, new_user_password, temporary=False
        )
        sys.stdout.write("done\n")


# The actual script

parser = argparse.ArgumentParser()
parser.add_argument("--keycloak-url", help="URL (with path) to Keycloak.")
parser.add_argument(
    "--admin-user",
    help='Name of keycloak admin user. The default is "admin".',
    default="admin",
)
parser.add_argument("--admin-password", help="Keycloak admin password")
parser.add_argument(
    "--realm",
    help="Name of the Keycloak realm to create or configure. "
    + 'The default is "Renku".',
    default="Renku",
)
parser.add_argument(
    "--users-file",
    help="""Path to a json file containing the users to be created""",
    default=None,
)
parser.add_argument(
    "--clients-file",
    help="""Path to a json file containing the clients to be created""",
    default=None,
)
args = parser.parse_args()


# Check if the file containting the user information is ok.
if args.users_file:
    try:
        with open(args.users_file, "r") as f:
            new_users = json.load(f)
    except FileNotFoundError:
        sys.stderr.write("No users-file found at {}.".format(args.users_file))
        exit(1)
    except json.JSONDecodeError:
        sys.stderr.write("Could not parse users-file at {}.".format(args.users_file))
        exit(1)
else:
    new_users = []

# Check if the file containting the client information is ok.
if args.clients_file:
    try:
        with open(args.clients_file, "r") as f:
            new_clients = json.load(f)
    except FileNotFoundError:
        sys.stderr.write("No clients-file found at {}.".format(args.clients_file))
        exit(1)
    except json.JSONDecodeError:
        sys.stderr.write(
            "Could not parse clients-file at {}.".format(args.clients_file)
        )
        exit(1)
else:
    new_clients = []


# Check if we have an admin password for keycloak, prompt
# the user if not.
keycloak_admin_password = args.admin_password
if not keycloak_admin_password:
    keycloak_admin_password = getpass.getpass(
        prompt="Password for user '{}' (will not be stored):".format(args.admin_user)
    )

# Acquire a admin access token for the kecyloak API. On timeout
# or 503 we follow the kubernetes philosophy of just retrying until
# the service is eventuelly up. After 5 minutes we give up and leave
# it to K8s to restart the job.
n_attempts = 0
success = False

while not success and n_attempts < 31:
    try:
        sys.stdout.write("Getting an admin access token for Keycloak...")
        keycloak_admin = KeycloakAdmin(
            server_url=args.keycloak_url,
            username=args.admin_user,
            password=keycloak_admin_password,
            verify=True,
        )
        success = True
    except (KeycloakConnectionError, KeycloakGetError):
        sys.stdout.write("Keycloak not responding, retrying in 10 seconds...\n")
        n_attempts += 1
        time.sleep(10)
if success:
    sys.stdout.write("done\n")
else:
    sys.stderr.write(
        "Could not get a token. Is Keycloak \
        running under {}?\n".format(
            args.keycloak_url
        )
    )
    exit(1)

# Now that we obviously have all we need, let's create the
# realm, clients and users, skipping what already exists.
sys.stdout.write(
    "Creating {} realm, skipping if it already exists...".format(args.realm)
)
keycloak_admin.create_realm(
    payload={
        "realm": args.realm,
        "enabled": True,
        "registrationAllowed": True,
        "accessTokenLifespan": 1800,
        "ssoSessionIdleTimeout": 86400,
        "ssoSessionMaxLifespan": 604800,
        "registrationEmailAsUsername": True,
        "loginTheme": "renku-theme",
        "accountTheme": "renku-theme",
    },
    skip_exists=True,
)
sys.stdout.write("done\n")

# Switching to the newly created realm
keycloak_admin.realm_name = args.realm


for new_client in new_clients:
    _check_and_create_client(keycloak_admin, new_client)

for new_user in new_users:
    _check_and_create_user(keycloak_admin, new_user)
