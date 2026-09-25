# Migration from Bitnami postgresql to CloudNativePG

The chart no longer deploys a database. You bring your own, and point
`global.externalServices.postgresql` at it. Any PostgreSQL works, we run and recommend
CloudNativePG, so that is what this describes moving the bundled bitnami data into.

Migrating somewhere else instead? Only step 1 and the CNPG import in step 2 are CNPG specific.
The dump and restore path works against any target, and the ordering in steps 3 to 5 is the same.

> [!IMPORTANT]
>
> Create the new database **before** upgrading the chart. The upgrade removes the bitnami
> StatefulSet, and with it the only copy of the old data if you have not moved it yet.

```bash
NS=renku
REL=renku
```

## 1. Create the cluster

The operator, the `Cluster` and the network policy it needs are documented under
[Requirements](https://docs.renkulab.io/en/latest/docs/admins/installation/requirements#postgresql).
Create them, but do not point renku at the new cluster yet, that happens in step 3.

## 2. Move the data

Two options, both with the old instance still running.

### Let CNPG import it

Add this to the Cluster **before creating it**, bootstrap only runs at creation. If it fails,
delete and recreate the cluster to retry.

```yaml
spec:
  bootstrap:
    initdb:
      import:
        # Monolith keeps databases, roles, ownership and privileges.
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
        # bitnami postgres serves plaintext by default here
        sslmode: prefer
      password:
        name: <release>-postgresql
        key: postgres-password
```

The cluster then also needs to reach the old instance:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: renku-pg-to-legacy
spec:
  podSelector:
    matchLabels:
      app.kubernetes.io/name: postgresql
      app.kubernetes.io/instance: <release>
  policyTypes:
    - Ingress
  ingress:
    - from:
        - podSelector: {matchLabels: {cnpg.io/cluster: renku-pg}}
      ports:
        - {protocol: TCP, port: 5432}
```

### Dump and restore by hand

> [!WARNING]
>
> **UNTESTED.** Try on a scratch namespace first.

Plan for downtime, the platform is down until the chart upgrade.

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

**Check the dumps before going on.** The restore happens after the upgrade, in step 4: the
databases and roles it writes into are created by the setup jobs, which only run during the upgrade.

## 3. Upgrade

Point renku at the CNPG generated secret directly. CNPG keeps it in `<cluster>-superuser` under the
key `password`.

```yaml
global:
  externalServices:
    postgresql:
      host: renku-pg-rw
      username: postgres
      existingSecret: renku-pg-superuser
      existingSecretPasswordKey: password
```

Drop the `postgresql` section from your values, then:

```bash
helm -n $NS upgrade $REL renku/renku -f my-values.yaml
```

The setup jobs have now created the databases and roles on the new cluster. If CNPG imported the
data for you, skip to step 5.

## 4. Restore (hand path only)

The upgrade started the services, so quiesce them again before writing underneath them.

```bash
kubectl -n $NS scale deploy --all --replicas=0
kubectl -n $NS scale statefulset $REL-keycloakx --replicas=0

PGPW=$(kubectl -n $NS get secret renku-pg-superuser -o jsonpath='{.data.password}' | base64 -d)
for db in renku authz keycloak; do
  kubectl -n $NS exec -i renku-pg-1 -- \
    bash -c "PGPASSWORD='$PGPW' psql -v ON_ERROR_STOP=1 -U postgres -h localhost -d $db" < $db.sql
done
```

`ON_ERROR_STOP=1` prevents `psql` from exiting 0 after skipping statements that failed.

Then scale back up:

```bash
helm -n $NS upgrade $REL renku/renku -f my-values.yaml
```

## 5. Verify and clean up

In order, check:
* the Cluster is healthy
* authz connects (i.e. the preserved spicedb password matches the restored role)
* keycloak starts and its realm is there
* you can log in and see the projects.

Only then drop the old volume. This is the last copy of the old state, and it goes with the claim
when the storage class reclaim policy is `Delete`.

```bash
kubectl -n $NS delete pvc data-$REL-postgresql-0
```

A run that dies partway leaves a broken state, both a half-restored database and a Solr
migration lock would block later deploys. Restart from a wiped cluster and Solr volume.
