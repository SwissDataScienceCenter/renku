#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -f /docker-entrypoint-initdb.d/mutation_db/00.create_db.postgres.sql

psql -v ON_ERROR_STOP=1 --dbname "graph-wal" --username "$POSTGRES_USER" -f /docker-entrypoint-initdb.d/mutation_db/01.init_db.postgres.sql

psql -v ON_ERROR_STOP=1 --dbname "graph-wal" --username "$POSTGRES_USER" -f /docker-entrypoint-initdb.d/mutation_db/02.create_tables.postgres.sql
