#!/usr/bin/env bash

log() {
    printf "%s\n" "$*" >&2;
}

# Pull repository
pull() {
    name=$1
    rootDir=$2

    repoDir="$rootDir/$name"

    if [ -d "$repoDir/.git" ]; then
        oldPwd="$PWD"
        cd $repoDir
        log "pulling $name"
        git pull
        cd $oldPwd
    fi
}

# Check if a project name and a project directory are provided
if [ ! $# -eq 2 ]; then
    log "Usage ${BASH_SOURCE} project projectDirectory"
    exit 1
fi

project=$1
projectDir=$2

baseDir="$2/.."

# Init variables
source "${BASH_SOURCE%/*}/project.env"

pull $project $baseDir
