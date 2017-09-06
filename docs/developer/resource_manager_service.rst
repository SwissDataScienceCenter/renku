.. _resource_manager:

Resource Manager Service
========================

Contents
--------

- :ref:`rm-overview`
- :ref:`Authorization Flow <rm-authorization_flow>`
- :ref:`Attributes <rm-attributes>`
- :ref:`Rules <rm-rules>`
- :ref:`Response from the RM <rm-response>`
- :ref:`Accessing multiple resources and encapsulation <rm-encapsulation>`

.. _rm-overview:

Overview
--------

The resource manager (RM) service is responsible for authorizing access requests to resources described in the
:ref:`knowledge_graph`.
The role of the RM is to apply Policy-Based Access Control (:ref:`PBAC <policy_based_access_controls>`) rules to grant or reject access to resources.

.. _rm-authorization_flow:

Authorization Flow
------------------

Whenever a user calls one of the platform services, said service may require to access
to resources on behalf of the user.
The same applies in the case of an application performing a request (e.g. read a file); the service
will request access to the resource on behalf of the user who launched that application.

.. _fig-resource_manager_api:

.. uml:: ../_static/uml/resource_manager_api.sequence.uml
   :alt: Sequence diagram of Resource Manager authorization request

Detail of messages:

1. client calls a platform service
2. service sends AccessRequest to RM
3. RM sends back authorization token
4. service sends a message containing the authorization token from the RM

For more details on the RM API, refer to the `Resource Manager API spec`_.

.. _Resource Manager API spec: https://github.com/SwissDataScienceCenter/renga-authorization/blob/master/swagger.yml

.. _rm-attributes:

Attributes
----------

The attributes used by the RM come from these sources:

1. the request itself - contains the id of the accessed resource (optional) and the requested permissions
2. the request context - extracted from the access token included in the HTTP headers
3. the knowledge graph - in general, these are metadata fetched using the id of the requested resource

From the request
^^^^^^^^^^^^^^^^

The request body contains:

- the subject of the access request is specified in the :code:`resource_id` field of the request body. Its absence is to be interpreted as a global access request (e.g. permission to create a bucket).
- the access permissions requested are described in the :code:`scope` field
- the content of the request (provided by the requesting service) is held in :code:`resource_id` (main subject) and :code:`service_claims` (detailed subject).

From the context
^^^^^^^^^^^^^^^^

The access token contains:

- the user who is requesting access, in the :code:`sub` claim
- attributes about the user (Note: need to configure roles, etc. from keycloak for this)
- if the request comes from a deployed application, the id representing that application in the knowledge graph. Note: as we do not generate tokens inserted in deployed containers as of today, this attribute is not handed to the RM.

From the knowledge graph
^^^^^^^^^^^^^^^^^^^^^^^^

If a :code:`resource_id` was specified in the request, then the RM will fetch the corresponding
vertex from the knowledge graph.
Note: It is planned to allow more complex data to be retrieved, but a specific graph ontology needs to
be defined first (:code:`authorization:extends` edge label for instance).

.. _rm-rules:

Rules
-----

No rule framework devised as of today.

This implies that the RM will allow access to any resource, provided that it exists and the request is valid.

.. _rm-response:

Response from the RM
--------------------

- the response is a json object containing a token at the :code:`access_token` field
- if a :code:`https://rm.datascience.ch/resource_id` was present, a :code:`resource_id` claim is present in the returned token
- the :code:`https://rm.datascience.ch/scope` field holds the granted scope (i.e. permissions), which can be empty (no permission granted)
- the optional :code:`https://rm.datascience.ch/service_claims` will contain a serialized json object of the same value as the incoming :code:`service_claims`

.. _rm-encapsulation:

Accessing multiple resources and encapsulation
----------------------------------------------

In some cases, a service may need to request access to multiple resources to fulfill the client request.

One such example could be when a client wants to create a deployment using a code repository.
There, the service which will create this deployment will need to ask for the right to create
a deployment and for the right to read/clone the code.

When these cases are brought up with the need to split the interface into an authorization call
followed by an action call (e.g. storage auth then io, or deployment auth then deploy),
a good practice is to use authorization token encapsulation.

Token encapsulation consists of first asking for authorization on all sub-resources,
then encapsulate all authorization tokens into the main authorization call on the Resource Manager.
The tokens are simply passed around in the :code:`service_claims` field and will come back intact in the :code:`https://rm.datascience.ch/service_claims` claim.

Example:

.. _fig-local_deployment:

.. uml:: ../_static/uml/local_deployment.sequence.uml
   :alt: Sequence diagram of local application deployment.


Message 5 :code:`getAuth` contains the authorization token from response 4 :code:`repoAuth` in the :code:`repo_auth_token` field as shown below:

.. highlight:: json

::

        {
          "permission_holder_id": 4356,
          "scope": "deployment:create",
          "extra_claims": {
            "xyz": "something",
            "...": "...",
            "repo_auth_token": "eyJdsfss...sdfssAA="
          }
        }



During the action call, the service can now parse and verify the authorization token to process
the request.
When other resources need to be accessed (e.g. code during deploy), the service can simply call
the action on the corresponding service using the proper token extracted from the
:code:`https://rm.datascience.ch/service_claims` claim of the encapsulating token.

.. highlight:: python

::

        /---------------------------------------------------------\
        | Main authorization token                                |
        |---------------------------------------------------------|
        | sub: john doe                                           |
        | ...                                                     |
        | https://rm.datascience.ch/resource_id: 4356             |
        | https://rm.datascience.ch/scope: "deployment:create"    |
        | https://rm.datascience.ch/service_claims:               |
        |  |- language: python                                    |
        |  |- ...                                                 |
        |  |- repo_auth_token:                                    |
        |     /-----------------------------------------------\   |
        |     | Encapsulated authorization                    |   |
        |     | token                                         |   |
        |     |-----------------------------------------------|   |
        |     | sub: john doe                                 |   |
        |     | ...                                           |   |
        |     | https://rm.datascience.ch/resource_id: 8734   |   |
        |     | https://rm.datascience.ch/scope: "code:clone" |   |
        |     \-----------------------------------------------/   |
        \---------------------------------------------------------/


[TODO: why encapsulation is hard to avoid and limitations of this model]
