{{/*
Outgoing HTTP proxy support, configured once in `global.proxy`.

When `global.proxy.enabled` is true, every Renku-owned container gets
HTTP_PROXY / HTTPS_PROXY / NO_PROXY in both upper and lower case (clients
disagree on which case they read and which one wins).

NO_PROXY must list every in-cluster host that Renku services call, because:
  - Go and Python httpx do not match a bare service name (e.g. `renku-authz`)
    against a suffix such as `.svc`, so each Service short name is listed;
  - httpx ignores CIDR entries, so the Kubernetes API server IP is added via
    the `$(KUBERNETES_SERVICE_HOST)` dependent env var, expanded by the kubelet.

IMPORTANT: when adding a new Service to this chart (or a new in-cluster host
that a Renku component calls), add its name to "renku.proxy.serviceHosts".
*/}}

{{/*
Validate `global.proxy` and fail rendering on an unusable configuration.
*/}}
{{- define "renku.proxy.validate" -}}
{{- $proxy := .Values.global.proxy -}}
{{- if not $proxy.httpProxy -}}
{{- fail "global.proxy.enabled is true but global.proxy.httpProxy is empty" -}}
{{- end -}}
{{- if not (regexMatch "^https?://" $proxy.httpProxy) -}}
{{- fail (printf "global.proxy.httpProxy must start with http:// or https://, got %q" $proxy.httpProxy) -}}
{{- end -}}
{{- if and $proxy.httpsProxy (not (regexMatch "^https?://" $proxy.httpsProxy)) -}}
{{- fail (printf "global.proxy.httpsProxy must start with http:// or https://, got %q" $proxy.httpsProxy) -}}
{{- end -}}
{{- end -}}

{{/*
NO_PROXY entries that only depend on `global` values. Safe to use from
subcharts (e.g. keycloakx), where `.Values` is the subchart's values.
*/}}
{{- define "renku.proxy.baseHosts" -}}
{{- $hosts := list "localhost" "127.0.0.1" ".svc" ".cluster.local" "kubernetes" "kubernetes.default" "kubernetes.default.svc" -}}
{{- $hosts = concat $hosts (.Values.global.proxy.noProxy | default list) -}}
{{- $hosts | toJson -}}
{{- end -}}

{{/*
Short names of all in-cluster hosts that Renku components call.
Must be rendered with the renku chart's root context.
*/}}
{{- define "renku.proxy.serviceHosts" -}}
{{- $fullname := include "renku.fullname" . -}}
{{- $hosts := list
  (printf "%s-authz" $fullname)
  (printf "%s-data-service" $fullname)
  (printf "%s-gateway" $fullname)
  (printf "%s-secrets-storage" $fullname)
  (printf "%s-ssh-proxy" $fullname)
  (printf "%s-swagger" $fullname)
  (include "ui.fullname" .)
  (include "ui-server.fullname" .)
  (printf "%s-notebooks" .Release.Name)
  (printf "%s-search-api" .Release.Name)
  (printf "%s-amalthea-sessions-metrics" .Release.Name)
  (include "keycloak.fullname" .)
  (printf "%s-http" (include "keycloak.fullname" .))
  (printf "%s-headless" (include "keycloak.fullname" .))
  (include "postgresql.fullname" .)
  (printf "%s-hl" (include "postgresql.fullname" .))
  (include "solr.fullname" .)
  (printf "%s-headless" (include "solr.fullname" .))
  .Values.global.redis.host
  .Values.redis.fullnameOverride
  (printf "%s-headless" .Values.redis.fullnameOverride)
  (printf "%s-metrics" .Values.redis.fullnameOverride)
-}}
{{- $hosts | compact | uniq | toJson -}}
{{- end -}}

{{/*
Full NO_PROXY value (comma separated) for the renku chart's own pods,
without the Kubernetes API server IP (which only exists inside pods).
*/}}
{{- define "renku.proxy.noProxy" -}}
{{- $hosts := include "renku.proxy.baseHosts" . | fromJsonArray -}}
{{- $hosts = append $hosts .Release.Namespace -}}
{{- with .Values.notebooks.sessionsNamespace -}}
{{- $hosts = append $hosts . -}}
{{- end -}}
{{- $hosts = concat $hosts (include "renku.proxy.serviceHosts" . | fromJsonArray) -}}
{{- $hosts | compact | uniq | join "," -}}
{{- end -}}

{{/*
Render the six proxy env vars.
Expects a dict with `httpProxy`, `httpsProxy` and `noProxy`.
*/}}
{{- define "renku.proxy.envVars" -}}
{{- range $name := list "HTTP_PROXY" "http_proxy" }}
- name: {{ $name }}
  value: {{ $.httpProxy | quote }}
{{- end }}
{{- range $name := list "HTTPS_PROXY" "https_proxy" }}
- name: {{ $name }}
  value: {{ $.httpsProxy | quote }}
{{- end }}
{{- range $name := list "NO_PROXY" "no_proxy" }}
- name: {{ $name }}
  value: {{ $.noProxy | quote }}
{{- end }}
{{- end -}}

{{/*
Proxy env vars for Renku-owned containers. Renders nothing when disabled.
Usage: {{- with include "renku.proxy.env" . }}{{ . | nindent 12 }}{{ end }}
*/}}
{{- define "renku.proxy.env" -}}
{{- $proxy := .Values.global.proxy | default dict -}}
{{- if $proxy.enabled -}}
{{- include "renku.proxy.validate" . -}}
{{- include "renku.proxy.envVars" (dict
  "httpProxy" $proxy.httpProxy
  "httpsProxy" ($proxy.httpsProxy | default $proxy.httpProxy)
  "noProxy" (printf "%s,$(KUBERNETES_SERVICE_HOST)" (include "renku.proxy.noProxy" .))
) | trim -}}
{{- end -}}
{{- end -}}

{{/*
Proxy env vars for the keycloakx subchart, rendered from its `extraEnv`
through `tpl` (use it there as `{{- include "renku.proxy.env.keycloak" . }}`).
Only reads `.Values.global`, since `.Values` there is the
subchart's. Renders nothing when disabled.
*/}}
{{- define "renku.proxy.env.keycloak" -}}
{{- $proxy := .Values.global.proxy | default dict -}}
{{- if $proxy.enabled -}}
{{- include "renku.proxy.validate" . -}}
{{- $hosts := include "renku.proxy.baseHosts" . | fromJsonArray -}}
{{- /* Leading newline so that `{{- include ... }}` in extraEnv works. */}}
{{ include "renku.proxy.envVars" (dict
  "httpProxy" $proxy.httpProxy
  "httpsProxy" ($proxy.httpsProxy | default $proxy.httpProxy)
  "noProxy" (printf "%s,$(KUBERNETES_SERVICE_HOST)" ($hosts | compact | uniq | join ","))
) | trim -}}
{{- end -}}
{{- end -}}
