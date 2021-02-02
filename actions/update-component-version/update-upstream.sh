#!/bin/bash
set -xe

if [ -z "$GITHUB_TOKEN" ]
then
  echo "Must specify GITHUB_TOKEN"
  exit 1
fi

if [[ $GITHUB_REF =~ "tags" ]]
then
  CHART_TAG="--tag $(echo ${GITHUB_REF} | cut -d/ -f3)"
fi

# set up environment variables
UPSTREAM_REPO=${UPSTREAM_REPO:=SwissDataScienceCenter/renku}
UPSTREAM_BRANCH=${UPSTREAM_BRANCH:=master}
GIT_EMAIL=${GIT_EMAIL:=renku@datascience.ch}
GIT_USER=${GIT_USER:="Renku Bot"}
CHART_NAME=${CHART_NAME:=$(echo $GITHUB_REPOSITORY | cut -d/ -f2)}

# build this chart to get the version
chartpress --skip-build $CHART_TAG
CHART_VERSION=$(yq r helm-chart/${CHART_NAME}/Chart.yaml version)

git clone --depth=1 --branch=${UPSTREAM_BRANCH} https://${GITHUB_TOKEN}@github.com/${UPSTREAM_REPO} upstream-repo

# update the upstream repo
cd upstream-repo

# set up git
git config --global user.email "$GIT_EMAIL"
git config --global user.name "$GIT_USER"

# update the chart requirements and push
git checkout -b auto-update/${CHART_NAME}-${CHART_VERSION} ${UPSTREAM_BRANCH}
yq w -i helm-chart/renku/requirements.yaml "dependencies.(name==${CHART_NAME}).version" $CHART_VERSION

git add helm-chart/renku/requirements.yaml
git commit -m "chore: updating ${CHART_NAME} version to ${CHART_VERSION}"
git push origin auto-update/${CHART_NAME}-${CHART_VERSION}

# clean up
cd ..
rm -rf upstream-repo
