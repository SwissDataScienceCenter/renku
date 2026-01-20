{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "renku.notebooks.name" -}}
notebooks
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "renku.notebooks.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- printf "%s-notebooks" .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-notebooks" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
