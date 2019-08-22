.. _aks:

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
