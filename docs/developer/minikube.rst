.. _minikube:

Development setup on minikube
=============================

This page describes how to deploy the Renku platform
on `minikube <https://github.com/kubernetes/minikube>`_.

Prerequisites
-------------

* python 3.6
* `pipenv <https://github.com/pypa/pipenv>`_
* `minikube <https://github.com/kubernetes/minikube>`_
* `kubectl <https://kubernetes.io/docs/tasks/tools/install-kubectl/>`_
* `helm <https://github.com/kubernetes/helm/blob/master/docs/install.md>`_ (>= 2.9.1)
* `GNU make <https://www.gnu.org/software/make/>`_

For OS X users, we also recommend to install the
`hyperkit vm driver for minikube <https://github.com/kubernetes/minikube/blob/master/docs/drivers.md#hyperkit-driver>`_.

Clone the ``renku`` repository as detailed in :ref:`setup`.

Building and deploying to minikube
----------------------------------

Starting minikube
^^^^^^^^^^^^^^^^^

Using the default vm driver:

.. code-block:: console

    $ minikube start --memory 6144

Using hyperkit:

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

Deploying Helm
^^^^^^^^^^^^^^
