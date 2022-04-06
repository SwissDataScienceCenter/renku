{{- define "certificates.volumeMounts.system" -}}
- name: etc-ssl-certs
  mountPath: /etc/ssl/certs/
  readOnly: true
{{- end -}}

{{- define "certificates.volumeMounts.openjdk14" -}}
- name: etc-ssl-certs
  mountPath: /opt/openjdk-14/lib/security/cacerts
  subPath: java/cacerts
  readOnly: true
{{- end -}}

{{- define "certificates.volumeMounts.openjdk15" -}}
- name: etc-ssl-certs
  mountPath: /opt/openjdk-15/lib/security/cacerts
  subPath: java/cacerts
  readOnly: true
{{- end -}}

{{- define "certificates.volumeMounts.openjdk18" -}}
- name: etc-ssl-certs
  mountPath: /opt/openjdk-15/lib/security/cacerts
  subPath: java/cacerts
  readOnly: true
{{- end -}}
