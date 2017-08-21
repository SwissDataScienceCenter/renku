#!/bin/bash

source ./functions.sh

echo "Example bash script"
echo ""

# Variables injected by the deployer:
# SDSC_DEPLOYMENT_ID
# SDSC_ACCESS_TOKEN
# SDSC_API_URL
# KEYCLOAK_API_URL

# Default values from docker-compose
SDSC_API_URL=${SDSC_API_URL:-http://localhost:9000}
KEYCLOAK_API_URL=${KEYCLOAK_API_URL:-http://localhost:8080/auth/realms/SDSC/protocol/openid-connect}
# Fetch token if none available
if [ -z "$SDSC_ACCESS_TOKEN" ]; then
  getAccessToken
fi

echo "Platform variables:"
echo "SDSC_DEPLOYMENT_ID: $SDSC_DEPLOYMENT_ID"
echo "SDSC_ACCESS_TOKEN: $SDSC_ACCESS_TOKEN"
echo "SDSC_API_URL: $SDSC_API_URL"
echo "KEYCLOAK_API_URL: $KEYCLOAK_API_URL"


# Get the id of a bucket, create one otherwise
getFirstBucket bucket
if [ "$bucket" == "null" ]; then
  createBucket bucket
fi
echo "bucket: $bucket"

# Get the authorization to write in 'file_01.txt'
authWriteOrCreateFile $bucket "file_01.txt" auth_token
echo $auth_token

# Write text to 'file_01.txt'
writeFile $auth_token "hello, from script!"


findFile $bucket "file_01.txt" file_id
authReadFile $file_id auth_token

readFile $auth_token data
echo $data

# local auth_token
# createFile $bucket "file_01.txt" auth_token
