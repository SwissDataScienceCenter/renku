.PHONY: all
all: docker-images apispec

.PHONY: docker-images
docker-images:
	$(MAKE) -C .. docker-all

.PHONY: apispec
apispec:
	$(eval HERE = $(shell pwd))
	echo $(HERE)
	cd ../apispec && npm install && npm run dist -- --http -H "localhost:9000" -o $(HERE)/target/apispec
