Helm Charts for Deploying RENKU on Kubernetes
=============================================

Testing locally
---------------

Requires minikube, kubectl and helm.

.. code-block:: console

    $ minikube start
    $ eval $(minikube docker-env)
    $ make -C .. tag
    $ minikube addons enable coredns
    $ helm init
    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts/
    $ helm dep build renku
    $ helm install --name nginx-ingress --namespace kube-system stable/nginx-ingress --set controller.hostNetwork=true
    $ helm upgrade renku --install \
        --namespace renku \
        -f minikube-values.yaml \
        --set global.renku.domain=$(minikube ip) \
        --set ui.gitlabUrl=http://$(minikube ip)/gitlab \
        --set jupyterhub.hub.extraEnv.GITLAB_HOST=http://$(minikube ip)/gitlab \
        ./renku

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
