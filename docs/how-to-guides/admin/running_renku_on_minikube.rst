.. _running_on_kind:

Running the platform
====================


This page describes how to deploy the Renku platform on
`kind <https://kind.sigs.k8s.io/>`_.
Running GitLab (which is a part of Renku) is quite resource
intensive. For this reason we describe a setup which excludes
GitLab from the Renku deployment and instead uses a GitLab
instance running elsewhere, for example, `gitlab.com <https://gitlab.com>`_
as the GitLab backend. For the Renku graph features to function
properly in this setup, the GitLab instance needs to be able to contact
the locally running platform through a webhook. This can be
achieved through many services like `ngrok <https://ngrok.com/>`_
or the like. We are going to use `localhost.run <http://localhost.run>`_,
a minimalistic method based on ssh tunneling which allows for a somewhat
static hostname.

The renku source code is hosted on github:
https://github.com/SwissDataScienceCenter/renku.


Prerequisites
^^^^^^^^^^^^^
You will need the following tools installed on your machine:

.. toctree::
   :maxdepth: 1

   git <https://git-scm.com/>
   python 3.6+ <python_setup>
   pipenv <https://github.com/pypa/pipenv>
   docker <https://www.docker.com/>
   kind <https://kind.sigs.k8s.io/>
   kubectl <https://kubernetes.io/docs/tasks/tools/install-kubectl/>
   helm 2 (>=2.9.1) <https://helm.sh/docs/intro/install/>
   jq <https://stedolan.github.io/jq/>


Clone the repo
^^^^^^^^^^^^^^
Get the code by cloning the Renku repository:

.. code-block:: console

    $ git clone https://github.com/SwissDataScienceCenter/renku.git
    $ cd renku/helm-chart


Install python dependencies
^^^^^^^^^^^^^^^^^^^^^^^^^^^
We recommend using ``pipenv`` for managing your virtual python environment. Install
all the necessary python dependencies:

.. code-block:: console

    $ pipenv install --dev


Start kind
^^^^^^^^^^
Next we start kind. We make it so that ports 8080 and 8443 on the 
host machine are forward respectively to port 80 and 443 to the cluster.
This will allow you to access renku.

.. code-block:: console

    $ cat <<EOF | kind create cluster --name kind --config=-
    kind: Cluster
    apiVersion: kind.x-k8s.io/v1alpha4
    nodes:
    - role: control-plane
      kubeadmConfigPatches:
      - |
        kind: InitConfiguration
        nodeRegistration:
          kubeletExtraArgs:
            node-labels: "ingress-ready=true"
      extraPortMappings:
      - containerPort: 80
        hostPort: 8080
        protocol: TCP
    EOF

Once kind has started, make sure you can access it by running:

.. code-block:: console

    $ kubectl get node
    NAME                 STATUS   ROLES                  AGE   VERSION
    kind-control-plane   Ready    control-plane,master   43s   v1.21.1


Deploy the NGINX ingress controller
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
We need an ingress controller to expose HTTP and HTTPS routes from outside the cluster to services within the cluster.
We use `nginx ingress <https://github.com/kubernetes/ingress-nginx>`_. Deploy the ingress controller and then
run the wait command below to make sure it becomes fully available before proceeding further.

.. code-block:: console

    $ kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
    $ kubectl wait --namespace ingress-nginx \
        --for=condition=ready pod \
        --selector=app.kubernetes.io/component=controller \
        --timeout=300s


Build and pull all necessary charts
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: console

    $ helm repo add renku https://swissdatasciencecenter.github.io/helm-charts
    $ pipenv run chartpress
    $ helm dep update renku


Set up localhost.run
^^^^^^^^^^^^^^^^^^^^
We use a service called `localhost.run <http://localhost.run>`_ to
expose the platform by establishing an ssh tunnel.

.. code-block:: console

    $ ssh -R 80:localhost:8080 ssh.localhost.run

This will start the tunnel and display your hostname of the style
``http://<some-name>.lhr.domains``. In subsequent steps this domain
will be added to the configuration for Renku.

**Note:** When you stop and restart the tunnel without waiting
for too long, you should receive the same subdomain that you have
previously had. If this is not the case you will have to reconfigure
the ``gitlab.com`` client application and recreate your Renku deployment
(see next steps).

**Note:** The second port number in the ``ssh`` command above (i.e. ``8080``) should
be the same as the port number you connected on your host machine to the kind cluster.
If you use a different port number in the command when you create your kind cluster then
make sure you use that port number here as well. Using a common port on the host side
like ``80`` is not recommended because it can cause issues with the routing between 
Renku, the host and the kind cluster.

Set up the gitlab client application
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Browse to GitLab, then click on the user icon and select
**settings > applications**. Register a new client application with the
following settings:

#. Name: renku-local
#. Scopes: all except :code:`sudo`
#. Redirect URIs (for ``<renku-domain>`` use the value provided by the ``localhost.run`` service,
   from earlier - this domain usually has a format like ``<some-name>.lhr.domains``):

  .. code-block:: console

    https://<renku-domain>/login/redirect/gitlab
    https://<renku-domain>/api/auth/gitlab/token

Copy the ``Application Id`` and the ``Secret`` that appear at the end of
this procedure.


Configure the ``values.yaml`` file
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The ``values.yaml`` file will depend on the URL that GitLab is running under.
We have two templates available in the cloned repository in the folder
``helm-chart/example-configurations/``.

The flagship ``gitlab.com`` instance is available at the root of the domain.
If you are using ``gitlab.com`` or any other instance where GitLab is
accessible at the root of the domain, use the file
``helm-chart/example-configurations/gitlab_dot_com-gitlab-values.yaml`` as
a template and copy this file to
``helm-chart/example-configurations/external-gitlab-values.yaml``.

If you are using `renkulab.io/gitlab <https://renkulab.io/gitlab>`_ or another instance of GitLab
that is accessible under the path ``/gitlab``, then use
``helm-chart/example-configurations/renkulab-gitlab-values.yaml`` as
a template and copy this file to
``helm-chart/example-configurations/external-gitlab-values.yaml``.

(If the GitLab instance is available under a different path than either
the root or ``/gitlab``, use the ``gitlab_dot_com-gitlab-values.yaml``
file as a template and adapt the path.)

To complete the template, you should open
``helm-chart/example-configurations/external-gitlab-values.yaml`` and enter
the required ``variables`` that are indicated by ``TODO:``. You need to
provide the ``Application Id`` and the
``Secret`` from the GitLab client application created in the previous step.


Deploy Renku to kind
^^^^^^^^^^^^^^^^^^^^^

Start the renku platform using helm:

.. code-block:: console

  $ kubectl create namespace renku
  $ helm upgrade renku --install --namespace renku ./renku \
    -f minikube-values.yaml -f example-configurations/external-gitlab-values.yaml \
    --timeout 3600s

Executing this command for the first time can easily take a long time depending
on your internet connection speed (even 30+ minutes) as all the necessary docker
images need to be pulled. Do not interrupt the `upgrade` command.

If you want to verify that things are moving on, open a new terminal any type
``kubectl -n renku get pod``. The `renku-keycloak-*` pod is the slowest. If the
`Ready` column reports `0/1` and `Restarts` is not higher than `1`, all is going
as expected.
This would be a good moment to grab yourself a coffee...

Once the above command has returned you should be able to access the platform using
your browser under ``http://<some-name>.lhr.domains``.

Manage Keycloak
^^^^^^^^^^^^^^^
`Keycloak <http://www.keycloak.org/documentation.html>`_ is an identity
management solution which is deployed as a part of Renku. You can access
the admin interface at ``http://<some-name>.lhr.domains/auth``. The
admin username is "admin" and the admin password can be looked up in
the corresponding kubernetes secret:

.. code-block:: console

    $ kubectl get secrets -n renku keycloak-password-secret -o json | \
        jq -r '.data["keycloak-password"]' | base64 --decode


Configure gitlab.com as the identity provider (optional)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
It is possible to configure ``gitlab.com`` or another external GitLab deployment as an identity provider
for your local Renku deployment. This has the benefit of having
only one source for user information and making the login process
for the platform more convenient.
While logged into the Keycloak admin interface, go to ``Identity Providers``
and choose "GitLab" from the "Add Provider..." drop-down menu. Add the
``Application Id`` and ``Secret`` from the ``Renku-local``
application created earlier. Save the identity provider and logout
from the Keycloak admin panel.


.. _runner_extra_config:

Configure GitLab CI
^^^^^^^^^^^^^^^^^^^


For each created project you will have to modify the :code:`.gitlab-ci.yaml`
in the repository for the image build to work. In the ``image_build`` job

- remove the :code:`image-build` tag
- add the following entry:

.. code-block:: console

  services:
    - docker:dind
