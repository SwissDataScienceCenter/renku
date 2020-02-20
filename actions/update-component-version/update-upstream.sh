#!/bin/sh
set -xe

if [ -z "$UPSTREAM_REPO" ]
then
  echo "Must specify UPSTREAM_REPO"
  exit 1
fi

if [ -z "$CHART_NAME" ]
then
  echo "Must specify CHART_NAME"
  exit 1
fi

if [ -z "$GITHUB_TOKEN" ]
then
  echo "Must specify GITHUB_TOKEN"
  exit 1
fi

if [[ $GITHUB_REF =~ "tags" ]]
then
  CHART_TAG="--tag $(echo ${GITHUB_REF} | cut -d/ -f3)"
fi

# build this chart to get the version
chartpress --skip-build $CHART_TAG
CHART_VERSION=$(yq r helm-chart/${CHART_NAME}/Chart.yaml version)

git clone --depth=1 https://${GITHUB_TOKEN}@github.com/${UPSTREAM_REPO} upstream-repo

# update the upstream repo
cd upstream-repo

# set up git
git config --global user.email "renku@datascience.ch"
git config --global user.name "Renku Bot"

# update the chart requirements and push
git checkout -b auto-update/${CHART_NAME}-${CHART_VERSION} master
yq w -i charts/renku/requirements.yaml "dependencies.(name==${CHART_NAME}).version" $CHART_VERSION
git commit -am "chore: updating ${CHART_NAME} version to ${CHART_VERSION}"
git push origin auto-update/${CHART_NAME}-${CHART_VERSION}

# clean up
cd ..
rm -rf upstream-repo
