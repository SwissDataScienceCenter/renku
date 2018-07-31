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

# Check k8s status
kubectl config view
kubectl get nodes

helm init --wait
helm upgrade --install nginx-ingress --namespace kube-system \
    --set controller.hostNetwork=true \
    --set tcp.2222=renku/renku-gitlab:22 \
    stable/nginx-ingress

helm repo add gitlab https://charts.gitlab.io
helm repo add jupyterhub https://jupyterhub.github.io/helm-chart

make minikube-deploy
