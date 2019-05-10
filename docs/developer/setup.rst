.. _minikube:

Running the platform
====================

This page describes how to deploy the Renku platform
on `minikube <https://github.com/kubernetes/minikube>`__.

The renku source code is hosted on github:
https://github.com/SwissDataScienceCenter/renku.

Prerequisites
-------------
If you want to start developing the Renku platform, you will need the following
dependencies:

.. toctree::
   :maxdepth: 1

   python 3.6+ <python_setup>
   pipenv <https://github.com/pypa/pipenv>
   minikube <https://github.com/kubernetes/minikube>
   kubectl <https://kubernetes.io/docs/tasks/tools/install-kubectl/>
   helm (>= 2.9.1) <https://github.com/kubernetes/helm/blob/master/docs/install.md>
   GNU make <https://www.gnu.org/software/make/>

For OS X users, we also recommend to install the
`hyperkit vm driver for minikube <https://github.com/kubernetes/minikube/blob/master/docs/drivers.md#hyperkit-driver>`_.

Getting the code
----------------

.. code-block:: console

    $ mkdir src
    $ cd src
    $ git clone https://github.com/SwissDataScienceCenter/renku.git
    $ cd renku
    $ cd charts

``renku`` is the "parent" repository of the collection of microservices, each
of which has its own repository.

Building and deploying to minikube
----------------------------------

Install Python dependencies
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Use ``pipenv`` to manage virtual environment:

.. code-block:: console

    $ pipenv shell
    $ pipenv install

Starting minikube
^^^^^^^^^^^^^^^^^

Using the default VM driver:

.. code-block:: console

    $ minikube start --memory 6144

Using HyperKit:

.. code-block:: console

    $ minikube delete # Make sure we do not reuse a minikube vm on virtualbox
    $ minikube start --memory 6144 --vm-driver hyperkit

If your machine as enough resources, you can increase the allocated
resources, e.g.

.. code-block:: console

    $ minikube start --cpus 2 --memory 8192 --disk-size 40g

Once minikube is started, make sure you can access it by running:

.. code-block:: console

    $ kubectl get node
    NAME       STATUS    ROLES     AGE       VERSION
    minikube   Ready     master    3s        v1.14.0

**Notice**: If minikube didn't start successfully issue ``minikube delete`` before next ``minikube start``.

Once minikube is started issue:

.. code-block:: console

    $ eval $(minikube docker-env)

Deploying Helm and Nginx ingress
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: console

    $ helm init
    $ helm upgrade --install nginx-ingress --namespace kube-system \
        --set controller.hostNetwork=true \
        stable/nginx-ingress

Building and deploying components
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Provide helm chart dependencies:

.. code-block:: console

    $ helm repo add gitlab https://charts.gitlab.io
    $ helm repo add jupyterhub https://jupyterhub.github.io/helm-chart
    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts

Build and deploy (from the ``renku`` root folder):

.. code-block:: console

    $ make minikube-deploy

This command will build and deploy the platform components on minikube.
You can edit and test code changes from ``renku``, ``renku-ui``, ``renku-gateway``,
``renku-notebooks`` and ``renku-graph``, then run ``make minikube-deploy``
to check out the changes.

For more on the Renku helm charts, go to ``charts/renku/README.rst``.

Running integration tests
^^^^^^^^^^^^^^^^^^^^^^^^^

Run the tests with ``helm``:

.. code-block:: console

    $ helm test renku

Accessing the platform components
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The main web interface for Renku can be accessed by running the following
in the terminal:

.. code-block:: console

    $ xdg-open "http://$(minikube ip)"

Here is the list of accessible services:

- ``/`` -> Renku web UI
- ``/auth`` -> Keycloak, identity manager
- ``/auth/admin/`` -> Keycloak admin console
- ``/gitlab`` -> GitLab

A default user ``demo`` with password ``demo`` is configured in the identity
manager Keycloak. The administration console of Keycloak is available at
``/auth/admin``, with the user ``admin`` and password ``admin``
(`Keycloak documentation <http://www.keycloak.org/documentation.html>`_).
