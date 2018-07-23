.. _minikube:

Development setup on minikube
=============================

This page describes how to deploy the Renku platform
on `minikube <https://github.com/kubernetes/minikube>`__.

Prerequisites
-------------

* python 3.6
* `pipenv <https://github.com/pypa/pipenv>`_
* `minikube <https://github.com/kubernetes/minikube>`__
* `kubectl <https://kubernetes.io/docs/tasks/tools/install-kubectl/>`_
* `helm <https://github.com/kubernetes/helm/blob/master/docs/install.md>`_ (>= 2.9.1)
* `GNU make <https://www.gnu.org/software/make/>`_

For OS X users, we also recommend to install the
`hyperkit vm driver for minikube <https://github.com/kubernetes/minikube/blob/master/docs/drivers.md#hyperkit-driver>`_.

Clone the ``renku`` repository as detailed in :ref:`setup`.

Building and deploying to minikube
----------------------------------

Install Python dependencies
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Use ``pipenv``:

.. code-block:: console

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

    $ minikube start --cpus 2 --memory 8192

Once minikube is started, make sure you can access it by running:

.. code-block:: console

    $ kubectl get node
    NAME       STATUS    ROLES     AGE       VERSION
    minikube   Ready     <none>    3s        v1.10.0

Deploying Helm and Nginx ingress
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: console

    $ helm init
    $ helm upgrade --install nginx-ingress --namespace kube-system \
        --set controller.hostNetwork=true \
        --set tcp.2222=renku/renku-gitlab:22 \
        stable/nginx-ingress

Building and deploying components
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Provide helm chart dependencies:

.. code-block:: console

    $ helm repo add gitlab https://charts.gitlab.io
    $ helm repo add jupyterhub https://jupyterhub.github.io/helm-chart

Build and deploy:

.. code-block:: console

    $ make minikube-deploy

This command will build and deploy the platform components on minikube.
You can edit and test code changes from ``renku``, ``renku-ui`` and
``renku-notebooks`` then run ``make minikube-deploy``
to check out the changes.

For more on the Renku helm charts, go to ``charts/renku/README.rst``.
