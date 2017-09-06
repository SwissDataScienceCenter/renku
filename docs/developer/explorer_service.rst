.. _explorer:

Explorer Service
================

Contents
--------

- :ref:`explorer_overview`
- :ref:`explorer_endpoints`

.. _explorer_overview:

Overview
--------

The knowledge graph can be queried using the traversal language Gremlin. This is done in the form of prepared statements. 

.. _explorer_endpoints:

Explorer endpoints
------------------

The explorer API provides the following endpoints.

**GET /storage/list**

  :code:`controllers.StorageExplorerController.bucketList()`

  Retrieve all buckets

**GET /storage/file/:id**

  :code:`controllers.StorageExplorerController.fileMetadata(id: Long)`

  Retrieve metadata of file with id

**GET /storage/:id/files**

  :code:`controllers.StorageExplorerController.fileList(id: Long)`

  Retrieve list of files in bucket with id

**GET /storage/:id**

  :code:`controllers.StorageExplorerController.bucketMetadata(id: Long)`

  Retrieve metadata of bucket with id

**GET /storage/:id/:path**

  :code:`controllers.StorageExplorerController.fileMetadatafromPath(id: Long, path: String)`

  Retrieve metadata of a file with path (filename) in bucket id

**GET /storage/file/:id/versions**

  :code:`controllers.StorageExplorerController.retrievefileVersions( id: Long )`

  Retrieve all versions of a file; the query to find all instances of this filename is executed using the :code:`resource:version_of` edge. The :code:`file_version` resource vertices are returned.

**GET /storage/file/timestamps/:date1/:date2**

  :code:`controllers.StorageExplorerController.retrieveFilesDate(date1: Long, date2: Long)`

  The timestamps are put on the resource:file_version nodes.
  This query retrieves all vertices with a timestamp between date1 and date2. (That means, that date1 and date2 are not included!)
  Currently the files are timestamped with the difference between the current time and midnight, January 1, 1970 UTC, measured in milliseconds.

**GET /storage/list/:userId**

  :code:`controllers.StorageExplorerController.retrieveByUserName(userName: String)`

  Retrieve files with "resource:owner" = UserId. Limited to 100 vertices by default.


**GET /graph/full/**

  :code:`controllers.GenericExplorerController.retrieveGraphSubset`

  Get a limited amount of node->edge->node as [Seq[GraphSubSet]]. Limited to 10 by default.

**GET /graph/node/:id**

  :code:`controllers.GenericExplorerController.retrieveNodeMetaData(id: Long)`

  Retrieve the metadata of a node with a specific id. Valid for all nodes.
  
**GET /lineage/context/:id**                    
  
  :code:`controllers.AnthologyExplorerController.lineageFromContext(id: Long)`

  Get the lineage starting from the deployer node (not implemented yet)

**GET /project/list**                            

  :code:`controllers.ProjectExplorerController.retrieveProjects`

  Get the list with all projects in the graph. Limited to 100 by default.
 
**GET /project/user**

  :code:`controllers.ProjectExplorerController.retrieveProjectByUserName(userId: Option[String] )`

  Get all projects of a user with userId given or else userId from request. Limited to 100 by default.
