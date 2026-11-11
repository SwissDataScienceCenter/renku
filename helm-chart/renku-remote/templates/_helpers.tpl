{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "renku-remote.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "renku-remote.fullname" -}}
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
{{- define "renku-remote.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels, without the `app` label which is set per resource.
*/}}
{{- define "renku-remote.labels" -}}
chart: {{ template "renku-remote.chart" . }}
release: {{ .Release.Name }}
heritage: {{ .Release.Service }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: renku
{{- end -}}

{{/*
Name of the service account that the main Renku deployment uses to access this cluster.
*/}}
{{- define "renku-remote.crossClusterServiceAccountName" -}}
{{- default (printf "%s-cross-cluster" (include "renku-remote.fullname" .)) .Values.crossClusterAccess.serviceAccountName -}}
{{- end -}}

{{/*
Selector labels that match all Amalthea session pods.
*/}}
{{- define "renku-remote.sessionPodSelector" -}}
app.kubernetes.io/created-by: controller-manager
app.kubernetes.io/name: AmaltheaSession
{{- end -}}
