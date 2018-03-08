#!/usr/bin/env sh
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

# wait for containers to be up
docker run \
    --network renga_default --rm \
    --link gitlab \
    --link keycloak \
    --link storage \
    --link ui \
    -e TARGETS=keycloak:8080,gitlab:80,ui:3000 \
    -e TIMEOUT=180 \
    waisbrot/wait

# wait for services to become available
URLS="${KEYCLOAK_URL}/auth ${GITLAB_URL}/help ${PLATFORM_DOMAIN}"

for URL in $URLS
do
    TIMEOUT=1
    until sleep 1; curl --retry-max-time 1 $URL > /dev/null 2>&1; do
        if [ ! "$TIMEOUT" ]; then
            TIMEOUT=`expr $TIMEOUT - 1`
        else
            echo "Failed to access $URL."
            exit 1
        fi
    done
done
echo "All services are accessible."
