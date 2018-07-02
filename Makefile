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


#
# This Makefile provides convenient targets for building the various pieces of
# the Renku platform.

#
# There are two main sections in this Makefile:
#
# 1. Environment configuration
# 2. Repository management and docker image builds

# ----------------------------
# 1. Environment configuration
# ----------------------------
#
# IMPORTANT: if the .env file exists, the values defined there take precedent
# over all other environment variable definitions.

# The first time you run any of the docker image build or deployment actions,
# the .env file will be created with default values, unless you override them
# on the command line or if the variable is already defined in your
# environment. So, for example, in a clean repository:
#
# $ GITLAB_URL=https://gitlab.com make start

# will set the GITLAB_URL in .env and all subsequent actions you invoke via
# the Makefile will use this value.

RENKU_BASE_DIR?=..
RENKU_BASE_REPO_URL?=https://github.com/SwissDataScienceCenter
RENKU_REPO_TPL?=$(RENKU_BASE_REPO_URL)/$*.git

components = \
	renku-notebooks \
	renku-storage \
	renku-ui

.PHONY: all
all:
	@echo make all: Do nothing for now

clone: $(foreach s, $(components), $(RENKU_BASE_DIR)/$(s))

$(RENKU_BASE_DIR)/%:
	git clone $(RENKU_REPO_TPL) $@

.PHONY: minikube-deploy
minikube-deploy:
	pipenv run python scripts/minikube_deploy.py

.PHONY: python-env
python-env:
	@pipenv run python -V
	@pipenv lock --requirements
	@pipenv lock --dev --requirements
