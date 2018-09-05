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

cat > /tmp/keycloak.yaml <<EOF
keycloak:
  keycloak:
    extraVolumes: |
        - name: realm-secret
          secret:
            secretName: $RENKU_DEPLOY
            items:
            - key: renku-realm.json
              path: renku-realm.json
        - name: theme
          emptyDir: {}
EOF

helm upgrade $RENKU_DEPLOY charts/renku \
    --install --namespace $RENKU_DEPLOY \
    -f charts/minikube-values.yaml \
    --set "global.renku.domain=$MAXIKUBE_HOST:32080" \
    --set "ui.gitlabUrl=http://$MAXIKUBE_HOST:32080/gitlab" \
    --set "ui.jupyterhubUrl=http://$MAXIKUBE_HOST:32080/jupyterhub" \
    --set "ui.gatewayUrl=http://$MAXIKUBE_HOST:32080/api" \
    --set "gateway.keycloakUrl=http://$MAXIKUBE_HOST:32080" \
    --set "gateway.gitlabUrl=http://$MAXIKUBE_HOST:32080/gitlab" \
    --set "notebooks.jupyterhub.hub.db.url=postgres+psycopg2://jupyterhub:jupyterhub@$RENKU_DEPLOY-postgresql:5432/jupyterhub" \
    --set "notebooks.jupyterhub.hub.extraEnv.GITLAB_URL=http://$MAXIKUBE_HOST:32080/gitlab" \
    --set "notebooks.jupyterhub.hub.extraEnv.IMAGE_REGISTRY=10.100.123.45:8105" \
    --set "gitlab.registry.externalUrl=http://10.100.123.45:8105/" \
    --set "gitlab.sshPort=30022" \
    --set "gitlab.enableSSHNodePortService=true" \
    --set "keycloak.keycloak.persistence.dbHost=$RENKU_DEPLOY-postgresql" \
    --set "keycloak.keycloak.persistence.existingSecret=$RENKU_DEPLOY" \
    -f /tmp/keycloak.yaml \
    --timeout 1800
