.. _storage:

Storage Service
===============

Contents
--------

- :ref:`Storage Abstraction <stg_abstraction>`
- :ref:`Storage Endpoints <stg_endpoints>`
- :ref:`Storage Workflow <stg_workflow>`
- :ref:`Versioning <stg_versioning>`

The storage service is a thin abstraction layer on top of various storage systems, adding the access control and auditing capabilities.

.. _stg_abstraction:

Storage abstraction
-------------------
To be able to map to a large number of possible backends, we defined here the main concepts, directly inspired by the `Object storage <https://en.wikipedia.org/wiki/Object_storage>`_.

Bucket
^^^^^^

 A bucket is a container that regroups objects/files that would share some common semantics, e.g. multiple files of a same datasets or the outputs of the same execution. In addition to the globally unique identifier, a bucket has a name, an internal name, and a backend. See :ref:`here for the representation in the Knowledge Graph<kg_data>`.

 * The name is given by the user and doesn't necessarily need to be unique. It is mostly used for display purposes.
 * The internal name is unique per backend instance and represents usually the folder/bucket/container name in the specific storage system.
 * The backend is a string (whose choice is limited to a given list per platform deployment) mapping to an instance of a storage backend.

File
^^^^

  A file is a sequence of bytes stored together that is agnostic of its own content. As the reading API allows for accessing directly a range of bytes, it would even by possible to abstract more complex storage structures inside a single file. However it should be noted that writing must be done all at once, in sequence (in the current implementation). In addition to the globally unique identifier, a file has only a name. The file name can contain separators such as "/", allowing to represent a folder hierarchy.

File_Location
^^^^^^^^^^^^^

  A file_location represents a concrete instance of a file stored in a given bucket. A file can have several file_locations, allowing for replication, caching and various optimisations.

.. _stg_endpoints:

Storage endpoints
-----------------

The storage API provides the following endpoints (`storage API spec`_).

.. _storage API spec: https://github.com/SwissDataScienceCenter/renga-storage/blob/master/swagger.yml

Authorization
^^^^^^^^^^^^^

Reading
.......

 **POST /authorize/read**

 The json-encoded body of the request must contain a ReadResourceRequest object with the globally unique identifier of the file to read. First the corresponding graph vertices are retrieved. The service chooses then the most suited file_location, possibly forwarding the request in a federated scenario.
 Then a query to the resource manager is sent for checking the access rights and signing the access authorization. The access authorization contains all the needed information for the backend to retrieve the given file_location, in the form of a json-object that is included in the JWT claim :code:`resource_extras` (You can find more details on the Resource Manager page).
 The response is validated (verifying the JWT signature and content) and forwarded to the client. The user can also provide an execution ID to record which process must be logged in the graph to have read this resource.

Writing
.......

 **POST /authorize/write**

 The process is identical to the previous one, with a WriteResourceRequest object. The file to write must have been first created. This will create a new version of the file. See below for the details about versioning.

Creating a file
...............

 **POST /authorize/create_file**

 The process is similar to the Reading and Writing, except that the request refers to the bucket into which the file should be created and which ACLs will be validated by the Resource Manager.

Creating a bucket
.................

 **POST /authorize/create_bucket**

 To create a bucket, the body of the request must contain a CreateBucketRequest, with the definition of the bucket name, its backend and potentially some backend specific options. The authorizations is then similar to the previous processes.


Input/Output
^^^^^^^^^^^^

Reading a file
..............

 **GET /io/read**

 This call needs an authorization token signed by the Resource Manager and with the scope :code:`storage:read`. The JWT token contains in its :code:`resource_extras` claim all the needed informations for accessing the file. The :code:`Range` html header can also be used (`more info <https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Range>`_). The result is sent back as a chunked response.

Writing a file
..............

 **POST /io/write**

 This call needs an authorization token signed by the Resource Manager and with the scope :code:`storage:write` or :code:`storage:create`. The JWT token contains in its :code:`resource_extras` claim all the needed informations for accessing the file. In the case of an existing file, a new version of the file is created (see below for the versioning). The content of the file is then to be sent as the body of the request. If the authorization fails, the request is immediately aborted.

Listing backends
................

 **GET /io/backends**

 The response is the list of all active backends on this particular deployment, that can be then used as values in the :code:`backend` parameter when creating a bucket.

.. _stg_workflow:

Storage access workflow
-----------------------

 In a typical workflow for accessing a file, the client performs first a preflight call to the corresponding /authorize endpoint and then uses the received JWT in the Authorization header for the subsequent call to the /io endpoint.

 **/authorize/read** is followed by **/io/read**

 **/authorize/write** is followed by **/io/write**

 **/authorize/create_file** is followed by **/io/write**

 **/authorize/create_bucket** directly creates the bucket

.. _stg_versioning:

Versioning
----------

Time-based versioning
^^^^^^^^^^^^^^^^^^^^^

All files are automatically versioned, by the storage backend, every time a new **write** is called on an existing file. The versioning scheme consists in appending the timestamp of the authorization call to the filename. This means that two **write** calls with the same permission token would overwrite a the same file, whereas two calls with different tokens, will create two distinct versions.

At the level of the graph, this is abstracted by :code:`file_version` vertices which are linked to the :code:`file` vertex and that can have one or more :code:`file_location` vertices. File_versions have an attribute with their creation timestamp. Resolving the latest version of a file needs to get all versions and take the one with the largest timestamp.
