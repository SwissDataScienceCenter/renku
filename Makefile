SBT = sbt
SBT_PUBLISH_TARGET = publish-local
SBT_DOCKER_TARGET = docker:publishLocal
PLATFORM_BASE_DIR = ..
PLATFORM_VERSION = 0.1.0-SNAPSHOT

scala_services += renga-graph
scala_services += renga-commons
scala_services += renga-storage
scala_services += renga-authorization
scala_services += renga-explorer

scala_base = renga-graph renga-commons
scala_services-docker = $(foreach s,$(scala_services),$(s)-docker)
dockerfile-builds += renga-deployer

.PHONY: all
all: scala-base docker-images storage-data

.PHONY: scala-base
scala-base: $(scala_base)

.PHONY: $(scala_base)
$(scala_base):
	cd $(PLATFORM_BASE_DIR)/$@ && $(SBT) $(SBT_PUBLISH_TARGET)

.PHONY: docker-images
docker-images:	$(scala_services-docker) $(dockerfile-builds)

.PHONY: $(scala_services-docker)
$(scala_services-docker):
	$(eval target = $(subst -docker,,$@))
	cd $(PLATFORM_BASE_DIR)/$(target) && $(SBT) $(SBT_DOCKER_TARGET)

.PHONY: $(dockerfile-builds)
$(dockerfile-builds):
	docker build --tag $@:$(PLATFORM_VERSION) $(PLATFORM_BASE_DIR)/$@

.PHONY: storage-data
storage-data:
	$(eval HERE = $(shell pwd))
	mkdir -p $(HERE)/storage-data
	chmod a+rwx $(HERE)/storage-data
