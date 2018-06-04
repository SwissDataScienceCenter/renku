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

-include .env

ifeq ($(OS),Windows_NT)
    detected_OS := Windows
else
    detected_OS := $(shell sh -c 'uname -s 2>/dev/null || echo not')
endif

# Use build instead of local. See GitLab-CE issue:
# https://gitlab.com/gitlab-org/gitlab-ce/issues/45008
PLATFORM_DOMAIN?=renku.build

PLATFORM_BASE_DIR?=..
PLATFORM_BASE_REPO_URL?=https://github.com/SwissDataScienceCenter
PLATFORM_REPO_TPL?=$(PLATFORM_BASE_REPO_URL)/$*.git
PLATFORM_VERSION?=$(or ${TRAVIS_BRANCH},${TRAVIS_BRANCH},$(shell git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/^* //'))

ifeq ($(PLATFORM_VERSION), master)
	PLATFORM_VERSION=latest
endif

GIT_MASTER_HEAD_SHA:=$(shell git rev-parse --short=12 --verify HEAD)

GITLAB_URL?=http://gitlab.$(PLATFORM_DOMAIN)
GITLAB_REGISTRY_URL?=http://gitlab.$(PLATFORM_DOMAIN):5081
GITLAB_RUNNERS_TOKEN?=$(shell openssl rand -hex 32)

JUPYTERHUB_CRYPT_KEY?=$(shell openssl rand -hex 32)
JUPYTERHUB_RENKU_NOTEBOOKS_SERVICE_TOKEN?=$(shell openssl rand -hex 32)
JUPYTERHUB_URL?=http://jupyterhub.$(PLATFORM_DOMAIN)

PLAY_APPLICATION_SECRET?=$(shell openssl rand -hex 32)

DOCKER_REPOSITORY?=renku/
DOCKER_PREFIX:=${DOCKER_REGISTRY}$(DOCKER_REPOSITORY)
DOCKER_NETWORK?=review
DOCKER_COMPOSE_ENV=\
	DOCKER_NETWORK=$(DOCKER_NETWORK) \
	DOCKER_PREFIX=$(DOCKER_PREFIX) \
	DOCKER_REPOSITORY=$(DOCKER_REPOSITORY) \
	GITLAB_CLIENT_SECRET=$(GITLAB_CLIENT_SECRET) \
	GITLAB_REGISTRY_URL=$(GITLAB_REGISTRY_URL) \
	GITLAB_RUNNERS_TOKEN=$(GITLAB_RUNNERS_TOKEN) \
	GITLAB_SUDO_TOKEN=$(GITLAB_SUDO_TOKEN) \
	GITLAB_URL=$(GITLAB_URL) \
	JUPYTERHUB_CLIENT_SECRET=$(JUPYTERHUB_CLIENT_SECRET) \
	JUPYTERHUB_CRYPT_KEY=$(JUPYTERHUB_CRYPT_KEY) \
	JUPYTERHUB_RENKU_NOTEBOOKS_SERVICE_TOKEN=$(JUPYTERHUB_RENKU_NOTEBOOKS_SERVICE_TOKEN)\
	JUPYTERHUB_URL=$(JUPYTERHUB_URL) \
	KEYCLOAK_URL=$(KEYCLOAK_URL) \
	PLATFORM_DOMAIN=$(PLATFORM_DOMAIN) \
	PLATFORM_VERSION=$(PLATFORM_VERSION) \
	PLAY_APPLICATION_SECRET=$(PLAY_APPLICATION_SECRET) \
	RENKU_ENDPOINT=$(RENKU_ENDPOINT) \
	RENKU_UI_URL=$(RENKU_UI_URL)

ifndef KEYCLOAK_URL
	KEYCLOAK_URL=http://keycloak.$(PLATFORM_DOMAIN):8080
	export KEYCLOAK_URL
endif

ifndef RENKU_ENDPOINT
	RENKU_ENDPOINT=http://$(PLATFORM_DOMAIN)
	export RENKU_ENDPOINT
endif

ifndef RENKU_UI_URL
	# The ui should run under localhost instead of docker.for.mac.localhost
	RENKU_UI_URL=http://$(PLATFORM_DOMAIN)
	export RENKU_UI_URL
endif

ifndef GITLAB_CLIENT_SECRET
	GITLAB_CLIENT_SECRET=dummy-secret
	export GITLAB_CLIENT_SECRET
endif

ifndef GITLAB_SUDO_TOKEN
	GITLAB_SUDO_TOKEN=dummy-secret
	export GITLAB_SUDO_TOKEN
endif

ifndef JUPYTERHUB_CLIENT_SECRET
	JUPYTERHUB_CLIENT_SECRET=dummy-secret
	export JUPYTERHUB_CLIENT_SECRET
endif

# ------------------------------------------------
# 2. Repository management and docker image builds
# ------------------------------------------------
#
# Here we define make targets that allow you to manage the Renku component
# repositories and build docker images. The repositories are defined in the
# "repos" variable below. The other variables define how the various service
# docker images should be built.

# make targets:
#
# Repository management
# ---------------------
#
# clone: clones the repositories into this directory's parent directory
# checkout: checks out the PLATFORM_VERSION branch of all repositories
# pull: pull latest changes from remote
#

# Docker image builds
# -------------------
#
# Images are build and tagged with the value of PLATFORM_VERSION.
#
# docker-images: builds all the docker images locally. If you do not have the
#	repositories checked out, this is done first. You should not normally need
#   to do this unless you are working on the bleeding edge of the codebase as
#   all the images are pushed to the docker registry and will be fetched for
#   you. Typically you will only need to build some of the components yourself,
#   depending on what you are developing.
#
# It's possible to build images for individual components - for example, to
# build just the singleuser image, you simply use
#
# $ make singleuser
#


define DOCKER_BUILD
set version in Docker := "$(PLATFORM_VERSION)"
set dockerRepository := Option("$(DOCKER_REPOSITORY)".replaceAll("/$$", ""))
docker:publishLocal
endef

export DOCKER_BUILD

# Please keep values bellow sorted. Thank you!
repos = \
	renku-storage \
	renku-python \
	renku-ui

# scala-services = \
# 	renku-storage

makefile-services = \
	renku-notebooks \
	renku-storage \
 	renku-python \
 	renku-ui

dockerfile-services = \
	apispec \
	jupyterhub \
	keycloak \
	singleuser

.PHONY: all clone checkout pull docker-images $(dockerfile-services) $(makefile-services) tag
all: docker-images

.env:
	@for x in $(DOCKER_COMPOSE_ENV); do echo $$x >> .env; done

# fetch missing repositories
$(PLATFORM_BASE_DIR)/%:
	git clone $(PLATFORM_REPO_TPL) $@

clone: $(foreach s, $(repos), $(PLATFORM_BASE_DIR)/$(s))

%-checkout: $(PLATFORM_BASE_DIR)/%
	cd $< && git checkout $(PLATFORM_VERSION)

checkout: $(foreach s, $(repos), $(s)-checkout)

%-pull: $(PLATFORM_BASE_DIR)/%
	cd $< && git pull

pull: $(foreach s, $(repos), $(s)-pull)

# build docker images
.PHONY: docker-images $(dockerfile-services) $(makefile-services) tag
docker-images: $(dockerfile-services) $(makefile-services)

$(dockerfile-services): %: .env services/%/Dockerfile
	docker build --tag $(DOCKER_PREFIX)$@:$(PLATFORM_VERSION) \
		--tag $(DOCKER_PREFIX)$@:$(GIT_MASTER_HEAD_SHA) \
		services/$@

jupyterhub-k8s: .env services/jupyterhub/jupyterhub-k8s.Dockerfile
	docker build --tag $(DOCKER_PREFIX)$@:$(PLATFORM_VERSION) -f services/jupyterhub/$@.Dockerfile services/jupyterhub/

tag: $(dockerfile-services) jupyterhub-k8s

# build docker images from makefiles
$(makefile-services): %: $(PLATFORM_BASE_DIR)/%
	$(MAKE) -C $(PLATFORM_BASE_DIR)/$@

.PHONY: demo start stop test wipe

demo: .env
	@echo
	@echo Running the renku demo
	@echo
	docker run --rm --network host renku/renku-demo:$(PLATFORM_VERSION)

start: .env
	@./scripts/renku-start.sh

stop: .env
	@docker-compose stop

test: .env demo
	@pip install -r tests/requirements.txt
	@pip install -r docs/requirements.txt
	@./scripts/run-tests.sh

wipe: .env
	@./scripts/renku-wipe.sh
