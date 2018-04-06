{{/* vim: set filetype=mustache: */}}
{{/*
Postgres passwords
*/}}
{{- define "postgresPasswords" -}}
keycloak-password: {{ .Values.global.keycloak.postgresPassword | b64enc | quote }}
gitlab-password: {{ .Values.global.gitlab.postgresPassword | b64enc | quote }}
jupyterhub-password: {{ .Values.global.jupyterhub.postgresPassword | b64enc | quote }}
{{- end -}}

{{/*
Postgres init scripts
*/}}
{{- define "postgresInitScripts" -}}
# Init scripts that populate /docker-entrypoint-initdb.d
init_keycloak_db.sh: |-
  #!/bin/bash
  set -e

  KEYCLOAK_PASSWORD=$(cat /passwords/keycloak-password)

  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
      create database "{{ .Values.global.keycloak.postgresDatabase }}";
      create user "{{ .Values.global.keycloak.postgresUser }}" password '$KEYCLOAK_PASSWORD';
  EOSQL

  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "{{ .Values.global.keycloak.postgresDatabase }}" <<-EOSQL
      revoke all on schema "public" from "public";
      grant all privileges on database "{{ .Values.global.keycloak.postgresDatabase }}" to "{{ .Values.global.keycloak.postgresUser }}";
      grant all privileges on schema "public" to "{{ .Values.global.keycloak.postgresUser }}";
  EOSQL

init_gitlab_db.sh: |-
    #!/bin/bash
    set -e

    GITLAB_PASSWORD=$(cat /passwords/gitlab-password)

    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
        create database "{{ .Values.global.gitlab.postgresDatabase }}";
        create user "{{ .Values.global.gitlab.postgresUser }}" password '$GITLAB_PASSWORD';
    EOSQL

    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "{{ .Values.global.gitlab.postgresDatabase }}" <<-EOSQL
        create extension if not exists "pg_trgm";
        revoke all on schema "public" from "public";
        grant all privileges on database "{{ .Values.global.gitlab.postgresDatabase }}" to "{{ .Values.global.gitlab.postgresUser }}";
        grant all privileges on schema "public" to "{{ .Values.global.gitlab.postgresUser }}";
    EOSQL

init_jupyterhub_db.sh: |-
    #!/bin/bash
    set -e

    JUPYTERHUB_PASSWORD=$(cat /passwords/jupyterhub-password)

    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
        create database "{{ .Values.global.jupyterhub.postgresDatabase }}";
        create user "{{ .Values.global.jupyterhub.postgresUser }}" password '$JUPYTERHUB_PASSWORD';
    EOSQL

    psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "{{ .Values.global.jupyterhub.postgresDatabase }}" <<-EOSQL
        create extension if not exists "pg_trgm";
        revoke all on schema "public" from "public";
        grant all privileges on database "{{ .Values.global.jupyterhub.postgresDatabase }}" to "{{ .Values.global.jupyterhub.postgresUser }}";
        grant all privileges on schema "public" to "{{ .Values.global.jupyterhub.postgresUser }}";
    EOSQL
{{- end -}}
