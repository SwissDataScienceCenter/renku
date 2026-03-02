{{- define "certificates.volumes" -}}
{{- $customCAsEnabled := .Values.global.certificates.customCAs -}}
- name: etc-ssl-certs
  emptyDir:
    medium: "Memory"
- name: custom-ca-certs
  projected:
    defaultMode: 0444
    sources:
      - secret:
          name: {{ include "renku.CASecretName" . }}
          items:
            - key: tls.crt
              path: {{ include "renku.CASecretName" . }}-internal-communication-ca.crt
    {{- if $customCAsEnabled }}
    {{- range $customCA := .Values.global.certificates.customCAs }}
      - secret:
          name: {{ $customCA.secret }}
    {{- end -}}
    {{- end -}}
{{- end -}}

{{- define "certificatesForMounts.volumes" -}}
{{- $customCAsEnabled := .Values.global.certificates.customCAs -}}
{{- $customCAsForMountsEnabled := .Values.global.certificates.customCAsForDataConnectorMounts -}}
- name: etc-ssl-certs
  emptyDir:
    medium: "Memory"
- name: custom-ca-certs
  projected:
    defaultMode: 0444
    sources:
      - secret:
          name: {{ include "renku.CASecretName" . }}
          items:
            - key: tls.crt
              path: {{ include "renku.CASecretName" . }}-internal-communication-ca.crt
    {{- if $customCAsEnabled }}
    {{- range $customCA := .Values.global.certificates.customCAs }}
      - secret:
          name: {{ $customCA.secret }}
    {{- end -}}
    {{- end -}}
    {{- if $customCAsForMountsEnabled }}
    {{- range $customCA := .Values.global.certificates.customCAsForDataConnectorMounts }}
      - secret:
          name: {{ $customCA.secret }}
    {{- end -}}
    {{- end -}}
{{- end -}}
