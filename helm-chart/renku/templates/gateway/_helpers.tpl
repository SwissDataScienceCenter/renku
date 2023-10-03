{{/*
Template core service paths as a comma separated list
*/}}
{{- define "gateway.core.paths" -}}
{{- $paths := list -}}
{{- range $i, $k := (keys .Values.global.core.versions | sortAlpha) -}}
{{- $paths = mustAppend $paths (printf "/api/renku/%s" (get $.Values.global.core.versions $k).prefix) -}}
{{- if eq $k "latest" -}}
{{- $paths = mustAppend $paths "/api/renku" -}}
{{- end -}}
{{- end -}}
{{- join "," $paths | quote -}}
{{- end -}}

{{/*
Template core service names as a comma separated list
*/}}
{{- define "gateway.core.serviceNames" -}}
{{- $serviceNames := list -}}
{{- $coreBaseName := printf "%s-core" .Release.Name -}}
{{- range $i, $k := (keys .Values.global.core.versions | sortAlpha) -}}
{{- $serviceName := printf "%s-%s" $coreBaseName (get $.Values.global.core.versions $k).name -}}
{{- $serviceNames = mustAppend $serviceNames $serviceName -}}
{{- if eq $k "latest" -}}
{{- $serviceNames = mustAppend $serviceNames $serviceName -}}
{{- end -}}
{{- end -}}
{{- join "," $serviceNames | quote -}}
{{- end -}}

{{/*
Expand the name of the chart.
*/}}
{{- define "gateway.name" -}}
gateway
{{- end -}}
