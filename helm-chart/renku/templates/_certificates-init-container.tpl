{{- define "certificates.initContainer" -}}
{{- $customCAsEnabled := .Values.global.certificates.customCAs -}}
- name: init-certificates
  image: "{{ .Values.global.certificates.image.repository }}:{{ .Values.global.certificates.image.tag }}"
  securityContext:
    {{- toYaml .Values.securityContext | nindent 4 }}
  volumeMounts:
    - name: etc-ssl-certs
      mountPath: /etc/ssl/certs/
{{- if $customCAsEnabled }}
    - name: custom-ca-certs
      mountPath: /usr/local/share/ca-certificates
      readOnly: true
{{- end -}}
{{- end -}}
