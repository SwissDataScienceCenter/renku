#!/usr/bin/env sh

if [ "$#" -lt "1" ]; then
  TARGET="test"
else
  TARGET="testOnly $1"
fi

cd /tests
echo "Target: " $TARGET
sbt -Dsbt.color=always -Dsbt.supershell=false -Dsbt.global.base=/tests/.sbt/global -Dsbt.boot.directory=/tests/.sbt/boot/ -Dsbt.coursier.home=/tests/.sbt/coursier/ scalafmtAll "$TARGET"

TESTS_RC=$?

if [ ! -z $RENKU_TEST_S3_HOST ] && [ $TESTS_RC != 0 ]; then
  if [ -z $RENKU_TEST_S3_ACCESS_KEY ] || [ -z $RENKU_TEST_S3_SECRET_KEY ] || \
  [ -z $RENKU_TEST_S3_BUCKET ] || [ -z $RENKU_TEST_S3_FILENAME ]; then
    echo "[ERROR] To upload the tests to a S3 bucket then\
    RENKU_TEST_S3_HOST, RENKU_TEST_S3_BUCKET, RENKU_TEST_S3_FILENAME,\
    RENKU_TEST_S3_ACCESS_KEY and RENKU_TEST_S3_SECRET_KEY\
    must all be specified as environment variables. Aborting."
    exit 1
  fi
  mkdir test-artifacts
  cp target/*.png test-artifacts
  cp target/*.log test-artifacts
  rm -rf target/20*/.renku/cache
  cp -r target/20* test-artifacts
  tar czvf tests-artifacts.tgz test-artifacts
  mv tests-artifacts.tgz ${RENKU_TEST_S3_FILENAME}.tgz

  export MC_HOST_bucket="https://${RENKU_TEST_S3_ACCESS_KEY}:${RENKU_TEST_S3_SECRET_KEY}@${RENKU_TEST_S3_HOST}"
  mc cp ${RENKU_TEST_S3_FILENAME}.tgz bucket/${RENKU_TEST_S3_BUCKET}
  exit $TESTS_RC
fi
