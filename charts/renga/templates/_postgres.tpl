{{/* vim: set filetype=mustache: */}}
{{/*
Postgres passwords
*/}}
{{- define "postgresPasswords" -}}
keycloak-password: {{ .Values.global.keycloak.postgresPassword | b64enc | quote }}
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

  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "keycloak" <<-EOSQL
      revoke all on schema "public" from "public";
      grant all privileges on database "{{ .Values.global.keycloak.postgresDatabase }}" to "{{ .Values.global.keycloak.postgresUser }}";
      grant all privileges on schema "public" to "{{ .Values.global.keycloak.postgresUser }}";
  EOSQL
{{- end -}}
