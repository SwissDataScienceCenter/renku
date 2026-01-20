{{/* vim: set filetype=mustache: */}}
{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
*/}}
{{- define "ui.fullname" -}}
{{- printf "%s-ui" (include "renku.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "ui.canary.fullname" -}}
{{- printf "%s-ui-canary" (include "renku.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "ui-server.fullname" -}}
{{- printf "%s-uiserver" (include "renku.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "ui-server.labels" -}}
app.kubernetes.io/name: uiserver
{{ template "renku.labels" . }}
{{- end -}}

{{/*
Return the appropriate apiVersion for autoscaling.
*/}}
{{- define "ui-server.autoscaling.apiVersion" -}}
{{- if semverCompare ">1.23-0" .Capabilities.KubeVersion.GitVersion -}}
{{- print "autoscaling/v2" -}}
{{- else -}}
{{- print "autoscaling/v2beta2" -}}
{{- end -}}
{{- end -}}

{{/*
Template a json list of cookies that should not be stripped by the ui-server proxy
*/}}
{{- define "ui-server.keepCookies" -}}
{{- $cookieNames := .Values.ui.server.keepCookies | uniq | toJson -}}
{{- end -}}
