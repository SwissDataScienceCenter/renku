RENGA Architecture
==================

This document describes the architecture of the (web) services that are part of the **RENGA** platform.

.. _fig-architecture:

.. figure:: /images/architecture.*

   -- Service Architecture

As depicted in :numref:`fig-architecture`, **RENGA** operates as a middleware that sits between applications, backend computing, and storage services.
Applications interact with the platform services via client application programming interfaces.
The interfaces are packaged as a collection of Software Development Kits (SDK) adapted to support a variety of programming languages and domain-specific environments.
In the backend, connector services provide homogeneous interfaces to expose the various resource provider services.
Broadly speaking, the system intercepts, authorizes and logs resource access transactions into a knowledge graph representation.
The event log can be consulted by the user and by other system services to search, retrieve the lineage information and reenact previously executed data processing chains.

The platform consists of loosely coupled services operating in a micro-services architecture style using the `Play framework <https://www.playframework.com/>`_.
It is based on stateless HTTP objects that respond to standard HTTP methods.
The technology used is JSON on REST/HTTP. It is implemented in Scala and Python, and runs on Docker containers in the cloud.
Because of the micro-service architecture it is by definition modular, and therefore should be able to accommodate other languages or implementations used for individual components.

Components
----------

- [knowledge graph](https://github.com/SwissDataScienceCenter/documentation/wiki/Knowledge-Graph), spans multiple services (see [Knowledge Graph](#knowledge-graph))
- identity manager, provides user authentication
- resource manager, provides authorization
- deployment, provides (container) deployment
- storage, provides file storage
- explorer, provides views over data stored in the knowledge graph.

Knowledge Graph
---------------

[Full article](https://github.com/SwissDataScienceCenter/documentation/wiki/Knowledge-Graph)

The knowledge graph is decomposed into three separate parts:

- Model: the global state of the platform. This global state is stored in the form of a knowledge graph.
- Update: a way to update the global state. This is provided by the graph mutation service.
- Read: a way to read the state. This is (implicitly) provided by the direct read-only access of the graph by the other platform services.

Global Architecture
-------------------

The platform services allow users to perform various actions (read/write files, execute code, etc.) which
are then described in the knowledge graph.
As such the platform services can do the following:

- model: Provide service-specific graph modeling (e.g. data storage descriptions)
- update: Use the graph mutation service to update the global state according to user interaction.
- read: have direct read-only access to the knowledge graph

