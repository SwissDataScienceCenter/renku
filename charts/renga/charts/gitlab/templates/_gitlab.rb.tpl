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
external_url '{{ template "http" . }}://{{ .Values.global.renga.domain }}/gitlab'

##! **Override only if you use a reverse proxy**
##! Docs: https://docs.gitlab.com/omnibus/settings/nginx.html#setting-the-nginx-listen-port
nginx['listen_port'] = 80

##! **Override only if your reverse proxy internally communicates over HTTP**
##! Docs: https://docs.gitlab.com/omnibus/settings/nginx.html#supporting-proxied-ssl
nginx['listen_https'] = false

### OmniAuth Settings
###! Docs: https://docs.gitlab.com/ce/integration/omniauth.html
gitlab_rails['omniauth_enabled'] = true
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
            'site' => '{{ template "http" . }}://{{ .Values.global.renga.domain }}/auth/',
            'authorize_url' => '/auth/realms/Renga/protocol/openid-connect/auth',
            'user_info_url' => '/auth/realms/Renga/protocol/openid-connect/userinfo',
            'token_url' => '/auth/realms/Renga/protocol/openid-connect/token'
          },
          user_response_structure: {
            attributes: { email:'email', first_name:'given_name', last_name:'family_name', name:'name', nickname:'preferred_username' }, # if the nickname attribute of a user is called 'username'
            id_path: 'preferred_username'
          }
        },
        label: 'Renga Login'
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
gitlab_rails['redis_host'] = "{{ template "redis.fullname" . }}"
# gitlab_rails['redis_port'] = 6379
# gitlab_rails['redis_password'] = nil
# gitlab_rails['redis_database'] = 0

prometheus['enable'] = false

{{- end -}}