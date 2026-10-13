# Migration from Bitnami postgresql to CloudNativePG

> [!IMPORTANT]
> You MUST create a full, fresh backup of your database before you start the migration.

> [!IMPORTANT]
>
> The recommended route is to use the cloud-native PostgreSQL operator (CNPG). Either with the chart's built-in `cnpg.autoMigration: true`
> or with an out-of-band CNPG cluster you manage (See [Ouf of Band CNPG cluster](#out-of-band-cnpg-cluster)),
> together with `postgresql.enabled: true`. The new Cluster can then import every database and role 
> on creation and none of this is needed. The procedure below is the manual alternative, for a CNPG 
> cluster or any external postgres instance.

> [!NOTE]
>
> For large databases, prefer an out-of-band cluster. With `cnpg.autoMigration` the setup jobs start
> before the Cluster and give up after 10 minutes, which has to cover the whole import.

## Manual migration

This works with any PostgreSQL. You dump the old databases, point Renku at the new instance (the
upgrade's setup jobs create the databases and roles there, empty), then restore the dumps into
them. Plan for downtime, the platform is down from step 1 to step 4.

```bash
export NS=renku
export REL=renku
# the new instance and its superuser
export PGHOST=postgres.example.org
export PGUSER=postgres
export PGPASSWORD=<superuser-password>
```

`NS` refers to the Kubernetes namespace where Renku is installed. `REL` refers to the name of the
Renku Helm release as you have installed it on your cluster.

### 1. Quiesce and dump

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

Per database rather than `pg_dumpall`: the setup jobs create the roles on the new instance in step 3,
with the same passwords. `--clean --if-exists` lets the restore overwrite the schema the services
create.

**Check the dumps before going on.** If you changed `global.keycloak.postgresDatabase`, replace
`keycloak` in the `for db in ...` loops (here and in step 4) with its value.

### 2. Deploy postgres

Deploy the new instance using your preferred method. Renku needs a superuser on it, since the setup
jobs create their own databases and roles, and it must be reachable from the Renku namespace on
port 5432.

### 3. Upgrade

Keep `postgresql.enabled: true`, set `cnpg.install: false`, and point the renku chart values at the
new instance:

```yaml
cnpg:
  install: false
postgresql:
  enabled: true
global:
  externalServices:
    postgresql:
      enabled: true
      host: postgres.example.org
      username: postgres
      # either an inline password, or a secret holding it, not both
      password: <superuser-password>
      # existingSecret: <secret-name>
      # existingSecretPasswordKey: <key-in-that-secret>
```

```bash
helm -n $NS upgrade $REL renku/renku -f my-values.yaml
```

The setup jobs create the databases and roles on the new instance, and the services start on empty
schemas.

### 4. Restore

The upgrade restarted the services, so stop them again first. The old pod still runs and has a
`psql` matching the dumps, so it serves as the client. The new instance must accept connections
from it, otherwise run the same `psql` from any client that can reach it.

```bash
kubectl -n $NS scale deploy --all --replicas=0
kubectl -n $NS scale statefulset $REL-keycloakx --replicas=0

for db in renku authz keycloak; do
  kubectl -n $NS exec -i $REL-postgresql-0 -- \
    env PGPASSWORD="$PGPASSWORD" psql -v ON_ERROR_STOP=1 -h "$PGHOST" -U "$PGUSER" -d $db < $db.sql
done
```

`ON_ERROR_STOP=1` prevents `psql` from exiting 0 after skipping statements that failed.

Then scale back up:

```bash
helm -n $NS upgrade $REL renku/renku -f my-values.yaml
```

### 5. Verify

In order, check that:
* authz connects (i.e. the preserved spicedb password matches the restored role)
* keycloak starts and its realm is there
* you can log in and see the projects.

### 6. Clean up

Only once verified. Set `postgresql.enabled: false` and upgrade. This removes the bitnami
StatefulSet and the `$REL-postgresql` secret holding its password.

```bash
helm -n $NS upgrade $REL renku/renku -f my-values.yaml
```

The volume is the last copy of the old state, and it goes with the claim when the storage class
reclaim policy is `Delete`.

```bash
kubectl -n $NS delete pvc data-$REL-postgresql-0
```

A run that dies partway leaves a half-restored database, which blocks later deploys. Restart
from a wiped cluster.

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
        - podSelector: {matchLabels: {app: renku-secrets-storage}}
        - podSelector: {matchLabels: {app: postgres-setup}}
        - podSelector: {matchLabels: {app.kubernetes.io/name: keycloakx}}
        - podSelector: {matchLabels: {cnpg.io/cluster: mypg}}
        # only for the manual restore, which runs psql from the old bitnami pod
        - podSelector: {matchLabels: {app.kubernetes.io/name: postgresql}}
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

The old instance then also needs a policy letting your cluster in:

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

The import copies the data once, when the Cluster is created. Anything written to the old instance
after that is lost, so scale the services down first (as in step 1 of the manual migration).

Order matters:
1. Create policies
2. Create the Cluster
3. Upgrade the chart

Point Renku at the CNPG generated secret directly.
CNPG keeps it in `<cluster>-superuser` under the key `password`. Keep the old instance until you
have verified the migration, then remove it as in step 6 of the manual migration.

```yaml
cnpg:
  install: false
postgresql:
  enabled: true
global:
  externalServices:
    postgresql:
      enabled: true
      host: mypg-rw
      username: postgres
      existingSecret: mypg-superuser
      existingSecretPasswordKey: password
```

