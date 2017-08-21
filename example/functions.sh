#!/bin/bash

function getAccessToken() {
    echo "Getting token..."
    CURL_CMD="curl -s -X POST \"$KEYCLOAK_API_URL/token\" \
        -H \"accept: application/json\" \
        -H \"content-type: application/x-www-form-urlencoded\" \
        -d \"grant_type=password&username=demo&password=demo&client_id=demo-client&client_secret=5294a18e-e784-4e39-a927-ce816c91c83e\""
    JQ_CMD="jq -r \".access_token\""
    CMD="$CURL_CMD | $JQ_CMD"
    echo $CMD
    SDSC_ACCESS_TOKEN=$(eval $CMD)
}

function getFirstBucket() {
    echo "Listing buckets..."
    CURL_CMD="curl -s -X GET \"$SDSC_API_URL/api/storage/explore/list\" \
        -H \"accept: application/json\" \
        -H \"Authorization: Bearer $SDSC_ACCESS_TOKEN\""
    JQ_CMD="jq \".[0].id\""
    CMD="$CURL_CMD | $JQ_CMD"
    echo $CMD
    local __resultvar=$1
    eval $__resultvar="'$(eval $CMD)'"
}

function createBucket() {
    echo "Creating bucket 'my_bucket' ..."
    CURL_CMD="curl -s -X POST \"$SDSC_API_URL/api/storage/authorize/create_bucket\" \
        -H \"accept: application/json\" \
        -H \"content-type: application/json\" \
        -H \"Authorization: Bearer $SDSC_ACCESS_TOKEN\" \
        -d \"{ \\\"name\\\": \\\"my_bucket\\\", \\\"backend\\\": \\\"local\\\", \\\"request_type\\\": \\\"create_bucket\\\"}\""
    JQ_CMD="jq \".response.event.results[0].id\""
    CMD="$CURL_CMD | $JQ_CMD"
    echo $CMD
    local __resultvar=$1
    eval $__resultvar="'$(eval $CMD)'"
}

function listFiles() {
    local bucket=$1
    echo "Listing files in '$bucket' ..."
    CURL_CMD="curl -s -X GET \"$SDSC_API_URL/api/storage/explore/$bucket/files\" \
        -H \"accept: application/json\" \
        -H \"Authorization: Bearer $SDSC_ACCESS_TOKEN\""
    JQ_CMD="jq \"[.[] | {file_id: .id, file_name: .properties | map(select(.key == \\\"resource:file_name\\\")) | .[0].values[0].value }]\""
    CMD="$CURL_CMD | $JQ_CMD"
    echo $CMD
    local __resultvar=$2
    eval $__resultvar="'$(eval $CMD)'"
}

function findFile() {
    local bucket=$1
    local filename=$2

    local files
    listFiles $bucket files
    local match=$(echo $files | jq "map(select(.file_name == \"$filename\"))[0].file_id")

    local __resultvar=$3
    eval $__resultvar="'$match'"
}

function authReadFile() {
  local file_id=$1
  echo "Writing to file '$file_id' ..."
  local deployment_id_curl=""
  if [ -n "$SDSC_DEPLOYMENT_ID" ]; then
      deployment_id_curl="-H \"SDSC-DEPLOYMENT-ID: $SDSC_DEPLOYMENT_ID\""
  fi
  CURL_CMD="curl -s -X POST \"$SDSC_API_URL/api/storage/authorize/read\" \
      -H \"accept: application/json\" \
      -H \"content-type: application/json\" \
      $deployment_id_curl \
      -H \"Authorization: Bearer $SDSC_ACCESS_TOKEN\" \
      -d \"{ \\\"resource_id\\\": $file_id, \\\"request_type\\\": \\\"read_file\\\"}\""
  JQ_CMD="jq -r \".access_token\""
  CMD="$CURL_CMD | $JQ_CMD"
  echo $CMD
  local __resultvar=$2
  eval $__resultvar="'$(eval $CMD)'"
}

function createFile() {
    local bucket=$1
    local filename=$2
    echo "Creating file '$bucket/$filename' ..."
    local deployment_id_curl=""
    if [ -n "$SDSC_DEPLOYMENT_ID" ]; then
        deployment_id_curl="-H \"SDSC-DEPLOYMENT-ID: $SDSC_DEPLOYMENT_ID\""
    fi
    CURL_CMD="curl -s -X POST \"$SDSC_API_URL/api/storage/authorize/create_file\" \
        -H \"accept: application/json\" \
        -H \"content-type: application/json\" \
        $deployment_id_curl \
        -H \"Authorization: Bearer $SDSC_ACCESS_TOKEN\" \
        -d \"{ \\\"bucket_id\\\": $bucket, \\\"file_name\\\": \\\"$filename\\\", \\\"request_type\\\": \\\"create_file\\\"}\""
    JQ_CMD="jq -r \".access_token\""
    CMD="$CURL_CMD | $JQ_CMD"
    echo $CMD
    local __resultvar=$3
    eval $__resultvar="'$(eval $CMD)'"
}

function authWriteFile() {
    local file_id=$1
    echo "Writing to file '$file_id' ..."
    local deployment_id_curl=""
    if [ -n "$SDSC_DEPLOYMENT_ID" ]; then
        deployment_id_curl="-H \"SDSC-DEPLOYMENT-ID: $SDSC_DEPLOYMENT_ID\""
    fi
    CURL_CMD="curl -s -X POST \"$SDSC_API_URL/api/storage/authorize/write\" \
        -H \"accept: application/json\" \
        -H \"content-type: application/json\" \
        $deployment_id_curl \
        -H \"Authorization: Bearer $SDSC_ACCESS_TOKEN\" \
        -d \"{ \\\"resource_id\\\": $file_id, \\\"request_type\\\": \\\"write_file\\\"}\""
    JQ_CMD="jq -r \".access_token\""
    CMD="$CURL_CMD | $JQ_CMD"
    echo $CMD
    local __resultvar=$2
    eval $__resultvar="'$(eval $CMD)'"
}

function authWriteOrCreateFile() {
    local bucket=$1
    local filename=$2

    local file_id
    findFile $bucket $filename file_id

    local access_token
    if [ "$file_id" == "null" ]; then
        createFile $bucket $filename access_token
    else
        authWriteFile $file_id access_token
    fi

    local __resultvar=$3
    eval $__resultvar="'$access_token'"
}

function readFile() {
    local auth_token=$1
    echo "Reading data..."
    CURL_CMD="curl -s -X GET \"$SDSC_API_URL/api/storage/io/read\" \
        -H \"Authorization: Bearer $auth_token\""
    CMD="$CURL_CMD"
    echo $CMD
    local __resultvar=$2
    eval $__resultvar="'$(eval $CMD)'"
}

function writeFile() {
    local auth_token=$1
    local data=$2
    echo "Writing data..."
    CURL_CMD="curl -s -X POST \"$SDSC_API_URL/api/storage/io/write\" \
        -H \"accept: application/json\" \
        -H \"content-type: text/plain\" \
        -H \"Authorization: Bearer $auth_token\"
        -d \"$data\""
    CMD="$CURL_CMD"
    echo $CMD
    eval $CMD
}
