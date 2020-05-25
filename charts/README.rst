Helm Charts for Deploying RENKU on Kubernetes
=============================================

Helm 2.9.1 or later (but currently not Helm 3) is necessary as we use
the :code:`before-hook-creation` hook deletion policy. See also:
`before-hook-creation delete policy <https://github.com/kubernetes/helm/commit/1d4883bf3c85ea43ed071dff4e02cc47bb66f44f>`_.


Deploying from a Helm repository
--------------------------------

Create a values file using ``renku/values.yaml`` as a template. Then run:

.. code-block:: console

    $ helm init
    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts/
    $ helm upgrade --install renku/renku \
        --namespace renku \
        -f my-values.yaml \
        --version 0.6.2

See the `helm chart registry <https://swissdatasciencecenter.github.io/helm-charts/>`_ for
available versions.


Testing locally
---------------
Checkout our `developer docs <https://renku.readthedocs.io/en/latest/developer/setup.html>`_
for a detailed description of how to deploy a local version using minikube.


Building images
---------------

If you want to build the Renku images required by the chart locally
(``apispec``, ``singleuser``, ``jupyterhub-k8s``, ``tests``, ``notebooks``),
you can do so by using ``chartpress``.

.. code-block:: console

    $ pip install chartpress
    $ chartpress --tag latest

You can the use the same ``helm upgrade`` command as above to redeploy the
services using the new images. If you ommit the ``--tag latest``,
``chartpress`` will tag the images with the current commit sha and update the
relevant values in the charts.


Tests
-----

To run tests on the deployment, use

.. code-block:: console

    $ helm test --cleanup renku
