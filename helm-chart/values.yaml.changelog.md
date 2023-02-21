# `values.yaml` changelog
Necessary changes to the values files for upgrading the Renku chart version are listed here.
For changes that require manual steps other than changing values, please check out [the chart readme](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart#upgrading).

Please follow this convention when adding a new row
* `<type: NEW|EDIT|DELETE> - *<resource name>*: <details>`

----
## Upgrading to Renku 0.23.0
* EDIT - the `redis` chart was upgraded from `10.7.11` to `17.4.2`; the values
in the newer `redis` chart differ from those in the older chart. As the `renku`
`values.yaml` passes some values to the dependent `redis` chart, some values
need to be changed.

In summary, the main differences are that the newer `redis` chart no longer has
notions of masters and slaves; nodes are simply replicas. The default
deployment is one in which Redis Sentinel is enabled and 3 replicas are
deployed. There are also changes to where secrets are located in the helm
chart.

For reference to the `redis` `values.yaml`, for version `17.4.2` of the bitnami
`redis` helm chart see:

https://github.com/bitnami/charts/blob/0274e44ae4460f91a3e25c20e12be11bc8874c95/bitnami/redis/values.yaml

Also, see the default `renku` `values.yaml` file for more comments relating to
the impact of these settings.

Old
  ```
redis:
  # If set to true, a HA redis will be included in the Renku release.
  install: true
  # If set to true, we'll create a k8s secret to be used as existingSecret
  # for password auth.
  createSecret: true
  # The actual password, ignored if createSecret is false.
  password: # openssl rand -hex 32

  fullnameOverride: renku-redis
  redisPort: 6379

  usePassword: true
  existingSecret: redis-secret
  existingSecretPasswordKey: redis-password

  master:
    persistence:
      enabled: false
    resources:
      requests:
        cpu: 200m
        memory: 256Mi
  slave:
    persistence:
      enabled: false
    resources:
      requests:
        cpu: 200m
        memory: 256Mi
  sentinel:
    port: 26379
    enabled: true
    resources:
      requests:
        cpu: 200m
        memory: 64Mi
    downAfterMilliseconds: 1000
    failoverTimeout: 2000
  networkPolicy:
    enabled: true
    allowExternal: false
  ```
New
  ```
redis:
  ###########################################################################
  ### Configuration that is unknown to the redis chart but picked up      ###
  ### by the renku chart.                                                 ###
  ###########################################################################

  # If set to true, a HA redis will be included in the Renku release.
  install: true
  # If set to true, we'll create a k8s secret to be used as existingSecret
  # for password auth.
  createSecret: true
  # The actual password, ignored if createSecret is false.
  password: # openssl rand -hex 32

  fullnameOverride: renku-redis

  architecture: replication
  auth:
    enabled: true
    sentinel: true
    existingSecret: redis-secret
    existingSecretPasswordKey: redis-password
  
    commonConfiguration: |-
      appendonly no
      save ""

  replica:
    replicaCount: 3
    resources:
      limits: 
        cpu: 250m
        memory: 256Mi
      requests: 
        cpu: 250m
        memory: 256Mi
    updateStrategy:
      type: RollingUpdate
    persistence:
      enabled: false
    autoscaling:
      enabled: false

  sentinel:
    enabled: true
    quorum: 2
    service:
      sentinel: 26379
    resources:
      requests:
        cpu: 200m
        memory: 64Mi
    downAfterMilliseconds: 1000
    failoverTimeout: 2000

  networkPolicy:
    enabled: true
    allowExternal: false

  metrics:
    enabled: true
  ```


## Upgrading to Renku 0.18.0
* EDIT - the ui chart was simplified and given a more regular structure.

Old
  ```
  ui:
    baseUrl: <string>
    gatewayUrl: <string>
  ```
New
  ```
  ui:
    client:
      url: [old baseUrl]
      [... other client properties]
    gateway:
      url: <string>
  ```

## Upgrading to Renku 0.16.0
* DELETE - renku graph's Jena does not need the *dataset* property anymore so *global.graph.jena.dataset* property can be removed.

## Upgrading to Renku 0.15.0
* EDIT - the ui and ui-server charts were unified, requiring that the values be merged.

The new structure looks like
  ```
  ui:
    baseUrl: <string>
    gatewayUrl: <string>
    gitlabUrl: <string>
    ingress: <object>
    client: <object>  # the remaining old ui values
    server: <object>  # the old uiserver values
  ```
  Specifically, for property not in [baseUrl, gatewayUrl, gitlabUrl, ingress]:
  * rename *ui.[property]* to *ui.client.[property]*
  * rename *uiserver.[property]* to *ui.server.[property]*

## Upgrading to Renku 0.12.1
* NEW/EDIT - sentry values across the repositories were changed to follow this pattern:
  ```
  sentry:
    enabled: <bool>
    dsn: <string>
    environment: <string>
    sampleRate: <number>   # available only for some repos
  ```
  Specifically:
  * rename *ui.sentry.url* and *uiserver.sentry.url* to *ui.sentry.dsn* and *uiserver.sentry.dsn*
  * rename *ui.sentry.namespace* and *uiserver.sentry.namespace* to *ui.sentry.environment* and *uiserver.sentry.environment*
  * add *ui.sentry.sampleRate* and *uiserver.sentry.sampleRate*
  * add *core.sentry.enabled* and *core.sentry.sampleRate*
  * add *gateway.sentry.enabled*
  * rename *graph.sentry.url* to *graph.sentry.dsn* and *graph.sentry.environmentName* to *graph.sentry.environment*
  * add *notebooks.sentry.enabled*
  * rename *notebooks.sentry.env* to *notebooks.sentry.environment*

## Upgrading to Renku 0.11.0
* NEW/EDIT *postgresql.persistence.existingClaim* might need to be modified in the course of upgrading your PostgreSQL version. See [these instructions](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/utils/postgres_migrations/version_upgrades/README.md)
* NEW/EDIT/DELETE *gitlab.image.tag* might have to be adjusted as we do a GitLab major version bump in with this release. See [these instructions](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart#upgrading-to-0110)

## Upgrading to Renku 0.10.3
* NEW - *redis-password* Generate through `openssl rand -hex 32`.

## Upgrading to Renku 0.10.2
* NEW - *uiserver* has been added with the required values for the ui-server component.
* NEW - *global.uiserver.clientSecret* has been added when introducing a new `renku-ui` client application in keycloak. Generate through `openssl rand -hex 32`.
* NEW - *notebooks.amalthea.resourceUsageCheck.enabled* needs to be set to false for the time being.
* NEW - *notebooks.sessionTolerations* and *notebooks.sessionAffinity* is now configurable in order to have user session dedicated nodes.

## Upgrading to Renku 0.10.0 (breaking changes)
The use of Amalthea and removal of Jupyterhub require some changes.
This version is not backward compatible with the user sessions from older versions. During the deployment the admin should clean up all remaining user sessions and then deploy this version.

* NEW - *notebooks.amalthea* several new sections have been added to the `values.yaml` file which are required by Amalthea. Please refer to the renku values file for more details.
* DELETE - *notebooks.jupyter* and all references to Jupyterhub in the values have been removed and are not required anymore.
* DELETE - anonymous sessions do not require a separate namespace and renku-notebooks deployment, if enabled in the values file they now run in the same namespace as regular user sessions.

## Upgrading to Renku 0.9.0
* NEW - *ui.homepage* has been added for customizing the content on the RenkuLab home page. See the values.yml file for more detailed documentation.

## Upgrading to Renku 0.8.4
* NEW *notebooks.serverDefaults* has been added with default values that will be
used to create a session when specific server options are left out of the request to launch
a session. See the [values.yaml file](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/renku/values.yaml) for more details.
* NEW *notebooks.userSessionPersistentVolumes* has been added which enables the use of persistent volumes for user sessions. The use of persistent volumes is disabled by default however. To turn this feature on set the `enabled` flag and specify a storage class that should be used. We strongly recommend using a storage class with a `Delete` retain policy because otherwise the persistent volumes from the user sessions will keep accumulating and will require manual intervention for cleanup.


## Upgrading to Renku 0.8.0 (breaking changes)
* NEW/EDIT *postgresql.persistence.existingClaim* will most likely need to be modified in the course of upgrading your postgresql version. See [these instructions](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/utils/postgres_migrations/version_upgrades/README.md)
* NEW/EDIT/DELETE *gitlab.image.tag* might have to be adjusted as we do a GitLab major version bump in with this release. See [these instructions](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart#upgrading-to-080)


## Upgrading to Renku 0.7.13
* NEW - *global.gateway.cliClientSecret* has been added when introducing a new "renku-cli" client application in keycloak. Generate through `openssl rand -hex 32`.


## Upgrading to Renku 0.7.9
* NEW - *ui.uiserverUrl* has been added. The default value is `<domain>/ui-server`.


## Upgrading to Renku 0.7.8 (INCLUDES BREAKING CHANGES!)
* Keycloak chart dependency upgraded from `4.10.2` to `9.8.1`, check out [the instructions](https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#upgrading) on how to upgrade aspects not covered by default in the Renku chart. Most notably, keycloak values are
less nested, so at the level of the Renku chart values, *keycloak.keycloak.X.Y* becomes *keycloak.X.Y*.
* EDIT - the section *keycloak.keycloak.persistence* has been removed. Database connection
details are specified through the *keycloak.extraEnv* and *keycloak.extraEnvFrom* blocks. See
the [Renku values file](https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/values.yaml) for reference.
* EDIT - *keycloak.keycloak.username* has been moved to *global.keycloak.user*.


## Upgrading to Renku 0.7.0 (INCLUDES BREAKING CHANGES!)
* EDIT - *postgresql.persistence.existingClaim*: Renku `0.7.0` upgrades the postgres chart dependency, which requires modification of the postgres data volume of existing deployments. See [this helper job manifest](https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/utils/migrate-pg-to-bitnami.yaml).
* EDIT - *postgresql.postgresPassword* renamed to *postgresql.postgresqlPassword*.
* DELETE - *minio.enabled*: minio has been removed from the Renku dependencies, deploy minio as a standalone service if you need it.
* DELETE - *ui.templatesRepository*
* NEW - *ui.templates*: the UI project creation has been overhauled and can be configured here.
* NEW - *ui.templates.repositories* - list of renku templates repositories to be used in the ui. Each object contains a `url` pointing to the repository, a `ref` specifying the target tag or commit, a `name` acting as a user friendly short name.
* DELETE - *global.gitlab.sudoToken* - this is no longer needed.
* DELETE - *notebooks.gitlab.registry.secret* - this is no longer needed.
* DELETE - *notebooks.gitlab.registry.token* - this is no longer needed.
* DELETE - *notebooks.gitlab.registry.username* - this is no longer needed.
* DELETE - *gateway.graph* - this is no longer needed.


## Changes on top of Renku 0.6.5
* NEW - *ui.statuspage.id*: the id for the statuspage.io instance. See the values.yml file
for more detailed documentation.

## Changes on top of Renku 0.6.1
Interactive sessions for logged-out users are now possible, see
[the docs](https://renku.readthedocs.io/en/latest/admin/index.html#enabling-notebooks-for-anonymous-users).
* NEW - *global.anonymousSessions.enabled*: set to true to turn on notebook sessions
for logged out users.
* NEW - *global.anonymousSessions.postgresPassword.overwriteOnHelmUpgrade*: set to `true` when upgrading an existing deployment, then back to `false` (default value).

## Changes on top of Renku 0.5.2
* NEW - *ui.sentry*: added Sentry information for logging runtime exceptions

## Changes on top of Renku 0.4.3
* EDIT - *notebooks.serverOptions*: added `<resource>.order` to control ui rendering order. The default values defined in notebooks are now different.
* DELETE - *graph.knowledgeGraph.services.renku.url*: no need for this anymore as it's linked to `global.renku.domain` internally. Ref: [refactor KG remove services renku URL](https://github.com/SwissDataScienceCenter/renku/commit/6d4a0e5cf02833d193f86a38cc14762609fcd9c0)
* DELETE - *keycloak.keycloak.extraVolumes.-name:realm-secret* : need to delete `renku-realm.json` in order to work with [remove realm template](https://github.com/SwissDataScienceCenter/renku/commit/825c10e72d185bfaff78af8c7693d06cd745014c) commit
* EDIT - *global.gitlab.postgresPassword*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.
* EDIT - *global.gitlab.sudoToken*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.
* EDIT - *global.keycloak.postgresPassword*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.
* NEW - *global.keycloak.password*: split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.
* DELETE - *keycloak.keycloak.password*: the admin password for keycloak is now provided through a k8s secret which is set in the global values section.
* EDIT - *global.jupyterhub.postgresPassword*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.
* EDIT - *global.graph.dbEventLog.postgresPassword*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.
* EDIT - *global.graph.tokenRepository.postgresPassword*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.
* EDIT - *notebooks.jupyterhub.hub.db.url*: remove the password (between ":" and "@" from the URL).
* EDIT - *notebooks.jupyterhub.hub.db.extraEnv*: due to the inability of helm to merge maps, all extra environment variables must be explicitly provided after the introduction of secrets auto-generation:
```
        extraEnv:
          - name: GITLAB_URL
            value: <your-gitlab-url>
          - name: JUPYTERHUB_SPAWNER_CLASS
            value: spawners.RenkuKubeSpawner
          - name: PGPASSWORD
            valueFrom:
              secretKeyRef:
                name: renku-jupyterhub-postgres
                key: jupyterhub-postgres-password
```
