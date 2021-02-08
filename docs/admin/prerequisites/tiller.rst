.. _tiller:

Helm
======

Renku is deployed via Helm.

`Helm <https://helm.sh/>`_ is a tool used to install and manage Kubernetes applications.

Helm has two parts: a client side (helm) and a server side (tiller).

Helm client
--------------

To install helm client in your laptop, a cluster VM or wherever you want to run the helm commands from, please refer to `Helm documentation <https://helm.sh/docs/using_helm/#installing-the-helm-client>`_ and `Helm github repository <https://helm.sh/docs/intro/install/>`_.

If you want to use Helm 2 you need to deploy Tiller in your cluster.

Tiller
--------

Tiller is the server side of Helm and runs inside the Kubernetes cluster.

The following steps are an example of how to setup RBAC Tiller (see also: `Helm RBAC documentation <https://docs.helm.sh/using_helm/#role-based-access-control>`_).

To run the following commands you can use this `yaml file <https://github.com/SwissDataScienceCenter/renku-admin-docs/blob/master/helm-installs/tiller-rbac-config.yaml>`_

.. code-block:: bash

   $ kubectl create -f helm-installs/tiller-rbac-config.yaml
   $ helm init --override 'spec.template.spec.containers[0].command'='{/tiller,--storage=secret,--listen=localhost:44134}' --service-account tiller --upgrade
