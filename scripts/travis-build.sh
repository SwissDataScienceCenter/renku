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

set -ex

cd charts
helm dependency update renku
chartpress
cd ..

helm upgrade renku charts/renku \
    --install --namespace renku \
    -f charts/minikube-values.yaml \
    --set "global.renku.domain=$(minikube ip)" \
    --set "ui.gitlabUrl=http://$(minikube ip)/gitlab" \
    --set "ui.jupyterhubUrl=http://$(minikube ip)/jupyterhub" \
    --set "ui.gatewayUrl=http://$(minikube ip)/api" \
    --set "gateway.keycloakUrl=http://$(minikube ip)" \
    --set "gateway.gitlabUrl=http://{mip}/gitlab" \
    --set "jupyterhub.hub.extraEnv.GITLAB_URL=http://{mip}/gitlab" \
    --set "jupyterhub.hub.extraEnv.IMAGE_REGISTRY=10.100.123.45:8105" \
    --set "gitlab.registry.externalUrl=http://10.100.123.45:8105/" \
    --timeout 1800 \
    --force
