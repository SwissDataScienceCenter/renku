#!/usr/bin/env bash
# -*- coding: utf-8 -*-
#
# Copyright 2017-2018 - Swiss Data Science Center (SDSC)
# A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
# Eidgenössische Technische Hochschule Zürich (ETHZ).
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -ex

export MINIKUBE_WANTUPDATENOTIFICATION=false
export MINIKUBE_WANTREPORTERRORPROMPT=false
export MINIKUBE_HOME=$HOME
export CHANGE_MINIKUBE_NONE_USER=true
mkdir -p $HOME/.kube
touch $HOME/.kube/config

export KUBECONFIG=$HOME/.kube/config
sudo minikube start --vm-driver=none --bootstrapper=localkube --kubernetes-version=v1.10.0
minikube update-context

# this for loop waits until kubectl can access the api server that Minikube has created
set +e
for i in {1..150}; do # timeout for 5 minutes
    nodes=$(kubectl get node -ojson | jq '.items | length')
    if [ $nodes -gt 0 ]; then
        break
    fi
    sleep 2
done
for i in {1..150}; do # timeout for 5 minutes
    ready=$(kubectl get node -ojson | jq -r '.items[0].status.conditions[] | select(.type | contains("Ready")) | .status')
    if [ $ready = "True" ]; then
        break
    fi
    sleep 2
done
set -e

# kubectl commands are now able to interact with Minikube cluster

kubectl config view
kubectl get node

helm init
sleep 60
helm upgrade --install nginx-ingress --namespace kube-system \
    --set controller.hostNetwork=true \
    --set tcp.2222=renku/renku-gitlab:22 \
    stable/nginx-ingress

helm repo add gitlab https://charts.gitlab.io
helm repo add jupyterhub https://jupyterhub.github.io/helm-chart

make minikube-deploy
