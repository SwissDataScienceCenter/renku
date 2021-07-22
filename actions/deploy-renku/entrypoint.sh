#!/bin/sh

RENKU_NAMESPACE=${RENKU_NAMESPACE:-$RENKU_RELEASE}
RENKU_TMP_NAMESPACE=${RENKU_TMP_NAMESPACE:-${RENKU_NAMESPACE}-tmp}

# set up docker credentials
echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin

# set up kube context and values file
echo "$RENKUBOT_KUBECONFIG" > "$KUBECONFIG" && chmod 400 "$KUBECONFIG"

# set up the values file
printf "%s" "$RENKU_VALUES" | sed "s/<replace>/${RENKU_RELEASE}/" > $RENKU_VALUES_FILE

# enable anonymous notebooks
yq w -i $RENKU_VALUES_FILE "global.anonymousSessions.enabled" "true"

# register the GitLab app
if [[ -n "$GITLAB_TOKEN" ]]; then
gitlab_app=$(curl -s -X POST https://dev.renku.ch/gitlab/api/v4/applications \
                      -H "private-token: $GITLAB_TOKEN" \
                      --data "name=${RENKU_RELEASE}" \
                      --data "redirect_uri=https://${RENKU_RELEASE}.dev.renku.ch/auth/realms/Renku/broker/dev-renku/endpoint https://${RENKU_RELEASE}.dev.renku.ch/api/auth/gitlab/token https://${RENKU_RELEASE}.dev.renku.ch/api/auth/jupyterhub/token https://${RENKU_RELEASE}.dev.renku.ch/jupyterhub/hub/oauth_callback" \
                      --data "scopes=api read_user read_repository read_registry openid")
APP_ID=$(echo $gitlab_app | jq -r '.application_id')
APP_SECRET=$(echo $gitlab_app | jq -r '.secret')

# gateway gitlab app/secret
yq w -i $RENKU_VALUES_FILE "gateway.gitlabClientId" "$APP_ID"
yq w -i $RENKU_VALUES_FILE "gateway.gitlabClientSecret" "$APP_SECRET"

# jupyterhub gitlab app/secret
yq w -i $RENKU_VALUES_FILE "notebooks.jupyterhub.auth.gitlab.clientId" "$APP_ID"
yq w -i $RENKU_VALUES_FILE "notebooks.jupyterhub.auth.gitlab.clientSecret" "$APP_SECRET"
fi

# create the namespace in a Rancher project
if [[ -n "$RANCHER_PROJECT_ID" ]]; then
  NAMESPACE_EXISTS=$( curl -s -H "Authorization: Bearer $RENKUBOT_RANCHER_BEARER_TOKEN" \
        -X GET \
        -d "projectId=${RANCHER_PROJECT_ID}" \
        "${RANCHER_DEV_API_ENDPOINT}/namespaces"| grep $RENKU_NAMESPACE)
  if [[ -n NAMESPACE_EXISTS ]]; then
    curl -s -H "Authorization: Bearer $RENKUBOT_RANCHER_BEARER_TOKEN" \
        -X POST \
        -d "name=${RENKU_NAMESPACE}" \
        -d "projectId=${RANCHER_PROJECT_ID}" \
        "${RANCHER_DEV_API_ENDPOINT}/namespaces"
  fi
  if [[ $RENKU_ANONYMOUS_SESSIONS = "true" ]]; then
    NAMESPACE_EXISTS=$( curl -s -H "Authorization: Bearer $RENKUBOT_RANCHER_BEARER_TOKEN" \
        -X GET \
        -d "projectId=${RANCHER_PROJECT_ID}" \
        "${RANCHER_DEV_API_ENDPOINT}/namespaces"| grep $RENKU_TMP_NAMESPACE)
    if [[ -n NAMESPACE_EXISTS ]]; then

      curl -s -H "Authorization: Bearer $RENKUBOT_RANCHER_BEARER_TOKEN" \
        -X POST \
        -d "name=${RENKU_TMP_NAMESPACE}" \
        -d "projectId=${RANCHER_PROJECT_ID}" \
        "${RANCHER_DEV_API_ENDPOINT}/namespaces"
    fi
  fi
fi

# deploy renku - reads config from environment variables
helm repo add renku https://swissdatasciencecenter.github.io/helm-charts
helm repo update

python3 /deploy-dev-renku.py
