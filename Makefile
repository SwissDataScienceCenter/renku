RENGA_DEPLOYER_DIR=../renga-deployer

.PHONY: all
all: docker-images apispec storage-data renga-deployer

.PHONY: docker-images
docker-images:
	$(MAKE) -C .. docker-all

.PHONY: apispec
apispec:
	$(eval HERE = $(shell pwd))
	cd ../apispec && npm install && npm run dist -- --http -H "localhost" -o $(HERE)/target/apispec

.PHONY: renga-deployer
renga-deployer:
	docker build --tag renga-deployer:latest $(RENGA_DEPLOYER_DIR)

.PHONY: storage-data
storage-data:
	$(eval HERE = $(shell pwd))
	mkdir -p $(HERE)/storage-data
	chmod a+rwx $(HERE)/storage-data
