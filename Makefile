
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

.PHONY: docker-images
docker-images: $(foreach s, $(components), $(s)-docker-images)
	@echo TODO: build images from this repo

.PHONY: %-docker-images
%-docker-images: $(RENKU_BASE_DIR)/%
	@echo $(MAKE) -C $(RENKU_BASE_DIR)/$* docker-images

$(components): %: $(RENKU_BASE_DIR)/%
