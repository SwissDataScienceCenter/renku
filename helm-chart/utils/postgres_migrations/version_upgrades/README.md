# Upgrading postgresql

Here we describe how the version of a PostgreSQL instance which is installed through the Renku chart can be upgraded. While minor version upgrades are unproblematic and do not need any manual intervention, major version upgrades require some care. There are two recommended ways of performing this task:

1) Dump all the data in the old instance using `pd_dumpall`, start a fresh instance using the target version and import the previous data dump. You can find detailed instructions on this below.

2) Using the [pg_upgrade](https://www.postgresql.org/docs/devel/pgupgrade.html) command.

While `pg_upgrade` would achieve this task with much shorter downtime and if desired without copying any data, it seems a bit more complicated to use in a k8s context as one needs to run both versions (old and new postgresql) alongside in one container. We therefore describe option number 1) here which seems simple enough for databases of moderate size.

## Preparation

- Modify the example manifest files in this folder according to your needs. You can use the followig command to figure out the total amount of data currently stored in your database. This will give you a fair estimate of the size of your data dump.

```bash
kubectl -n renku exec renku-postgresql-0 -- bash -c 'PGUSER=$POSTGRES_USER PGPASSWORD=$POSTGRES_PASSWORD psql -c "SELECT pg_database.datname as "database_name", pg_database_size(pg_database.datname)/1024/1024 AS size_in_mb FROM pg_database ORDER by size_in_mb DESC;"'
```

- If you're not using dynamic volume provisioning, create a new empty PV/PVC that will contain the data of your upgraded postresql instance.
- Remove the renku postgresql statefulSet. You might also want to scale down the services which try to connect to the postgresql instance, but it is not strictly required.
- While we are not going to modify the data in your original data volume, you still might want to take this opportunity to store a snapshot of that volume.

## Dumping the data

- Execute `kubectl apply -f psql_dump.yaml`, wait for the `postgresql-dump` pod to become available.
- Execute the following command inside the container:
`PGUSER=$POSTGRES_USER PGPASSWORD=$POSTGRES_PASSWORD pg_dumpall -f /psql-dump-data/dump`
- Once this command has completed, delete the `postgresql-dump` pod.

## Loading the data

- Execute `kubectl apply -f psql_load.yaml`, wait for the `postgresql-load` pod to become available.
- Execute the following commands inside the container:

```bash
PGUSER=$POSTGRES_USER PGPASSWORD=$POSTGRES_PASSWORD psql -f /psql-dump-data/dump postgres
chown -R 1001:1001 /bitnami/postgresql/data
```

- Once these commands have completed, delete the `postgresql-load` pod.
- Add or modify the `postgresql.persistence.existingClaim` entry in your values file (provide the name of the new PVC here.)
- Bump the postresql version `postgresql.image.tag` in your values file to `11` and
redeploy Renku through helm.

## Cleanup

- Delete the temporary volume which held the dump: `kubectl delete -n <your-namespace> data-renku-postgresql-tmp`. If the chosen storage class does not have the `Reclaim Policy` set to `Delete`, you want to also delete the PV.
- If everything works fine, you can delete your old postgres volume.
