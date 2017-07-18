.PHONY: all
all: docker-images

.PHONY: docker-images
docker-images:
	$(MAKE) -C .. docker-all

.PHONY: apispec
apispec:
	$(eval HERE = $(shell pwd))
	echo $(HERE)
	cd ../apispec && npm run dist -- -H "localhost:9000" -o $(HERE)/target/apispec

