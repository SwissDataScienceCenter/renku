.. _service_architecture:

RENGA Architecture
==================

This document describes the architecture of the (web) services that are part of the **RENGA** platform.

.. _fig-architecture:

.. figure:: /_static/images/architecture.*

   -- Service Architecture

As depicted in :numref:`fig-architecture`, **RENGA** operates as a middleware that sits between applications, backend computing, and storage services.
Applications interact with the platform services via client application programming interfaces.
The interfaces are packaged as a collection of Software Development Kits (SDK) adapted to support a variety of programming languages and domain-specific environments.
In the backend, connector services provide homogeneous interfaces to expose the various resource provider services.

Broadly speaking, the platform services allow users to perform various actions (read/write files, execute code, etc.) which
are then described in the knowledge graph.
The system intercepts, authorizes and logs resource access transactions into the knowledge graph.
The event log can be consulted by the user and by other system services to search, retrieve the lineage information and reenact previously executed data processing chains.

The platform consists of loosely coupled services operating in a micro-services architecture style using the `Play framework <https://www.playframework.com/>`_.
It is based on stateless HTTP objects that respond to standard HTTP methods.
The technology used is JSON on REST/HTTP. It is implemented in Scala and Python, and runs on Docker containers in the cloud.
Because of the micro-service architecture it is by definition modular, and therefore should be able to accommodate other languages or implementations used for individual components.

Important Concepts
------------------

Before delving into the details of the architecture components it is strongly advised to first get familiarized
with a few foundational concepts:

.. toctree::
   :hidden:
   :maxdepth: 2

   JWT  <json_web_tokens>
   PBAC <policy_based_access_controls>

- :ref:`json_web_tokens` (JWT)
- :ref:`policy_based_access_controls` (PBAC)

Components
----------

Details about the main service components of the architecture can be found here:

- Identity Manager (IM), provides user authentication
- :ref:`resource_manager` (RM), provides authorization
- :ref:`knowledge_graph` (KG), spans multiple services
- :ref:`deployer` (DEP), provides (container) deployment
- :ref:`storage` (STG), provides file storage
- :ref:`explorer` (EXP), provides views over data stored in the knowledge graph.


