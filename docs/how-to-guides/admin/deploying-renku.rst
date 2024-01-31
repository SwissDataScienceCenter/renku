.. _deploying-renku:

Deploy Renku
============

This guide will help you deploying and customizing your own Renku instance in a cluster.

Cluster requirements
--------------------

To deploy Renku in a cluster, you need to have the following prerequisites:

- a `Kubernetes  <https://kubernetes.io/>`_ cluster
- `Helm <https://helm.sh/docs/>`_
- :ref:`a DNS domain <dns>` that resolves to the IP of your cluster load-balancer
- :ref:`TLS certificates (or a cert-manager LetsEncrypt) <certificates>`
- :ref:`NGINX (or other) ingress <nginx>` capable of interpreting standard
  Kubernetes Ingress objects
- A GitLab instance

.. toctree::
   :hidden:

   prerequisites/dns
   prerequisites/certificates
   prerequisites/nginx

You may choose to either deploy GitLab as a part of the Renku deployment or link
to an existing GitLab instance. If you are deploying GitLab as a part of Renku,
you need to configure data storage and be prepared to manage the CI runners etc.
We encourage production deployments to *not* use the GitLab chart bundled with
Renku, but instead either acquire GitLab as a service or deploy it using the
`official GitLab cloud-native kubernetes chart
<https://docs.gitlab.com/charts/>`_.

Some parts of these requirements are beyond the scope of this documentation -
for example, load-balancer setup is highly cluster-dependent. If you have a
configuration that works on a public cloud provider, please consider
contributing to these docs! 🙏

Pre-flight check
~~~~~~~~~~~~~~~~

If you have your cluster configured with a DNS, a TLS certificate, and an Nginx ingress running,
you should be able to do

.. code-block::

   $ curl https://<hostname>
   default backend - 404

This response basically means that:

* a DNS server can resolve your hostname to the load-balancer IP
* TLS termination was successful
* the request made it to the ingress (but there are no applications serving the request, which is fine)


Officially supported Kubernetes versions
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Renku versions ``0.6.x`` and older only work with Helm version 2.

Newer versions of Renku only work with Helm version 3 and should be supported
on the latest version of Kubernetes and 3-4 older minor versions.

Pre-deployment steps
-----------------------

1. Setting up Gitlab
~~~~~~~~~~~~~~~~~~~~

.. toctree::
   :maxdepth: 1

   GitLab <configurations/external-gitlab>

1. (Optional) Using external Keycloak
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. toctree::
   :maxdepth: 1

   Keycloak <configurations/external-keycloak>

1. (Optional) Create and configure Renku PVs and PVCs
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

All of Renku information is stored in two volumes needed by Jena and Postgresql. 
You can leave Kubernetes to dynamically provision these volumes but we advise to create them 
beforehand and make regular backups.

1. Create a renku-values.yaml file
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This step is very important as it will define the values that helm will use to install your Renku instance.

Basic configuration
^^^^^^^^^^^^^^^^^^^^

The full values file in all its glory can be found as a part of the `Renku helm
chart
<https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/values.yaml>`_.
We tried to document in-line all the important pieces that someone might want to
configure, but there are `a lot` of options and many you probably won't care about immediately.

To generate a basic values file, you can use a script we have prepared. You need to have Python3 and ``wget`` installed.

.. code-block::

   $ mkdir renku-values
   $ cd renku-values
   $ wget https://raw.githubusercontent.com/SwissDataScienceCenter/renku/master/scripts/generate-values/generate-values.sh
   $ sh generate-values.sh -o renku-values.yaml

By default, this will create a python virtual environment and run the script; if
you prefer to avoid any installation, you can pass the ``--docker`` flag to the
``generate-values.sh`` script and it will execute in a docker container.

After preparing your values and before deploying Renku please make sure to check
the following in the values file:

  - the URLs and DNS names are correct
  - all the necessary secrets are configured
  - the ingress configuration is correct
  - add/verify, if necessary, the persistent volume claims

.. note::
   If you created persistent volumes in the step above, make sure to add their
   references to the appropriate components under `storage.existingClaim`.

Further configuration
^^^^^^^^^^^^^^^^^^^^^^

You might want to modify some default values, here are some examples:

   * Logged-out users can be given the permission to launch interactive sessions
     from public notebooks. This feature is turned off by default. For details
     on how to enable this feature, see the dedicated section below.
   * A custom privacy notice and cookie banner can be configured (as of Renku
     0.6.8). See the dedicated section below.
   * It is possible to dedicate some nodes for running interactive user sessions.

   .. toctree::
      :maxdepth: 1

      Enabling notebooks for anonymous users <anonymous-sessions>
      Configure privacy notice and cookie banner <privacycookie>
      Configuring the RenkuLab Homepage <homepage>
      Mounting cloud storage in sessions <sessions-cloud-storage>

Deploying RenkuLab
-------------------

Once all the pieces are in place, you can deploy Renku with the following commands:

.. code-block:: console

    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts/
    $ helm upgrade --install renku renku/renku \
     --namespace renku \
     -f renku-values.yaml \
     --timeout 1800s

During deployment you can check the Renku pods being started.
If deploying for the first time, Keycloak, PostgreSQL and Redis can take some time to get started. 
It is normal to see other pods of components that depend on these to be in a `CrashLoopBackOff` state 
and restart a few times during this time.

Once all the pods are correctly running or completed, a `GitLab Runner <prerequisites/gitlabrunner>`_ can be configured.

.. toctree::
   :hidden:

   prerequisites/gitlabrunner

Verifying RenkuLab deployment
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Check list:

  * Helm command completed successfully.
  * RenkuLab pods should be all running and jobs completed.
  * RenkuLab pods logs should not show errors.
  * Perform a quick check:
     #. go to your RenkuLab instance domain (this will verify the ingress is working properly)
     #. login with a valid user, you can register a new one (this will verify various authentication steps)
     #. create a project (this will verify further authentication, templates and GitLab runner)
     #. add a dataset (this will verify the core backend, LFS objects, and Knowledge Graph components work)
     #. launch an environment (this will verify the notebooks component and GitLab image registry)
  * You should be now able to follow "First steps"

If some Renku pods are not starting or present some errors please check the troubleshooting page.

.. toctree::
   :maxdepth: 1

   Troubleshooting a RenkuLab deployment <troubleshooting>

Acceptance tests (optional)
~~~~~~~~~~~~~~~~~~~~~~~~~~~

If the Helm deployment ran successfully according to the check list above, you might want to run Renku's acceptance tests.
You simply need to:

  * create a user for testing (with no admin rights) and optionally have an S3 bucket credentials for saving screenshots in case a test fails.
  * fill in the credentials (notably email and password) in the test section in the values file and update the deployment with ``helm``.

  .. code-block:: console

    $ helm upgrade --install renku renku/renku \
     --namespace renku \
     --version <renku-version> \
     -f renku-values.yaml \
     --timeout 1800s

  * run the tests. This will create a test pod that needs around 4GB of RAM available. Note that running the complete acceptance test suit takes around 40 minutes to complete.

  .. code-block:: console

      $ helm test renku \
       --namespace renku \
       --log-file renku-tests.log \
       --timeout 80m

Post deployment configurations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

After RenkuLab has been deployed you can make some post deployment configurations.

External GitLab as identity provider
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If the RenkuLab deployment is relying on an existing GitLab instance, this instance 
could be configured as an identity provider for RenkuLab's keycloak. This way all of the 
users from the existing GitLab instance can log into RenkuLab with their existing login information.

#. Go to \https://<renku-domain>/auth (or to your stand-alone Keycloak dashboard), login to ``Admin console`` using ``admin`` as username and the decoded password stored in ``keycloak-password-secret``.
#. Add an ``Identity Provider`` of type ``OpenID Connect v1.0``.
#. Set ``Alias`` to <renku-domain>, ``Authorization URL`` to \https://<gitlab-domain>/oauth/authorize, ``Token URL`` to \https://<gitlab-domain>/oauth/token and ``Client ID`` and ``Client Secret`` to the values used in step 1.
#. Click ``Save``. The new Identity Provider should appear and any user from the stand-alone GitLab instance should be able to login.

Upgrading RenkuLab
------------------

To upgrade RenkuLab to a newer version please check the `release notes <https://github.com/SwissDataScienceCenter/renku/releases>`_ 
as well as the `values file changelog <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/values.yaml.changelog.md>`_ 
in case the renku-values.yaml file needs to be modified. Following, you can upgrade Renku via Helm.

.. code-block:: console

    $ helm repo update
    $ helm upgrade --install renku renku/renku \
     --namespace renku \
     --version <renku-version> \
     -f renku-values.yaml \
     --timeout 1800s
