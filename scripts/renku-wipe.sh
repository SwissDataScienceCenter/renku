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
# This script starts the Renga platform
#

# load the environment from .env

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
    echo "[Info] Removing .env..."
    rm .env
}

stopContainers() {
    echo "Stopping containers..."
    docker-compose down --volumes --remove-orphans
}

removeNetwork() {
    if [ -z $(docker network ls -q -f name=${DOCKER_NETWORK}) ]; then
        echo "Removing docker network ${DOCKER_NETWORK}..."
        docker network rm $(DOCKER_NETWORK)
    fi
}

main() {
    loadEnv;
    stopContainers;
    removeNetwork;
}

main
