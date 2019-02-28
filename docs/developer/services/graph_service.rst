.. _graph_service:

Graph service
=============

In Renku, the dependencies of research artifacts are recorded into a knowledge
graph. Each project's local knowledge graph is recorded in its repository; the
creation of the global knowledge graph is possible via the graph service. When
a project's repository is pushed to the server, a webhook is triggered that
causes the changes represented by the commits and all of the captured
dependencies to be rendered as RDF triples and pushed to the triple store.

The graph service is made up of three micro-services: the webhook-service,
triples-generator, and the triple store (currently Apache Jena). The basic
architecture is illustrated below.

.. _fig-graph-service-architecture:

.. graphviz:: /_static/graphviz/graph_service_architecture.dot
