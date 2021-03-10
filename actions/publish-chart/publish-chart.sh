#!/bin/sh
set -xe

if echo $GITHUB_REF | grep "tags" - > /dev/null; then
  CHART_TAG="--tag $(echo ${GITHUB_REF} | cut -d/ -f3)"
fi

if [ -z "$GITHUB_TOKEN" ]; then
  echo "GITHUB_TOKEN must be set."
  exit 1
fi

if [ -z "$GIT_EMAIL" ]; then
  echo "GIT_EMAIL must be set."
  exit 1
fi

if [ -z "$GIT_USER" ]; then
  echo "GIT_USER must be set."
  exit 1
fi

if [ -z "$CHART_NAME" ]; then
  echo "CHART_NAME must be set."
  exit 1
fi

if [ -z "$CHART_DIR" ]; then
  CHART_DIR="helm-chart/"
fi

if [ ! -z "$IMAGE_PREFIX" ]; then
  IMAGE_PREFIX="--image-prefix ${IMAGE_PREFIX}"
fi

# set up git
git config --global user.email "$GIT_EMAIL"
git config --global user.name "$GIT_USER"

helm dep update $CHART_DIR/$CHART_NAME

# log in to docker
echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin

# build and push the chart and images
chartpress --push --publish-chart $CHART_TAG $IMAGE_PREFIX
