Helm Charts for Deploying RENKU on Kubernetes
=============================================

Helm 2.9.0 or newer is recommended as we use the :code:`before-hook-creation` hook deletion policy.
See also: `before-hook-creation delete policy <https://github.com/kubernetes/helm/commit/1d4883bf3c85ea43ed071dff4e02cc47bb66f44f>`_.

Testing locally
---------------

Requires minikube, kubectl, helm and python.


.. code-block:: console

    $ minikube start --memory 6144
    $ eval $(minikube docker-env)
    $ pip install chartpress
    $ chartpress --tag latest
    $ minikube addons enable coredns
    $ helm init
    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts/
    $ helm repo add gitlab https://charts.gitlab.io
    $ helm dep build renku
    $ helm upgrade --install nginx-ingress --namespace kube-system \
        --set controller.hostNetwork=true \
        --set tcp.2222=renku/renku-gitlab:22 \
        stable/nginx-ingress
    $ helm upgrade renku --install \
        --namespace renku \
        -f minikube-values.yaml \
        ./renku

Make sure you have `$(minikube ip) renku-k8s gitlab.renku-k8s` line
in your `/etc/hosts`.

Due to issue `minikube #1568
<https://github.com/kubernetes/minikube/issues/1568>`_,
you also need to run:

.. code-block:: console

    $ minikube ssh sudo ip link set docker0 promisc on

The platform takes some time to start, to check the pods status do:

.. code-block:: console

    $ kubectl -n renku get po --watch

and wait until all pods are running.
Now, we can go to: :code:`http://renku-k8s.build/`


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


Deploying from a Helm repository
--------------------------------

.. code-block:: console

    $ minikube start
    $ helm init
    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts/
    $ helm fetch --devel renku/renku
    $ ls renku-*.tgz
    renku-0.1.0-XXXXXX.tgz
    $ helm upgrade --install renku --namespace renku \
        -f minikube-values.yaml \
        --set global.renku.domain=$(minikube ip) \
        --set ui.gitlabUrl=http://$(minikube ip)/gitlab \
        --set jupyterhub.hub.extraEnv.GITLAB_HOST=http://$(minikube ip)/gitlab \
        renku-0.1.0-XXXXXX.tgz


Tests
-----

To run tests on the deployment, use

.. code-block:: console

    $ helm test --cleanup renku
