.. _admin_documentation:

Deploying the Renku platform
============================

This guide will help you deploying and customizing your own Renku instance in a cluster.

Cluster requirements
-----------------------

To deploy Renku in a cluster, you need to have the following prerequisites:

- a `Kubernetes  <https://kubernetes.io/>`_ cluster

.. toctree::
   :maxdepth: 1

   Helm <prerequisites/tiller>
   a certificate (or LetsEncrypt) <prerequisites/certificates>
   NGINX ingress <prerequisites/nginx>
   a DNS domain <prerequisites/dns>
   a GitLab-runner VM for the docker image builder with sufficient storage <prerequisites/gitlabrunner>

Optionally, you can choose to manage your own instances of the following:

   - a `GitLab stand-alone instance <https://about.gitlab.com/install/>`_
   - a Keycloak stand-alone instance

Officially supported versions
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The following component versions have been tested in production.

===================   ============   ============
Renku version         Kubernetes     Helm
===================   ============   ============
0.6.x                 1.14, 1.15     2
0.7.x (coming soon)   1.16, 1.17     3
===================   ============   ============

Pre-deployment steps
-----------------------

1. (Optional) Stand-alone configurations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. toctree::
   :maxdepth: 1

   Stand-alone GitLab <configurations/standalone-gitlab>
   Stand-alone Keycloak <configurations/standalone-keycloak>

2. Create and configure Renku PVs and PVCs
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

All of Renku information is stored in three volumes needed by Jena, Postgresql and GitLab (if not stand-alone).
To this end, persistent volumes and persistent volume claims need to be created. You can leave k8s to dynamically provision these volumes but we advise to create them before hand and make regular backups.
You can use the following yaml files as a base to execute the `kubectl` commands below.

   - `Renku PV yaml file <https://github.com/SwissDataScienceCenter/renku-admin-docs/blob/master/renku-pv.yaml>`_
   - `Renku PVC yaml file <https://github.com/SwissDataScienceCenter/renku-admin-docs/blob/master/renku-pvc.yaml>`_

   .. code-block:: console

     $ kubectl create -f renku-pv.yaml
     $ kubectl create ns renku
     $ kubectl -n renku create -f renku-pvc.yaml

Ensure that PVs are added correctly and have the appropriate storage class.

3. Create a renku-values.yaml file
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This step is very important as it will define the values that helm will use to install your Renku instance.

Basic configuration
^^^^^^^^^^^^^^^^^^^^

The basic configuration file with is in: `Renku values file <https://github.com/SwissDataScienceCenter/renku/blob/master/charts/renku/values.yaml>`_

You can also use the `make-values.sh <https://github.com/SwissDataScienceCenter/renku/blob/master/charts/example-configurations/make-values.sh>`_ script to generate a valid values file with your DNS and (optionally) your Gitlab URL, application ID and secret.

Before deploying Renku please make sure to have the following configured:

  - URLs and DNS names that correspond to your configuration
  - all the necessary secrets

Further configuration
^^^^^^^^^^^^^^^^^^^^^^

You might want to modify some default values, here are some examples:

   * Logged-out users can be given the permission to launch interactive sessions from public notebooks. This feature is turned off by default. For details on how to enable this feature, see the dedicated section below.
   * A custom privacy notice and cookie banner can be configured (as of Renku 0.6.8). See the dedicated section below.
   * Resources requests and limits can be adjusted for the diverse RenkuLab components
   * For not stand-alone Gitlab you can configure an S3 backend for `lfs objects <https://docs.gitlab.com/ce/administration/lfs/index.html>`_ and `registry <https://docs.gitlab.com/ee/administration/packages/container_registry.html>`_, this configuration can be added in the ``gitlab`` section
   * It is possible to dedicate some nodes for running user environments.

   .. toctree::
      :maxdepth: 1

      Enabling notebooks for anonymous users <anonymous-sessions>
      Configure privacy notice and cookie banner <privacycookie>

Deploying RenkuLab
-------------------

1. (Optional) Certificates
~~~~~~~~~~~~~~~~~~~~~~~~~~

If you chose to create a certificate manually instead of using LetsEncrypt or similar, you can create the TLS secret with the following command:

.. code-block:: bash

   $ kubectl -n renku create secret tls renku-mydomain-ch-tls --cert=certificate.crt --key=certificate.key

Note that ``renku-mydomain-ch-tls`` should correspond to the `ingress TLS value in Renku values file <https://github.com/SwissDataScienceCenter/renku/blob/master/charts/example-configurations/minimal-values.tmpl#L12>`_

2. Deploy RenkuLab
~~~~~~~~~~~~~~~~~~~~

Once all the pieces are in place, you can deploy Renku with the following commands:

.. code-block:: console

    $ helm init
    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts/
    $ helm upgrade --install renku renku/renku \
     --namespace renku \
     --version <renku-version> \
     -f renku-values.yaml \
     --timeout 1800

During deployment you can check the Renku pods being started.


3. Verifying RenkuLab deployment
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Check list:

  * Helm command completed successfully.
  * RenkuLab pods should be all running and jobs completed.
  * RenkuLab pods should not show errors.
  * Perform a quick check:
    #. go to your RenkuLab instance domain
    #. login with a valid user (you can register a new one)
    #. create a project
    #. launch a notebook
  * You should be now able to follow "First steps"

If some Renku pods are not starting or present some errors please check the troubleshooting page.

.. toctree::
   :maxdepth: 1

   Troublesooting a Renku deployment <troubleshooting>

**Note**. For deployments with stand-alone GitLab and/or Keycloak, the post-install jobs need to be deleted manually in order for the Helm deployment succeeds.

4. Post deployment configurations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

After Renku has been deployed you can make some post deployment configurations.

Not stand-alone GitLab
^^^^^^^^^^^^^^^^^^^^^^^

If your RenkuLab deployment includes GitLab you need to follow some additional steps to configure a user admin on GitLab.

#. turn off automatic redirect to GitLab by setting redeploying with the value ``gitlab.oauth.autoSignIn: false``
#. log in as the root user using the password from ``gitlab.password``
#. modify any users you want to modify (e.g. to make them admin)
#. turn the ``autoSignIn`` setting back on

Stand-alone GitLab as identity provider
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If the RenkuLab deployment is using an existing GitLab, this application could be configured as an identity provider for RenkuLab's keycloak. This way all of the users from the existing GitLab instance could authenticate.

#. Go to https://<renku-domain>/auth (or to your stand-alone Keycloak dashboard), login to ``Admin console`` using admin username and the decoded password stored in ``keycloak-password-secret``.
#. Add an ``Identity Provider`` of type ```OpenID Connect v1.0``.
#. Set ``Alias`` to <renku-domain>, ``Authorization URL`` to https://<gitlab-domain>/oauth/authorize , ``Token URL`` to https://<gitlab-domain>/oauth/token and ``Client ID`` and ``Client Secret`` to the values used in step 1.
#. Click ``Save``. The new Identity Provider should appear and any user from the stand-alone GitLab instance should be able to login.

Updating RenkuLab
------------------

To update Renku to a newer version please check the **Upgrading from 0.x.x** section of the `release notes <https://github.com/SwissDataScienceCenter/renku/releases>`_ in case the renku-values.yaml file needs to be modified.
Following, you can upgrade Renku via Helm.

.. code-block:: console

    $ helm repo update
    $ helm upgrade --install renku renku/renku \
     --namespace renku \
     --version <renku-version> \
     -f renku-values.yaml \
     --timeout 1800
