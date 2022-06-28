{{- define "certificates.volumes" -}}
{{- $customCAsEnabled := .Values.global.certificates.customCAs -}}
- name: etc-ssl-certs
  emptyDir:
    medium: "Memory"
{{- if $customCAsEnabled }}
- name: custom-ca-certs
  projected:
    defaultMode: 0444
    sources:
    {{- range $customCA := .Values.global.certificates.customCAs }}
      - secret:
          name: {{ $customCA.secret }}
    {{- end -}}
{{- end -}}
{{- end -}}
