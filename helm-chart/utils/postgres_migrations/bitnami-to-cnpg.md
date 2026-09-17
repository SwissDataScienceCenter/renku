# Manual Migration from Bitnami postgresql to CloudNativePG


> [!IMPORTANT]
> 
> Try `cnpg.autoMigration: true` first, together with `postgresql.enabled: true`. The new Cluster
> then imports every database and role as it initialises and none of this is needed. It only works
> while the Cluster is being *created*, so use this procedure when it already exists. The procedure
> below is the manual alternative.

> [!WARNING]
>
> **UNTESTED.** Try on a scratch namespace first.

Without `cnpg.autoMigration`, The upgrade creates an **empty** cnpg cluster: the setup jobs create
the databases and roles, the services create their schema, but the rows are not copied. Plan for
downtime, the platform is down from step 1 to step 4. The operator must already be installed.

> [!NOTE]
>
> It is recommended to make a volume snapshot beforehand to roll back safely.

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

## If it goes wrong

The old volume is untouched by all of the above: reinstall the previous chart version with the
original values file and the bitnami StatefulSet reattaches to it.

A run that dies partway leaves a broken state, both a half-restored database and a Solr
migration lock would block later deploys. Restart from a wiped cluster and Solr volume.
