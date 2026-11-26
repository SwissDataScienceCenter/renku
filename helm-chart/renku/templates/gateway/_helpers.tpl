{{/*
Expand the name of the chart.
*/}}
{{- define "gateway.name" -}}
gateway
{{- end -}}

{{/*
Common labels
*/}}
{{- define "gateway.labels" -}}
app.kubernetes.io/component: gateway
{{ template "renku.labels" . }}
{{- end }}
