{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "renku.graph.webhookService.name" -}}
{{- "webhook-service" -}}
{{- end -}}

{{- define "renku.graph.triplesGenerator.name" -}}
{{- "triples-generator" -}}
{{- end -}}

{{- define "renku.graph.tokenRepository.name" -}}
{{- "token-repository" -}}
{{- end -}}

{{- define "renku.graph.knowledgeGraph.name" -}}
{{- "knowledge-graph" -}}
{{- end -}}

{{- define "renku.graph.eventLog.name" -}}
{{- "event-log" -}}
{{- end -}}

{{- define "renku.graph.commitEventService.name" -}}
{{- "commit-event-service" -}}
{{- end -}}

{{- define "renku.graph.jena.name" -}}
{{- "jena" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "renku.graph.webhookService.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- printf "%s-webhook-service" .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-webhook-service" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "renku.graph.triplesGenerator.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- printf "%s-triples-generator" .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-triples-generator" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "renku.graph.tokenRepository.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- printf "%s-token-repository" .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-token-repository" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "renku.graph.knowledgeGraph.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- printf "%s-knowledge-graph" .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-knowledge-graph" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "renku.graph.eventLog.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- printf "%s-event-log" .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-event-log" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "renku.graph.jena.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- printf "%s-jena" .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-jena" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "renku.graph.commitEventService.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- printf "%s-commit-event-service" .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-commit-event-service" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{- define "renku.graph.core.latestUrl" -}}
{{- $coreBaseName := printf "%s-core" .Release.Name -}}
{{- printf "http://%s-%s" $coreBaseName (get $.Values.global.core.versions "latest").name -}}
{{- end -}}

{{/*
Comma separated list of renku-core service names
*/}}
{{- define "renku.graph.core.urls" -}}
{{- $serviceUrls := list -}}
{{- $coreBaseName := printf "%s-core" .Release.Name -}}
{{- range $i, $k := (keys .Values.global.core.versions | sortAlpha) -}}
{{- $serviceUrl := printf "http://%s-%s" $coreBaseName (get $.Values.global.core.versions $k).name -}}
{{- $serviceUrls = mustAppend $serviceUrls $serviceUrl -}}
{{- end -}}
{{- join "," $serviceUrls -}}
{{- end -}}

{{- /*
    Returns given number of random Hex characters.
    - randNumeric 4 | atoi generates a random number in [0, 10^4)
      This is a range range evenly divisble by 16, but even if off by one,
      that last partial interval offsetting randomness is only 1 part in 625.
    - mod N 16 maps to the range 0-15
    - printf "%x" represents a single number 0-15 as a single hex character
*/}}
{{- define "renku.graph.randHex" -}}
    {{- $result := "" }}
    {{- range $i := until . }}
        {{- $rand_hex_char := mod (randNumeric 4 | atoi) 16 | printf "%x" }}
        {{- $result = print $result $rand_hex_char }}
    {{- end }}
    {{- $result }}
{{- end }}
