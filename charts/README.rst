Helm Charts for Deploying RENGA on Kubernetes
=============================================

Testing locally
---------------

Requires minikube, kubectl and helm.

.. code-block:: console

    $ minikube start
    $ helm init
    $ helm install --name nginx-ingress --namespace kube-system stable/nginx-ingress
    $ helm install --name renga-staging --namespace renga renga \
        --set 'ingress.enabled=true' \
        --set 'ingress.annotations.kubernetes\.io/ingress\.class=nginx' \
        --set 'ingress.hosts={localhost}'

To access the deployed services, first get the ingress pod:

.. code:: console

    $ kubectl -n kube-system get po

Find the nginx-ingress-controller pod, then port forward to it (sudo for access to port 80):

.. code:: console

    $ sudo kubectl -n kube-system port-forward nginx-ingress-controller-[...] 80

Now, we can go to: http://localhost/auth/
