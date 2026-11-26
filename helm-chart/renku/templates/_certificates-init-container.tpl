{{- define "certificates.initContainer" -}}
{{- $customCAsEnabled := .Values.global.certificates.customCAs -}}
- name: init-certificates
  image: "{{ .Values.global.certificates.image.repository }}:{{ .Values.global.certificates.image.tag }}"
  securityContext:
    {{- toYaml .Values.securityContext | nindent 4 }}
  {{- with (include "renku.proxy.env" .) }}
  env:
    {{- . | nindent 4 }}
  {{- end }}
  volumeMounts:
    - name: etc-ssl-certs
      mountPath: /etc/ssl/certs/
    - name: custom-ca-certs
      mountPath: /usr/local/share/ca-certificates
      readOnly: true
{{- end -}}
