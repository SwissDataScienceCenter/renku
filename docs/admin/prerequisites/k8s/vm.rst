.. _vm:

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

Known **caveats** for this setup:

- Notebook servers can only be launched from public projects. This is due to the
  fact that the users credentials are needed for pulling images for private
  projects from the renkulab.io registry. These credentials are currently not
  injected into the notebook pod.

