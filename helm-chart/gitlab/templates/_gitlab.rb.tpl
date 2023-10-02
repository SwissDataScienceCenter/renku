{{/* vim: set filetype=mustache: */}}
{{/*
GitLab Omnibus configuration
*/}}
{{- define "gitlab.config" -}}
## GitLab configuration settings
##! Check out the latest version of this file to know about the different
##! settings that can be configured by this file, which may be found at:
##! https://gitlab.com/gitlab-org/omnibus-gitlab/raw/master/files/gitlab-config-template/gitlab.rb.template


## GitLab URL
##! URL on which GitLab will be reachable.
##! For more details on configuring external_url see:
##! https://docs.gitlab.com/omnibus/settings/configuration.html#configuring-the-external-url-for-gitlab
external_url '{{ template "renku.http" . }}://{{ .Values.global.renku.domain }}/gitlab'

##! **Override only if you use a reverse proxy**
##! Docs: https://docs.gitlab.com/omnibus/settings/nginx.html#setting-the-nginx-listen-port
nginx['listen_port'] = 80

##! **Override only if your reverse proxy internally communicates over HTTP**
##! Docs: https://docs.gitlab.com/omnibus/settings/nginx.html#supporting-proxied-ssl
nginx['listen_https'] = false

## Configure SSH port to be displayed correctly
gitlab_rails['gitlab_shell_ssh_port'] = {{ default 22 .Values.ssh.externalPort }}

### OmniAuth Settings
###! Docs: https://docs.gitlab.com/ce/integration/omniauth.html
gitlab_rails['omniauth_enabled'] = true
{{- if .Values.oauth.autoSignIn }}
gitlab_rails['omniauth_auto_sign_in_with_provider'] = 'oauth2_generic'
{{- end }}
gitlab_rails['omniauth_allow_single_sign_on'] = ['oauth2_generic']
gitlab_rails['omniauth_block_auto_created_users'] = false
gitlab_rails['omniauth_providers'] = [
    {
        'name' => 'oauth2_generic',
        'app_id' => 'gitlab',
        'app_secret' => ENV['GITLAB_CLIENT_SECRET'],
        'args' => {
          client_options: {
            # Traefik maps keycloak to the URL below
            # CAREFUL: This must be accessible from inside the keycloak container
            # for server-to-server communication.
            'site' => '{{ template "renku.http" . }}://{{ .Values.global.renku.domain }}/auth/',
            'authorize_url' => '/auth/realms/Renku/protocol/openid-connect/auth',
            'user_info_url' => '/auth/realms/Renku/protocol/openid-connect/userinfo',
            'token_url' => '/auth/realms/Renku/protocol/openid-connect/token'
          },
          user_response_structure: {
            attributes: { email:'email', first_name:'given_name', last_name:'family_name', name:'name', nickname:'preferred_username' }, # if the nickname attribute of a user is called 'username'
            id_path: 'sub'
          },
          authorize_params: {
            scope: "openid profile email"
          }
        },
        label: 'Renku Login'
      }
    ]

gitlab_rails['initial_root_password'] = ENV['GITLAB_PASSWORD']

### GitLab database settings
###! Docs: https://docs.gitlab.com/omnibus/settings/database.html
###! **Only needed if you use an external database.**
postgresql['enable'] = false
gitlab_rails['db_adapter'] = "postgresql"
gitlab_rails['db_encoding'] = "utf-8"
gitlab_rails['db_database'] = ENV['POSTGRES_DATABASE']
gitlab_rails['db_username'] = ENV['POSTGRES_USER']
gitlab_rails['db_password'] = ENV['PGPASSWORD']
gitlab_rails['db_host'] = '{{ template "postgresql.fullname" . }}'
gitlab_rails['db_port'] = 5432

### GitLab Redis settings
###! Connect to your own Redis instance
###! Docs: https://docs.gitlab.com/omnibus/settings/redis.html

#### Redis TCP connection
# gitlab_rails['redis_host'] = localhost
# gitlab_rails['redis_port'] = 6379
# gitlab_rails['redis_password'] = nil
# gitlab_rails['redis_database'] = 0

### GitLab LFS object store
### Docs: https://docs.gitlab.com/ce/workflow/lfs/lfs_administration.html
{{ if .Values.lfsObjects.enabled -}}
gitlab_rails['lfs_object_store_enabled'] = true
gitlab_rails['lfs_object_store_remote_directory'] = "{{ .Values.lfsObjects.bucketName }}"
gitlab_rails['lfs_object_store_direct_upload'] = {{ .Values.lfsObjects.directUpload }}
gitlab_rails['lfs_object_store_background_upload'] = {{ .Values.lfsObjects.backgroundUpload }}
gitlab_rails['lfs_object_store_proxy_download'] = {{ .Values.lfsObjects.proxyDownload }}
gitlab_rails['lfs_object_store_connection'] = eval(ENV['GITLAB_LFS_CONNECTION'])
{{- end }}

prometheus['enable'] = false
gitlab_rails['monitoring_whitelist'] = ['127.0.0.0/8', '10.0.0.0/8']
gitlab_rails['env'] = { 'prometheus_multiproc_dir' => '/dev/shm' }

### GitLab Registry settings
registry_external_url '{{ .Values.registry.externalUrl }}'
gitlab_rails['registry_enabled'] = {{ .Values.registry.enabled }}
registry_nginx['enable'] = false
registry['registry_http_addr'] = '0.0.0.0:8105'
### Registry backend storage
###! Docs: https://docs.gitlab.com/ce/administration/container_registry.html#container-registry-storage-driver
{{- if .Values.registry.storage }}
registry['storage'] = eval(ENV['GITLAB_REGISTRY_STORAGE'])
{{- end }}
registry['health_storagedriver_enabled'] = {{ .Values.registry.backendHealthcheck }}

### GitLab rack-attack
### See: https://docs.gitlab.com/ce/security/rack_attack.html
### Disabled, as it is banning ingress controller IPs
gitlab_rails['rack_attack_git_basic_auth'] = {
  'enabled' => false
}

{{ if .Values.logging.useJson -}}
gitaly['logging_format'] = 'json'
gitlab_shell['log_format'] = 'json'
gitlab_workhorse['log_format'] = 'json'
registry['log_formatter'] = 'json'
sidekiq['log_format'] = 'json'
gitlab_pages['log_format'] = 'json'
{{- end }}

{{ .Values.extraConfig }}

{{- end -}}
