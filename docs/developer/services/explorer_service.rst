.. _explorer:

Explorer Service
================

.. contents::
    :depth: 1
    :local:

.. _explorer_overview:

Overview
--------

The knowledge graph can be queried using the traversal language Gremlin. The
explorer service exposes an API that submits queries in the form of prepared
statements.

.. _explorer_endpoints:

Explorer endpoints
------------------

The explorer API provides the following endpoints.

**GET /storage/bucket**

  ``StorageExplorerController.bucketList()``

  Retrieve all buckets

**GET /storage/file/:id**

  ``StorageExplorerController.fileMetadata(id: Long)``

  Retrieve metadata of file with ``id``

**GET /storage/bucket/:id/files**

  ``StorageExplorerController.fileList(id: Long)``

  Retrieve a list of files in bucket with ``id``

**GET /storage/bucket/:id**

  ``StorageExplorerController.bucketMetadata(id: Long)``

  Retrieve metadata of bucket with ``id``

**GET /storage/bucket/:id/files/*path**

  ``StorageExplorerController.fileMetadatafromPath(id: Long, path:
  String)``

  Retrieve metadata of a file with ``path`` (filename) in bucket ``id``

**GET /storage/file/:id/versions**

  ``StorageExplorerController.retrievefileVersions( id: Long )``

  Retrieve all versions of a file with ``id``; the query to find all instances
  of this filename is executed using the ``resource:version_of`` edge. The
  ``file_version`` resource vertices are returned.

**GET /storage/file/timestamps/:date1/:date2**

  ``StorageExplorerController.retrieveFilesDate(date1: Long,
  date2: Long)``

  The timestamps are put on the resource:file_version nodes. This query
  retrieves all vertices with a timestamp between ``date1`` and ``date2``
  (exclusive). Currently the files are timestamped with Unix time.

**GET /storage**

  ``StorageExplorerController.retrieveByUserName(userName:
  String)``

  Retrieve files with ``resource:owner == UserId``. Limited to 100 vertices by
  default.


**GET /graph/full**

  ``GenericExplorerController.retrieveGraphSubset``

  Get a limited amount of ``node->edge->node`` as ``[Seq[GraphSubSet]]``.
  Limited to 10 by default.

**GET /graph/node/:id**

  ``GenericExplorerController.retrieveNodeMetaData(id: Long)``

  Retrieve the metadata of a node with ``id``. Valid for all nodes.

**GET /lineage/context/:id**

  ``AnthologyExplorerController.lineageFromContext(id: Long)``

  Get the lineage starting from the context node with ``id``.

**GET /lineage/file/:id**

  ``LineageExplorerController.lineageFromFile(id: Long)``

  Get the lineage starting from a file node with ``id``.

**GET /projects**

  ``ProjectExplorerController.retrieveProjects``

  Get the list with all projects in the graph. Limited to 100 by default.

**GET /projects/user**

  ``ProjectExplorerController.retrieveProjectByUserName(userId:
  Option[String] )``

  Get all projects of a user with ``userId`` given or else ``userId`` from
  request. Limited to 100 by default.

**GET /projects/:id**

  ``ProjectExplorerController.retrieveProjectMetadata( id: Long
  )``

  Get metadata for project node with ``id``.

**GET /projects/:id/lineage**

  ``ProjectExplorerController.retrieveProjectLineage(id: Long)``

  Get project lineage for project node with ``id``.
