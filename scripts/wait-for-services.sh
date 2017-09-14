#!/usr/bin/env sh
# -*- coding: utf-8 -*-
#
# Copyright 2017 - Swiss Data Science Center (SDSC)
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

docker run \
    --network renga_default --rm \
    --link deployer \
    --link explorer \
    --link graph-mutation \
    --link graph-navigation \
    --link graph-typesystem \
    --link keycloak \
    --link resource-manager \
    --link storage \
    -e TARGETS=keycloak:8080,deployer:5000,explorer:9000,graph-mutation:9000,graph-navigation:9000,graph-typesystem:9000,resource-manager:9000,storage:9000 \
    -e TIMEOUT=90 \
    waisbrot/wait
