{{/* vim: set filetype=mustache: */}}
{{/*
Common labels
*/}}
{{- define "renku-core.labels" -}}
app.kubernetes.io/name: core
{{ template "renku.labels" . }}
{{- end -}}

{{- define "renku-core.name" -}}
core
{{- end -}}

{{- define "renku-core.fullname" -}}
{{- printf "%s-%s" (include "renku.fullname" .) (include "renku-core.name" .) -}}
{{- end -}}
