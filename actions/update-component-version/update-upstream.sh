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

# install helm
HELM_URL=${HELM_URL:=https://storage.googleapis.com/kubernetes-helm}
HELM_TGZ=${HELM_TGZ:=helm-v2.14.3-linux-amd64.tar.gz}

mkdir -p /tmp/helm
wget -q ${HELM_URL}/${HELM_TGZ} -O /tmp/helm/${HELM_TGZ}
tar -C /tmp/helm -xzv -f /tmp/helm/${HELM_TGZ}
PATH=/tmp/helm/linux-amd64/:$PATH
helm init --client-only

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
helm repo add renku https://swissdatasciencecenter.github.io/helm-charts/
helm dep update charts/renku

git add charts/renku/requirements.yaml
git add charts/renku/requirements.lock
git commit -m "chore: updating ${CHART_NAME} version to ${CHART_VERSION}"
git push origin auto-update/${CHART_NAME}-${CHART_VERSION}

# clean up
cd ..
rm -rf upstream-repo
