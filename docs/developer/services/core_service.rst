.. _core_service:

Core service
============

A lot of Renku core functionality is sometimes needed to be accessed via HTTP or executed
in the background. Main purpose of core service is to address those needs.

Following diagram describes components and architecture of the service:


.. _fig-core-service-architecture:

.. graphviz:: /_static/graphviz/core_service_architecture.dot

The resources are segmented by user and could not be shared between them.
They could be files or projects on top of which user executes subsequent operations. Each resource within the service
has a default time to live after which the resource is evicted.

Sequence diagram of service APIs
""""""""""""""""""""""""""""""""
**POST /<prefix>/cache.files_upload**

An endpoint that allows file upload and tracks it for each user using the service.

.. uml:: ../../_static/uml/core-files-upload.uml


**GET /<prefix>/cache.files_list**

An endpoint to list all uploaded files for a given user.

.. uml:: ../../_static/uml/core-files-list.uml

**POST /<prefix>/cache.project_clone**

An endpoint to clone remote project to service cache. Used as a first step in the
flow for executing operations on top of the cloned project.

.. uml:: ../../_static/uml/core-project-clone.uml

**GET /<prefix>/cache.project_list**

An endpoint to list all cloned project to the service cache for a given user.

.. uml:: ../../_static/uml/core-project-list.uml

**GET /<prefix>/datasets.list**

An endpoint to list of all datasets within a given project. This command is equivalent
to ``renku dataset`` command.

.. uml:: ../../_static/uml/core-datasets-list.uml

**GET /<prefix>/datasets.files_list**

An endpoint to list all dataset files within a given project. This command is equivalent
to ``renku dataset ls-files`` command.

.. uml:: ../../_static/uml/core-datasets-files-list.uml

**POST /<prefix>/datasets.add**

An endpoint to add a file to dataset. This command is equivalent to ``renku dataset add`` command.

The following diagram describes the case when we are adding a file from a local service cache
(e.g. user uploaded files):

.. uml:: ../../_static/uml/core-datasets-add-from-cache.uml

Another case which is supported by this endpoint is the case in which user would like to add a
large file by downloading it from some external place.

.. uml:: ../../_static/uml/core-datasets-add-from-url.uml

**POST /<prefix>/datasets.create**

An endpoint to create a new dataset within a project. This command is equivalent to ``renku dataset create`` command.

.. uml:: ../../_static/uml/core-datasets-create.uml


**POST /<prefix>/datasets.import**

An endpoint for importing datasets from external providers. This command is equivalent
to ``renku dataset import`` command.

.. uml:: ../../_static/uml/core-datasets-import.uml

**POST /<prefix>/datasets.edit**

An endpoint for editing dataset metadata. This command is equivalent to ``renku dataset edit`` command.

.. uml:: ../../_static/uml/core-datasets-edit.uml

