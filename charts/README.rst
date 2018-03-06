Helm Charts for Deploying RENGA on Kubernetes
=============================================

Testing locally
---------------

Requires minikube, kubectl and helm.

.. code-block:: console

    $ minikube start
    $ helm init
    $ helm install --name nginx-ingress --namespace kube-system stable/nginx-ingress --set 'controller.hostNetwork=true'
    $ helm install --name renga-staging --namespace renga renga \
        --set 'ingress.enabled=true' \
        --set 'ingress.annotations.kubernetes\.io/ingress\.class=nginx' \
        --set 'ingress.hosts={renga-minikube}' \
        --set 'global.renga.domain=renga-minikube' \
        --set 'externalName.enabled=true' \
        --set 'externalName.target=nginx-ingress-controller.kube-system.svc.cluster.local'

As seen in the install above, we expose renga through the name :code:`renga-minikube`.
We need to make this name available to the host:

.. code-block:: console

    $ minikube status
    > minikube: Running
    > cluster: Running
    > kubectl: Correctly Configured: pointing to minikube-vm at <ip-adress>
    $ sudo bash -c 'echo "<ip-adress> renga-minikube" >> /etc/hosts'

The platform takes some time to start, to check the pods status do:

.. code-block:: console

    $ kubectl -n renga get po --watch

and wait until all pods are running.
Now, we can go to: http://renga-minikube
