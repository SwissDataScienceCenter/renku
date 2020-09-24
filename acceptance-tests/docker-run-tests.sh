#!/usr/bin/env sh

if [ "$#" -lt "1" ]; then
  TARGET="test"
else
  TARGET="testOnly $1"
fi

cd /tests
echo "Target: " $TARGET
sbt -Dsbt.color=always -Dsbt.supershell=false -Dsbt.global.base=/tests/.sbt/global -Dsbt.boot.directory=/tests/.sbt/boot/ -Dsbt.coursier.home=/tests/.sbt/coursier/ scalafmtAll "$TARGET"
