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
   a certificate <prerequisites/certificates>
   NGINX ingress <prerequisites/nginx>
   a DNS domain <prerequisites/dns>
   a GitLab-runner VM for the docker image builder with sufficient storage <prerequisites/gitlabrunner>

Optionally, you can choose to manage your own instances of the following:

   - a `GitLab stand-alone instance <https://about.gitlab.com/install/>`_
   - a Keycloak stand-alone instance

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
To this end, persistent volumes and persistent volume claims need to be created. You can use the following yaml files as a base to execute the `kubectl` commands below.

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

You can find a basic configuration file in: `Renku values file <https://github.com/SwissDataScienceCenter/renku/blob/master/charts/renku/values.yaml>`_

Ensure the following has been taken care of before deploying Renku:

  - `variables_switchboard` section has URLs and DNS names that correspond to your configuration
  - `credentials` section has correctly populated values. Please see the comments in the renku-values file for more information.
  - check the `resources.requests.memory` value makes sense
  - `lfsObjects` & `registry` require to setup your S3 back-ends, so you need to have that configuration handy and in place

Deploying Renku
------------------

1. (Optional) Step for stand-alone GitLab
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To be able to support notebooks in private projects we need to insert the GitLab admin token as a secret so that docker can download the corresponding private images from the registry.

.. code-block:: bash

  $ kubectl -n renku create secret docker-registry renku-notebooks-registry \\
  --docker-server=<registryURL>:<port> --docker-username=root \\
  --docker-password=<gitlabSudoToken> --docker-email=root@renku-mydomain

2. (Optional) Certificates
~~~~~~~~~~~~~~~~~~~~~~~~~~

If you chose to create a certificate manually instead of using LetsEncrypt or similar, you can create the TLS secret with the following command:

.. code-block:: bash

   $ kubectl -n renku create secret tls renku-mydomain-ch-tls --cert=certificate.crt --key=certificate.key

Note that ``renku-mydomain-ch-tls`` should correspond to the `ingress TLS value in Renku values file <https://github.com/SwissDataScienceCenter/renku-admin-docs/blob/master/renku-values.yaml#L12>`_

3. Deploy renku
~~~~~~~~~~~~~~~

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

4. Post deployment configuration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

After Renku has been deployed you can make some post deployment configurations.
For instance, make a user admin on GitLab.

1. turn off automatic redirect to GitLab by setting redeploying with the value ``gitlab.oauth.autoSignIn: false``
2. log in as the root user using the password from ``gitlab.password``
3. modify any users you want to modify (e.g. to make them admin)
4. turn the automatic redirect back on

5. (Optional) Notebooks for anonymous users
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Logged-out users can be given the permission to launch interactive sessions from public notebooks. This feature is
turned off by default. For details on how to enable this feature, see the dedicated section.

.. toctree::
   :maxdepth: 1

   Enabling notebooks for anonymous users <anonymous-sessions>

Verifying Renku
------------------

Check list:

  - After a while (around 5 minutes) Renku pods should be all running.
  - Verify logs of pods and check there are no errors.
  - Perform a quick check:
    1. go to your Renku instance domain
    2. login with a valid user
    3. create a project
    4. launch a notebook
  - You should be now able to follow "First steps"

Additional configurations
-------------------------

Privacy page
~~~~~~~~~~~~

The UI has a privacy page with a completely configurable content, suited for showing
any policy/terms related information, like the `Privacy Policy Statement` or the
`Terms of Use`.

The content is read from the privacy ``ConfigMap`` installed with RenkuLab, precisely
the ``privacy_statement`` key.

.. note::

  The exact name of the privacy ConfigMap may slightly vary depending on your specific
  RenkuLab configuration. The default is ``renku-ui-privacy``. All the variations
  will end with `-privacy` in the name.

We support the `Markdown syntax <https://en.wikipedia.org/wiki/Markdown>`_ for the
privacy page content.

You can refer to the current
`ConfigMap in the renku-ui repository <https://github.com/SwissDataScienceCenter/renku-ui/blob/master/helm-chart/renku-ui/templates/configmap.yaml>`_
to find an example of the default Markdown.


Cookie banner
~~~~~~~~~~~~~

Since RenkuLab requires user identification and implements a login system, cookies are
required at all levels to provide basic functionality. Please mind that cookies are also used
for anonymous users (i.e. without an account or not currently logged in).

To comply with international laws, it's strongly advised to explicitly require consent to the
user for storing these data and using cookies. The default cookie banner simply informs the
user about these requirements and points to the privacy page for further details.

It's possible to customize the banner to your own needs, changing both the appearance and the
content. We use `cookie consent <https://github.com/Mastermindzh/react-cookie-consent>`_ to
create the banner and we support customization of all the properties exposed by the library.
All the adjustments should be provided as values in ``ui.privacy.banner`` as follows:

1. ``content``: The HTML content of the cookie banner. Please mind that links should point
   to local content. Use the privacy page to add references to third party sites.
2. ``layout``: The list of properties for the cookies consent plugin. We use
   `Bootstrap <https://getbootstrap.com/docs>`_ for the UI, therefore all the Bootstrap
   classes can be used to customize the appearance.

You can refer to the current
`values.yaml in the renku-ui repository <https://github.com/SwissDataScienceCenter/renku-ui/blob/master/helm-chart/renku-ui/values.yaml>`_
to check the default values and to find an example of how to apply different settings.

Troubleshooting
------------------

If some Renku pods are not starting or present some errors please check the troubleshooting page.

.. toctree::
   :maxdepth: 1

   Troublesooting a Renku deployment <troubleshooting>
