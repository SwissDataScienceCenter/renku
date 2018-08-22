# -*- coding: utf-8 -*-
#
# Copyright 2017, 2018 - Swiss Data Science Center (SDSC)
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


# This Makefile provides convenient targets for building the various pieces of
# the Renku platform.

# Here we define make targets that allow you to manage the Renku component
# repositories and build docker images. The repositories are defined in the
# "repos" variable below. The other variables define how the various service
# docker images should be built.

# clone: clones the repositories into this directory's parent directory
# checkout: checks out the BRANCH branch of all repositories (defaults to master)
#
#  $ make checkout BRANCH=test
#
# pull: pull latest changes from remote

# Image builds
# ------------
#
# Images are built and tagged using chartpress (https://github.com/jupyterhub/chartpress)
#
# To build the images:
#
# $ pip install -U pip && pip install pipenv
# $ pipenv run cd helm-charts; chartpress
#
# Deploying Renku
# ---------------
#
# To deploy the Renku platform on your local minikube, run
#
# $ make minikube-deploy
#
# This will first build all the necessary images from the currently
# checked-out repositories before deploying the entire renku service stack
# to your minikube.

PLATFORM_BASE_DIR?=..
PLATFORM_BASE_REPO_URL?=https://github.com/SwissDataScienceCenter
PLATFORM_REPO_TPL?=$(PLATFORM_BASE_REPO_URL)/$*.git

# branch to use across repos for checkout/pull commands; defaults to master
BRANCH?=master

# Please keep values bellow sorted. Thank you!
repos = \
	renku-gateway \
	renku-notebooks \
	renku-python \
	renku-storage \
	renku-ui


.PHONY: clone checkout pull python-env test

# fetch missing repositories
$(PLATFORM_BASE_DIR)/%:
	git clone $(PLATFORM_REPO_TPL) $@

clone: $(foreach s, $(repos), $(PLATFORM_BASE_DIR)/$(s))

%-checkout: $(PLATFORM_BASE_DIR)/%
	cd $< && git checkout $(BRANCH)

checkout: $(foreach s, $(repos), $(s)-checkout)

%-pull: $(PLATFORM_BASE_DIR)/%
	cd $< && git pull

pull: $(foreach s, $(repos), $(s)-pull)

.PHONY: minikube-deploy
minikube-deploy: clone
	pipenv run python scripts/minikube_deploy.py

python-env:
	@pipenv run python -V
	@pipenv lock --requirements
	@pipenv lock --dev --requirements

test:
	@pip install pipenv
	@pipenv install --system --deploy --dev
	@./scripts/run-tests.sh
