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

import argparse
import getpass
import json
import time
import logging
import os
from typing import Dict, List

from keycloak import KeycloakAdmin
from keycloak.exceptions import (
    KeycloakConnectionError,
    KeycloakGetError,
    KeycloakPostError,
)

from utils import DemoUserConfig, OIDCClientsConfig, OIDCGitlabClient, OIDCClient, OAuthFlow

logging.basicConfig(level=logging.INFO)

def _check_existing(existing_object: Dict, new_object: Dict, case, id_key) -> bool:
    """
    Compare the new object to the existing one, warn about mismatches. Return True if there are mismatches.
    """

    def sorted_list(data: List) -> List:
        return sorted(data, key=lambda e: json.dumps(e, sort_keys=True))

    changed: bool = False

    for key in new_object.keys():
        if key not in existing_object:
            changed = True
            warning = f"Found missing key '{key}' at {case} '{new_object[id_key]}'!"
            logging.warning(warning)

        elif new_object[key] != existing_object[key]:
            # If element is a list then sort and compare again
            if isinstance(new_object[key], list):
                if sorted_list(new_object[key]) == sorted_list(existing_object[key]):
                    continue

            changed = True
            warning = f"Found mismatch for key '{key}' at {case} '{new_object[id_key]}'!"
            logging.warning(warning)

    return changed


def _fix_json_values(data: Dict) -> Dict:
    """
    Fix quoted booleans in the JSON document from the Keycloak API.
    """
    return json.loads(json.dumps(data).replace('"true"', "true").replace('"false"', "false"))


def _check_and_create_client(keycloak_admin, new_client: OIDCClient, force: bool):
    """
    Check if a client exists. Create it if not. Alert if
    it exists but with different details than what is provided.
    """

    logging.info("Checking if {} client exists...".format(new_client.id))
    realm_clients = keycloak_admin.get_clients()
    client_ids = [c["clientId"] for c in realm_clients]
    realm_management_client_id = keycloak_admin.get_client_id("realm-management")
    if new_client.id in client_ids:
        logging.info("found")
        realm_client = realm_clients[client_ids.index(new_client.id)]

        # We have to separately query the secret as it is not part of
        # the original response
        secret = keycloak_admin.get_client_secrets(realm_client["id"])
        # public clients don't have secrets so default to None
        realm_client["secret"] = secret.get("value", None)

        # We have to remove the auto-generated IDs of the protocol mapper(s)
        # before comparing to the to-be-created client.
        if "protocolMappers" in realm_client:
            for mapper in realm_client["protocolMappers"]:
                del mapper["id"]
                mapper["config"] = _fix_json_values(mapper["config"])

        if "attributes" in realm_client:
            realm_client["attributes"] = _fix_json_values(realm_client["attributes"])

        roles_changed = False
        service_account_user = None
        existing_roles = []
        if new_client.oauth_flow == OAuthFlow.client_credentials:
            try:
                service_account_user = keycloak_admin.get_client_service_account_user(realm_client["id"])
            except KeycloakGetError as err:
                if err.response_code != 404:
                    raise
            if isinstance(service_account_user, dict):
                try:
                    existing_roles = keycloak_admin.get_client_roles_of_user(service_account_user["id"], realm_management_client_id)
                except KeycloakGetError as err:
                    if err.response_code != 404:
                        raise
            existing_roles_names = [role["name"] for role in existing_roles]
            if set(existing_roles_names) != set(new_client.service_account_roles):
                logging.warning(f"Roles changed existing roles {set(existing_roles_names)} != new roles {set(new_client.service_account_roles)}")
                roles_changed = True
        changed = _check_existing(realm_client, new_client.to_dict(), "client", "clientId")

        if not force or (not changed and not roles_changed):
            return

        logging.info(f"Recreating modified client '{realm_client['clientId']}'...")

        keycloak_admin.delete_client(realm_client["id"])
        created_client_id = keycloak_admin.create_client(new_client.to_dict())

        if isinstance(service_account_user, dict) and service_account_user.get("id"):
            logging.info(f"Reassigning service account roles {new_client.service_account_roles}")
            realm_management_roles = keycloak_admin.get_client_roles(realm_management_client_id)
            matching_roles = [{"name": role["name"], "id": role["id"]} for role in realm_management_roles if role["name"] in new_client.service_account_roles ]
            logging.info(f"Found and assigning matching roles: {matching_roles}")
            keycloak_admin.assign_client_role(service_account_user["id"], realm_management_client_id, matching_roles)

        logging.info("done")

    else:
        logging.info("not found")
        logging.info("Creating {} client...".format(new_client.id))
        created_client_id = keycloak_admin.create_client(new_client.to_dict())
        if new_client.oauth_flow == OAuthFlow.client_credentials and new_client.service_account_roles:
            service_account_user = keycloak_admin.get_client_service_account_user(created_client_id)
            logging.info(f"Assigning service account roles {new_client.service_account_roles}")
            realm_management_client_id = keycloak_admin.get_client_id("realm-management")
            realm_management_roles = keycloak_admin.get_client_roles(realm_management_client_id)
            matching_roles = [{"name": role["name"], "id": role["id"]} for role in realm_management_roles if role["name"] in new_client.service_account_roles ]
            logging.info(f"Found and assigning matching roles: {matching_roles}")
            keycloak_admin.assign_client_role(service_account_user["id"], realm_management_client_id, matching_roles)

        logging.info("done")


def _check_and_create_user(keycloak_admin, new_user):
    """
    Check if a user exists. Create it if not. Alert if
    it exists but with different details than what is provided.
    """

    logging.info("Checking if {} user exists...".format(new_user["username"]))
    realm_users = keycloak_admin.get_users(query={})
    usernames = [u["username"] for u in realm_users]

    if new_user["username"] in usernames:
        del new_user["password"]
        logging.info("found")
        realm_user = realm_users[usernames.index(new_user["username"])]
        _check_existing(realm_user, new_user, "user", "username")

    else:
        new_user_password = new_user["password"]
        del new_user["password"]
        logging.info("not found")
        logging.info("Creating user {} ...".format(new_user["username"]))
        keycloak_admin.create_user(payload=new_user)
        new_user_id = keycloak_admin.get_user_id(new_user["username"])
        keycloak_admin.set_user_password(new_user_id, new_user_password, temporary=False)
        logging.info("done")


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
    help='Name of the Keycloak realm to create or configure. The default is "Renku".',
    default="Renku",
)
parser.add_argument(
    "--force",
    help="If existing clients do not exactly match the provided configuration, recreate them using the provided "
    "configuration.",
    action="store_true",
)
args = parser.parse_args()

# Check if we have an admin password for keycloak, prompt
# the user if not.
keycloak_admin_password = args.admin_password
if not keycloak_admin_password:
    keycloak_admin_password = getpass.getpass(
        prompt="Password for user '{}' (will not be stored):".format(args.admin_user)
    )

# Acquire a admin access token for the keycloak API. On timeout
# or 503 we follow the kubernetes philosophy of just retrying until
# the service is eventually up. After 5 minutes we give up and leave
# it to K8s to restart the job.
n_attempts = 0
success = False

while not success and n_attempts < 31:
    try:
        logging.info("Getting an admin access token for Keycloak...")
        # NOTE: The keycloak python library does not follow redirect fully so passing in
        # "http://dev.renku.ch/auth" without the trailing "/" will fail with a 405 whereas
        # "http://dev.renku.ch/auth/" will work without a problem.
        keycloak_admin = KeycloakAdmin(
            server_url=args.keycloak_url if args.keycloak_url.endswith("/") else args.keycloak_url + "/",
            username=args.admin_user,
            password=keycloak_admin_password,
            verify=True,
        )
        success = True
    except (KeycloakConnectionError, KeycloakGetError, KeycloakPostError) as error:
        msg = "Keycloak not responding"
        if error.response_code is not None:
            msg += f" (status code: {error.response_code})"
        msg += ", retrying in 10 seconds..."
        logging.info(msg)
        n_attempts += 1
        time.sleep(10)
if success:
    logging.info("done")
else:
    logging.info(
        "Could not get a token. Is Keycloak \
        running under {}?".format(
            args.keycloak_url
        )
    )
    exit(1)

# Now that we obviously have all we need, let's create the
# realm, clients and users, skipping what already exists.
logging.info("Creating {} realm, skipping if it already exists...".format(args.realm))
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
logging.info("done")

realm = keycloak_admin.get_realm(args.realm)
event_retention_seconds = 86400
if not realm.get("eventsEnabled"):
    logging.info(
        f"Enabling user events tracking for realm with retention {event_retention_seconds}"
    )
    keycloak_admin.update_realm(args.realm, {"eventsEnabled": False, "eventsExpiration": event_retention_seconds})
if not realm.get("adminEventsEnabled"):
    logging.info(
        f"Enabling admin events tracking for realm with retention {event_retention_seconds}"
    )
    keycloak_admin.update_realm(
        args.realm,
        {
            "adminEventsEnabled": True,
            "adminEventsDetailsEnabled": True,
            "attributes": {"adminEventsExpiration": event_retention_seconds},
        },
    )

# Switching to the newly created realm
keycloak_admin.connection.realm_name = args.realm


for client in OIDCClientsConfig.from_env().to_list():
    _check_and_create_client(keycloak_admin, client, args.force)

if os.environ.get("INTERNAL_GITLAB_ENABLED", "false").lower() == "true":
    gitlab_oidc_client = OIDCGitlabClient.from_env()
    _check_and_create_client(keycloak_admin, gitlab_oidc_client, args.force)

# Create renku-admin realm role
logging.info("Creating renku-admin realm role, skipping if it already exists...")
realm_role_payload = {
    "name": "renku-admin",
    "composite": True,
    "composites": {
        "client": {
            "realm-management": ["query-users", "view-users"],
        },
    },
    "clientRole": False,
}
keycloak_admin.create_realm_role(realm_role_payload, skip_exists=True)
logging.info("done")

demo_user = DemoUserConfig.from_env().to_dict()
if demo_user is not None:   
    logging.info("Creating Keycloak demo user...")
    _check_and_create_user(keycloak_admin, demo_user)
    logging.info("done")
