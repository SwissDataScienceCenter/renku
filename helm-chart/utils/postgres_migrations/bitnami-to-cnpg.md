# Manual Migration from Bitnami postgresql to CloudNativePG


> [!IMPORTANT]
>
> The recommended route is to use CNPG. Either with the chart's built-in `cnpg.autoMigration: true`
> or with an out-of-band CNPG cluster you manage (See [Ouf of Band CNPG cluster](#out-of-band-cnpg-cluster)),
> together with `postgresql.enabled: true`. The new Cluster then imports every database and role 
> on creation and none of this is needed. The procedure below is the manual alternative.

> [!WARNING]
>
> **UNTESTED.** Try on a scratch namespace first.

Without `cnpg.autoMigration`, The upgrade creates an **empty** cnpg cluster: the setup jobs create
the databases and roles, the services create their schema, but the rows are not copied. Plan for
downtime, the platform is down from step 1 to step 4. The operator must already be installed.

```bash
NS=renku
REL=renku
```

## 1. Quiesce and dump

The old instance has to still be running, so keep `postgresql.enabled: true`.

```bash
kubectl -n $NS scale deploy --all --replicas=0
kubectl -n $NS scale statefulset $REL-keycloakx --replicas=0

for db in renku authz keycloak; do
  kubectl -n $NS exec $REL-postgresql-0 -- \
    bash -c "PGPASSWORD=\$POSTGRES_PASSWORD pg_dump -U postgres --clean --if-exists -d $db" \
    > $db.sql
done
```

Per database rather than `pg_dumpall`, because the roles already exist in the new cluster with the
same passwords. `--clean --if-exists` lets the restore overwrite the schema the services create.

**Check the dumps before going on.** Setting `postgresql.enabled: false` removes the StatefulSet.

## 2. Upgrade

Keep `postgresql.enabled: true`, leave `cnpg.autoMigration: false`, and set `cnpg.operatorNamespace`
if the operator does not run in `cnpg-system`.

```bash
helm -n $NS upgrade $REL renku/renku -f my-values.yaml
kubectl -n $NS get cluster $REL-pg -w     # until healthy
kubectl -n $NS get jobs
```

## 3. Restore

The upgrade restarted the services, so stop them again first.

```bash
kubectl -n $NS scale deploy --all --replicas=0
kubectl -n $NS scale statefulset $REL-keycloakx --replicas=0

PGPW=$(kubectl -n $NS get secret $REL-pg-superuser -o jsonpath='{.data.password}' | base64 -d)
for db in renku authz keycloak; do
  kubectl -n $NS exec -i $REL-pg-1 -- \
    bash -c "PGPASSWORD='$PGPW' psql -v ON_ERROR_STOP=1 -U postgres -h localhost -d $db" < $db.sql
done
```

`ON_ERROR_STOP=1` prevents `psql` from exiting 0 after skipping statements that failed.

## 4. Start up and verify

```bash
helm -n $NS upgrade $REL renku/renku -f my-values.yaml
```

In order:
* the Cluster is healthy
* authz connects (i.e. the preserved spicedb password matches the restored role)
* keycloak starts and its realm is there
* you can log in and see the projects.

## 5. Clean up

Only once verified. This is the last copy of the old state, and the volume goes with the claim when
the storage class reclaim policy is `Delete`.

```bash
kubectl -n $NS delete pvc data-$REL-postgresql-0
```

A run that dies partway leaves a broken state, both a half-restored database and a Solr
migration lock would block later deploys. Restart from a wiped cluster and Solr volume.

## Out of Band CNPG Cluster

You may manage your own CNPG cluster outside of the chart, in this namespace or elsewhere. Set
`cnpg.install: false` and point `global.externalServices.postgresql` at it. You may either
restore the cluster as above, or let it import the data itself on creation (See the last
part of this section).

The Cluster needs a superuser to let renku setup jobs create databases and roles.
CNPG defaults this off and deletes the secret.

```yaml
apiVersion: postgresql.cnpg.io/v1
kind: Cluster
metadata:
  name: mypg
spec:
  instances: 1
  enableSuperuserAccess: true
  storage:
    size: 8Gi
```

Your cluster needs its own network policy to be reachable by Renku.
Not needed for a cluster in another namespace, or outside kubernetes.

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: mypg-ingress
spec:
  podSelector:
    matchLabels:
      cnpg.io/cluster: mypg
  policyTypes:
    - Ingress
  ingress:
    - from:
        - podSelector: {matchLabels: {app: renku-data-service}}
        - podSelector: {matchLabels: {app: renku-data-tasks}}
        - podSelector: {matchLabels: {app: renku-k8s-watcher}}
        - podSelector: {matchLabels: {app: renku-authz}}
        - podSelector: {matchLabels: {app: postgres-setup}}
        - podSelector: {matchLabels: {app: post-install-postgres}}
        - podSelector: {matchLabels: {app: keycloak-sync}}
        - podSelector: {matchLabels: {app.kubernetes.io/name: keycloakx}}
        - podSelector: {matchLabels: {cnpg.io/cluster: mypg}}
      ports:
        - {protocol: TCP, port: 5432}
    # the operator polls each instance on 8000 or the Cluster never reports ready
    - from:
        - namespaceSelector:
            matchLabels:
              kubernetes.io/metadata.name: cnpg-system
          podSelector:
            matchLabels:
              app.kubernetes.io/name: cloudnative-pg
      ports:
        - {protocol: TCP, port: 5432}
        - {protocol: TCP, port: 8000}
```

Instead of the dump and restore above, you can rely on CNPG built-in import mechanism.
If it fails, delete / recreate the cluster to re-trigger. Add this to the cluster:

```yaml
spec:
  bootstrap:
    initdb:
      import:
        type: monolith
        databases: ["*"]
        roles: ["*"]
        source:
          externalCluster: legacy-bitnami
  externalClusters:
    - name: legacy-bitnami
      connectionParameters:
        host: <release>-postgresql
        user: postgres
        dbname: postgres
        sslmode: prefer
      password:
        name: <release>-postgresql
        key: postgres-password
```

Your cluster then also needs its own ingress policy in to the old instance:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: mypg-to-legacy
spec:
  podSelector:
    matchLabels:
      app.kubernetes.io/name: postgresql
      app.kubernetes.io/instance: <release>
  policyTypes:
    - Ingress
  ingress:
    - from:
        - podSelector: {matchLabels: {cnpg.io/cluster: mypg}}
      ports:
        - {protocol: TCP, port: 5432}
```

Order matters:
1. Create policies
2. Create the Cluster
3. Upgrade the chart

Point Renku at the CNPG generated secret directly.
Cnpg keeps it in `<cluster>-superuser` under the key `password`.

```yaml
cnpg:
  install: false
global:
  externalServices:
    postgresql:
      enabled: true
      host: mypg-rw
      username: postgres
      existingSecret: mypg-superuser
      existingSecretPasswordKey: password
```

