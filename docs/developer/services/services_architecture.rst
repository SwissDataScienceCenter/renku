.. _service_architecture:

Renku Architecture
==================

This document describes the architecture of the services that are parts
of the **Renku** platform.

.. contents::
    :depth: 1
    :local:

.. _fig-architecture:

.. figure:: /_static/images/architecture.*

   -- Service Architecture

As depicted in :numref:`fig-architecture`, **Renku** operates as a middleware
that sits between applications, backend computing, and storage services.
Applications interact with the platform services via client application
programming interfaces. The interfaces are packaged as a collection of
Software Development Kits (SDK) adapted to support a variety of programming
languages and domain-specific environments. In the backend, connector services
provide homogeneous interfaces to expose the various resource provider
services.

Broadly speaking, the platform services allow users to perform various actions
(read/write files, execute code, etc.) which are then described in the
knowledge graph. The system intercepts, authorizes and logs resource access
transactions into the knowledge graph. The event log can be consulted by the
user and by other system services to search, retrieve the lineage information
and reenact previously executed data processing chains.

The Renku platform consists of several off-the-shelf components from the
software engineering and data science software stacks, as well as customized or
newly developed services. The services communicate among each other through a
combination of REST APIs and event queues. The deployment is orchestrated on
Kubernetes through the use of Helm charts. Because of the micro-service
architecture it is by definition modular, and therefore should be able to
accommodate other languages or implementations used for individual components.

Components
----------

 The services built by the Renku team are:

- renku_: the meta repository with deployment scripts and kubernetes helm charts
- :ref:`renku-notebooks <notebooks_service>`: a service integrating GitLab repositories with JupyterHub
- renku-python_: python API & Command Line Interface (CLI)
- renku-storage_: storage service exposing LFS and S3 APIs
- renku-ui_: web front-end interface

In addition, we make use of:

- JupyterHub_: management of interactive notebook servers
- GitLab_: repository management, CI and various related APIs
- Keycloak_: user authentication

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
   PBAC <../general/policy_based_access_controls>

- :ref:`json_web_tokens` (JWT)
- :ref:`policy_based_access_controls` (PBAC)


.. _renku: https://github.com/SwissDataScienceCenter/renku
.. _renku-notebooks:
.. _renku-python: https://github.com/SwissDataScienceCenter/renku-python
.. _renku-storage: https://github.com/SwissDataScienceCenter/renku-storage
.. _renku-ui: https://github.com/SwissDataScienceCenter/renku-ui
.. _JupyterHub: https://github.com/jupyterhub/jupyterhub
.. _GitLab: https://gitlab.com/
.. _Keycloak: https://www.keycloak.org/
