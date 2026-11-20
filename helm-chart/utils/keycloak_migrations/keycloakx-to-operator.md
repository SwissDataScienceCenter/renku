# Migration from the keycloakx chart to the Keycloak Operator

> [!IMPORTANT]
> Back up the Keycloak database first. 

> [!IMPORTANT]
> Install an operator no older than the running Keycloak. Downgrading Keycloak is not supported.
> Read the running version with:
> `kubectl -n $NS get sts $REL-keycloakx -o jsonpath='{.spec.template.spec.containers[0].image}'`

Keycloak stores its data in Postgres, this migration only affects the pods deployment. Plan for
an outage during the migration.

```bash
export NS=renku          # your renku namespace
export REL=renku         # your release name
```

### 1. Install the operator

Follow [the operator cluster-wide installation guide](https://www.keycloak.org/operator/installation), minding the
version constraint above. Check it runs before upgrading.

```bash
kubectl get crd keycloaks.k8s.keycloak.org
kubectl -n keycloak get deploy keycloak-operator    # adjust to where you installed it
```

### 2. Check the database role, and save the passwords

If `KC_DB_USERNAME` does not match `global.keycloak.postgresUser` in your values, stop: the upgrade
would point Keycloak at a role with no access to the existing tables.

```bash
kubectl -n $NS get secret renku-keycloak-postgres -o jsonpath='{.data.KC_DB_USERNAME}' | base64 -d
kubectl -n $NS get secret renku-keycloak-postgres  -o jsonpath='{.data.KC_DB_PASSWORD}' > kc-db-pw
kubectl -n $NS get secret keycloak-password-secret -o jsonpath='{.data.KEYCLOAK_ADMIN_PASSWORD}' > kc-admin-pw
```

### 3. Back up the Keycloak database

For the bundled CloudNativePG instance:

```bash
kubectl -n $NS exec $REL-pg-1 -- pg_dump -U postgres keycloak > keycloak-backup.sql
```

Use your own procedure for an external one.

### 4. Update your values

Delete the whole `keycloakx` values section. See the [values changelog](../../values.yaml.changelog.md) for the settings that move to
`keycloak`.

### 5. Upgrade

```bash
helm upgrade --install $REL renku/renku -n $NS -f values.yaml --wait --timeout 20m
```

### 6. Verify

```bash
# passwords preserved, both must be silent
kubectl -n $NS get secret renku-keycloak-postgres  -o jsonpath='{.data.KC_DB_PASSWORD}'       | diff kc-db-pw -
kubectl -n $NS get secret keycloak-password-secret -o jsonpath='{.data.KEYCLOAK_ADMIN_PASSWORD}' | diff kc-admin-pw -

# no database authentication errors, schema migration completed
kubectl -n $NS logs statefulset/$REL-keycloak
```

Passwords should be carried over from existing secrets.
If they changed (e.g. offline render failing to lookup the secret), the Postgres role and the Keycloak admin still contain the old one.
You can then patch the saved value back in:

```bash
kubectl -n $NS patch secret renku-keycloak-postgres -p "{\"data\":{\"KC_DB_PASSWORD\":\"$(cat kc-db-pw)\"}}"
kubectl -n $NS rollout restart statefulset/$REL-keycloak
```

Delete `kc-db-pw` and `kc-admin-pw` afterwards, they hold live credentials.

Then open the login page in a browser. Check it uses the Renku theme, log in, and make an API
call on that session.

### Rollback

In case of failure, restore the database backup, then redeploy the previous chart version.
Reverting the chart alone is not enough once the schema has migrated.
