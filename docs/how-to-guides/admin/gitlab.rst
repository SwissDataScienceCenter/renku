.. _gitlab:

GitLab
======

Renku needs a GitLab deployment in order to fully function. 
Every Renku project has a corresponding GitLab project.

You may choose to either deploy the GitLab that comes bundled with Renku or link
to an existing GitLab instance. If you are deploying GitLab as a part of Renku,
you need to configure data storage and be prepared to manage the CI runners etc.
We encourage production deployments to *not* use the GitLab chart bundled with
Renku, but instead either acquire GitLab as a service or deploy it using the
`official GitLab cloud-native Helm chart
<https://docs.gitlab.com/charts/>`_.

External GitLab
---------------

This section explains how to link Renku with an existing GitLab deployment.

External GitLab Setup
^^^^^^^^^^^^^^^^^^^^^

Renku needs to authenticate users with the Gitlab instance it is using.
Depending on whether you have admin access to the external GitLab instance, 
the following options are available to you:

  * If you *do not* have admin access to GitLab, choose one of the following options:
     * Use GitLab as an identity provider for Renku
     * Have separate Renku and GitLab identities
   
  * If you *do* have admin access to GitLab, choose one of the following options:
     * Use GitLab as an identity provider for Renku
     * Use Renku's Keycloak as an identity provider for GitLab
     * Have separate Renku and GitLab identities

To use an external GitLab as a backend for a Renku deployment you must register
the Renku application in GitLab. If you are not an admin user, this is done under
your personal user preferences. It is possible that the GitLab instance you are
using prevents users from registering external applications - in this case you
must contact your GitLab server administrator for assistance.

Renku GitLab application details
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Name: renku

Callback URLs:

.. code-block:: console

  https://<your-renku-dns>/login/redirect/gitlab
  https://<your-renku-dns>/api/auth/gitlab/token

Scopes:

.. code-block:: console

    api (Access the authenticated user's API)
    read_user (Read the authenticated user's personal information)
    read_repository (Allows read-access to the repository)
    read_registry (Grants permission to read container registry images)
    openid (Authenticate using OpenID Connect)

Copy the secret and client ID for use in the step below.

Configuring an External GitLab as Identity Provider for Renku
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If the Renku deployment is relying on an external GitLab instance, 
this GitLab instance could be configured as an identity provider for Renku's Keycloak. 
This way the Renku login screen will show the option to "Login via GitLab" 
and existing GitLab users can use Renku without creating a separate Renku account.

#. Login to the admin console of Renku's Keycloak, usually found at ``https://<renku-domain>/auth``, using the username and password stored in the Kubernetes secret called  ``keycloak-password-secret``. Ensure you ``base64`` decode these values before using them to login. If you use an external Keycloak with Renku, login to the external Keycloak instead.
#. Add an ``Identity Provider`` of type ``OpenID Connect v1.0``.
#. Set ``Alias`` to ``<renku-domain>``, ``Authorization URL`` to ``https://<gitlab-domain>/oauth/authorize``, ``Token URL`` to ``https://<gitlab-domain>/oauth/token`` and ``Client ID`` and ``Client Secret`` to the respective values copied from the Renku GitLab application details.
#. Click ``Save``. The new Identity Provider should appear and any user from the stand-alone GitLab instance should be able to login.

GitLab deployed as part of Renku
--------------------------------

We do *not* recommend deploying the Renku-bundled GitLab as part of a production Renku deployment, 
and instead suggest deploying GitLab using the `official GitLab cloud-native Kubernetes chart 
<https://docs.gitlab.com/charts/>`_. Deploying GitLab as part of Renku may be deprecated in the future.

If your Renku deployment includes GitLab you need to follow some additional steps to configure an admin user on GitLab.

To grant a GitLab user the GitLab admin role without having access to the GitLab Web UI, the following steps can be taken in the GitLab container console.

#. Run ``gitlab-rails console -e production`` (this might take a while).
#. Find the user you would like to grant the admin role, for example by running ``user = User.find_by(email: 'renku@renkulab.io')`` or ``user = User.find_by(username: 'renku')``.
#. Grant the user the administrator role by running ``user.admin = true``.
#. Save the user's profile by running ``user.save!``.
#. Leave the console by running ``exit``.

Migrate from Renku-bundled Omnibus GitLab to cloud-native Gitlab Helm chart
---------------------------------------------------------------------------

Important information
^^^^^^^^^^^^^^^^^^^^^

This guide does **not** cover all steps which may need be taken for the migration, 
but is a reflection of the steps we have taken for our own deployments. 
Other deployments may require more, fewer, or different steps. **You have been warned!**

The steps below are intended to add some Renku-specific context to `GitLab's own migration guide
<https://docs.gitlab.com/charts/installation/migration/package_to_helm.html>`_, 
which should be read and understood before attempting the migration.

The version of the GitLab cloud-native Helm chart you will want to use depends on which 
version of GitLab you wish to migrate to. `GitLab's version mapping documentation
<https://docs.gitlab.com/charts/installation/version_mappings.html>`_ is helpful for determining this.

These steps outline the method for migrating GitLab version ``14.10.5``, which at the 
time of writing, is the version of GitLab used in our production deployments. Steps 
for migrating other versions of GitLab may differ, due to the change in the values 
required by the GitLab cloud-native Helm chart between versions.

If you migrate from the Renku-bundled Omnibus GitLab to the cloud-native GitLab Helm chart, 
your GitLab instance can no longer be deployed at the ``/gitlab`` relative path of 
the Renku URL. To ease the migration process and prevent broken URLs, if an external
GitLab used, Renku will forward traffic with the ``/gitlab`` relative path prefix to your 
external GitLab URL.

This information can also be loosely followed for setting up a new, non-migrated cloud-native
GitLab deployment, for use with Renku. Configuration options such as using Renku's Keycloak as
the OAuth2 provider should obviously only be set when Renku itself has been deployed.

Useful links
^^^^^^^^^^^^

* `GitLab's reference architecture docs for sizing environment <https://docs.gitlab.com/ee/administration/reference_architectures/#cloud-native-hybrid>`_
* `GitLab-specific Kubernetes information <https://docs.gitlab.com/charts/troubleshooting/kubernetes_cheat_sheet.html#gitlab-specific-kubernetes-information>`_
* `GitLab's migrate from the Linux package to the Helm chart guide <https://docs.gitlab.com/charts/installation/migration/package_to_helm.html>`_
* `GitLab's configure charts using globals documentation <https://docs.gitlab.com/charts/charts/globals.html>`_
* `Helm Charts for Deploying Renku on Kubernetes <https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart>`_

Upgrade GitLab & PostgreSQL (optional, recommended)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The version of GitLab being migrated from, and the restore target GitLab version must
be the same. This means, there should be no GitLab upgrade as part of the actual migration.

If you are planning to upgrade GitLab, the upgrade should ideally take place before migration
to the cloud-native Helm chart, as per `GitLab's own recommendation
<https://gitlab.com/gitlab-org/charts/gitlab/-/issues/2192#note_378447440>`_.

To upgrading multiple versions of GitLab, please see `GitLab's upgrade paths documentation
<https://docs.gitlab.com/ee/update/#upgrade-paths>`_. You can also use their helpful 
`upgrade path tool <https://gitlab-com.gitlab.io/support/toolbox/upgrade-path/>`_.

Note: If upgrading from GitLab versions < 14.0, PostgreSQL will need to be updated to
version 12, see `GitLab's PostgreSQL versions documentation
<https://docs.gitlab.com/ee/administration/package_information/postgresql_versions.html>`_. 
If you are using the PostgreSQL version 11 instance provided by Renku for GitLab, 
please see `the Renku Postgres upgrade guide
<https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/utils/postgres_migrations/version_upgrades/README.md>`_.

Ensure you complete `GitLab's pre upgrade and post upgrade checks
<https://docs.gitlab.com/ee/update/plan_your_upgrade.html#pre-upgrade-and-post-upgrade-checks>`_ 
before and after each upgrade step.

Backup existing GitLab
^^^^^^^^^^^^^^^^^^^^^^

GitLab should to be backed up to an S3 bucket using the built-in GitLab backup tool.

#. Create a deployment-specific S3 bucket for backing up GitLab. This should be the same S3 bucket that will be used for backing up the cloud-native Helm chart GitLab deployment.
#. Append ``gitlab.rb`` in the ``renku-gitlab-config`` ConfigMap with ``backup_upload_connection`` and ``backup_upload_remote_directory``, with the connection details for the backup S3 bucket filled in. For example:
   
    .. code-block:: console

        gitlab_rails['backup_upload_connection'] = {
            "provider" => "AWS",
            "region" => "ZH",
            "aws_access_key_id" => "",
            "aws_secret_access_key" => "",
            "endpoint" => "https://os.zhdk.cloud.switch.ch",
            'aws_signature_version' => 2,
            'path_style' => true,
        }
        gitlab_rails['backup_upload_remote_directory'] = ''; # backup s3 bucket name

#. In the GitLab pod shell, run ``gitlab-ctl reconfigure``. After reconfiguration has completed, scale the GitLab deployment to ``0`` and back to verify new configuration
#. Run ``gitlab-backup create`` to start backup process. This can take quite a while, depending on the size of the GitLab deployment.
#. Verify GitLab backup has been uploaded to S3 bucket
#. GitLab rails secrets are not included in the normal GitLab backup. Follow steps 1-4 of `this guide <https://docs.gitlab.com/charts/backup-restore/restore.html#restore-the-rails-secrets>`_ to backup the GitLab rails secrets, and copy the secrets file to your local machine or somewhere else safe. In later steps, we refer to this file as ``gitlab-secrets.yaml``. You can use ``kubectl cp`` to copy files to your local filesystem from the GitLab pod: ``kubectl cp renku/renku-gitlab:/etc/gitlab/gitlab-secrets.json ~/gitlab-secrets.json``

Set values in configuration files
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

#. Configure GitLab cloud-native Helm chart values file using the existing ``renku-gitlab-config`` ConfigMap for reference. The configuration options of the Helm chart can be found `here <https://docs.gitlab.com/charts/charts/globals.html>`_. This is not a task that should be taken lightly, as many of the default CN GitLab Helm chart values are not suitable for a production deployment.
#. Create a file containing the example OAuth2 provider config below. Get the client secret from the GitLab client in the Keycloak admin portal (\https://<renku-domain>/auth, Keycloak admin credentials can be retrieved from the Keycloak Kubernetes secret) and paste it in the relevant field in the example config.

    .. code-block:: console

        name: oauth2_generic
        label: 'Renku login'
        app_id: 'gitlab'
        app_secret: '' # add gitlab client secret from keycloak here
        args:
          client_options:
            site: '' # keycloak url (renku url with /auth/ path)
            authorize_url: '/auth/realms/Renku/protocol/openid-connect/auth'
            user_info_url: '/auth/realms/Renku/protocol/openid-connect/userinfo'
            token_url: '/auth/realms/Renku/protocol/openid-connect/token'
          user_response_structure:
            attributes: { email:'email', first_name:'given_name', last_name:'family_name', name:'name', nickname:'preferred_username' }
            id_path: 'sub'

#. Create a file with the connection details for the backup s3 bucket using the `example provided by s3tools <https://s3tools.org/kb/item14.htm>`_. The config file can optionally be generated and verified by using the `s3cmd <https://s3tools.org/s3cmd>`_ tool. `GitLab's backups to S3 documentation <https://docs.gitlab.com/charts/backup-restore/#backups-to-s3>`_
#. Create a file with the connection details for the various non-backup Gitlab S3 buckets using the `registry.s3.yaml example provided by GitLab <https://gitlab.com/gitlab-org/charts/gitlab/-/blob/master/examples/objectstorage/registry.s3.yaml>`_. If connection details vary between buckets, multiple files can be created for each connection. `GitLab's bucket connection documentation <https://docs.gitlab.com/charts/charts/globals.html#connection>`_

Migration
^^^^^^^^^

#. Uninstall Renku & modify your Renku Helm values to use an external GitLab, specifying the new GitLab URL. You can also start with a newly generated Renku values using the `Renku generate-values script <https://github.com/SwissDataScienceCenter/renku/tree/master/scripts/generate-values>`_.
#. (Optional, recommended) Create a new namespace for GitLab
#. Create secrets in the namespace you wish to deploy GitLab to with the content of the files containing the S3 bucket connection details and OAuth2 provider. Ensure the name of the secret and the key of the values match the associated values provided in your cloud-native Helm chart values.
#. Install GitLab with Helm, providing the namespace, chart version and values file as arguments.
#. After GitLab has successfully installed, restore the backup of your existing GitLab instance by following `Gitlab's restoring a GitLab installation documentation <https://docs.gitlab.com/charts/backup-restore/restore.html>`_, starting with restoring the rails secrets. There might be errors or warnings during the restore, but according to `this GitLab issue <https://gitlab.com/gitlab-org/gitlab/-/issues/266988>`_, that is often to be expected, and does not mean that the restore has actually failed.
#. Ensure you can log in to GitLab, even just as root user, and do some checks to ensure the restore was successful.

Upgrading Renku with the newly modified Helm values
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

#. Backup your current unedited values file
#. Replace every GitLab URL from \https://$RENKU_URL/gitlab to \https://gitlab.$RENKU_URL. There should be 4 instances, at ``gateway.gitlabUrl``, ``notebooks.gitlab.url`` and ``ui.gitlabUrl``.
#. If you have a value set at ``global.gitlab.urlPrefix`` change it from ``/gitlab`` to ``/``
#. Set ``gitlab.enabled`` to ``false``.
#. Re-install the Renku Helm chart with the newly modified values.
