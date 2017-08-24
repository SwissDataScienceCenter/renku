#!/bin/bash

echo "Example bash script"
echo ""

export RENGA_API=http://reverse-proxy
export KEYCLOAK_API=http://reverse-proxy

echo "Getting an access token..."
curl -X POST "$KEYCLOAK_API/auth/realms/Renga/protocol/openid-connect/token" -H "accept: application/json" -H "content-type: application/x-www-form-urlencoded" -d "grant_type=password&username=demo&password=demo&client_id=demo-client&client_secret=5294a18e-e784-4e39-a927-ce816c91c83e" | jq -r ".access_token"

# CURL_CMD="curl -X POST \"$KEYCLOAK_API/auth/realms/Renga/protocol/openid-connect/token\" \
#   -H \"accept: application/json\" \
#   -H \"content-type: application/x-www-form-urlencoded\" \
#   -d \"grant_type=password&username=demo&password=demo&client_id=demo-client&client_secret=5294a18e-e784-4e39-a927-ce816c91c83e\""
# JQ_CMD="jq -r \".access_token\""
# CMD="$CURL_CMD | $JQ_CMD"
# echo $CMD
# export RENGA_ACCESS_TOKEN=$($CMD)
# echo $RENGA_ACCESS_TOKEN
