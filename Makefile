SBT = sbt
SBT_PUBLISH_TARGET = publish-local
SBT_DOCKER_TARGET := $(PWD)/docker-build
PLATFORM_BASE_DIR = ..
PLATFORM_VERSION = latest
PLATFORM_BASE_REPO_URL = git@github.com:SwissDataScienceCenter
IMAGE_REPOSITORY=registry.gitlab.com/swissdatasciencecenter/images/

scala-services = renga-graph renga-storage renga-authorization renga-explorer
scala-targets = $(foreach s,$(scala-services),$(s)-scala)
scala-artifact = renga-graph-artifact renga-commons-artifact

dockerfile-services = renga-deployer

service-dirs = $(foreach s,$(scala-services) $(dockerfile-services),$(PLATFORM_BASE_DIR)/$(s))

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

%-scala: $(PLATFORM_BASE_DIR)/% $(scala-artifact)
	cd $< && $(SBT) $(SBT_DOCKER_TARGET)

$(scala-services): %: %-scala

# build docker images
.PHONY: docker-images
docker-images: $(scala-services) $(dockerfile-services)

.PHONY: $(dockerfile-services)
$(dockerfile-services):
	docker build --tag $(IMAGE_REPOSITORY)$@:$(PLATFORM_VERSION) $(PLATFORM_BASE_DIR)/$@
