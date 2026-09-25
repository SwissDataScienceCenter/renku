{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "renku.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "renku.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "renku.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Define http scheme
*/}}
{{- define "renku.http" -}}
{{- if .Values.global.useHTTPS -}}
https
{{- else -}}
http
{{- end -}}
{{- end -}}

{{/*
Define subcharts full names
*/}}
{{- define "postgresql.fullname" -}}
{{- if not .Values.global.externalServices.postgresql.enabled -}}
{{- printf "%s-%s" .Release.Name "postgresql" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- .Values.global.externalServices.postgresql.host -}}
{{- end -}}
{{- end -}}

{{- define "solr.fullname" -}}
{{- printf "%s-%s" .Release.Name "solr" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Name of the Keycloak custom resource. 48 leaves room for the suffixes the
operator appends resources (longest is -network-policy.) */}}
{{- define "keycloak.fullname" -}}
{{- printf "%s-%s" .Release.Name "keycloak" | replace "+" "_" | trunc 48 | trimSuffix "-" -}}
{{- end -}}

{{- define "gitlab.fullname" -}}
{{- printf "%s-%s" .Release.Name "gitlab" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "ui.fullname" -}}
{{- printf "%s-%s" .Release.Name "ui" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "uiserver.fullname" -}}
{{- printf "%s-%s" .Release.Name "uiserver" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "core.fullname" -}}
{{- printf "%s-%s" .Release.Name "core" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Admin credentials are under: 
* KEYCLOAK_ADMIN + KEYCLOAK_ADMIN_PASSWORD -> read by realm init job reads
* and username and password -> keys used by Keycloak operator in secret spec.bootstrapAdmin. */}}
{{- define "keycloak.admin-secret" -}}
{{- $d := (lookup "v1" "Secret" .Release.Namespace "keycloak-password-secret").data | default dict -}}
{{- $user := $d.KEYCLOAK_ADMIN | default $d.KEYCLOAK_USER | default (b64enc .Values.global.keycloak.user) -}}
{{- $password := $d.KEYCLOAK_ADMIN_PASSWORD | default $d.KEYCLOAK_PASSWORD | default (b64enc (default (randAlphaNum 64) .Values.global.keycloak.password.value)) -}}
KEYCLOAK_ADMIN: {{ $user | quote }}
KEYCLOAK_ADMIN_PASSWORD: {{ $password | quote }}
username: {{ $user | quote }}
password: {{ $password | quote }}
{{- end -}}

{{- define "keycloak.postgres-secret" -}}
{{- $d := (lookup "v1" "Secret" .Release.Namespace "renku-keycloak-postgres").data | default dict -}}
KC_DB_USERNAME: {{ $d.KC_DB_USERNAME | default $d.DB_USER | default (b64enc .Values.global.keycloak.postgresUser) | quote }}
KC_DB_PASSWORD: {{ $d.KC_DB_PASSWORD | default $d.DB_PASSWORD | default (b64enc (default (randAlphaNum 64) .Values.global.keycloak.postgresPassword.value)) | quote }}
{{- end -}}

{{/* The bundled Keycloak, or an external one we were handed admin credentials for. */}}
{{- define "renku.keycloak.provisionRealm" -}}
{{- if or .Values.keycloak.install .Values.global.keycloak.password.value -}}true{{- end -}}
{{- end -}}

{{- define "renku.baseUrl" -}}
{{ printf "%s://%s" (include "renku.http" .) .Values.global.renku.domain }}
{{- end -}}

{{- define "renku.keycloakUrl" -}}
{{- if .Values.keycloak.install -}}
{{/* NOTE: If the url for keycloak does not end with '/' then the python keycloak client library will fail to connect */}}
{{- printf "%s://%s/auth/" (include "renku.http" .) .Values.global.renku.domain -}}
{{- else -}}
{{- .Values.global.keycloak.url -}}
{{- end -}}
{{- end -}}

{{- define "renku.keycloakIssuerUrl" -}}
{{- printf "%s/realms/%s" (include "renku.keycloakUrl" . | trimSuffix "/") (include "renku.keycloak.realm" .) -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "renku.labels" -}}
helm.sh/chart: {{ include "renku.chart" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{- define "renku.keycloak.realm" -}}
{{ .Values.global.keycloak.realm | default "Renku" }}
{{- end -}}

{{- define "renku.dataService.keycloak.clientId" -}}
data-service
{{- end -}}

{{- define "renku.authz.tlsSecretName" -}}
renku-authz-tls-cert
{{- end -}}

{{- define "renku.CASecretName" -}}
renku-ca
{{- end -}}

{{- define "notebooks.cullingThresholdsJson" -}}
{{- $registered := dict "idle" .Values.notebooks.culling.idleSecondsThreshold.registered "hibernation" .Values.notebooks.culling.hibernatedSecondsThreshold.registered -}}
{{- $anonymous := dict "idle" .Values.notebooks.culling.idleSecondsThreshold.anonymous "hibernation" 0 -}}
{{- dict "registered" $registered "anonymous" $anonymous | toJson -}}
{{- end -}}

{{- define "renku.events.streamEnvVars" -}}
- name: "RS_REDIS_QUEUE_PROJECT_CREATED"
  value: "project.created"
- name: "RS_REDIS_QUEUE_PROJECT_UPDATED"
  value: "project.updated"
- name: "RS_REDIS_QUEUE_PROJECT_REMOVED"
  value: "project.removed"
- name: "RS_REDIS_QUEUE_PROJECTAUTH_ADDED"
  value: "projectAuth.added"
- name: "RS_REDIS_QUEUE_PROJECTAUTH_UPDATED"
  value: "projectAuth.updated"
- name: "RS_REDIS_QUEUE_PROJECTAUTH_REMOVED"
  value: "projectAuth.removed"
- name: "RS_REDIS_QUEUE_USER_ADDED"
  value: "user.added"
- name: "RS_REDIS_QUEUE_USER_UPDATED"
  value: "user.updated"
- name: "RS_REDIS_QUEUE_USER_REMOVED"
  value: "user.removed"
- name: "RS_REDIS_QUEUE_GROUP_ADDED"
  value: "group.added"
- name: "RS_REDIS_QUEUE_GROUP_UPDATED"
  value: "group.updated"
- name: "RS_REDIS_QUEUE_GROUP_REMOVED"
  value: "group.removed"
- name: "RS_REDIS_QUEUE_GROUPMEMBER_ADDED"
  value: "groupMember.added"
- name: "RS_REDIS_QUEUE_GROUPMEMBER_UPDATED"
  value: "groupMember.updated"
- name: "RS_REDIS_QUEUE_GROUPMEMBER_REMOVED"
  value: "groupMember.removed"
- name: "RS_REDIS_QUEUE_DATASERVICE_ALLEVENTS"
  value: "data_service.all_events"
{{- end -}}
