#!/bin/bash

echo "Example bash script"
echo ""

export SDSC_API=http://reverse-proxy
export KEYCLOAK_API=http://keycloak:8080

echo "Getting an access token..."
curl -X POST "http://keycloak:8080/auth/realms/SDSC/protocol/openid-connect/token" -H "accept: application/json" -H "content-type: application/x-www-form-urlencoded" -d "grant_type=password&username=demo&password=demo&client_id=demo-client&client_secret=5294a18e-e784-4e39-a927-ce816c91c83e" | jq -r ".access_token"

# CURL_CMD="curl -X POST \"$KEYCLOAK_API/auth/realms/SDSC/protocol/openid-connect/token\" \
#   -H \"accept: application/json\" \
#   -H \"content-type: application/x-www-form-urlencoded\" \
#   -d \"grant_type=password&username=demo&password=demo&client_id=demo-client&client_secret=5294a18e-e784-4e39-a927-ce816c91c83e\""
# JQ_CMD="jq -r \".access_token\""
# CMD="$CURL_CMD | $JQ_CMD"
# echo $CMD
# export SDSC_ACCESS_TOKEN=$($CMD)
# echo $SDSC_ACCESS_TOKEN
