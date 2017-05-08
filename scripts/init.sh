#!/usr/bin/env bash

log() {
    printf "%s\n" "$*" >&2;
}

log 'init'

# Clone graph-core repository
if [ ! -d "graph-core/.git" ]; then
  git clone git@github.com:SwissDataScienceCenter/graph-core.git tmp-graph-core
  rsync -a --ignore-existing tmp-graph-core/ graph-core/
  rm -rf tmp-graph-core
fi
