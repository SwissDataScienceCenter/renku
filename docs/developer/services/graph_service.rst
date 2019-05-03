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


Sequence diagram of Graph Services APIs and processes.
""""""""""""""""""""""""""""""""""""""""""""""""""""""
POST <webhook-service>/projects/:id/webhooks/validation

.. uml:: ../../_static/uml/graph-validate-hook-sequence.uml

POST <webhook-service>/projects/:id/webhooks

.. uml:: ../../_static/uml/graph-create-hook-sequence.uml

GET <webhook-service>/projects/:id/events/status

.. uml:: ../../_static/uml/graph-events-status-sequence.uml

POST <webhook-service>/webhooks/events

.. uml:: ../../_static/uml/graph-push-event-sequence.uml

Commit Events to RDF Triples

.. uml:: ../../_static/uml/graph-commit-event-sequence.uml

Missed commits synchronization job

.. uml:: ../../_static/uml/graph-sync-events-sequence.uml
