#!/bin/bash

function main() {
  local uid="$(id -u)"
  local gid="$(id -g)"
  echo "uid=$uid gid=$gid"

  makeStorageData

  local from_image
  local to_image
  getFromImage from_image
  getToImage to_image
  echo "from_image=$from_image"
  echo "to_image=$to_image"

  makeStorageImage $uid $gid $from_image $to_image
}

function makeStorageData() {
  local here="$(pwd)"

	mkdir -p "$here/storage-data"
}

function makeStorageImage() {
  local here="$(pwd)"
  local uid=$1
  local gid=$2
  local from_image=$3
  local to_image=$4

  docker rmi "$to_image"
  echo "docker build -t $to_image --build-arg uid=$uid --build-arg gid=$gid --build-arg from_image=$from_image $here/renga-storage-local"
  docker build --no-cache -t "$to_image" --build-arg "uid=$uid" --build-arg "gid=$gid" --build-arg "from_image=$from_image" "$here/renga-storage-local"
}

function getFromImage() {
  source ./.env

  local __resultvar=$1
  eval $__resultvar="$IMAGE_REPOSITORY/renga-storage:$PLATFORM_VERSION"
}

function getToImage() {
  source ./.env

  local __resultvar=$1
  eval $__resultvar="renga-storage:local-$PLATFORM_VERSION"
}

main
