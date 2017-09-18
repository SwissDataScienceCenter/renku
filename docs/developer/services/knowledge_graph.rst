.. _knowledge_graph:

Knowledge Graph
===============

Contents
--------

- :ref:`Overview <kg_overview>`
- :ref:`Introduction to Model <kg_introduction_to_model>`
- :ref:`Model <kg_model>`
- :ref:`Read <kg_read>`
- :ref:`Update <kg_update>`
- :ref:`Code Modules <kg_code_modules>`

.. _kg_overview:

Overview
--------

The knowledge graph service components manage the platform's global state. The knowledge graph establishes the relationship between users, data, algorithms, workflows, execution engines and other contextual information.
It allows the lineage of a result to be traced back through the chain of operations to its original data. It does not store the content of the data sets, but references to the physical location of the content.

As shown in :numref:`fig-kg_architecture`, the knowledge graph is decomposed into separate parts:

- :ref:`Model <kg_model>`: the global state of the platform. This global state is stored in the form of a knowledge graph.

- :ref:`Read <kg_read>`: a way to read the state. This is (implicitly) provided by the direct read-only access of the graph by the
  other platform services.

- :ref:`Update <kg_update>`: a way to update the global state. This is provided by the graph mutation service.

- Write Ahead Log (WAL): makes it possible to support archiving, point-in-time recovery, and roll-forward recovery to rebuild the graph database from the log in case of a failure.

Read also: `The Elm Architecture <https://guide.elm-lang.org/architecture/>`_, `Redux, three principles <http://redux.js.org/docs/introduction/ThreePrinciples.html>`_.

.. _fig-kg_architecture:

.. figure:: /_static/images/kg_architecture.*

   -- Knowledge graph Architecture

.. _kg_introduction_to_model:

Introduction to Model
---------------------

The knowledge graph is based on the Apache `TinkerPop3 <http://tinkerpop.apache.org/docs/current/reference/>`_ framework,
with `Janusgraph <http://docs.janusgraph.org/latest/>`_ powering the graph database.

As such, the knowledge graph is a property graph, leading to an intuitive way of modeling the state of the platform. First time readers should focus on understanding the concepts described in `this section <http://tinkerpop.apache.org/docs/current/reference/#vertex-properties>`_ of the tinkerpop documentation.

.. _fig-tinkerpop-model:

.. figure:: /_static/images/tinkerpop.apache.com_the-crew-graph.png
   :width: 600

   -- Knowledge graph representation (Image source: http://tinkerpop.apache.org)

.. _kg_model:

Model
-----

The concepts coming from TinkerPop are extended in the knowledge graph by the addition of a graph type system.
The purpose of the type system is to prevent inconsistent data to be written to the graph (e.g. a file must always have a file name).

Type system
^^^^^^^^^^^
The type system leverages Janusgraph's graph `schema capabilities <http://docs.janusgraph.org/latest/schema.html>`_.

The type system restricts data types to the following subset:

.. tabularcolumns:: |l|l|

+-----------+------------------------------+
| Name      | Description                  |
+===========+==============================+
| String    | Character sequence           |
+-----------+------------------------------+
| Character | Individual character         |
+-----------+------------------------------+
| Boolean   | true or false                |
+-----------+------------------------------+
| Byte      | byte value                   |
+-----------+------------------------------+
| Short     | short value                  |
+-----------+------------------------------+
| Integer   | integer value                |
+-----------+------------------------------+
| Long      | long value                   |
+-----------+------------------------------+
| Float     | 4 byte floating point number |
+-----------+------------------------------+
| Double    | 8 byte floating point number |
+-----------+------------------------------+
| UUID      | UUID                         |
+-----------+------------------------------+

JanusGraph's `automatic schema maker <http://docs.janusgraph.org/latest/schema.html#_automatic_schema_maker>`_ is
also deactivated to strictly enforce data typing.

Property keys are separated into two categories:

- system property keys, which have a global interpretation (e.g the `type` property)
- (regular) property keys, which are directly manipulated

To avoid name clashing, all non-system property keys must follow the pattern
``<namespace>:<name>``, where namespace and name respectively adhere to the regular expressions
``[-A-Za-z0-9_/.]*`` and ``[-A-Za-z0-9_/.]+``. This naming convention is also used with edge labels
and named types.

Named types are used to provide data consistency checks on graph vertices, and are not applicable to edges
nor vertex properties (seen as objects).
They consist of:

- a name, which follows the ``<namespace>:<name>`` pattern
- a set of supertypes, consisting of a set of ``<namespace>:<name>`` values (names)
- a set of property keys, consisting of a set of ``<namespace>:<name>`` values (property keys)

Examples:

1. ``name = "geom:point2d"``, :code:`supertypes = {}`, :code:`properties = { "geom:x", "geom:y" }`
   Here, if a vertex ``v`` is know to be of type :code:`geom:point2d`, then we know that v has
   ``geom:x`` and :code:`geom:y` properties.
2. ``name = "geom:labeledPoint2d"``, :code:`supertypes = { "geom:point2d" }`,
   ``properties = { "geom:x", "geom:y", "geom:label" }``
   Here, if a vertex ``v`` is know to be of type :code:`geom:labeledPoint2d`, then as :code:`geom:point2d` is a supertype of
   ``geom:labeledPoint2d``, :code:`v` is also of type :code:`geom:point2d`.
   Notice also that the properties of ``geom:labeledPoint2d`` are a superset of the properties of type :code:`geom:point2d`.

The type system is initialized with the system property keys, (regular) property keys, edge labels and
named types present in the type_init.json_ file.

The type system concepts are implemented in the graph-core_ module, see package `ch.datascience.graph.types`_.

.. _kg_read:

Read
----

Trusted platform services can use one the `gremlin variants <http://tinkerpop.apache.org/docs/current/reference/#gremlin-variants>`_ to read data from the graph.
The graph traversals must be generated with a graph traversal source marked with the `ReadOnlyStrategy <http://tinkerpop.apache.org/docs/current/reference/#_readonlystrategy>`_.

If vertices or edges are extracted using a graph traversal, it may be desirable to perform the following:

- discard properties that do not follow the ``<namespace>:<name>`` pattern
- in the case of vertices, transform the values from the ``type`` system property into named type constructs
  (by mapping names to the named type construct they are associated with)

These steps are implemented in the VertexReader_ and the EdgeReader_ classes.

.. _kg_update:

Update
------

In a similar fashion as in `the Elm architecture <https://guide.elm-lang.org/architecture/>`_, services
need to send mutation requests to the graph mutation service when they need to update the knowledge graph.

A mutation request consists of a sequence of operations. The whole sequence of operations is processed
in a single transaction, i.e. mutations are atomic with respect to transaction atomicity.
Currently, there are four supported operations:

- ``create_vertex``, create a new vertex in the graph
- ``create_edge``, create a new edge in the graph
- ``create_vertex_property``, add a (property key, value) pairing to a given vertex
- ``update_vertex_property``, modify a vertex property. This is done by first removing the old (property key, value) pairing and then adding the (property key, new value) pairing

The full definition of the graph mutation API resides in the `mutation API spec`_ file.

.. _fig-kg_mutation_seqdiag:

.. uml:: ../_static/uml/graph_mutation.sequence.uml
   :alt: Graph mutation sequence diagram.

Detail of messages:

1. client sends a mutation request as described above
2. mutation service sends back an acknowledgement message containing the request and its assigned **uuid**
3. client requests status of mutation identified by **uuid** received at (2 request received)
4. mutation service sends back the mutation status

The response sent at (4 mutation status) will contain a ``status`` field which can have two values:

- pending: the mutation has not been processed yet
- completed: the mutation has been processed

In the case of ``completed`` status, the response will contain more information about the result of
processing the mutation.
Notably, the response will display an error message if for some reason (e.g. invalid mutation), the mutation failed.
Otherwise, if the mutation was successfully processed, then the response will contain a sequence of graph
identifiers mapped from the incoming mutation request.

.. tabularcolumns:: |l|l|

+----------------------------+---------------------------+
| Request                    | Result id                 |
+============================+===========================+
| ``create_vertex``          | id of the created vertex  |
+----------------------------+---------------------------+
| ``create_edge``            | id of the created edge    |
+----------------------------+---------------------------+
| ``create_vertex_property`` | id of the affected vertex |
+----------------------------+---------------------------+
| ``update_vertex_property`` | id of the affected vertex |
+----------------------------+---------------------------+

Example: consider the following mutation request. ::

    { "operations": [ { "type": "create_vertex", [...] }, { "type": "create_edge", [...] } ] }

Then, the response will contain::

    "results": [ { "id": 1234, "id": "1234->5678" } ]

where ``1234`` is a vertex identifier and :code:`1234->5678` is an edge identifier.

Note that the resulting ids follow the same order as the order of operations in the request.

.. _kg_code_modules:

Code Modules
------------

- graph-core_ - contains definitions for graph elements, typing, etc.
- graph-typesystem_ - contains the graph typesystem management
- graph-mutation_ - contains the graph mutation service
- graph-init_ - contains the code used to initialize the graph type system with definitions in type_init.json_
- graph-navigation_ - contains code to read the graph without a gremlin-shell

.. _graph-core: https://github.com/SwissDataScienceCenter/renga-graph/tree/master/core
.. _graph-typesystem: https://github.com/SwissDataScienceCenter/renga-graph/tree/master/typesystem
.. _graph-mutation: https://github.com/SwissDataScienceCenter/renga-graph/tree/master/mutation
.. _graph-init: https://github.com/SwissDataScienceCenter/renga-graph/tree/master/init
.. _graph-navigation: https://github.com/SwissDataScienceCenter/renga-graph/tree/master/navigation/service

.. _type_init.json: https://github.com/SwissDataScienceCenter/renga-graph/blob/master/init/src/main/resources/type_init.json
.. _VertexReader: https://github.com/SwissDataScienceCenter/renga-graph/blob/master/core/src/main/scala/ch/datascience/graph/elements/tinkerpop_mappers/VertexIdReader.scala
.. _EdgeReader: https://github.com/SwissDataScienceCenter/renga-graph/blob/master/core/src/main/scala/ch/datascience/graph/elements/tinkerpop_mappers/EdgeReader.scala
.. _mutation API spec: https://github.com/SwissDataScienceCenter/renga-graph/blob/master/mutation/service/swagger.yml
.. _`ch.datascience.graph.types`: https://github.com/SwissDataScienceCenter/renga-graph/tree/master/core/src/main/scala/ch/datascience/graph/types
