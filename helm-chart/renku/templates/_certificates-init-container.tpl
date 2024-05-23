{{- define "certificates.initContainer" -}}
{{- $customCAsEnabled := .Values.global.certificates.customCAs -}}
- name: init-certificates
  image: "{{ .Values.global.certificates.image.repository }}:{{ .Values.global.certificates.image.tag }}"
  securityContext:
    allowPrivilegeEscalation: false
    runAsUser: 1000
    runAsGroup: 1000
    runAsNonRoot: true
  volumeMounts:
    - name: etc-ssl-certs
      mountPath: /etc/ssl/certs/
    - name: custom-ca-certs
      mountPath: /usr/local/share/ca-certificates
      readOnly: true
{{- end -}}
