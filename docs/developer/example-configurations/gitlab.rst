.. _gitlab.com:

gitlab.com
===========

Running GitLab is quite resource-intensive. For development purposes
or if you already have an existing GitLab instance running, you might want to
use this pre-existing instance as a backend service for Renku.

Here we describe how to run a local version of the
platform against gitlab.com **and** also use gitlab.com as an identity provider.
(In regular Renku deployments, the dependency is the other way around meaning that
the Renku GitLab instance uses the Renku Keycloak instance as identity provider).

Most of the configurations needed are already set in a special `minikube-values.yaml file`_
which you will have to modify slightly while going through the following steps.

.. _`minikube-values.yaml file`:
  https://github.com/SwissDataScienceCenter/renku/blob/master/charts/example-configurations/minikube-values-gitlab.yaml

There are two known **caveats** for this setup:

- Notebook servers can only be launched from public projects. This is due to the
  fact that the users credentials are needed for pulling images for private
  projects from the gitlab.com registry. These credentials are currently not
  injected into the notebook pod.

- gitlab.com limits the total size of each repository to 10 GB (including the files added
  to git-lfs). This is fine for development work but very restrictive for actual
  Renku projects.

Set up the gitlab application
-----------------------------

Browse to **gitlab.com > user settings > applications** and register a new
client application (Renku-local) with the following settings:

#. Activate all scopes except :code:`sudo`
#. Add the following redirect URLs:

  .. code-block:: console

    http://<your-minikube-ip>/auth/realms/Renku/broker/gitlab/endpoint
    http://<your-minikube-ip>/api/auth/gitlab/token
    http://<your-minikube-ip>/api/auth/jupyterhub/token
    http://<your-minikube-ip>/jupyterhub/hub/oauth_callback

Configure the ``values.yaml`` file
----------------------------------

Open the file :code:`charts/example-configurations/minikube-values-gitlab.yaml`
in your cloned Renku repository and paste the Application Id and Secret of the
Renku-local application created in gitlab.com into the designated places.

Now start the Renku platform following the steps described in the `Renku charts README`_,
replacing the final :code:`helm upgrade` command with the following:

.. _`Renku charts README`: https://github.com/SwissDataScienceCenter/renku/blob/master/charts/README.rst

.. code-block:: console

  $ helm upgrade renku --install --namespace renku \
    -f minikube-values.yaml -f example-configurations/minikube-values-gitlab.yaml \
    --set global.renku.domain=$(minikube ip) \
    --set ui.jupyterhubUrl=http://$(minikube ip)/jupyterhub \
    --set ui.gatewayUrl=http://$(minikube ip)/api \
    --set gateway.keycloakUrl=http://$(minikube ip) \
    --set notebooks.jupyterhub.hub.services.gateway.oauth_redirect_uri=http://$(minikube ip)/api/auth/jupyterhub/token \
    --set notebooks.jupyterhub.auth.gitlab.callbackUrl=http://$(minikube ip)/jupyterhub/hub/oauth_callback \
    ./renku

Configure the identity provider
-------------------------------

Open :code:`http://<your-minikube-ip>/auth` in your browser and login to the
admin console using admin/admin. Under Identity Providers choose "GitLab" from
the "Add Provider..." drop-down menu. Add the Application Id and Secret from
the Renku-local application created in Step 1. Save the identity provider and
logout from the Keycloak admin panel.

Step 4
--------
For each created project you will have to modify the :code:`.gitlab-ci.yaml`
in the repository for the image build to work. In the image_build job

- remove the :code:`image-build` tag
- add the following entry:

.. code-block:: console

  services:
    - docker:dind
