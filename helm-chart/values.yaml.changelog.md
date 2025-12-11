# `values.yaml` changelog
Necessary changes to the values files for upgrading the Renku chart version are listed here.
For changes that require manual steps other than changing values, please check out [the chart readme](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart#upgrading).

Please follow this convention when adding a new row
* `<type: NEW|EDIT|DELETE> - *<resource name>*: <details>`

## Upgrading to Renku 2.12.0

* DELETE `enableV1Services` as detailed in the 2.11.0 release.

## Upgrading to Renku 2.11.0

* EDIT `enableV1Services` is now set to `false` by default.
  Legacy (v1) services are now deprecated in Renku and version 2.11 is the last Renku
  release where legacy services can be enabled. In the next Renku release (2.12),
  the `enableV1Services` chart value will be removed.
* NEW `dataService.imageBuilders.builderImage`, sets the builder image used for Renku builds.
* NEW `dataService.imageBuilders.runImage`, sets the run image used for Renku builds.
* NEW `dataService.imageBuilders.platformOverrides`, overrides for image builds, keyed by platform. See commented values for a working example for `linux/arm64`.
* DELETE `dataService.imageBuilders.vscodiumPythonRunImage`, as it is replaced by `dataService.imageBuilders.runImage`.

## Upgrading to Renku 2.9.0

* NEW `notebooks.sessionIngress.ingressClassName`, set to `nginx` by default. Replaces the deprecated `kubernetes.io/ingress.class` annotation.
* NEW `dataService.localClusterSessionServiceAccount`, set to "" by default.
  The service account used in the Amalthea session pods for the resource pool(s) that use the local cluster.
  The only use for this is if you want to use a different SCC than the default in Openshift.
  The service account is not mounted in the session. Leaving the default value of "" means that
  a service account will not be specified for the Session spec.
* NEW `dataService.remoteClustersKubeconfigSecretName`, set to "" by default.
  A secret name that contains kubeconfigs for all remote clusters you want to use.
  Each key in the secret should be a file name and should contain the YAML kubeconfig
  for a remote cluster you would like to use. The name of the kubeconfig can then be used
  in the payload for creating a cluster in the API, under the `config_name` field.
  If the value is not set then a secret is not mounted.
  The secret should be in the same namespace as where all the Renku services are installed.

## Upgrading to Renku 2.8.0

* NEW `enableInternalGitlab`, set to `true` by default. Indicates that the Gitlab that comes with legacy (V1) Renku
should still be used. If the value is set to `false` then the gateway will not ask users to
log into the Renku legacy Gitlab and will not inject credentials for it. Note also that the
`enableInternalGitlab` flag cannot be set to `false` if the `enableV1Services` flag is set to `true`.
If you are creating a brand new deployment of Renku, then both the `enableInternalGitlab` and the
`enableV1Services` flags should be set to `false`.

## Upgrading to Renku 2.7.0

The git-proxy sidecar container in sessions has been updated to have ports configured inside the reserved range
for session services (range 65400-65535). Previously, the ports 8080 and 8081 were used which would break sessions
configured with either port (custom session environments).

* NEW `notebooks.gitHttpsProxy.port` to set the git-proxy port
* NEW `notebooks.gitHttpsProxy.healthPort` to set the git-proxy health check port

## Upgrading to Renku 2.6.0

* NEW `ui.client.supportLegacySessions` used to disable Legacy sessions.

## Upgrading to Renku 2.5.0

The notebook service and asscociated K8s services and components like Service, Roles, Rolebindings, etc. have been
removed so the portions of the values file related to them have been removed and will be ignored
going forward. This does not require immediate action from administrators but it will allow for the
simplification of existing values files which now can contain fewer sections.

* DELETE `notebooks.autoscaling`
* DELETE `notebooks.ingress`
* DELETE `notebooks.image`
* DELETE `notebooks.resources`
* DELETE `notebooks.tests`
* DELETE `notebooks.k8sWatcher`
* DELETE `notebooks.dummyStores`

## Upgrading to Renku 2.3.0

* DELETE `search`, now the search and search provisioning is part of the data services and data tasks and are not separate services.

This will result in 2 fewer deployments in your cluster. If the values are left in the values file they will be ignored. Therefore this does not require immediate action by administrators, it is just good practice to remove deprecated sections that you may have in your values file.

* DELETE `ui.client.podSecurityContext`, replaced by `podSecurityContext`
* DELETE `ui.client.securityContext`, replaced by `securityContext`
* DELETE `ui.server.podSecurityContext`, replaced by `podSecurityContext`
* DELETE `ui.server.securityContext`, replaced by `securityContext`
* EDIT `securityContext` added "drop all capabilities" to the default value

There are also several other components that were using hardcoded security contexts that 
now are using the centralized `securityContext` and `podSecurityContext` from the values file. 
These are:
- Helm tests
- setup job for the authorization database
- setup job for for the Keycloak database in Postgres
- setup job for the Keycloak realms
- setup job for the platform initiliazation
- setup job for the Renku database initialization in Postgres
- self-signed CA certificates initialization container
- Keycloak initialization container that injects self signed CA certificates is now using the 
  centralized security context instead of a custom one

## Upgrading to Renku 0.71.0

+* NEW `enableV1Services` used to indicate whether services needed exclusively for Renku V1 are deployed or not. The support for this feature is experimental and it should not yet be used in production at all.

## Upgrading to Renku 0.70.0

* DELETE `dataService.backgroundJobs` backgroundJobs have been rolled into dataTasks deployment.

## Upgrading to Renku 0.68.0

Renku now includes anonymized product metrics, making it easier to know what features are used by users to help with product development and statistics.

* NEW `posthog.enabled` to set up gathering product metrics using posthog (note: renku won't deploy posthog for you, either deploy it yourself or use their cloud offering)
* NEW `posthog.host` posthog host to log metrics to
* NEW `posthog.apiKey` apiKey for your posthog project
* NEW `posthog.environment` adds an `environment` property to all logged metrics events, allowing to distinguish e.g. `development` and `production`

* NEW `core.resources.rqmetrics` to set up cpu/memory requests and limits

## Upgrading to Renku 0.66.0

* NEW `dataService.imageBuilders` to configure session image builds using Shipwright.
  This feature is experimental - enable at your own risk! It depends on Shipwright (version >= 0.15.x) 
  which must be installed independently from the Renku Helm chart.

* EDIT ``notebooks.replicaCount`` - set to zero by default because the data service is handling all sessions now, the notebook service will be fully decomissioned and removed from the helm chart in a subsequent PR.

## Upgrading to Renku 0.65.1

* Many default requests and limits have been defined in the values file. We recommend all administrators to set requests and limits in their values file
to make sure that they satisfy the needs of their deployment.
If limits are not set, the default will be applied by Helm and if those are too restrictive, it might result in services being killed by Kubernetes (for Out Of Memory errors).

## Upgrading to Renku 0.62.0

* DELETE ``gitlab.*`` - all values related to the bundled GitLab have been removed. GitLab must from now on be provided as an external service and is no longer supplied as a part of the Renku Helm chart.
* NEW `search.sentry.environment|dsn|enabled` to set the sentry environment for the search services

## Upgrading to Renku 0.61.0

* NEW ``networkPolicies.allowAllIngressFromPods`` specify pod selectors that will allow the selected pods to access all other services in the Renku release namespace.
* NEW ``networkPolicies.allowAllIngressFromNamespaces`` specify a list of namespaces that should be allowed to access all other services in the Renku release namespace.

## Upgrading to Renku 0.60.0

* NEW ``gateway.idleSessionTTLSeconds`` to set the session idle TTL in seconds.
* NEW ``gateway.maxSessionTTLSeconds`` to set the session max TTL in seconds.
* NEW ``gateway.debug`` to enable debug logs from the gateway.

## Upgrading to Renku 0.59.1

* NEW ``notebooks.bypassCacheOnFailure`` has been added. Setting this to false prevents renku-notebooks to call
  the k8s api directly if its k8s cache has issues or is not running.

## Upgrading to Renku 0.57.0

* DELETE ``gateway.image.auth`` has been removed.
* EDIT ``gateway.reverseProxy`` settings have been moved to ``gateway``:

Old
  ```
  gateway:
    reverseProxy:
      image:
        repository: renku/renku-revproxy
        tag: "0.24.0"
        pullPolicy: IfNotPresent
      metrics:
        enabled: true
        port: 8765
      replicaCount: 2
      podAnnotations: {}
      resources: {}
      autoscaling:
        enabled: false
        minReplicas: 2
        maxReplicas: 5
        targetMemoryUtilizationPercentage: 75
        targetCPUUtilizationPercentage: 75
      updateStrategy: {}
  ```
New
  ```
  gateway:
    image:
      repository: renku/renku-gateway
      tag: "1.0.0"
      pullPolicy: IfNotPresent
    metrics:
      enabled: true
      port: 8765
    replicaCount: 2
    podAnnotations: {}
    resources: {}
    autoscaling:
      enabled: false
      minReplicas: 2
      maxReplicas: 5
      targetMemoryUtilizationPercentage: 75
      targetCPUUtilizationPercentage: 75
    updateStrategy: {}
  ```

## Upgrading to Renku 0.54.0

* NEW ``global.platformConfig``: The YAML string can now contain a new key, `secretServicePreviousPrivateKey` which allows for rotating the secret-storage private key.
  To rotate keys, set this to the previous `secretServicePrivateKey` value and set a new key for `secretServicePrivateKey`. Secrets-storage will then rotate all secrets
  once its started. You can monitor the progress of the rotation in prometheus using the `secrets_rotation_count` (for total secrets rotated so far) and `secrets_rotation_state`
  (either `running`, `finished` or `errored`) for the overall state of the rotation. Please make sure to unset `secretServicePreviousPrivateKey` once rotation is finished
  as a matter of best practice.

  NOTE: Make sure that you do not redeploy or rollback the Renku Helm chart while a key rotation is underway. Even if the
  deployment is broken it is best to wait for the key rotation to finish before attempting another deployment or a rollback.

## Upgrading to Renku 0.53.0

The `data-service` configuration has been updated to support trusting reverse proxies.

* NEW ``dataService.trustedProxies.proxiesCount`` to set the reverse proxy count
* NEW ``dataService.trustedProxies.realIpHeader`` to set the real IP header value

## Upgrading to Renku 0.52.1

* EDIT ``dataService.keycloakSync`` has been renamed to ``dataService.backgroundJobs``
* NEW ``dataService.backgroundJobs.events.resources`` to set the resources for the users short period synchronization job
* NEW ``dataService.backgroundJobs.total.resources`` to set the resources for the users long period synchronization job

## Upgrading to Renku 0.52.0

* NEW ``global.platformConfig`` a YAML string that contains the secret keys used by renku-data-services and secrets storage. In the future we plan to also consolidate other
  platform specific configuration here. The YAML string should contain the following keys:
    - `secretServicePrivateKey`: An RSA private key, generated by `ssh-keygen -m PKCS8 -t rsa -b 4096` without a password. You can leave this empty to have one automatically generated
      but we recommend setting it manually.
    - `dataServiceEncryptionKey`: A 32 byte random string, used for encryption at rest.

## Upgrading to Renku 0.51.0

* NEW ``ui.client.sessionClassEmailUs`` to customize the content of the Email Us button on the Session class option.

## Upgrading to Renku 0.50.0

The gitlab configuration has been unified in the `global` section of the values, which requires modifications for existing deployments.

* EDIT - *notebooks.gitlab.registry.host* -> *global.gitlab.registry.host*
* DELETE - *notebooks.gitlab* has been removed.
* NEW - *solr*: [solr](https://bitnami.com/stack/solr) has been added
  to support searching. It is run as single node mode and required by
  new search services. The size of the persistent volume is set to 8G.
  Details can be found in the [respective values section](https://github.com/SwissDataScienceCenter/renku/blob/85412f0821fa482bbe398c64ed97174af2800aa/helm-chart/renku/values.yaml#L492)
* NEW - *search*: Renku introduces a new search service consisting of
  `search-api` and `search-provision`. The first is responsible for
  reading from solr, providing a http api to search across renku. The
  latter is responsible for populating solr with appropriate data.

## Upgrading to Renku 0.49.0

The PostgreSQL chart dependency has been upgraded, which requires modification of the postgres data volume of existing deployments. See [these instructions](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/utils/postgres_migrations/version_upgrades/README.md) for more details.

* NEW/EDIT - *postgresql.persistence.existingClaim*: Renku `0.49.0` upgrades the postgres chart dependency, which requires modification of the postgres data volume of existing deployments. See [these instructions](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/utils/postgres_migrations/version_upgrades/README.md)

* EDIT - *postgresql*: The upgrade of the postgres chart dependency requires some restructuring of the postgres subchart values to match those of bitnami/postgresql chart version 14.0.1, namely:

* EDIT - *postgresql.postgresqlPassword* -> *postgresql.auth.postgresPassword*: Note that the new key does not have `ql` anymore in it.

Old
  ```
  postgresql:
    postgresqlDatabase: <string>
    postgresqlUsername: <string>
    postgresqlPassword: <string>
    existingSecret: <string>
    persistence:
      enabled: <bool>
      size: <string>
      existingClaim: <string>
    replication:
      enabled: <bool>
      user: <string>
      password: <string>
      slaveReplicas: <int>
  ```
New
  ```
  postgresql:
    auth:
      username: <string>
      database: <string>
      postgresPassword: <string>
      existingSecret: <string>
      replicationUsername: <string>
      replicationPassword: <string>
    primary:
      persistence:
        enabled: <bool>
        size: <string>
        existingClaim: <string>
      readReplicas:
        enabled: <bool>
        replicaCount: <int>
  ```

## Upgrading to Renku 0.48.1

The handling of privacy policy and terms of service content has been fine tuned.

* DELETE `ui.client.privacy.page.configMapName` has been removed.
* DELETE `ui.client.privacy.page.configMapPolicyKey` has been removed.
* DELETE `ui.client.privacy.page.configMapTermsKey` has been removed.
* NEW ``ui.client.privacy.page.privacyPolicyContent`` to customize the content of the Privacy Policy page (supports Markdown).
* NEW ``ui.client.privacy.page.termsContent`` to customize the content of the Terms of Use page (supports Markdown).


## Upgrading to Renku 0.48.0

The handling of privacy policy and terms of service content has been slightly changed to make
it more flexible.

* DELETE `ui.privacy.enabled` has been removed to make the privacy policy and cookie banner configurable independently.
* NEW `ui.privacy.banner.enabled` allows turning on the cookie banner (defaults to false).
* DELETE `ui.client.privacy.page.configMapKey` which has been renamed to `ui.client.privacy.page.configMapPolicyKey`.
* NEW `ui.client.privacy.page.configMapPolicyKey` the key in the ConfigMap where the content for the privacy policy is located.
* NEW `ui.client.privacy.page.configMapTermsKey` the key in the ConfigMap where the content for the terms of use is located.


## Upgrading to Renku 0.47.0

We completely overhauled how mounting cloud storage in sessions works, relying on a new CSI driver based on RClone
which has to be installed in the cluster for things to work. Either install it as part of Renku using the flag
mentioned below or install the csi-rclone chart manually and set the correct storage class in the values for the
notebooks service.

* NEW `noteboks.cloudstorage.enabled` - set to `true` to enable mounting cloud storage in sessions.
* DELETE `notebooks.cloudstorage.s3.enabed` - superseeded by previous property.
* NEW `notebooks.cloudstorage.storageClass` - the storage class for the CSI Rclone chart, needed for new cloudstorage
  to work. The default `csi-rclone` should be fine unless already in use.
* NEW `global.csi-rclone.install` - if `true` installs the csi-rclone chart alongside Renku. The chart is needed for
  cloud storage in sessions to work.
* NEW `csi-rclone.storageClassName` - the storage class name the CSI drivers uses, should match what is configured in
  the `storageClass` property mentioned above.
* NEW `csi-rclone.csiNodePlugin.tolerations` - Tolerations for the node plugin part of the CSI driver. Need to be set
  in a way that allows it to be scheduled on user session nodes. By default this would mean `key=renku.io/dedicated`,
  `operator=Equal`, `value=user` and `effect=NoSchedule`


## Upgrading to Renku 0.43.0

* DELETE `graph.gitlab.url` has been removed as graph services uses the `global.gitlab.url`.

The encryption keys used by the Webhook service and Token Repository have been moved around and now they do not have to
be base64 encoded in the values file anymore. Although the Helm chart will take care of generating a new secret in the
correct form from the old one (for existing deployment being upgraded), we suggest to also store those secrets in the
value file.

To cleanup the old secrets, please remove the following fields:

* DELETE `graph.tokenRepository.tokenEncryption.secret`
* DELETE `graph.webhookService.hookToken.secret`

Then extract the content of the old secrets for the Token Repository service using `kubectl`:

```shell
kubectl -n renku get secrets renku-token-repository -o jsonpath="{.data['tokenRepository-tokenEncryption-secret']}" | base64 -d | base64 -d
```
And add it to the following new field in the value file:

* NEW `graph.tokenRepository.aesEncryptionKey`

Similarly extract the content of the old secret for the Webhook service:

```shell
kubectl -n renku get secrets renku-webhook-service -o jsonpath="{.data['webhookService-hookToken-secret']}" | base64 -d | base64 -d
```
And add it to the following new field in the value file:

* NEW `graph.webhookService.aesEncryptionKey`

## Upgrading to Renku 0.41.0

The UI includes a feature that allows projects to be displayed in the _Showcase_ section of the RenkuLab home page.
This is an improvement that has been planned for a while, and the _ui.homepage.projects_ field was introduced into the
values file with the intention of being used to configure this. Support for that field was never actually implemented, and
as part of the implementation of this feature, the configuration structure was changed somewhat.

To clean up, please remove the following field:

* DELETE - `ui.homepage.projects`

To keep the RenkuLab homepage as before, ensure that the following field/value has been added:

* NEW - `ui.homepage.showcase.enabled: false`

Follow the _Homepage_ section of the how-to guide for admins to learn how to configure this feature if you wish to
highlight showcase projects.

The Amalthea scheduler (which is not enabled by default) has changes in the values file under `amalthea.scheduler`.
Please note that either of the two new Amalthea schedulers remain disabled by default (just as before) and by default
Amalthea will simply use your default Kubernetes scheduler.

* DELETE `amalthea.scheduler.image` - deprecated will be ignored if provided
* DELETE `amalthea.scheduler.enable` - deprecated will be ignored if provided
* DELETE `amalthea.scheduler.priorities` - deprecated will be ignored if provided
* NEW `amalthea.scheduler.packing` - can be used to enable a preset scheduler that will try to pack sessions on the smallest number of nodes and favor the most used nodes
* NEW `amalthea.scheduler.custom` - can be used to add any custom scheduler for Amalthea, admins just have to provide the scheduler name
* EDIT `crc` - the field has been renamed to `dataService`, all child fields and functionality remains the same
* NEW `global.gitlab.url` has been added and needs to be specified, this will be the single place where the Gitlab URL will be specified in future releases we will deprecated all the other Gitlab URL fields in the values file.

## Upgrading to Renku 0.39.0

This is a big change to the Renku Helm chart. We have now combined all Renku components to be present
in a single Helm chart. This will allow us to gradually simplify the complicated and repetitive nature of
our `values.yaml` file. So now all Renku components have their templates in the `https://github.com/SwissDataScienceCenter/renku`
repository rather than having many separate helm charts for each component. This change does not
mean that the `requirements.yaml` file is not empty, but it does mean that it contains true third-party dependencies like
Redis, Postgres, Keycloak, Jena, Amalthea and Datashim (used to mount S3 buckets in sessions).

Furthermore, this change combines all relevant Renku configuration in a single `values.yaml` file with
all defaults for Renku components now located in a single file. Prior to this if someone had issues with
for example the `gateway` component they would have to go to the `gateway` helm chart to understand what
configuration is possible from its `values.yaml` file.

* EDIT - `graph.jena.*` moved to `jena.*`
* EDIT - `notebooks.amalthea.*` moved to `amalthea.*`
* EDIT - `notebooks.dlf-chart.*` moved to `dlf-chart.*`

In addition going forward we will follow a much stricter versioning scheme that will distinguish changes to
the Renku Helm chart as opposed to changes to the application. Notably:
- Patch changes (i.e. `0.50.1` -> `0.50.2`) indicate that there are NO changes in the Helm chart and that
only application level bug fixes are present in the new release.
- Minor version changes (i.e. `0.50.2` -> `0.51.0`) indicate that there are NO changes in the Helm chart and that
only application level new features and/or application level breaking changes are present.
- Major version changes (i.e. `0.50.0` -> `1.0.0`) will be reserved for changes in the Helm chart, either when the
values file changes or when the Helm templates change.

## Upgrading to Renku 0.37.0
* EDIT - `notebooks.culling.idleThresholdSeconds` in the notebooks' values file was renamed to
  `notebooks.culling.idleSecondsThreshold`. This needs to be acted upon once the corresponding
  [PR](https://github.com/SwissDataScienceCenter/renku/pull/3253) in Renku repository is merged.

## Upgrading to Renku 0.34.0
* NEW - *ingress.className* is now available to select a specific IngressClass to
  be used for the ingress. While often supporting both, current ingress
  controllers uses this value over the deprecated kubernetes.io/ingress.class
  annotation. Existing instances of Renku do not need to be changed unless their
  administrators wish to take advantage of the IngressClass.

For more information about the move to IngressClass see this [Kubernetes blog
entry](https://kubernetes.io/blog/2020/04/02/improvements-to-the-ingress-api-in-kubernetes-1.18/).

To make use of it:
  ```
  ingress:
    className: nginx

    # optional
    annotations:
      kubernetes.io/ingress.class: null
  ```

## Upgrading to Renku 0.29.0
* NEW - *global.graph.triplesGenerator.postgresPassword.value* should be specified. If it is not specified, a password will be generated automatically when the database is initialized. It is strongly recommended, however, to specify it here such that the password is explicitly managed and can be restored in disaster scenarios. Generate through `openssl rand -hex 32`.

## Upgrading to Renku 0.27.0
* EDIT - The keycloak chart has been replaced with keycloakx, if any values are specified under `keycloak`, now they
need to be under `keycloakx`.
NOTE: the values under `global.keycloak` remain unchanged.

Old
  ```
  keycloak:
    enabled: true
    resources:
      limits:
        cpu: 1000m
        memory: 1Gi
      requests:
        cpu: 1000m
        memory: 1Gi
  ```
New
  ```
  keycloakx:
    enabled: true
    resources:
      limits:
        cpu: 1000m
        memory: 1Gi
      requests:
        cpu: 1000m
        memory: 1Gi
  ```

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
