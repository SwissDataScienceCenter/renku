{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "renku-logging.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "renku-logging.fullname" -}}
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
{{- define "renku-logging.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Define subcharts full names
*/}}
{{- define "elasticsearch.fullname" -}}
{{- printf "%s-%s-%s" .Release.Name "renku-logging" "elasticsearch" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "logstash.fullname" -}}
{{- printf "%s-%s-%s" .Release.Name "renku-logging" "logstash" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "kibana.fullname" -}}
{{- printf "%s-%s-%s" .Release.Name "renku-logging" "kibana" | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}
