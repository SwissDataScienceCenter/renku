.. _admin_documentation:

Deploying the Renku platform
============================

[TODO] Give some intro about Renku and a link

This guide will help you deploying and customizing your own Renku instance in a cluster.

Cluster requirements
-----------------------

To deploy Renku in a cluster, you need to have the following prerequisites:

   - a `Kubernetes  <https://kubernetes.io/>`_ cluster
       - `Setup Kubernetes on Openstack <prerequisites/k8s/openstack.html>`_
       - You can also `Setup a Kubernetes cluser with Rancher <prerequisites/k8s/rancher.html>`_
   - a `loadbalancer <prerequisites/loadbalancer.html>`_
   - a :ref:`gitlabrunner` VM for the docker image builder with **sufficient storage**
   - :ref:`tiller`
   - :ref:`certificates`
   - :ref:`nginx`
   - :ref:`dns`

Optionally, you can choose to manage your own instances of the following:

   - a `Gitlab stand-alone instance <https://about.gitlab.com/install/>`_
   - a Keycloak stand-alone instance

Pre-deployment steps
-----------------------

1. (optional) Stand-alone configurations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

   - `Stand-alone Gitlab <configurations/standalone-gitlab.html>`_
   - `Stand-alone Keycloak <configurations/standalone-keycloak.html>`_

2. Create and configure Renku PVs and PVCs
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

All of Renku information is stored in three volumes needed by Jena, Postgresql and Gitlab (if not stand-alone).
To this end, persistent volumes and persistent volume claims need to be created. You can use the following yaml files as a base to execute the `kubectl` commands below.

   - `Renku PV yaml file <https://github.com/SwissDataScienceCenter/renku-admin-docs/blob/master/renku-pv.yaml>`_
   - `Renku PVC yaml file <https://github.com/SwissDataScienceCenter/renku-admin-docs/blob/master/renku-pvc.yaml>`_

   .. code-block:: console

     $ kubectl create -f renku-pv.yaml
     $ kubectl create ns renku
     $ kubectl -n renku create -f renku-pvc.yaml
     $ kubectl -n renku edit pvc renku-postgresql
     $ kubectl get pv
     $ kubectl get pvc -n renku
     $ kubectl describe persistentvolumeclaim -n renku  ## this should not show any error, just PVs ready to be used

Ensure that PVs have the appropriate storageclass and the following value is set:

   .. code-block:: console

     metadata:
       name: default
       annotations:
         storageclass.beta.kubernetes.io/is-default-class: "true"

3. Create a renku-values.yaml file
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This step is very important as it will define the values that helm will use to install your Renku instance.

You can find a basic file in `Renku values file <https://github.com/SwissDataScienceCenter/renku-admin-docs/blob/master/renku-values.yaml>`_

Ensure the following has been taken care of before deploying Renku:

  - `variables_switchboard` section has URLs and DNS names that correspond to your configuration
  - `credentials` section has correctly populated values. Please see the comments in the renku-values file for more information.
  - check the `resources.requests.memory` value makes sense
  - `lfsObjects` & `registry` require to setup your S3 back-ends, so you need to have that configuration handy and in place

C. Deploying Renku
------------------

1. (Optional step for stand-alone Gitlab)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To be able to support notebooks in private projects we need to insert the gitlab admin token as a secret so that docker can download the corresponding private images from the registry.

.. code-block:: bash

  $ kubectl -n renku create secret docker-registry renku-notebooks-registry \\
  --docker-server=<registryURL>:<port> --docker-username=root \\
  --docker-password=<gitlabSudoToken> --docker-email=root@renku-mydomain

2. (Optional certificates)
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
For instance, make a user admin on gitlab.

1. turn off automatic redirect to gitlab by setting redeploying with the value ``gitlab.oauth.autoSignIn: false``
2. log in as the root user using the password from ``gitlab.password``
3. modify any users you want to modify (e.g. to make them admin)
4. turn the automatic redirect back on

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
  - [TODO] Helm test, integration tests
  - You should be now able to follow "First steps"

E. Troubleshooting
------------------

If some Renku pods are not starting please check our `Troubleshooting <troubleshooting.html>`_

[TODO] make a FAQ page
