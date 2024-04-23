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
          name: {{ template "renku.fullname" . }}-ca
          items:
            - key: tls.crt
              path: {{ include "renku.fullname" . | nospace  }}-{{ randAlpha 6 }}.crt
    {{- if $customCAsEnabled }}
    {{- range $customCA := .Values.global.certificates.customCAs }}
      - secret:
          name: {{ $customCA.secret }}
    {{- end -}}
    {{- end -}}
{{- end -}}
