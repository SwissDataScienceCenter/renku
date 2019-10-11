.. _renkulab.io:

Developing against another renku deployment
===========================================

Here we describe how to run a local version of the platform against
renkulab.io **and** also use renkulab.io as an identity provider. (In regular
Renku deployments, the dependency is the other way around meaning that the
Renku GitLab instance uses the Renku Keycloak instance as identity provider).
The procedure can also be used with any other renku deployment by substituting
the appropriate URL for :code:`renkulab.io`.

Most of the configurations needed are already set in a special `minikube-values.yaml file`_
which you will have to modify slightly while going through the following steps.

.. _`minikube-values.yaml file`:
  https://github.com/SwissDataScienceCenter/renku/blob/master/charts/example-configurations/minikube-values-renkulab-template.yaml

Known **caveat** for this setup:

- Notebook servers can only be launched from public projects. This is due to the
  fact that the users credentials are needed for pulling images for private
  projects from the renkulab.io registry. These credentials are currently not
  injected into the notebook pod.

Set up the GitLab application
-----------------------------

Browse to **renkulab.io/gitlab > user settings > applications** and register a
new client application (Renku-local) with the following settings:

#. Activate all scopes except :code:`sudo`
#. Add the following redirect URLs:

  .. code-block:: console

    http://<your-minikube-ip>/auth/realms/Renku/broker/renkulab/endpoint
    http://<your-minikube-ip>/api/auth/renkulab/token
    http://<your-minikube-ip>/api/auth/jupyterhub/token
    http://<your-minikube-ip>/jupyterhub/hub/oauth_callback

Configure the ``values.yaml`` file
----------------------------------

In your cloned Renku repository copy the file :code:`charts/example-configurations/minikube-values-renkulab-
template.yaml` to :code:`charts/example-configurations/minikube-values-
renkulab.yaml`. Complete the :code:`minikube-values-renkulab.yaml` file by
filling in the Application Id and Application Secret from
the Renku GitLab application created in Step 1. Also fill out the
``notebooks.gitlab.registry.host`` value (for renkulab.io it should be
``registry.renkulab.io``).

Ensure that the version of :code:`renku-gateway` specified in
:code:`requirements.yaml` is at least :code:`0.3.1` and run ``helm dep update
renku`` from the charts directory.

Now start the Renku platform following the steps described in the `Renku
charts README`_, replacing the final :code:`helm upgrade` command with the
following:

.. _`Renku charts README`: https://github.com/SwissDataScienceCenter/renku/blob/master/charts/README.rst

.. code-block:: console

  $ helm upgrade renku --install --namespace renku \
    -f minikube-values.yaml -f example-configurations/minikube-values-renkulab.yaml \
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
admin console using admin/admin. Under Identity Providers choose "OpenID Connect" from
the "Add Provider..." drop-down menu. Enter the following values into the form:

+-------------------+--------------------------------------------+
| Field             | Value                                      |
+===================+============================================+
| Alias             | renkulab                                   |
+-------------------+--------------------------------------------+
| Authorization URL | https://renkulab.io/gitlab/oauth/authorize |
+-------------------+--------------------------------------------+
| Token URL         | https://renkulab.io/gitlab/oauth/token     |
+-------------------+--------------------------------------------+
| Client ID         | [Application Id from Step 1]               |
+-------------------+--------------------------------------------+
| Client Secret     | [Secret from Step 1]                       |
+-------------------+--------------------------------------------+


Save the identity provider and logout from the Keycloak admin panel.

Use
---

The setup should now be ready to use. Image build should already work correctly,
so it should be possible to run notebooks (on a local JupyterHub) using images
built on the renkulab server.
