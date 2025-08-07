FRONTEND ?= vscodium
BUILDER ?= ghcr.io/swissdatasciencecenter/renku-frontend-buildpacks/selector:0.0.6
RUN_IMAGE ?= ghcr.io/swissdatasciencecenter/renku-frontend-buildpacks/base-image:0.0.6
GIT_SHA := $(shell git rev-parse --short=7 HEAD)

.PHONY: all basic datascience

all: basic datascience

basic datascience:
	@BASE=$@; \
	PUBLISH_FLAG=""; \
	if [ -n "$$PUBLISH" ]; then PUBLISH_FLAG="--publish"; fi; \
	if [ -n "$$DOCKER_PREFIX" ]; then \
		case "$$DOCKER_PREFIX" in \
			*/) FINAL_PREFIX="$$DOCKER_PREFIX";; \
			*) FINAL_PREFIX="$$DOCKER_PREFIX/";; \
		esac; \
	else \
		FINAL_PREFIX=""; \
	fi; \
	TAGS=$${TAGS:-$${FINAL_PREFIX}py-$$BASE-$(FRONTEND):$(GIT_SHA)}; \
	echo "Building from \"$$BASE\" sub-directory"; \
	FIRST_TAG=$${TAGS%%,*}; \
	pack build $$FIRST_TAG \
		-p $$BASE \
		--builder $(BUILDER) \
		--run-image $(RUN_IMAGE) \
		--env BP_RENKU_FRONTENDS=$(FRONTEND) \
		--tag $$TAGS \
		$$PUBLISH_FLAG \
