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

  controllers.StorageExplorerController.bucketList()

  Retrieve all buckets

**GET /storage/file/:id**

  controllers.StorageExplorerController.fileMetadata(id: Long)

  Retrieve metadata of file with id

**GET /storage/:id/files**

  controllers.StorageExplorerController.fileList(id: Long)

  Retrieve list of files in bucket with id

**GET /storage/:id**

  controllers.StorageExplorerController.bucketMetadata(id: Long)

  Retrieve metadata of bucket with id

**GET /storage/:id/:path**

  controllers.StorageExplorerController.fileMetadatafromPath(id: Long, path: String)

  Retrieve metadata of a file with path (filename) in bucket id

**GET /storage/file/:id/versions**

  controllers.StorageExplorerController.retrievefileVersions( id: Long )

  Retrieve all versions of a file; the query to find all instances of this filename is executed using the :code:`resource:version_of` edge. The :code:`file_version` resource vertices are returned.

**GET /storage/file/timestamps/:date1/:date2**

  controllers.StorageExplorerController.retrieveFilesDate(  date1: Long, date2: Long  )

  The timestamps are put on the resource:file_version nodes.
  This query retrieves all vertices with a timestamp between date1 and date2. (That means, that date1 and date2 are not included!)
  Currently the files are timestamped with the difference between the current time and midnight, January 1, 1970 UTC, measured in milliseconds.
