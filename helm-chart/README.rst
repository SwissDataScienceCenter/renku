Helm Charts for Deploying RENKU on Kubernetes
=============================================

Helm 2.9.1 or later (including Helm 3) is necessary as we use
the :code:`before-hook-creation` hook deletion policy. See also:
`before-hook-creation delete policy <https://github.com/kubernetes/helm/commit/1d4883bf3c85ea43ed071dff4e02cc47bb66f44f>`_.


Deploying from a Helm repository
--------------------------------

Create a values file using ``renku/values.yaml`` as a template. Then run:

.. code-block:: console

    $ helm init
    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts/
    $ helm upgrade --install renku/renku \
        --namespace renku \
        -f my-values.yaml

See the `helm chart registry <https://swissdatasciencecenter.github.io/helm-charts/>`_ for
available versions.


Testing locally
---------------
Checkout our `developer docs <https://renku.readthedocs.io/en/latest/developer/setup.html>`_
for a detailed description of how to deploy a local version using minikube.


Building images
---------------

If you want to build the Renku images required by the chart locally
(``singleuser``, ``jupyterhub-k8s``, ``tests``, ``notebooks``),
you can do so by using ``chartpress``.

.. code-block:: console

    $ pip install chartpress
    $ chartpress --tag latest

You can the use the same ``helm upgrade`` command as above to redeploy the
services using the new images. If you ommit the ``--tag latest``,
``chartpress`` will tag the images with the current commit sha and update the
relevant values in the charts.


Tests
-----

To run tests on the deployment, use

.. code-block:: console

    $ helm test --cleanup renku


Upgrading
---------
Most information related to upgrading from one chart version to another is covered
in the `values changelog file <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/values.yaml.changelog.md>`_.
For upgrades that require some steps other than modifying the values files to be
executed, we add some instructions here.

Upgrading to 0.9.0
******************
Our chart requirements changed to include the GitLab cloud native charts.

Reference: https://docs.gitlab.com/charts/installation/migration/package_to_helm.html


1. Move LFS, registry storage, artifacts and uploads to an S3 object store
++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

**Note** If your Renku deployment uses an external GitLab you can skip this step.

Reference: https://docs.gitlab.com/ee/administration/uploads.html#s3-compatible-connection-settings

A. Configure your values file and redeploy.

For configuring S3 for LFS objects and registry storage, please refer to an earlier version of Renku values file, ``LFS`` and ``registry`` gitlab sections respectively.
For artifacts and uploads you can add the configuration under ``extraConfig`` gitlab section in your Renku values file, as the example below.

.. code-block:: console

extraConfig: |
  gitlab_rails['uploads_object_store_enabled'] = true
  gitlab_rails['uploads_object_store_remote_directory'] = "renku-gitlab-uploads" ## Bucket name
  gitlab_rails['uploads_object_store_connection'] = {
    'provider' => 'AWS',
    'region' => 'ZH',
    'aws_access_key_id' => 'xyz',
    'aws_secret_access_key' => 'xyz',
    'endpoint' => 'https://os.zhdk.cloud.switch.ch',
    'aws_signature_version' => 2,
    'path_style' => true,
  }
  gitlab_rails['artifacts_enabled'] = true
  gitlab_rails['artifacts_object_store_enabled'] = true
  gitlab_rails['artifacts_object_store_remote_directory'] = "renku-gitlab-artifacts" ## Bucket name
  gitlab_rails['artifacts_object_store_connection'] = {
    'provider' => 'AWS',
    'region' => 'ZH',
    'aws_access_key_id' => 'xyz',
    'aws_secret_access_key' => 'xyz',
    'endpoint' => 'https://os.zhdk.cloud.switch.ch',
    'aws_signature_version' => 2,
    'path_style' => true,
  }

B. Migrate local files to object store

We use gitlab-rake tasks to migrate existing local files to object store. You can execute the following instructions if you use ``kubectl exec -ti <gitlab-pod-name> -n renku bash``.

**Note** This might take some time depending on how much data your deployment has, however during the migration the service will be up and users can work seamlessly.

.. code-block:: console

  gitlab-rake gitlab:uploads:migrate:all
  gitlab-rake gitlab:artifacts:migrate
  gitlab-rake "gitlab:packages:migrate"
  gitlab-rake gitlab:lfs:migrate


To verify that all the objects are on object store and not stored locally you can open a shell on the PostgreSQL pod.
You will need the postgres user password at hand, you can find it in the ``renku-postgresql`` secret.

``psql -U postgres -d gitlabhq_production``

.. code-block:: console

  gitlabhq_production=# SELECT count(*) AS total, sum(case when store = '1' then 1 else 0 end) AS filesystem, sum(case when store = '2' then 1 else 0 end) AS objectstg FROM uploads;
  gitlabhq_production=# SELECT count(*) AS total, sum(case when file_store = '1' then 1 else 0 end) AS filesystem, sum(case when file_store = '2' then 1 else 0 end) AS objectstg FROM ci_job_artifacts;
  gitlabhq_production=# SELECT count(*) AS total, sum(case when file_store = '1' then 1 else 0 end) AS filesystem, sum(case when file_store = '2' then 1 else 0 end) AS objectstg FROM lfs_objects;


You should see an output similar to this:

  total | filesystem | objectstg
  ------+------------+-----------
  2409 |          0 |      2409


2. Create a snapshot
++++++++++++++++++++++

Once artifacts, lfs and uploads have been migrated to object store you can create a tarball backup.
In order to reduce the service down time you can prepare steps 3 and 4 before creating the snapshot.

.. code-block:: console

   gitlab-rake gitlab:backup:create SKIP=artifacts,lfs,uploads

You might need to increase gitlab user's rights just for this and the following step.
The easiest is to make the gitlab user a super user temporarily.

```
   ALTER USER gitlab WITH SUPERUSER;
```

3. Create GitLab-related secrets needed for new charts
++++++++++++++++++++++++++++++++++++++++++++++++++++++

Reference: https://docs.gitlab.com/charts/backup-restore/restore.html

Now we create the necessary secret for rails.
Create a file with the below content and fill it with the secrets in ``/etc/gitlab/gitlab-secrets.json``.

.. code-block:: console

  production:
    db_key_base: <your key base value>
    secret_key_base: <your secret key base value>
    otp_key_base: <your otp key base value>
    openid_connect_signing_key: <your openid signing key>
    ci_jwt_signing_key: <your ci jwt signing key>

``` kubectl create secret generic renku-rails-secret-name --from-file=secrets.yml=<local-yaml-filepath> -n renku```

4. Prepare the new Renku values file
+++++++++++++++++++++++++++++++++++++

You need to make the following changes to your renku values file:

GitLab is now hosted in a subdomain rather than the `/gitlab` prefix.
```
gateway.gitlabUrl = https://gitlab.<your-renku-dns>
graph.gitlab.url = https://gitlab.<your-renku-dns>
global.gitlab.urlPrefix = /
```

The GitLab clientSecret now needs to be
```
global.keycloak.gitlabClientSecret = <old-global.gitlab.clientSecret>
global.gitlab.appSecret = <old-global.gitlab.clientSecret>
```

```
gateway.gitlabClientId = renku
notebooks.jupyterhub.auth.gitlab.clientId = renku
gateway.gitlabClientSecret =
```

5. Deploy the new chart including GitLab
++++++++++++++++++++++++++++++++++++++++

6. Restore GitLab's data from the backup
++++++++++++++++++++++++++++++++++++++++

7. Verify all is in order
++++++++++++++++++++++++++

Upgrading to 0.8.4
******************
We have added add a new section called `serverDefaults` to the `values.yaml` for the notebook service.
The information in this new `serverDefaults` section is used for any server options that are not specified
explicitly when launching a session. This allows a renku admin to leave out a specific option from the
`serverOptions` section and apply the value specified in the `serverDefaults` section for all sessions.
Please note that the default values specified in the  `serverDefaults` should be available as one of the options
in `serverOptions` - if the specific option appears in both sections. The defaults in the `serverOptions`
section now only refer to the default selection that is shown to the user in the UI.

This ability to use persistent volumes for user sesssions is also introduced with this release. This is optional and can be enabled in the values
file for the helm chart. In addition to enabling this feature users have the ability to select the storage class used by the persistent
volumes. We strongly recommend that a storage class with a `Delete` reclaim policy is used, otherwise persistent volumes from all user
sessions will keep accumulating.

Lastly, unlike previous versions, with 0.8.4 the amount of disk storage will be **strongly enforced**,
regardless of whether persistent volumes are used or not. With persistent volumes users will simply run out of space. However,
when persistent volumes are not used, going over the amount of storage that a user has requested when starting their session
will result in eviction of the k8s pod that runs the session and termination of the session. Therefore, admins are advised
to review and set proper options for disk sizes in the `notebooks.serverOptions` portion of the values file.


Upgrading to 0.8.0
******************
We bump the PostgreSQL version from 9.6 to 11 and the GitLab major version from 11 to 13.
It is important to first perform the PostgreSQL upgrade, then upgrade to the ``0.8.0`` chart version
while keeping the GitLab version fixed, and finally upgrade the GitLab version.

1. Upgrading postgresql
+++++++++++++++++++++++
If PostgreSQL was deployed as part of Renku, please follow [these instructions](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/utils/postgres_migrations/version_upgrades/README.md)
for the postgresql upgrade.

2. Bump the chart version
+++++++++++++++++++++++++
Now it's time to upgrade to the ``0.8.0`` version of the Renku chart. Before doing this, make sure
to pin the GitLab version by setting ``gitlab.image.tag`` in your values file. If you had not pinned
this version explicitly before, pin it to ``11.9.11-ce.0`` which is the default version set in the Renku
chart prior to the upgrade. Otherwise you can leave it at the previously pinned version. Then deploy the
new chart version through ``helm upgrade ... --version 0.8.0 ...``.

3. Upgrade GitLab
+++++++++++++++++
Please read the `GitLab documentation on this topic <https://docs.gitlab.com/ce/update>`_ before proceeding.
Following the `recommended upgrade paths <https://docs.gitlab.com/ce/update/#upgrade-paths>`_) and assuming
your GitLab instance is at version ``11.9.11``, this means that your upgrade path will be
``11.11.8 -> 12.0.12 -> 12.1.17 -> 12.10.14 -> 13.0.14 -> 13.1.11 -> 13.10.4``. The corresponding
image tags are:

- 11.11.8-ce.0
- 12.0.12-ce.0
- 12.1.17-ce.0
- 12.10.14-ce.0
- 13.0.14-ce.0
- 13.1.11-ce.0
- 13.10.4-ce.0 (default in the Renku ``0.8.0`` helm chart)

For each step, set the corresponding tag in your values file at ``gitlab.image.tag``, redeploy through
helm and wait for the gitlab pod to be recreated and all migrations to finish. Repeat this procedure until
you've reached the target version of this upgrade ``13.10.4-ce.0``. Note that this version does not have
to be selected explicitly in your own values file as it is the default of the ``0.8.0`` renku chart.

Upgrading to 0.7.8
******************
This upgrade comes with an upgrade of the keycloak chart from ``4.10.2`` to ``9.8.1``! For
details on this upgrade check the dedicated section in the
`the keycloak chart docs <https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#upgrading>`_
and the `keycloak docs <https://www.keycloak.org/docs/latest/upgrading/>`_.

- Before starting, make sure to check out `the values changelog for this upgrade <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/values.yaml.changelog.md#upgrading-to-renku-080-includes-breaking-changes>`_
  and update your values file accordingly.

- The upgrade of keycloak will perform an **irreversible database migration**. It is therefore recommended
  to **back up your postgres volume** before performing this upgrade.

- **Warning: Persist keycloak-related secrets!**

  If ``global.keycloak.postgresPassword.value`` and ``global.keycloak.password.value``
  have not been explicitly defined in the values file (and thus have been autocreated by helm),
  add them to the values file now.

  * Get the ``keycloak-postgres-password`` from the ``renku-keycloak-postgres`` secret and add it as ``global.keycloak.postgresPassword.value``.
  * Get the ``keycloak-password`` from the ``keycloak-password-secret`` and add it as ``global.keycloak.password.value``.

  This should result in something like
.. code-block:: bash

    global:
      keycloack:
        postgresPassword:
          value: <actual-keycloak-postgres-password>
        password:
          value: <actual-keycloak-admin-password>


- Delete the two secrets which need to be recreated as well as the keycloak StatefulSet:

.. code-block:: bash

    kubectl delete secrets -n <namespace> keycloak-password-secret renku-keycloak-postgres
    KEYCLOAK_NAME=`kubectl get statefulsets.apps -n <namespace> -l app=keycloak --no-headers=true -o custom-columns=":metadata.name"`
    kubectl delete statefulsets.apps -n <namespace> $KEYCLOAK_NAME

- Perform the appropriate ``helm upgrade`` command to use the new chart version and your modified values file.

- If you should find yourself in the place where you have to rollback these changes, a simple ``helm rollback``
  will unfortunately not work. Instead, recover the postgres volume from your backup, remove both secrets mentioned
  above and the keycloak StatefulSet, make sure ``global.keycloak.postgresPassword.value`` and ``global.keycloak.password.value``
  set also in your original values file. Then perform an *upgrade* to the previously deployed Renku chart version.
