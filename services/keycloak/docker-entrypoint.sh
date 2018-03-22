#!/bin/bash
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

echo "==================================================="
echo " Configuration:"
echo " RENGA_ENDPOINT=$RENGA_ENDPOINT"
echo " GITLAB_URL=$GITLAB_URL"
echo " RENGA_UI_URL=$RENGA_UI_URL"
echo " KEYCLOAK_MIGRATION_FILE=$KEYCLOAK_MIGRATION_FILE"
echo "==================================================="

cat $KEYCLOAK_MIGRATION_FILE.tpl \
  | sed -e "s|{{RENGA_ENDPOINT}}|${RENGA_ENDPOINT}|" \
  | sed -e "s|{{RENGA_UI_URL}}|${RENGA_UI_URL}|" \
  | sed -e "s|{{GITLAB_URL}}|${GITLAB_URL}|" \
  | sed -e "s|{{GITLAB_CLIENT_SECRET}}|${GITLAB_CLIENT_SECRET}|" \
  | sed -e "s|{{JUPYTERHUB_URL}}|${JUPYTERHUB_URL}|" \
  | sed -e "s|{{JUPYTERHUB_CLIENT_SECRET}}|${JUPYTERHUB_CLIENT_SECRET}|" \
  > $KEYCLOAK_MIGRATION_FILE

exec /opt/jboss/docker-entrypoint.sh $@
exit $?
