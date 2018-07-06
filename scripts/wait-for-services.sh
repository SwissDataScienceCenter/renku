#!/usr/bin/env bash
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

set -e

CONTAINERS=($(docker-compose ps -q keycloak gitlab ui jupyterhub))

TIMEOUT=360

echo -n "Waiting "

while [[ $(docker inspect --format="{{json .State.Health.Status}}" "${CONTAINERS[@]}" | grep -c -v '"healthy"') -gt "0" ]]; do
    echo -n .
    if [[ "$TIMEOUT" -gt "0" ]]; then
        TIMEOUT=$((TIMEOUT - 5))
    else
        echo " [TIMEOUT]"
        exit 1
    fi
    sleep 5
done

echo " [OK]"
