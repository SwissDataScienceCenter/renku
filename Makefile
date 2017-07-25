.PHONY: all
all: docker-images apispec storage-data

.PHONY: docker-images
docker-images:
	$(MAKE) -C .. docker-all

.PHONY: apispec
apispec:
	$(eval HERE = $(shell pwd))
	cd ../apispec && npm install && npm run dist -- --http -H "localhost:9000" -o $(HERE)/target/apispec

.PHONY: storage-data
storage-data:
	$(eval HERE = $(shell pwd))
	mkdir -p $(HERE)/storage-data
	chmod a+rwx $(HERE)/storage-data
