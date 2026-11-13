# Migration from Bitnami postgresql to CloudNativePG

> [!IMPORTANT]
> You MUST create a full, fresh backup of your database before you start the migration.

> [!IMPORTANT]
>
> The recommended route is to use the cloud-native PostgreSQL operator (CNPG). Either with the chart's built-in `cnpg.autoMigration: true`
> or with an out-of-band CNPG cluster you manage (See [Ouf of Band CNPG cluster](#cnpg-cluster-creation)),
> together with `postgresql.enabled: true`. The new Cluster can then import every database and role 
> on creation and none of this is needed. The procedure below is the manual alternative, for a CNPG 
> cluster or any external postgres instance.

> [!NOTE]
>
> For large databases, prefer an out-of-band cluster. With `cnpg.autoMigration` the setup jobs start
> before the Cluster and give up after 10 minutes, which has to cover the whole import.

## CNPG cluster creation

You may manage your own CNPG cluster outside of the chart, in this namespace or elsewhere. Set
`cnpg.install: false` and point `global.externalServices.postgresql` at it. You may either
restore the cluster as above, or let it import the data itself on creation (See the last
part of this section).

Order matters:
1. Create policies
2. Create the Cluster
3. Upgrade the chart

### 1. Network policy

Your cluster needs its own network policy to be reachable by Renku.
Not needed for a cluster in another namespace, or outside kubernetes.

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: mypg-ingress
  namespace: renku
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

### 2. Cluster creation

The Cluster needs a superuser to let renku setup jobs create databases and roles.
CNPG defaults this off and deletes the secret.

```yaml
apiVersion: postgresql.cnpg.io/v1
kind: Cluster
metadata:
  name: mypg
  namespace: renku
spec:
  instances: 1
  enableSuperuserAccess: true
  storage:
    size: 8Gi
```

## Automatic data migration

You can rely on CNPG built-in import mechanism.
If it fails, delete / recreate the cluster to re-trigger.

The import copies the data once, when the Cluster is created. Anything written to the old instance
after that is lost, so scale the services down first (as in step 1 of the manual migration).

### 1. Cluster specification

Create your cluster with this additional block:

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
  namespace: renku
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

### 2. Chart upgrade

Edit your values to point Renku at the CNPG generated secret directly.
CNPG keeps it in `<cluster>-superuser` under the key `password`. Keep the old instance until you
have verified the migration, then remove it.

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

Then upgrade the chart to apply values:

```sh
helm -n renku upgrade $HELM_RELEASE renku/renku -f my-values.yaml
```

## Manual migration

This works with any PostgreSQL. You dump the old databases, restore into a new one.
Only then, point Renku at the new instance. Plan for downtime, the platform is down from step 1 to step 5.

```bash
# The new cnpg cluster must already exist and be empty.
set -euo pipefail

NS=renku
REL=renku
OLD_PG=$REL-postgresql-0
NEW_CLUSTER=renku-pg  # Name of cnpg cluster
VALUES=my-values.yaml
```

`NS` refers to the Kubernetes namespace where Renku is installed. `REL` refers to the name of the
Renku Helm release as you have installed it on your cluster.

### 1. Dump

. Stop Renku and dump the whole old instance (users, owners, grants, data)

```bash
kubectl -n $NS scale deploy --all --replicas=0
kubectl -n $NS scale statefulset $REL-keycloakx --replicas=0
kubectl -n $NS exec $OLD_PG -- \
  bash -c 'PGPASSWORD=$POSTGRES_PASSWORD pg_dumpall -c --if-exists -U postgres' > dumpall.sql
```

### 2. Fix dump

Leave the new instance's own superuser (and its password) untouched

```bash
sed -E -i.bak \
  -e '/^(CREATE|ALTER) ROLE postgres( |;)/d' \
  -e '/DROP ROLE IF EXISTS postgres/d'  dumpall.sql
```

### 3. Restore

Restore into the new instance

`ON_ERROR_STOP=1` prevents `psql` from exiting 0 after skipping statements that failed.

```bash
NEW_PG=$(kubectl -n $NS get pod -l cnpg.io/cluster=$NEW_CLUSTER,cnpg.io/instanceRole=primary -o name)
kubectl -n $NS exec -i $NEW_PG -c postgres -- psql -v ON_ERROR_STOP=1 -U postgres < dumpall.sql
```

### 4. Repair SpiceDB

Move the new server's transaction counter past the restored SpiceDB data

```bash
AUTHZ_PW=$(kubectl -n $NS get secret $REL-authz -o jsonpath='{.data.SPICEDB_POSTGRES_PASSWORD}' | base64 -d)
AUTHZ_IMG=$(kubectl -n $NS get deploy $REL-authz -o jsonpath='{.spec.template.spec.containers[0].image}')
kubectl -n $NS run spicedb-repair --rm -i --restart=Never --labels=app=renku-authz --image="$AUTHZ_IMG" -- \
  datastore repair transaction-ids --datastore-engine=postgres \
  --datastore-conn-uri="postgres://authz:$AUTHZ_PW@$NEW_CLUSTER-rw:5432/authz"
```

### 5. Upgrade

Update your values, keeping `postgresql.enabled: true`, and set `cnpg.install: false`.
Point the renku chart values at the new instance:

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

Apply new values to the chart:

```bash
helm -n $NS upgrade $REL renku/renku -f $VALUES
```


Then scale back up:

```bash
helm -n $NS upgrade $REL renku/renku -f my-values.yaml
```

### 6. Verify

In order, check that:
* authz connects (i.e. the preserved spicedb password matches the restored role)
* keycloak starts and its realm is there
* you can log in and see the projects.

### 7. Clean up

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

