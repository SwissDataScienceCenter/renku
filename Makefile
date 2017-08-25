SBT = sbt
SBT_PUBLISH_TARGET = publish-local
PLATFORM_BASE_DIR = ..
PLATFORM_VERSION = 0.1.0-SNAPSHOT
PLATFORM_BASE_REPO_URL = git@github.com:SwissDataScienceCenter
IMAGE_REPOSITORY=registry.gitlab.com/swissdatasciencecenter/images/

define DOCKER_BUILD
set version in Docker := "$(PLATFORM_VERSION)"
set dockerRepository := Option("$(IMAGE_REPOSITORY)".replaceAll("/$$", ""))
docker:publishLocal
endef

export DOCKER_BUILD

scala-services = renga-authorization renga-explorer \
	renga-graph-init renga-graph-mutation-service \
	renga-graph-navigation-service renga-graph-typesystem-service

scala-targets = $(foreach s,$(scala-services),$(s)-scala)
scala-artifact = renga-graph-artifact renga-commons-artifact

dockerfile-services = renga-deployer

service-dirs = $(foreach s,$(scala-services) $(dockerfile-services), $(PLATFORM_BASE_DIR)/$(s))

.PHONY: all
all: docker-images

# fetch missing repositories
$(PLATFORM_BASE_DIR)/%:
	$(eval target = $(lastword $(subst /, ,$@)))
	git clone $(PLATFORM_BASE_REPO_URL)/$(target).git $@

.PHONY: clone
clone: $(service-dirs)

# build scala services
%-artifact: $(PLATFORM_BASE_DIR)/%
	cd $< && $(SBT) $(SBT_PUBLISH_TARGET)

renga-graph-%-scala: $(PLATFORM_BASE_DIR)/renga-graph $(scala-artifact)
	cd $(PLATFORM_BASE_DIR)/renga-graph && echo "project $*\n$$DOCKER_BUILD" | $(SBT)

%-scala: $(PLATFORM_BASE_DIR)/% $(scala-artifact)
	cd $< && echo "$$DOCKER_BUILD" | $(SBT)

$(scala-services): %: %-scala

# build docker images
.PHONY: docker-images
docker-images: $(scala-services) $(dockerfile-services)

.PHONY: $(dockerfile-services)
$(dockerfile-services):
	docker build --tag $(IMAGE_REPOSITORY)$@:$(PLATFORM_VERSION) $(PLATFORM_BASE_DIR)/$@
