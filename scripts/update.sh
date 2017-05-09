#!/usr/bin/env bash

log() {
    printf "%s\n" "$*" >&2;
}

update() {
    # Name arguments
    name=$1

    rootDir="${BASH_SOURCE%/*}/.."
    repoDir="$rootDir/$name"

    # Check if sbt made non empty subproject directories (target non empty)
    if [ -d $repoDir ] && [ ! -e "$repoDir/.git" ]; then
        log "$repoDir is non empty, removing..."
        rm -rf $repoDir
    fi

    git submodule update --init --recursive $1
}

if [ ! $# -eq 1 ]; then
    log "Usage: ${BASH_SOURCE} <project>"
fi

# Name arguments
name=$1

update $name
