{{/*
Common labels
*/}}
{{- define "netpol.labels" -}}
app.kubernetes.io/name: {{ template "renku.name" . }}
{{ template "renku.labels" . }}
{{- end }}

{{/*
Common selectors
*/}}
{{- define "netpol.commonSelectors" -}}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
