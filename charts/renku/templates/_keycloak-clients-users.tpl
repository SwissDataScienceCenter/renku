{{/* vim: set filetype=mustache: */}}
{{/*
Define clients and users for Keycloak
*/}}
{{- define "renku.keycloak.clients" -}}
[
  {
    "clientId": "gateway",
    "baseUrl": "{{ template "http" . }}://{{ .Values.global.renku.domain }}/api",
    "secret": "{{ required "Fill in .Values.global.gateway.clientSecret with `uuidgen -r`" .Values.global.gateway.clientSecret }}",
    "redirectUris": [
        "{{ template "http" . }}://{{ .Values.global.renku.domain }}/*"
    ],
    "webOrigins": [
        "{{ template "http" . }}://{{ .Values.global.renku.domain }}/*"
    ]
  }

  {{- if .Values.gitlab.enabled -}}
  ,{
    "clientId": "gitlab",
    "baseUrl": "{{ template "http" . }}://{{ .Values.global.renku.domain }}/gitlab",
    "secret": "{{ required "Fill in .Values.global.gitlab.clientSecret with `uuidgen -r`" .Values.global.gitlab.clientSecret }}",
    "redirectUris": [
      "{{ template "http" . }}://{{ .Values.global.renku.domain }}/gitlab/users/auth/oauth2_generic/callback"
    ],
    "webOrigins": []
  }
  {{- end -}}
]
{{- end -}}

{{- define "renku.keycloak.users" -}}
[
  {{- if .Values.keycloak.createDemoUser -}}
  {
    "username": "demo",
    "password": "demo",
    "enabled": true,
    "emailVerified": true,
    "firstName": "John",
    "lastName": "Doe",
    "email": "demo@datascience.ch"
  }
  {{- end -}}
]
{{- end -}}
