#!/usr/bin/env sh
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

# commenting out the image builds on cron due to disk space issues on travis
#
# if [ "$TRAVIS_EVENT_TYPE" = "cron" ]
# then
#     make -e PLATFORM_BASE_DIR=/tmp
# fi
set -ex

# install graphviz
sudo apt-get update && sudo apt-get install -y graphviz

# # install geckodriver
# wget https://github.com/mozilla/geckodriver/releases/download/v0.19.1/geckodriver-v0.19.1-linux64.tar.gz
# tar -xvzf geckodriver*
# chmod +x geckodriver
# sudo mv geckodriver /usr/local/bin

# install nsenter
sudo apt-get update
sudo apt-get install -y git build-essential libncurses5-dev libslang2-dev gettext zlib1g-dev libselinux1-dev debhelper lsb-release pkg-config po-debconf autoconf automake autopoint libtool
git clone git://git.kernel.org/pub/scm/utils/util-linux/util-linux.git util-linux
cd util-linux/
git checkout tags/v2.31.1 -b v2.31.1
./autogen.sh
./configure --without-python --disable-all-programs --enable-nsenter
make
chmod +x nsenter
sudo mv nsenter /usr/local/bin/
cd ..
rm -rf util-linux

# install minikube
curl -Lo minikube https://storage.googleapis.com/minikube/releases/v0.28.2/minikube-linux-amd64
chmod +x minikube
sudo mv minikube /usr/local/bin/
# install kubectl
curl -Lo kubectl https://storage.googleapis.com/kubernetes-release/release/v1.10.0/bin/linux/amd64/kubectl
chmod +x kubectl
sudo mv kubectl /usr/local/bin/
# install helm
curl -Lo helm.tar.gz https://storage.googleapis.com/kubernetes-helm/helm-v2.9.1-linux-amd64.tar.gz
tar xvf helm.tar.gz
chmod +x linux-amd64/helm
sudo mv linux-amd64/helm /usr/local/bin/
rm helm.tar.gz
rm -r linux-amd64


# install pipenv
pip install pipenv
# install required packages
pipenv install --dev --system --deploy
