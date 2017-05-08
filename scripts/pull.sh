#!/usr/bin/env bash

log() {
    printf "%s\n" "$*" >&2;
}

log 'pull'

# Pull graph-core repository
if [ -d "graph-core/.git" ]; then
  cd graph-core
  log 'pulling graph-core'
  git pull
fi
