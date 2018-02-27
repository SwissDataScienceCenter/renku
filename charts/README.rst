Helm Charts for Deploying RENGA on Kubernetes
=============================================

Testing locally
---------------

Requires minikube, kubectl and helm.

.. code-block:: console

    $ minikube start
    $ helm init
    $ helm install --name nginx-ingress --namespace kube-system stable/nginx-ingress
    $ helm install --name renga-staging --namespace renga renga

In order to authenticate the UI client with gitlab and keycloak, we need
to register it as an application in the gitlab database. Make sure that gitlab
is up and running:

.. code-block:: console

    $ helm status renga-staging

In the output, find the postgres pod and do:

.. code-block:: console

    $ kubectl exec -ti -n renga <gitlab-pod>
    $ psql -h renga-staging-renga-postgresql --user gitlab \
        -d gitlabhq_production -c "INSERT INTO oauth_applications (name, uid, scopes, redirect_uri, secret, trusted) VALUES ('renga-ui', 'renga-ui', 'api read_user', '${RENGA_DOMAIN}/login/redirect/gitlab', 'no-secret-needed', 'true')"

To access the deployed services, first get the ingress pod:

.. code:: console

    $ kubectl -n kube-system get po

Find the nginx-ingress-controller pod, then port forward to it (sudo for access to port 80):

.. code:: console

    $ sudo kubectl -n kube-system port-forward nginx-ingress-controller-[...] 80

Now, we can go to: http://localhost
