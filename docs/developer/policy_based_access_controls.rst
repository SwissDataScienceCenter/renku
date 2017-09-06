.. _policy_based_access_controls:

Policy Based Access Controls
============================

.. _fig-pbac_illustrated:

.. figure:: /_static/images/pbac_illustrated.svg

    Elements of PBAC workflow as implemented in **RENGA**: Policy Enforcement Point (PEP), Policy Decision Point (PDP), Policy Information Point (PIP).


The *Policy Based Access Control* (`PBAC wiki <https://en.wikipedia.org/wiki/Attribute-based_access_control>`_) some time referred as *Attribute Based Access Control* (ABAC),
is a pervasive concept of the platform, on which all resource access controls are based.

In the recommended architecture of the PBAC access control paradigm, the standard workflow is as follows:

- The *Policy Enforcement Point* (PEP) is the client-facing service. It is responsible for guarding the resource. When the PEP receives a request to access a resoure (1) it
  inspects the request and combines it with other contextual information to generate a authorization request to the *Policy Decision Point* (PDP) (2). The PEP enforces the permissions granted
  by the PDP and calls the underlying API business logic to the protected resource backend only if the PDP allows it (8,9,10).
- The *Policy Decision Point* (PDP) is the brain of the architecture. It evaluates the authorization request with access control policies attached to the targeted resource
  retrieved from the *Policy Information Point* (PIP) (3,4) and returns a permit-or-deny decision (5).
- The *Policy Information Point* bridges the PDP to a source of policies. This is where the policies are expressed. The expressivity allows things such as allowing access
  to a data set only if the data is requested by an authorized (signed) application running on behalf of an authorized user, and on an authorized compute cluster.


In **RENGA** we altered the standard PBAC design to decompose the PEP component into an *authorization PEP* service
and an *action PEP* service as illustrated in :numref:`fig-pbac_illustrated`.
At the end of the action phase (6) the client receives from the *authorization PEP* a verifiable,
expiring and unforgeable token that has all the information needed to access the resource.
The client can subsequently use this authorization token for a limited period of time (few minutes) to claim the resource from the *action PEP* service (7-10).

This decomposition into an authorization and action service has a number of advantages:

- It allows the two services to reside on different servers, and it gives the *authorization PEP*
  an opportunity to route the request to the most appropriate *action PEP*.
  In a federated mode of operation this allows the *authorization PEP* to redirect the request to the *authorization PEP* of the organization
  that owns the resource, and the latter can return a token with its *action PEP* as the target. The redirect is transparent (but not unberknowst)
  to the requesting client.
- It allows the authorization request to be cascaded to multiple *authorization PEPs* dependencies before acting on the response.
  It also gives the client an opportunity to choose between several alternatives before making a decision.
  This is particularly useful when accessing data that is mirrored in several locations, each possibly governed by different permission policies, SLA
  and pricing models.
- The design allows for a parallel access to the resource, such as using the same authorization token
  for reading shards of the requested data from different processes executing in parallel.

In the **RENGA** architecture, it is the :ref:`resource_manager` that fullfills the role of the PDP. The PIP is served by the :ref:`knowledge_graph`.
The PEP services are implemented by a number independent resource provider services, currently :ref:`storage` and :ref:`deployer`.

In addition the resource backend is not directly accessible to the user.
The PEP is an obligatory point of passage to the resource and it is thus here that the logic for updating the lineage in the knowledge graph is implemented (6a).


