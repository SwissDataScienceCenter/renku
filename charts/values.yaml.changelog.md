# `values.yaml` changelog
All the changes should be listed here so the dev-ops team knows what to do when upgrading Renku.

Please follow this convention when adding a new row
* `<type: NEW|EDIT|DELETE> - *<resource name>*: <details>`

----

## Changes on top of Renku 0.4.2
* EDIT - *notebooks.serverOptions*: added `<resource>.order` to control ui rendering order. The default values defined in notebooks are now different.
* DELETE - *graph.knowledgeGraph.services.renku.url*: no need for this anymore as it's linked to `global.renku.domain` internally. Ref: [refactor KG remove services renku URL](https://github.com/SwissDataScienceCenter/renku/commit/6d4a0e5cf02833d193f86a38cc14762609fcd9c0)
* DELETE - *keycloak.keycloak.extraVolumes.-name:realm-secret* : need to delete `renku-realm.json` in order to work with [remove realm template](https://github.com/SwissDataScienceCenter/renku/commit/825c10e72d185bfaff78af8c7693d06cd745014c) commit
