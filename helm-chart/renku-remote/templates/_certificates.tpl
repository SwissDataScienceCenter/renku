{{/*
CA trust wiring for pods in this chart that make outbound HTTPS calls (currently just the
ssh-proxy, reaching the main Renku deployment's public secret-service endpoint). The init
container runs `update-ca-certificates` against the image's own public root bundle plus any
configured customCAs, and writes the result to an emptyDir the main container mounts read-only.
*/}}
{{- define "renku-remote.certificates.initContainer" -}}
- name: init-certificates
  image: "{{ .Values.certificates.image.repository }}:{{ .Values.certificates.image.tag }}"
  securityContext:
    {{- toYaml .Values.securityContext | nindent 4 }}
  volumeMounts:
    - name: etc-ssl-certs
      mountPath: /etc/ssl/certs/
    {{- if .Values.certificates.customCAs }}
    - name: custom-ca-certs
      mountPath: /usr/local/share/ca-certificates
      readOnly: true
    {{- end }}
{{- end -}}

{{- define "renku-remote.certificates.volumes" -}}
- name: etc-ssl-certs
  emptyDir:
    medium: "Memory"
{{- if .Values.certificates.customCAs }}
- name: custom-ca-certs
  projected:
    defaultMode: 0444
    sources:
      {{- range $customCA := .Values.certificates.customCAs }}
      {{- if $customCA.secret }}
      - secret:
          name: {{ $customCA.secret }}
      {{- else if $customCA.configMap }}
      - configMap:
          name: {{ $customCA.configMap }}
      {{- end }}
      {{- end }}
{{- end }}
{{- end -}}

{{- define "renku-remote.certificates.volumeMounts.system" -}}
- name: etc-ssl-certs
  mountPath: /etc/ssl/certs/
  readOnly: true
{{- end -}}
