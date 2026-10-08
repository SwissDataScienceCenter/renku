{{/*
Common labels
*/}}
{{- define "secretsstorage.labels" -}}
app.kubernetes.io/component: secrets-storage
{{ template "renku.labels" . }}
{{- end }}
