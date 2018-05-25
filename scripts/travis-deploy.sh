#!/bin/env bash
#
# Copyright 2018 - Swiss Data Science Center (SDSC)
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

# generate ssh key to use for docker hub login
openssl aes-256-cbc -K "${encrypted_ba78b413d7f2_key}" -iv "${encrypted_ba78b413d7f2_iv}" -in github_deploy_key.enc -out github_deploy_key -d
chmod 600 github_deploy_key
eval $(ssh-agent -s)
ssh-add github_deploy_key
echo "${DOCKER_PASSWORD}" | docker login -u="${DOCKER_USERNAME}" --password-stdin ${DOCKER_REGISTRY}

# build charts/images and push
cd charts
helm repo add renku https://swissdatasciencecenter.github.io/helm-charts/
helm repo add gitlab https://charts.gitlab.io/
helm dependency update renku
chartpress --push --publish-chart
git diff
# push also images tagged with "latest"
chartpress --tag latest --push
cd ..
