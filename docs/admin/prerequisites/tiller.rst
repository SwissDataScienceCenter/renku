.. _tiller:

Setup Helm <v3.0.0
===================

Setup Helm/Tiller (see also: `Helm RBAC documentation <https://docs.helm.sh/using_helm/#role-based-access-control>`_).

To be able to run helm commands (how Renku is deployed) you need to have tiller running in your k8s cluster.
For deploying tiller you can run the following commands, using the renku-admin-docs `tiller-rbac-config.yaml file <https://github.com/SwissDataScienceCenter/renku-admin-docs/blob/master/helm-installs/tiller-rbac-config.yaml>`_

.. code-block:: bash

   $ kubectl create -f helm-installs/tiller-rbac-config.yaml
   $ helm init --override 'spec.template.spec.containers[0].command'='{/tiller,--storage=secret,--listen=localhost:44134}' --service-account tiller --upgrade

Setup the nodes which will run the ingress controller:

.. code-block:: bash

   $ kubectl edit node mynode-1 # Set a label like `ingress-node: "true"` AND verify afterwords it is in place.
