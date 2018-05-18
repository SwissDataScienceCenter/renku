#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -f /docker-entrypoint-initdb.d/gitlab_db/00.create_db.postgres.sql

psql -v ON_ERROR_STOP=1 --dbname "gitlab" --username "$POSTGRES_USER" -f /docker-entrypoint-initdb.d/gitlab_db/01.init_db.postgres.sql

