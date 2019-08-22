.. _openshift:

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

