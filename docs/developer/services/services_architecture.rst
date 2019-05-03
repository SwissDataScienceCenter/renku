.. _service_architecture:

Renku Architecture
==================

This document describes the architecture of the services that are parts
of the **Renku** platform.

.. contents::
    :depth: 1
    :local:



The Renku platform consists of several off-the-shelf components from the
software engineering and data science software stacks, as well as customized or
newly developed services. The services communicate among each other through
REST APIs. The deployment is orchestrated on Kubernetes through the use of
Helm charts. Because of its micro-service architecture, Renku is by definition
modular, and therefore able to accommodate other languages or implementations
used for individual components.

Components
----------

Renku has the following custom components:

- renku_: the meta repository with deployment scripts, documentation and kubernetes helm charts
- :ref:`renku-notebooks <notebooks_service>`: a service integrating GitLab repositories with JupyterHub
- :ref:`renku-graph <graph_service>`: a collection of services concerned with activating, building and querying the Renku knowledge graph
- :ref:`renku-gateway <api_gateway>`: an API gateway connecting clients to the APIs of the different backend services
- renku-ui_: web front-end interface
- renku-python_: python API & Command Line Interface (CLI)


In addition, we make use of:

- JupyterHub_: management of interactive notebook servers
- GitLab_: repository management, CI and various related APIs
- Keycloak_: identity management and user authentication

The figure below shows an overview of the components
and their interactions. Blue components are off-the-shelf, yellow components
are either built or extended by us.

.. _fig-component-architecture:

.. graphviz:: /_static/graphviz/renku_architecture.dot


Important Concepts
------------------

In order to develop for the platform, it is strongly advised to get
familiarized with a few foundational concepts:

.. toctree::
   :hidden:
   :maxdepth: 2

   JWT  <../general/json_web_tokens>

- :ref:`json_web_tokens` (JWT)


.. _renku: https://github.com/SwissDataScienceCenter/renku
.. _renku-python: https://github.com/SwissDataScienceCenter/renku-python
.. _renku-ui: https://github.com/SwissDataScienceCenter/renku-ui
.. _JupyterHub: https://github.com/jupyterhub/jupyterhub
.. _GitLab: https://gitlab.com/
.. _Keycloak: https://www.keycloak.org/
