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

{{- define "certificates.volumeMounts.javaCertsGeneral" -}}
- name: etc-ssl-certs
  mountPath: /etc/ssl/certs/java/cacerts
  subPath: java/cacerts
  readOnly: true
{{- end -}}

{{- define "certificates.volumeMounts.openjdk17" -}}
- name: etc-ssl-certs
  mountPath: /opt/java/openjdk/lib/security/cacerts
  subPath: java/cacerts
  readOnly: true
{{- end -}}
