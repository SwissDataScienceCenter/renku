# `values.yaml` changelog
All the changes should be listed here so the dev-ops team knows what to do when upgrading Renku.

Please follow this convention when adding a new row
* `<type: NEW|EDIT|DELETE> - *<resource name>*: <details>`

----

## 0.4.3
* EDIT - *global.gitlab.postgresPassword*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.

* EDIT - *global.gitlab.sudoToken*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.

* EDIT - *global.keycloak.postgresPassword*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.

* EDIT - *global.keycloak.password*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.

* EDIT - *global.jupyterhub.postgresPassword*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.

* EDIT - *global.graph.dbEventLog.postgresPassword*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.

* EDIT - *global.graph.tokenRepository.postgresPassword*: now split into `value` and `overwriteOnHelmUpgrade`, set the latter to true to generate a password or set value to use an existing one.

## 0.4.2
* EDIT - *notebooks.serverOptions*: added `<resource>.order` to control ui rendering order. The default values defined in notebooks are now different.
