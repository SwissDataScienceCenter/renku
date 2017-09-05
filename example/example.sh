#!/use/bin/env bash
# -*- coding: utf-8 -*-
#
# Copyright 2017 Swiss Data Science Center
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
