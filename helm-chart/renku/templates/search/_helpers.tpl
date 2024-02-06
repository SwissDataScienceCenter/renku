{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "renku.search.searchApi.name" -}}
{{- "search-api" -}}
{{- end -}}

{{- define "renku.search.searchProvision.name" -}}
{{- "search-provision" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "renku.search.searchService.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- printf "%s-search-api" .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-search-api" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "renku.search.searchProvision.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- printf "%s-search-provision" .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-search-provision" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
