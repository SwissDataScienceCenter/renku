#!/usr/bin/env bash

# Use `direnv allow` or `nix develop` to drop into a shell with
# required programs in place.

set -e

sbt "testOnly ch.renku.acceptancetests.$1"
