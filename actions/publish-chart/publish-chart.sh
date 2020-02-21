#!/bin/sh
set -xe

if [[ $GITHUB_REF =~ "tags" ]]
then
  CHART_TAG="--tag $(echo ${GITHUB_REF} | cut -d/ -f3)"
fi

if [ -z $GITHUB_TOKEN ]
then
  echo "GITHUB_TOKEN must be set."
  exit 1
fi

if [ -z $GIT_EMAIL ]
then
  echo "GIT_EMAIL must be set."
fi

if [ -z $GIT_USER ]
then
  echo "GIT_USER must be set."
fi

# configure variables
CHART_PATH=${CHART_PATH:=helm-chart/$(echo $GITHUB_REPOSITORY | cut -d/ -f2)}
HELM_URL=${HELM_URL:=https://storage.googleapis.com/kubernetes-helm}
HELM_TGZ=${HELM_TGZ:=helm-v2.16.1-linux-amd64.tar.gz}

# install helm
mkdir -p /tmp/helm
wget -q ${HELM_URL}/${HELM_TGZ} -O /tmp/helm/${HELM_TGZ}
tar -C /tmp/helm -xzv -f /tmp/helm/${HELM_TGZ}
PATH=/tmp/helm/linux-amd64/:$PATH
helm init --client-only

# set up git
git config --global user.email "$GIT_EMAIL"
git config --global user.name "$GIT_USER"

helm dep update $CHART_PATH

# log in to docker
echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin

# build and push the chart and images
chartpress --push --publish-chart $CHART_TAG
