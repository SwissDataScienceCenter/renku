{{/*
Environment variables for the OpenMeter integration of the data service.
Used by both the data service API and the data tasks deployments.
*/}}
{{- define "renku.dataService.openmeter.env" -}}
{{- $om := .Values.dataService.openmeter -}}
- name: OPENMETER_ENABLED
  value: {{ $om.enabled | quote }}
{{- if $om.enabled }}
- name: OPENMETER_ENDPOINT_URL
  value: {{ required "dataService.openmeter.endpointUrl is required when OpenMeter is enabled" $om.endpointUrl | quote }}
- name: OPENMETER_METER_KEY
  value: {{ $om.meterKey | quote }}
- name: OPENMETER_FEATURE_KEY
  value: {{ $om.featureKey | quote }}
- name: OPENMETER_CUSTOMER_KEY_PREFIX
  value: {{ $om.customerKeyPrefix | quote }}
{{- if $om.apiToken.secretName }}
- name: OPENMETER_API_TOKEN
  valueFrom:
    secretKeyRef:
      name: {{ $om.apiToken.secretName | quote }}
      key: {{ $om.apiToken.secretKey | quote }}
{{- end }}
{{- end }}
{{- end -}}
