.. _running_on_minikube:

Running the platform
====================


This page describes how to deploy the Renku platform on
`minikube <https://github.com/kubernetes/minikube>`_.
Running GitLab (which is a part of Renku) is quite resource
intensive. For this reason we describe a setup which excludes
GitLab from the Renku deployment and instead uses `<gitlab.com>`_
as GitLab backend. For the Renku graph features to function
properly in this setup, `<gitlab.com>`_ needs to be able to contact
the locally running platform through a webhook. This can be
achieved through many services like `ngrok <https://ngrok.com/>`_
or the like. We are going to use `localhost.run <http://localhost.run>`_,
a minimalistic method based on ssh tunneling which allows for a somewhat
static hostname.

The renku source code is hosted on github:
https://github.com/SwissDataScienceCenter/renku.


Prerequisites
^^^^^^^^^^^^^
You will need the following tools installed on your machine:

.. toctree::
   :maxdepth: 1

   git <https://git-scm.com/>
   python 3.6+ <python_setup>
   pipenv <https://github.com/pypa/pipenv>
   docker <https://www.docker.com/>
   minikube <https://github.com/kubernetes/minikube>
   kubectl <https://kubernetes.io/docs/tasks/tools/install-kubectl/>
   helm 2 (>=2.9.1) <https://github.com/kubernetes/helm/blob/master/docs/install.md>
   jq <https://stedolan.github.io/jq/>

For OS X users, we also recommend to install the
`hyperkit vm driver for minikube <https://github.com/kubernetes/minikube/blob/master/docs/drivers.md#hyperkit-driver>`_.


Clone the repo
^^^^^^^^^^^^^^
Get the code by cloning the Renku repository:

.. code-block:: console

    $ git clone https://github.com/SwissDataScienceCenter/renku.git
    $ cd renku/charts


Install python dependencies
^^^^^^^^^^^^^^^^^^^^^^^^^^^
We recommend using ``pipenv`` for managing your virtual python environment. Install
all the necessary python dependencies:

.. code-block:: console

    $ pipenv install --dev


Start minikube
^^^^^^^^^^^^^^
Next we start minikube. For OS X users we recommend using the hyperkit vm driver by
adding the `--vm-driver hyperkit` flag to the command.

.. code-block:: console

    $ minikube start --memory 6144 --kubernetes-version=1.14.8

Once minikube has started, make sure you can access it by running:

.. code-block:: console

    $ kubectl get node
    NAME       STATUS    ROLES     AGE       VERSION
    minikube   Ready     master    3s        v1.14.0

**Notice**: If minikube did not start successfully issue
``minikube delete`` before next ``minikube start``.

Once minikube has started, issue the following command (this will
configure your docker CLI to communicate with the docker daemon
running in your minikube virtual machine).

.. code-block:: console

    $ eval $(minikube docker-env)


Deploy tiller and the NGINX ingress controller
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Helm needs a server-side component which is called tiller and runs inside
the the kubernetes cluster. Also, we deploy `nginx ingress <https://github.com/kubernetes/ingress-nginx>`_.

.. code-block:: console

    $ helm init
    $ helm upgrade --install nginx-ingress --namespace kube-system \
        --set controller.hostNetwork=true \
        stable/nginx-ingress


Build and pull all necessary charts
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: console

    $ helm repo add jupyterhub https://jupyterhub.github.io/helm-chart
    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts
    $ pipenv run chartpress --tag latest
    $ helm dep build renku


Set up localhost.run
^^^^^^^^^^^^^^^^^^^^
We use a service called `localhost.run <http://localhost.run>`_ to
expose the platform by establishing an ssh tunnel.

.. code-block:: console

    $ ssh -R 80:$(minikube ip):80 ssh.localhost.run

This will start the tunnel and display your hostname of the style
``http://<some-name>.localhost.run``. Copy it and export it (without
the ``http(s)://``) into ``RENKU_DOMAIN``.

.. code-block:: console

    $ export RENKU_DOMAIN=<some-name>.localhost.run

**Note:** When you stop and restart the tunnel without waiting
for too long, you should receive the same subdomain that you have
previously had. If this is not the case you will have to reconfigure
the ``gitlab.com`` client application and recreate your Renku deployment
(see next steps).


Set up the gitlab.com client application
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Browse to **gitlab.com > user settings > applications** and register a new
client application (Renku-local) with the following settings:

#. Activate all scopes except :code:`sudo`
#. Add the following redirect URLs (use the value of the previously exported
   environment variable $RENKU_DOMAIN for <renku-domain>):

  .. code-block:: console

    http://<renku-domain>/auth/realms/Renku/broker/gitlab/endpoint
    http://<renku-domain>/api/auth/gitlab/token
    http://<renku-domain>/api/auth/jupyterhub/token
    http://<renku-domain>/jupyterhub/hub/oauth_callback


Configure the ``values.yaml`` file
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Open the file ``charts/example-configurations/external-gitlab-values.yaml``
in your cloned Renku repository and paste the ``Application Id`` and the
``Secret`` of the GitLab client application created in ``gitlab.com``
(Renku-local) into the designated places at the top of the file.


Deploy Renku to minikube
^^^^^^^^^^^^^^^^^^^^^^^^

Start the renku platform using helm:

.. code-block:: console

  $ helm upgrade renku --install --namespace renku ./renku \
    -f minikube-values.yaml -f example-configurations/external-gitlab-values.yaml \
    --timeout 1800 \
    --set global.renku.domain=$RENKU_DOMAIN \
    --set notebooks.jupyterhub.hub.services.gateway.oauth_redirect_uri=http://$RENKU_DOMAIN/api/auth/jupyterhub/token \
    --set notebooks.jupyterhub.auth.gitlab.callbackUrl=http://$RENKU_DOMAIN/jupyterhub/hub/oauth_callback

Executing this command for the first time can easily take 15 minutes (depending
on your internet connection speed) as all the necessary docker images need to be
pulled. This would therefore be a good moment to grab yourself a coffee...

Once the above command has returned you should be able to access the platform using
your browser under ``http://<some-name>.localhost.run``.

Manage Keycloak
^^^^^^^^^^^^^^^
`Keycloak <http://www.keycloak.org/documentation.html>`_ is an identity
management solution which is deployed as a part of Renku. You can access
the admin interface at ``http://<some-name>.localhost.run/auth``. The
admin username is "admin" and the admin password can be looked up in
the corresponding kubernetes secret:

.. code-block:: console

    $ kubectl get secrets -n renku keycloak-password-secret -o json | \
        jq -r '.data["keycloak-password"]' | base64 --decode


Configure gitlab.com as the identity provider (optional)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
It is possible to configure ``gitlab.com`` or another external GitLab deployment as an identity provider
for your local Renku deployment. This has the benefit of having
only one source for user information and making the login process
for the platform more convenient.
While logged into the Keycloak admin interface, go to ``Identity Providers``
and choose "GitLab" from the "Add Provider..." drop-down menu. Add the
``Application Id`` and ``Secret`` from the ``Renku-local``
application created earlier. Save the identity provider and logout
from the Keycloak admin panel.


.. _runner_extra_config:

Configure GitLab CI
^^^^^^^^^^^^^^^^^^^


For each created project you will have to modify the :code:`.gitlab-ci.yaml`
in the repository for the image build to work. In the ``image_build`` job

- remove the :code:`image-build` tag
- add the following entry:

.. code-block:: console

  services:
    - docker:dind
