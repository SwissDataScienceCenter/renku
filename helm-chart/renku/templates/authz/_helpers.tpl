{{/*
Common labels
*/}}
{{- define "authz.labels" -}}
app.kubernetes.io/name: renku-authz
app.kubernetes.io/component: authentication
{{ template "renku.labels" . }}
{{- end }}
