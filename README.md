<!-- # -*- coding: utf-8 -*-
#
# Copyright 2017 Swiss Data Science Center
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
 -->

# RENGA

The goal of this project is to provide a simple way to
instantiate the platform using docker-compose.

## Prerequisites

- `make`
- Java SDK 8
- `sbt`
- `node` and `npm`
- `docker`
- `docker-compose`

## Prepare images

```bash
make
```

## Start the platform

```bash
docker-compose up
```

## Exposed ports

- `80` all platform APIs through a reverse proxy (Traefik)
- `443` all platform APIs through a reverse proxy (Traefik)
- `81` Traefik monitoring

For development purposes:
- `5432` Postgres database
- `9160` Cassandra thrift interface

Additionally, all services expose their port, picked randomly by docker.
Use `docker-compose ps` to list them.

## Configuration

Some variables are defined in the `.env` file.

##  Documentation

### Dependencies:

* Sphinx 1.6 or higher

    * http://www.sphinx-doc.org/

* Sphinx scala module

    * https://pythonhosted.org/sphinxcontrib-scaladomain/

* Sphinx readthedoc themes:

    * http://docs.readthedocs.io/en/latest/theme.html
    * https://pypi.python.org/pypi/sphinx_rtd_theme#installation)

* Graphviz (Plantuml diagrams):

    * http://www.graphviz.org/Download..php

    pip install -r docs/requirements.txt

### Build:

    cd docs && make html

## Contributing

Please make sure that it is possible to run the integration tests and build
documentation without warnings and errors before creating pull request.

    ./run-tests.sh
