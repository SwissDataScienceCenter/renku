.. _projects:

Projects Service
================

Contents
--------

- :ref:`Projects Data Model<projects_model>`
- :ref:`Projects endpoints<projects_endpoints>`

The projects service exposes a lightweight abstraction used to group other resources together
(deployment contexts, deployment executions, storage buckets, storage files, ...).

.. _projects_model:

Projects Data Model
-------------------

Project
^^^^^^^
Projects within the Renga platform are given a unique identifier that should be used when manipulating them.
In addition, a project will have the following attributes:

* a project name, keyed with `project:project_name`.
* a set of owners, keyed with `resource:owner`.
* a set of labels, keyed with `annotation:label`. This set can be empty.

Children Resources
^^^^^^^^^^^^^^^^^^
Resources created within a given project will be linked to in the knowledge graph with `project:is_part_of` edges.

See :ref:`here for an example involving storage resources<kg_data>`.

.. _projects_endpoints:

Projects endpoints
------------------

The projects API provides the following endpoints (`projects API spec`_).

.. _projects API spec: https://github.com/SwissDataScienceCenter/renga-projects/blob/master/swagger.yml

**GET /**

  List projects.

**GET /:identifier**

  Retrieve project attributes.

**POST /**

  Create a project by supplying a name (mapped to `project:project_name`) and optional labels (mapped to `annotation:label`).
