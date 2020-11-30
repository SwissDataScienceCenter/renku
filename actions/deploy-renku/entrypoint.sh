#!/bin/sh

# set up docker credentials
echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin

# set up kube context and values file
echo "$RENKUBOT_KUBECONFIG" > "$KUBECONFIG"
echo "$RENKU_VALUES" > "$RENKU_VALUES_FILE"

# deploy renku - reads config from environment variables
/deploy_dev_renku.py
