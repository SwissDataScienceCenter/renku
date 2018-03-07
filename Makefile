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

ifeq ($(OS),Windows_NT)
    detected_OS := Windows
else
    detected_OS := $(shell sh -c 'uname -s 2>/dev/null || echo not')
endif

ifeq ($(detected_OS), Darwin)
	PLATFORM_DOMAIN?=docker.for.mac.localhost
else
	PLATFORM_DOMAIN?=localhost
endif

PLATFORM_BASE_DIR?=..
PLATFORM_BASE_REPO_URL?=https://github.com/SwissDataScienceCenter
PLATFORM_REPO_TPL?=$(PLATFORM_BASE_REPO_URL)/$*.git
PLATFORM_VERSION?=$(or ${TRAVIS_BRANCH},${TRAVIS_BRANCH},$(shell git branch 2> /dev/null | sed -e '/^[^*]/d' -e 's/^* //'))

ifeq ($(PLATFORM_VERSION), master)
	PLATFORM_VERSION=latest
endif

GIT_MASTER_HEAD_SHA:=$(shell git rev-parse --short=12 --verify HEAD)

GITLAB_URL?=http://$(PLATFORM_DOMAIN):5080
GITLAB_REGISTRY_URL?=http://$(PLATFORM_DOMAIN):5081
GITLAB_DIRS=config logs git-data lfs-data runner

DOCKER_REPOSITORY?=rengahub/
DOCKER_PREFIX:=${DOCKER_REGISTRY}$(DOCKER_REPOSITORY)
DOCKER_NETWORK?=review
DOCKER_COMPOSE_ENV=\
	DOCKER_PREFIX=$(DOCKER_PREFIX) \
	DOCKER_REPOSITORY=$(DOCKER_REPOSITORY) \
	GITLAB_URL=$(GITLAB_URL) \
	GITLAB_REGISTRY_URL=$(GITLAB_REGISTRY_URL) \
	GITLAB_CLIENT_SECRET=$(GITLAB_CLIENT_SECRET) \
	PLATFORM_DOMAIN=$(PLATFORM_DOMAIN) \
	PLATFORM_VERSION=$(PLATFORM_VERSION) \
	RENGA_ENDPOINT=$(RENGA_ENDPOINT) \
	RENGA_UI_URL=$(RENGA_UI_URL)

SBT_IVY_DIR := $(PWD)/.ivy
SBT = sbt -ivy $(SBT_IVY_DIR)
SBT_PUBLISH_TARGET = publish-local

ifndef RENGA_ENDPOINT
	RENGA_ENDPOINT=http://$(PLATFORM_DOMAIN)
	export RENGA_ENDPOINT
endif

ifndef RENGA_UI_URL
	# The ui should run under localhost instead of docker.for.mac.localhost
	RENGA_UI_URL=http://localhost
	export RENGA_UI_URL
endif

ifndef GITLAB_CLIENT_SECRET
	GITLAB_CLIENT_SECRET=dummy-secret
	export GITLAB_CLIENT_SECRET
endif

ifndef GITLAB_TOKEN
	GITLAB_TOKEN=dummy-secret
	export GITLAB_TOKEN
endif

define DOCKER_BUILD
set version in Docker := "$(PLATFORM_VERSION)"
set dockerRepository := Option("$(DOCKER_REPOSITORY)".replaceAll("/$$", ""))
docker:publishLocal
endef

export DOCKER_BUILD

# Please keep values bellow sorted. Thank you!
repos = \
	renga-storage \
	renga-python \
	renga-ui

scala-services = \
	renga-storage

makefile-services = \
 	renga-ui \
 	renga-python

scala-artifact = \
	renga-commons \
	renga-graph

.PHONY: all
all: docker-images

# fetch missing repositories
$(PLATFORM_BASE_DIR)/%:
	git clone $(PLATFORM_REPO_TPL) $@

.PHONY: clone
clone: $(foreach s, $(repos), $(PLATFORM_BASE_DIR)/$(s))

%-checkout: $(PLATFORM_BASE_DIR)/%
	cd $< && git checkout $(GIT_BRANCH)

.PHONY: checkout
checkout: $(foreach s, $(repos), $(s)-checkout)

%-pull: $(PLATFORM_BASE_DIR)/%
	cd $< && git pull

.PHONY: pull
pull: $(foreach s, $(repos), $(s)-pull)

# build scala services
%-artifact: $(PLATFORM_BASE_DIR)/%
	cd $< && $(SBT) $(SBT_PUBLISH_TARGET)
	rm -rf $(SBT_IVY_DIR)/cache/ch.datascience/$(*)*

renga-graph-%-scala: $(PLATFORM_BASE_DIR)/renga-graph $(scala-artifact)
	cd $< && echo "project $*\n$$DOCKER_BUILD" | $(SBT)

%-scala: $(PLATFORM_BASE_DIR)/% $(scala-artifact)
	cd $< && echo "$$DOCKER_BUILD" | $(SBT)

$(scala-services): %: %-scala

$(scala-artifact): %: %-artifact

# define this dependency explicitly
renga-commons-artifact: renga-graph-artifact

# build docker images
.PHONY: $(dockerfile-services)
$(dockerfile-services): %: $(PLATFORM_BASE_DIR)/%
	docker build --tag $(DOCKER_REPOSITORY)$@:$(PLATFORM_VERSION) $(PLATFORM_BASE_DIR)/$@

# build docker images from makefiles
.PHONY: $(makefile-services)
$(makefile-services): %: $(PLATFORM_BASE_DIR)/%
	$(MAKE) -C $(PLATFORM_BASE_DIR)/$@

# Docker actions
.PHONY: docker-images docker-network docker-compose-up
docker-images: $(scala-services) $(dockerfile-services) $(makefile-services)

docker-network:
ifeq ($(shell docker network ls -q -f name=$(DOCKER_NETWORK)), )
	@docker network create $(DOCKER_NETWORK)
endif
	@echo "[Info] Using Docker network: $(DOCKER_NETWORK)=$(shell docker network ls -q -f name=review)"

remove-docker-network:
ifeq ($(shell docker network ls -q -f name=$(DOCKER_NETWORK)), )
	@docker network rm $(DOCKER_NETWORK)
endif

docker-compose-up:
	$(DOCKER_COMPOSE_ENV) docker-compose up --build -d ${DOCKER_SCALE}
	@./scripts/wait-for-services.sh

# GitLab actions
services/gitlab/%:
	@mkdir -p $@

# Preregister the ui as a client with gitlab.
# This command will fail on restart when the client is already there - we don't care.
register-gitlab-oauth-applications: unregister-gitlab-oauth-applications
	@$(DOCKER_COMPOSE_ENV) docker-compose exec gitlab /opt/gitlab/bin/gitlab-psql \
		-h /var/opt/gitlab/postgresql -d gitlabhq_production \
		-c "INSERT INTO oauth_applications (name, uid, scopes, redirect_uri, secret, trusted) VALUES ('renga-ui', 'renga-ui', 'api read_user', '$(RENGA_UI_URL)/login/redirect/gitlab', 'no-secret-needed', 'true')"

unregister-gitlab-oauth-applications:
	@$(DOCKER_COMPOSE_ENV) docker-compose exec gitlab /opt/gitlab/bin/gitlab-psql \
		-h /var/opt/gitlab/postgresql -d gitlabhq_production \
		-c "DELETE FROM oauth_applications WHERE uid='renga-ui'"

register-gitlab-user-token: unregister-gitlab-user-token
	@$(DOCKER_COMPOSE_ENV) docker-compose exec gitlab /opt/gitlab/bin/gitlab-psql \
		-h /var/opt/gitlab/postgresql -d gitlabhq_production \
		-c "INSERT INTO personal_access_tokens ( user_id, token, name, revoked, expires_at, created_at, updated_at, scopes, impersonation) VALUES ( '1', '$(GITLAB_TOKEN)', 'managed storage token', 'f', NULL, '2018-02-26 10:46:16.602039', '2018-02-26 10:46:16.602039', E'--- \n- api\n- read_user\n- sudo\n- read_registry', 'f');"

unregister-gitlab-user-token:
	@$(DOCKER_COMPOSE_ENV) docker-compose exec gitlab /opt/gitlab/bin/gitlab-psql \
		-h /var/opt/gitlab/postgresql -d gitlabhq_production \
		-c "DELETE FROM personal_access_tokens WHERE user_id='1' AND name='managed storage token';"

register-runners: unregister-runners
ifeq (${RUNNER_TOKEN},)
	@echo "[Error] RUNNER_TOKEN needs to be configured. Check $(GITLAB_URL)/admin/runners"
	@exit 1
endif
	@for container in $(shell $(DOCKER_COMPOSE_ENV) docker-compose ps -q gitlab-runner) ; do \
		docker exec -ti $$container gitlab-runner register \
			-n -u $(GITLAB_URL) \
			--name $$container-shell \
			-r ${RUNNER_TOKEN} \
			--executor shell \
			--env RENGA_REVIEW_DOMAIN=$(PLATFORM_DOMAIN) \
			--env RENGA_REVIEW_NETWORK=$(DOCKER_NETWORK) \
			--locked=false \
			--run-untagged=false \
			--tag-list notebook \
			--docker-image $(DOCKER_PREFIX)renga-python:$(PLATFORM_VERSION) \
			--docker-pull-policy "if-not-present"; \
		docker exec -ti $$container gitlab-runner register \
			-n -u $(GITLAB_URL) \
			--name $$container-docker \
			-r ${RUNNER_TOKEN} \
			--executor docker \
			--env RENGA_REVIEW_DOMAIN=$(PLATFORM_DOMAIN) \
			--env RENGA_REVIEW_NETWORK=$(DOCKER_NETWORK) \
			--locked=false \
			--run-untagged=false \
			--tag-list cwl \
			--docker-image $(DOCKER_PREFIX)renga-python:$(PLATFORM_VERSION) \
			--docker-pull-policy "if-not-present"; \
	done
	@echo
	@echo "[Info] Edit gitlab/runner/config.toml to increase the number of concurrent runner jobs."
unregister-runners:
	@for container in $(shell $(DOCKER_COMPOSE_ENV) docker-compose ps -q gitlab-runner) ; do \
		docker exec -ti $$container gitlab-runner unregister \
			--name $$container-shell || echo ok; \
		docker exec -ti $$container gitlab-runner unregister \
			--name $$container-docker || echo ok; \
	done

# Platform actions
.PHONY: start stop restart test clean wipe
start: docker-network $(GITLAB_DIRS:%=services/gitlab/%) unregister-runners docker-compose-up register-gitlab-oauth-applications register-gitlab-user-token
ifeq (${GITLAB_CLIENT_SECRET}, dummy-secret)
	@echo
	@echo "[Warning] You have not defined a GITLAB_CLIENT_SECRET. Using dummy"
	@echo "          secret instead. Never do this in production!"
	@echo
endif
	@echo
	@echo "[Success] Renga UI should be under $(RENGA_UI_URL) and GitLab under $(GITLAB_URL)"
	@echo
	@echo "[Info] Register GitLab runners using:"
	@echo "         make register-runners"
ifeq (${DOCKER_SCALE},)
	@echo
	@echo "[Info] You can configure scale parameters: DOCKER_SCALE=\"--scale gitlab-runner=4\" make start"
endif

stop: remove-docker-network unregister-runners unregister-gitlab-oauth-applications unregister-gitlab-user-token
	$(DOCKER_COMPOSE_ENV) docker-compose stop

restart: stop start

clean:
	@$(DOCKER_COMPOSE_ENV) docker-compose down --volumes --remove-orphans

wipe: clean remove-docker-network
	@rm -rf services/storage/data/*
	@rm -rf gitlab

test: docs/requirements.txt tests/requirements.txt
	@pip install -r docs/requirements.txt
	@pip install -r tests/requirements.txt
	@scripts/run-tests.sh
