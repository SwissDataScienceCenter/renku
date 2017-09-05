#!/usr/bin/env sh
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
