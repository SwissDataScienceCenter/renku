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

  ``StorageExplorerController.bucketList``

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

**GET /storage/bucket/:id/files/:path**

  ``StorageExplorerController.fileMetadatafromPath(id: Long, path:  String)``

  Retrieve metadata of a file with ``path`` (filename) in bucket ``id``

**GET /storage/file/:id/versions**

  ``StorageExplorerController.retrievefileVersions( id: Long )``

  Retrieve all versions of a file with ``id``; the query to find all instances
  of this filename is executed using the ``resource:version_of`` edge. The
  ``file_version`` resource vertices are returned.

**GET /storage/file/timestamps/:date1/:date2**

  ``StorageExplorerController.retrieveFilesDate(date1: Long,  date2: Long)``

  The timestamps are put on the resource:file_version nodes. This query
  retrieves all vertices with a timestamp between ``date1`` and ``date2``
  (exclusive). Currently the files are timestamped with Unix time.

**GET /storage**

  ``StorageExplorerController.retrieveByUserName(userId:  String)``

  Retrieve files with ``resource:owner == UserId``. Limited to 100 vertices by
  default.


**GET /graph/full**

  ``GenericExplorerController.retrieveGraphSubset``

  Get a limited amount of ``node->edge->node`` as ``[Seq[GraphSubSet]]``.
  Limited to 10 by default.

**GET /graph/node/:id**

  ``GenericExplorerController.retrieveNodeMetaData(id: Long)``

  Retrieve the metadata of a node with ``id``. Valid for all nodes.

**GET /graph/node/:id/edges**                    

  ``GenericExplorerController.retrieveNodeEdges( id: Long )``

  Get all in- and outgoing edges of a node with  ``id``.


**GET /graph/nodes/:prop**                       

  ``GenericExplorerController.retrieveNodesWithProperty( prop: String )``

  Returns a list with all nodes which have property ``prop``. The property should be a string.


**GET /graph/:prop/values**                      

  ``GenericExplorerController.getValuesForProperty( prop: String )``

  Returns a list with all values in the graph for property ``prop``. (Values can be of any supported type)

**GET /graph/nodes/:prop/:value**

  ``GenericExplorerController.retrieveNodePropertyAndValue( prop: String, value: String)``

  Returns a list with all nodes that have property ``prop`` 
  with value ``value``  
  .

**GET /lineage/context/:id**

  ``LineageExplorerController.lineageFromContext(id: Long)``

  Get the lineage starting from the context node with ``id`` by traversing over the edges "deployer:launch", "resource:create", "resource:write" and "resource:read". 

**GET /lineage/file/:id**

  ``LineageExplorerController.lineageFromFile(id: Long)``

  Get the lineage starting from a file node with ``id``.

**GET /lineage/project/:id/**

  ``LineageExplorerController.retrieveProjectLineage(id: Long)``

  Get the lineage of a project with ``id`` by iterating over the  "deployer:launch", "project:is_part_of", "project:used_by" edges. This is different than the other lineage queries where the edges
   of resource create/write/read are traversed.

**GET /projects**

  ``ProjectExplorerController.retrieveProjects``

  Get the list with all projects in the graph. Limited to 100 by default.

**GET /projects/user**

  ``ProjectExplorerController.retrieveProjectByUserName(userId:   Option[String] )``

  Get all projects of a user with ``userId`` 
  given or else ``userId`` from
  request. Limited to 100 by default.

**GET /projects/:id**

  ``ProjectExplorerController.retrieveProjectMetadata( id: Long )``

  Get metadata for project node with ``id``.

**GET /projects/:id/resources**

  ``ProjectExplorerController.retrieveProjectResources( id: Long, resource: Option[String] )``

  Get project resources ``resource`` (file, bucket, execution or context) 
  for project node with ``id``. 
  If no ``resource`` is specified all types are returned. If a 
  ``resource`` is given that is not part of the 4 allowed types an error is given.
