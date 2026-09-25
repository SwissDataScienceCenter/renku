{{/*
Common labels
*/}}
{{- define "netpol.labels" -}}
app.kubernetes.io/name: {{ template "renku.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: renku
chart: {{ template "renku.chart" . }}
heritage: {{ .Release.Service }}
{{- end }}
