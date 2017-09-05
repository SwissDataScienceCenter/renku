# -*- coding: utf-8 -*-
#
# Copyright 2017 Swiss Data Science Center
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

SBT_IVY_DIR := $(PWD)/.ivy
SBT = sbt -ivy $(SBT_IVY_DIR)
SBT_PUBLISH_TARGET = publish-local
PLATFORM_BASE_DIR = ..
PLATFORM_VERSION = 0.1.0-SNAPSHOT
PLATFORM_BASE_REPO_URL = git@github.com:SwissDataScienceCenter
PLATFORM_REPO_TPL = $(PLATFORM_BASE_REPO_URL)/$*.git
IMAGE_REPOSITORY=registry.gitlab.com/swissdatasciencecenter/images/

define DOCKER_BUILD
set version in Docker := "$(PLATFORM_VERSION)"
set dockerRepository := Option("$(IMAGE_REPOSITORY)".replaceAll("/$$", ""))
docker:publishLocal
endef

export DOCKER_BUILD

# Please keep values bellow sorted. Thank you!
repos = \
	renga-authorization \
	renga-commons \
	renga-deployer \
	renga-explorer \
	renga-graph \
	renga-projects \
	renga-storage \
	renga-ui

scala-services = \
	renga-authorization \
	renga-explorer \
	renga-graph-init \
	renga-graph-mutation-service \
	renga-graph-navigation-service \
	renga-graph-typesystem-service \
	renga-projects \
	renga-storage

dockerfile-services = \
	renga-deployer \
	renga-ui

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
	docker build --tag $(IMAGE_REPOSITORY)$@:$(PLATFORM_VERSION) $(PLATFORM_BASE_DIR)/$@

.PHONY: docker-images
docker-images: $(scala-services) $(dockerfile-services)
