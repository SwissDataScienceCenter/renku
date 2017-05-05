#!/usr/bin/env bash

# Clone graph-core repository
if [ ! -e "graph-core/build.sbt" ]; then
  rm -rf graph-core/target
  git clone git@github.com:SwissDataScienceCenter/graph-core.git tmp
  rsync -rtv tmp/ graph-core/
  rm -rf tmp
fi

