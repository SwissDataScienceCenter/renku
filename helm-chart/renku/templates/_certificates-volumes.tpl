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
    {{- if $customCA.secret }}
      - secret:
          name: {{ $customCA.secret }}
    {{- else if $customCA.configMap }}
      - configMap:
          name: {{ $customCA.configMap }}
    {{- end }}
    {{- end }}
    {{- end }}
{{- end }}
