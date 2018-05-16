{{/* vim: set filetype=mustache: */}}
{{/*
hub db url
*/}}
{{- define "hub.db_url" -}}
{{- $hostname := include "postgresql.fullname" . -}}
{{- printf "postgres+psycopg2://%s:%s@%s:5432/%s" .Values.global.jupyterhub.postgresUser .Values.global.jupyterhub.postgresPassword $hostname .Values.global.jupyterhub.postgresDatabase | quote -}}
{{- end -}}
