#!/bin/bash

if [ "$#" -lt "1" ]; then
  TARGET="test"
else
  TARGET="testOnly $1"
fi

cd /tests
mkdir -p target


if [ ! -z $RENKU_TEST_S3_HOST ]; then
  if [ -z $RENKU_TEST_S3_ACCESS_KEY ] || [ -z $RENKU_TEST_S3_SECRET_KEY ] || \
  [ -z $RENKU_TEST_S3_BUCKET ] || [ -z $RENKU_TEST_S3_FILENAME ]; then
    echo "[ERROR] To upload the tests to a S3 bucket then\
    RENKU_TEST_S3_HOST, RENKU_TEST_S3_BUCKET, RENKU_TEST_S3_FILENAME,\
    RENKU_TEST_S3_ACCESS_KEY and RENKU_TEST_S3_SECRET_KEY\
    must all be specified as environment variables. Aborting."
    exit 1
  fi
  # mirror the test results folder in the background
  # the sleep command ensures the tests have started before mirroring starts
  echo "Will upload test artefacts from target to https://XXXX:XXXX@${RENKU_TEST_S3_HOST} bucket/${RENKU_TEST_S3_BUCKET}/${RENKU_TEST_S3_FILENAME}"
  sleep 60 && MC_HOST_bucket="https://${RENKU_TEST_S3_ACCESS_KEY}:${RENKU_TEST_S3_SECRET_KEY}@${RENKU_TEST_S3_HOST}" mc mirror --overwrite --remove --watch --exclude "scala*/*" --exclude "streams/*" --exclude "task-temp-directory/*" --exclude "global-logging/*"  --exclude "target/20*/.gitattributes" --exclude "target/20*/.renku/cache" target bucket/${RENKU_TEST_S3_BUCKET}/${RENKU_TEST_S3_FILENAME}/ 2> /dev/null 1> /dev/null &
  MIRROR_JOB_ID=$(echo $!)
fi

# formatting check needs to be run in a git repository
git init  2> /dev/null 1> /dev/null
git add .

echo "Target: " $TARGET
sbt -Dsbt.color=always -Dsbt.supershell=false -Dsbt.global.base=/tests/.sbt/global -Dsbt.boot.directory=/tests/.sbt/boot/ -Dsbt.coursier.home=/tests/.sbt/coursier/ scalafmtCheckAll "$TARGET"
TESTS_RC=$?
echo "Tests completed with exit code $TESTS_RC"

# cleanup the background mirroring job
if [ ! -z $MIRROR_JOB_ID ]
then
  echo "Terminating test artifacts background mirroring job."
  kill -15 $MIRROR_JOB_ID
fi

# if tests pass then remove the bucket
if [ $TESTS_RC -eq 0 ]
then
  echo "The tests PASSED with return code $TESTS_RC, removing the test artifacts at $RENKU_TEST_S3_BUCKET/$RENKU_TEST_S3_FILENAME."
  MC_HOST_bucket="https://${RENKU_TEST_S3_ACCESS_KEY}:${RENKU_TEST_S3_SECRET_KEY}@${RENKU_TEST_S3_HOST}" mc rm --recursive --force bucket/${RENKU_TEST_S3_BUCKET}/${RENKU_TEST_S3_FILENAME}/
fi

exit $TESTS_RC
