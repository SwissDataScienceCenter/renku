# Upgrading PostgreSQL

Here we describe how the version of a PostgreSQL instance which is installed through the Renku chart can be upgraded. While the naming of resources in these instructions suggest that the PostgreSQL instance to be upgraded was deployed as a component of a RenkuLab deployment, there is nothing else that is Renku-specific about these instrcutions. Minor version upgrades are usually unproblematic and do not need any manual intervention. Major version upgrades on the other hand require some care. There are two recommended ways of performing this task:

1) Dump all the data in the old instance using `pd_dumpall`, start a fresh instance using the target version and import the previous data dump. You can find detailed instructions on this below.

2) Using the [pg_upgrade](https://www.postgresql.org/docs/devel/pgupgrade.html) command.

While `pg_upgrade` would achieve this task with much shorter downtime and if desired without copying any data, it seems a bit more complicated to use in a k8s context as one needs to run both versions (old and new postgresql) alongside in one container. We therefore describe option number 1) here which seems simple enough for databases of moderate size.

## Preparation

- Modify the example manifest files in this folder according to your needs (see the locations marked with `## EDIT` comments). You can use the followig command to figure out the total amount of data currently stored in your database. This will give you a fair estimate of the size of your data dump.

```bash
kubectl -n renku exec renku-postgresql-0 -- bash -c 'PGUSER=$POSTGRES_USER PGPASSWORD=$POSTGRES_PASSWORD psql -c "SELECT pg_database.datname as "database_name", pg_database_size(pg_database.datname)/1024/1024 AS size_in_mb FROM pg_database ORDER by size_in_mb DESC;"'
```

- If you're not using dynamic volume provisioning, create a new empty PV/PVC that will contain the data of your upgraded postresql instance.
- Remove the renku postgresql statefulSet.
- While we are not going to modify the data in your original data volume, you still might want to take this opportunity to store a snapshot of that volume.

## Create the volumes

- Execute `kubectl apply -n <your-namespace> -f volumes.yaml`. This will create a new PVC for your upgraded PostgreSQL instance and one to hold the temporary data. Also, it will set the right file system permissions on the newly created volume. Wait for the `change-permission` job to complete and delete the job through `kubectl delete job -n <your-namespace> change-permission`.

## Dump the data

- Verify that your existing PostgreSQL statefulSet and its pod have been deleted from kubernetes.
- Execute `kubectl apply -n <your-namespace> -f psql_dump.yaml` and wait until the `postgresql-dump-job` has completed. Remove the temporary resources using `kubectl delete -n <your-namespace> -f psql_dump.yaml`.

## Load the data

- Execute `kubectl apply -f psql_load.yaml` and wait for the `postgresql-load-job` to complete. This might take a while - for example 20 minutes when testing with a PostgreSQL instance holding 1.5 GB of data.
- Remove the temporary resources using `kubectl delete -n <your-namespace> -f psql_load.yaml`.

## Restart PostgreSQL

- Add or modify the `postgresql.persistence.existingClaim` entry in your values file (provide the name of the new PVC, so probably `data-renku-postgresql-v<postgresql-major-version>`).
- Bump the postresql version `postgresql.image.tag` in your values file to the target version and redeploy Renku through helm.

## Cleanup

- Delete the temporary volume which held the dump: `kubectl delete -n <your-namespace> data-renku-postgresql-tmp`. If the chosen storage class does not have the `Reclaim Policy` set to `Delete`, you want to also delete the PV.
- If everything works fine, you can delete your old postgres volume.
