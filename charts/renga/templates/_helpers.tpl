{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "renga.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "renga.fullname" -}}
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
{{- define "renga.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Define http scheme
*/}}
{{- define "http" -}}
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
{{- printf "%s-%s-%s" .Release.Name "renga" "postgresql" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "keycloak.fullname" -}}
{{- printf "%s-%s-%s" .Release.Name "renga" "keycloak" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "redis.fullname" -}}
{{- printf "%s-%s-%s" .Release.Name "renga" "redis" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "gitlab.fullname" -}}
{{- printf "%s-%s-%s" .Release.Name "renga" "gitlab" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "ui.fullname" -}}
{{- printf "%s-%s-%s" .Release.Name "renga" "ui" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "jupyterhub.fullname" -}}
{{- printf "%s-%s-%s" .Release.Name "renga" "jupyterhub" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}
