#!/bin/bash

source ./functions.sh

echo "Example bash script"
echo ""

# Variables injected by the deployer:
# RENGA_DEPLOYMENT_ID
# RENGA_ACCESS_TOKEN
# RENGA_API_URL
# KEYCLOAK_API_URL

# Default values from docker-compose
RENGA_API_URL=${RENGA_API_URL:-http://localhost:9000}
KEYCLOAK_API_URL=${KEYCLOAK_API_URL:-http://localhost:8080/auth/realms/Renga/protocol/openid-connect}
# Fetch token if none available
if [ -z "$RENGA_ACCESS_TOKEN" ]; then
  getAccessToken
fi

echo "Platform variables:"
echo "RENGA_DEPLOYMENT_ID: $RENGA_DEPLOYMENT_ID"
echo "RENGA_ACCESS_TOKEN: $RENGA_ACCESS_TOKEN"
echo "RENGA_API_URL: $RENGA_API_URL"
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
