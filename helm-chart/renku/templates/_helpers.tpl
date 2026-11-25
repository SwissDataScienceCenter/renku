{{- define "renku.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "renku.fullname" -}}
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

{{- define "renku.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Public URL of this MCP server deployment */}}
{{- define "mcpServer.baseUrl" -}}
{{- printf "https://%s" .Values.mcpServer.domain -}}
{{- end -}}

{{/* Public URL of the remote Renku deployment the MCP server talks to */}}
{{- define "mcpServer.renkuUrl" -}}
{{- required "mcpServer.renku.url is required, e.g. https://renku.example.org" .Values.mcpServer.renku.url | trimSuffix "/" -}}
{{- end -}}

{{- define "mcpServer.keycloakIssuerUrl" -}}
{{- if .Values.mcpServer.renku.keycloakIssuer -}}
{{- .Values.mcpServer.renku.keycloakIssuer | trimSuffix "/" -}}
{{- else -}}
{{- printf "%s/auth/realms/Renku" (include "mcpServer.renkuUrl" .) -}}
{{- end -}}
{{- end -}}
