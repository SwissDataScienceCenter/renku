Helm Charts for Deploying RENGA on Kubernetes
=============================================

Testing locally
---------------

Requires minikube, kubectl and helm.

.. code-block:: console

    $ minikube start
    $ helm init
    $ helm install --name nginx-ingress --namespace kube-system stable/nginx-ingress --set controller.hostNetwork=true
    $ helm install --name renga-staging --namespace renga \
        -f minikube-values.yaml --set global.renga.domain=$(minikube ip) renga

Due to issue `minikube #1568
<https://github.com/kubernetes/minikube/issues/1568>`_,
you also need to run:

.. code-block:: console

    $ minikube ssh sudo ip link set docker0 promisc on

The platform takes some time to start, to check the pods status do:

.. code-block:: console

    $ kubectl -n renga get po --watch

and wait until all pods are running.
Now, we can go to: :code:`http://$(minikube-ip)/`
