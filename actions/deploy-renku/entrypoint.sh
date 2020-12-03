#!/bin/sh

set -ex

# set up docker credentials
echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin

# set up kube context and values file
echo "$RENKUBOT_KUBECONFIG" > "$KUBECONFIG" && chmod 400 "$KUBECONFIG"

# register the GitLab app
gitlab_app=$(curl -s -X POST https://dev.renku.ch/gitlab/api/v4/applications \
                      -H "private-token: $GITLAB_TOKEN" \
                      --data "name=${RENKU_RELEASE}" \
                      --data "redirect_uri=https://${RENKU_RELEASE}.dev.renku.ch/auth/realms/Renku/broker/dev-renku/endpoint https://${RENKU_RELEASE}.dev.renku.ch/api/auth/gitlab/token https://${RENKU_RELEASE}.dev.renku.ch/api/auth/jupyterhub/token https://${RENKU_RELEASE}.dev.renku.ch/jupyterhub/hub/oauth_callback" \
                      --data "scopes=api read_user read_repository read_registry openid")
APP_ID=$(echo $gitlab_app | jq -r '.application_id')
APP_SECRET=$(echo $gitlab_app | jq -r '.secret')

# set up the values file
printf "%s" "$RENKU_VALUES" | sed "s/<replace>/${RENKU_RELEASE}/" > values.yaml

# gateway gitlab app/secret
yq w -i values.yaml "gateway.gitlabClientId" "$APP_ID"
yq w -i values.yaml "gateway.gitlabClientSecret" "$APP_SECRET"

# jupyterhub gitlab app/secret
yq w -i values.yaml "notebooks.jupyterhub.auth.gitlab.clientId" "$APP_ID"
yq w -i values.yaml "notebooks.jupyterhub.auth.gitlab.clientSecret" "$APP_SECRET"

# enable anonymous notebooks
yq w -i values.yaml "global.anonymousSessions.enabled" "true"

# create the namespace in a Rancher project
curl -H "Authorization: Bearer $RENKUBOT_RANCHER_BEARER_TOKEN" \
        -X POST \
        -d "name=${RENKU_NAMESPACE}" \
        -d "projectId=${RANCHER_PROJECT_ID}" \
        'https://rancher.renku.ch/v3/cluster/c-l6jt4/namespaces'

curl -H "Authorization: Bearer $RENKUBOT_RANCHER_BEARER_TOKEN" \
        -X POST \
        -d "name=${RENKU_TMP_NAMESPACE}" \
        -d "projectId=${RANCHER_PROJECT_ID}" \
        'https://rancher.renku.ch/v3/cluster/c-l6jt4/namespaces'

# deploy renku - reads config from environment variables
python3 /deploy_dev_renku.py

# deploy anonymous notebooks
helm repo add renku https://swissdatasciencecenter.github.io/helm-charts
helm repo update
charts/deploy-tmp-notebooks.py --release-name $RENKU_RELEASE --renku-namespace $RENKU_NAMESPACE
