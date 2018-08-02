#!/usr/bin/env bash
# -*- coding: utf-8 -*-
#
# Copyright 2017, 2018 - Swiss Data Science Center (SDSC)
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

set -e

#
# This script starts the Renku platform
#

loadEnv() {
    # load the environment from .env

    if [ ! -f .env ]; then
        echo "[Error] .env not found - please create one or run \"make .env\"."
        exit 1
    fi
    echo
    echo "[Info] Loading environment variables from .env:"
    cat .env

    set -a
    source .env
    set +a
}

makeImages() {
    echo
    echo "[Info] Building the docker images..."
    make tag
}

gitlabDirs() {
    echo
    echo "[Info] Creating GitLab data directories..."
    for d in "config" "logs" "git-data" "lfs-data" "runner"
    do
        mkdir -p services/gitlab/$d
    done
}

dockerNetwork()  {
    if [ -z $(docker network ls -q -f name=${DOCKER_NETWORK}) ]; then
        echo "[Info] Creating docker network ${DOCKER_NETWORK}..."
        docker network create ${DOCKER_NETWORK}
    fi
}

unregisterGitlabRunners() {
    # Allow runner unregistration to fail (if there is no platform it will)
    set +e
    echo
    echo "[Info] Unregistering GitLab runners..."
    docker-compose exec gitlab /opt/gitlab/bin/gitlab-psql \
            -h /var/opt/gitlab/postgresql -d gitlabhq_production \
            -c "DELETE FROM personal_access_tokens WHERE user_id='1' AND name='managed storage token';" > /dev/null 2>&1
    set -e
}

registerGitlabRunners() {
    echo
    echo "[Info] Registering GitLab CI Runners..."
    if [ -z ${GITLAB_RUNNERS_TOKEN} ]; then
        echo "[Error] GITLAB_RUNNERS_TOKEN needs to be configured. Check ${GITLAB_URL}/admin/runners"
        exit 1
    fi
    for container in $(docker-compose ps -q gitlab-runner)
    do
        docker exec -ti $container gitlab-runner register \
                -n -u ${GITLAB_URL} \
                --name $$container-docker \
                -r ${GITLAB_RUNNERS_TOKEN} \
                --executor docker \
                --locked=false \
                --run-untagged=true \
                --docker-image "docker:stable" \
                --docker-network-mode=review \
                --docker-privileged \
                --docker-volumes "/var/run/docker.sock:/var/run/docker.sock" \
                --tag-list image-build \
                --docker-pull-policy "if-not-present"; \
    done
}

bootstrapContainers() {
    echo
    echo "[Info] Bootstrapping containers..."
    docker-compose up --build -d ${DOCKER_SCALE}
}

waitForServices() {
    # waiting for services
    scripts/wait-for-services.sh
}

registerGitlabSudoToken() {
    echo
    echo "[Info] Registering the gitlab sudo user token = ${GITLAB_SUDO_TOKEN}"
    docker-compose exec gitlab /opt/gitlab/bin/gitlab-psql \
        -h /var/opt/gitlab/postgresql -d gitlabhq_production \
        -c "INSERT INTO personal_access_tokens ( user_id, token, name, revoked, expires_at, created_at, updated_at, scopes, impersonation) VALUES ( '1', '${GITLAB_SUDO_TOKEN}', 'managed storage token', 'f', NULL, NOW(), NOW(), E'--- \n- api\n- read_user\n- sudo\n- read_registry', 'f');" > /dev/null 2>&1
}

registerGitlabSudoUser() {
    echo
    echo "[Info] Configuring user \"demo\" as gitlab admin..."
    curl -i -X POST -H "Private-token: ${GITLAB_SUDO_TOKEN}" \
      -d '{"username": "demo",
           "email": "demo@datascience.ch",
           "name": "John Doe",
           "extern_uid": "e144b235-793b-4e2e-bb1f-1f8baccc321f",
           "provider": "oauth2_generic",
           "skip_confirmation": true,
           "reset_password": true,
           "admin": true}' \
      -H 'Content-Type: application/json' \
      ${GITLAB_URL}/api/v4/users
}

registarGitlabApplications() {
    # Register the applications requiring access to GitLab

    # TODO: use the API
    GITLAB_PSQL_CMD='docker-compose exec gitlab /opt/gitlab/bin/gitlab-psql -t -h /var/opt/gitlab/postgresql -d gitlabhq_production -c'

    if [ -z $(${GITLAB_PSQL_CMD} "SELECT name FROM oauth_applications WHERE name LIKE 'renku-ui'" | tr -d '[:space:]' ) ]; then
        echo "[Info] Registering renku-ui with GitLab..."
        docker-compose exec gitlab /opt/gitlab/bin/gitlab-psql \
            -h /var/opt/gitlab/postgresql -d gitlabhq_production \
            -c "INSERT INTO oauth_applications (name, uid, scopes, redirect_uri, secret, trusted) VALUES ('renku-ui', 'renku-ui', 'api read_user', '${RENKU_UI_URL}/login/redirect/gitlab http://localhost:3000/login/redirect/gitlab', 'no-secret-needed', 'true')"
    fi

    if [ -z $(${GITLAB_PSQL_CMD} "SELECT name FROM oauth_applications WHERE name LIKE 'jupyterhub'" | tr -d '[:space:]' ) ]; then
        echo "[Info] Registering jupuyterhub with GitLab..."
        docker-compose exec gitlab /opt/gitlab/bin/gitlab-psql \
            -h /var/opt/gitlab/postgresql -d gitlabhq_production \
            -c "INSERT INTO oauth_applications (name, uid, scopes, redirect_uri, secret, trusted) VALUES ('jupyterhub', 'jupyterhub', 'api read_user', '${JUPYTERHUB_URL}/hub/oauth_callback ${JUPYTERHUB_URL}/hub/api/oauth2/authorize', 'no-secret-needed', 'true')"
    fi
}

configureLogout() {
    # configure the logout redirect
    echo
    echo "[Info] Configuring logout redirect..."
    curl -X PUT -H "Private-token: ${GITLAB_SUDO_TOKEN}" "${GITLAB_URL}/api/v4/application/settings?after_sign_out_path=${KEYCLOAK_URL}/auth/realms/Renku/protocol/openid-connect/logout?redirect_uri=${GITLAB_URL}" > /dev/null 2>&1

    if [ $GITLAB_CLIENT_SECRET = "dummy-secret" ]; then
        echo
        echo "[Warning] You have not defined a GITLAB_CLIENT_SECRET. Using dummy"
        echo "          secret instead. Never do this in production!"
        echo
    fi
}

checkServices() {
    echo
    echo "[Info] Checking that services are reachable..."
    CURL=$(curl -fIs ${PLATFORM_DOMAIN} && curl -fIs gitlab.${PLATFORM_DOMAIN} && curl -fIs keycloak.${PLATFORM_DOMAIN} && curl -fIs jupyterhub.${PLATFORM_DOMAIN}/services/notebooks)
    if [ -z "${CURL}" ]; then
        echo
        echo "[Error] Services unreachable -- if running locally, ensure name resolution with: "
        echo
        echo "$$ echo \"127.0.0.1 $(PLATFORM_DOMAIN) keycloak.$(PLATFORM_DOMAIN) gitlab.$(PLATFORM_DOMAIN) jupyterhub.$(PLATFORM_DOMAIN)\" | sudo tee -a /etc/hosts"
        echo
        exit 1
    fi
}

main() {
    # prepare the environment
    loadEnv;
    makeImages;
    gitlabDirs;

    # bootstrap the platform
    unregisterGitlabRunners;
    dockerNetwork;
    bootstrapContainers;
    waitForServices;

    # configure the platform
    registerGitlabRunners;
    registerGitlabSudoToken;
    registerGitlabSudoUser;
    registarGitlabApplications;
    configureLogout;

    # check that services are reachable
    checkServices;

    echo
    echo "[Success] Renku UI should be under ${RENKU_UI_URL} and GitLab under ${GITLAB_URL}"
    echo
}

main
