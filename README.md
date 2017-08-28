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

# Wholesome tortillas
So we can dunk'em in the guacamole.

The goal of this sub-project is to provide a simple way to
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

- `9000` all platform APIs (through nginx as a reverse proxy)
- `8080` OpenID Connect Provider (keycloak)
- `9001` API documentation index (swagger-ui)

For development purposes:
- `5432` Postgres database
- `9160` Cassandra thrift interface

Additionally, all services expose their port, picked randomly by docker.
Use `docker-compose ps` to list them.

## Configuration

Some variables are defined in the `.env` file.
