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
    $ helm repo add jupyterhub https://jupyterhub.github.io/helm-chart
    $ helm dep build renku
    $ helm upgrade --install nginx-ingress --namespace kube-system \
        --set controller.hostNetwork=true \
        stable/nginx-ingress
    $ helm upgrade --install \
        renku ./renku \
        --namespace renku \
        -f minikube-values.yaml \
        --set global.renku.domain=$(minikube ip) \
        --set ui.gitlabUrl=http://$(minikube ip)/gitlab \
        --set ui.jupyterhubUrl=http://$(minikube ip)/jupyterhub \
        --set ui.gatewayUrl=http://$(minikube ip)/api \
        --set gateway.keycloakUrl=http://$(minikube ip) \
        --set gateway.gitlabUrl=http://$(minikube ip)/gitlab \
        --set notebooks.jupyterhub.hub.extraEnv.GITLAB_URL=http://$(minikube ip)/gitlab \
        --set notebooks.jupyterhub.hub.services.gateway.oauth_redirect_uri=http://$(minikube ip)/api/auth/jupyterhub/token \
        --set notebooks.jupyterhub.auth.gitlab.callbackUrl=http://$(minikube ip)/jupyterhub/hub/oauth_callback \
        --set notebooks.gitlab.registry.host=10.100.123.45:8105 \
        --set gitlab.registry.externalUrl=http://10.100.123.45:8105/ \
        --set graph.gitlab.url=http://$(minikube ip)/gitlab \
        --set graph.knowledgeGraph.services.renku.url=http://$(minikube ip) \
        --set graph.knowledgeGraph.services.renku.resources-url=http://$(minikube ip)/knowledge-graph 
        --timeout 1800

Due to issue `minikube #1568
<https://github.com/kubernetes/minikube/issues/1568>`_,
you also need to run:

.. code-block:: console

    $ minikube ssh sudo ip link set docker0 promisc on

The platform takes some time to start, to check the pods status do:

.. code-block:: console

    $ kubectl -n renku get po --watch

and wait until all pods are running.
Now, we can go to: :code:`http://$(minikube-ip)/`


Configure the GitLab sign out path
==================================

There is one manual step that you need to perform in order to get the platform
fully operational. Go to :code:`http://$(minikube ip)/gitlab` and login as the
``root`` user (the default password is ``gitlabadmin``. Go to ``Admin area ->
Settings --> General -> Sign-in Restrictions`` and configure the ``After sign
out path`` to be:

..code-block:: console

    http://<renku-domain>/auth/realms/Renku/protocol/openid-connect/logout?redirect_uri=http://<renku-domain>/api/auth/logout%3Fgitlab_logout=1

``<renku-domain>`` should just be your minikube ip.

Once you have done this you can redeploy renku with:

.. code-block:: console

    $ helm upgrade renku renku/renku --reuse-values --set gitlab.oauth.autoSignIn=true

This will prevent the separate login screen for GitLab from appearing. If you
need to change user permissions at a later point you will need to log in as the
root user again, so do the uprade again toggling the :code:`--set
gitlab.oauth.autoSignIn` as needed.


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
