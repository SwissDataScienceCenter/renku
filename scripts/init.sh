#!/usr/bin/env bash

log() {
    printf "%s\n" "$*" >&2;
}

# Clone a given repository, $1=project name, $2=git base url, $3=root directory
clone() {
    # Name arguments
    name=$1
    baseUrl=$2
    rootDir=$3

    repoDir="$rootDir/$name"
    repoUrl="$baseUrl/$name.git"
    tempDir="$rootDir/tmp-$1"

    if [ ! -d "$repoDir/.git" ]; then
        log "Init project: $name"
        git clone $repoUrl $tempDir
        rsync -a --ignore-existing "$tempDir/" "$repoDir/"
        rm -rf $tempDir
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

clone $project $BASE_URL $baseDir
