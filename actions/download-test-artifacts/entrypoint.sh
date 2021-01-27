#!/bin/sh -e
printf "%s" "$RENKU_VALUES" > values.yaml
export S3_HOST=$(yq '.tests.resultsS3.host' values.yaml -r)
export S3_ACCESS=$(yq '.tests.resultsS3.accessKey' values.yaml -r)
export S3_SECRET=$(yq '.tests.resultsS3.secretKey' values.yaml -r)
export MC_HOST_bucket="https://${S3_ACCESS}:${S3_SECRET}@${S3_HOST}"
mc cp bucket/dev-acceptance-tests-results/$TEST_ARTIFACTS_PATH.tgz .
mc rm bucket/dev-acceptance-tests-results/$TEST_ARTIFACTS_PATH.tgz
tar -C $GITHUB_WORKSPACE/ -xzvf $TEST_ARTIFACTS_PATH.tgz
