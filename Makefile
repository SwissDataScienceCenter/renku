SBT = sbt -batch
SBT_PUBLISH_TARGET = publish-local
SBT_DOCKER_TARGET = docker:publishLocal
PLATFORM_BASE_DIR = ..
PLATFORM_VERSION = 0.1.0-SNAPSHOT
PLATFORM_BASE_REPO_URL = git@github.com:SwissDataScienceCenter

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
	docker build --tag $@:$(PLATFORM_VERSION) $(PLATFORM_BASE_DIR)/$@
