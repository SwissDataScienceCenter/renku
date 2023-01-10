.. _service_architecture:

Renku Architecture
==================

This document describes the architecture of the services that are parts
of the **Renku** platform.

.. contents::
    :depth: 1
    :local:



Renku consists of several off-the-shelf components from the
software engineering and data science software stacks, as well as customized or
newly developed services. The services communicate among each other through
REST APIs. The deployment is orchestrated on Kubernetes through the use of
Helm charts. Because of its micro-service architecture, Renku is by definition
modular, and therefore able to accommodate other languages or implementations
used for individual components.

Components
----------

Renku has the following custom components:

- renku_: the meta repository with deployment scripts, documentation and kubernetes helm charts
- :ref:`renku-gateway <api_gateway>`: an API gateway connecting clients to the APIs of the different backend services
- :ref:`renku-graph <graph_services>`: a collection of services concerned with activating, building and querying the Renku knowledge graph
- :ref:`renku-notebooks <notebooks_service>`: a service integrating GitLab repositories with Amalthea and Jupyter servers
- renku-python_: python API & Command Line Interface (CLI)
- renku-ui_: web front-end interface


In addition, we make use of:

- Amalthea_: k8s operator used to launch Jupyter servers
- GitLab_: repository management, CI and various related APIs
- Keycloak_: identity management and user authentication

The figure below shows an overview of the components
and their interactions. Blue components are off-the-shelf, yellow components
are either built or extended by us.

System Context
--------------

.. uml::

    @startuml
    !include <C4/C4_Context>

    skinparam linetype ortho

    Person(logged_in, "Logged-In user")
    Person(cli, "Renku CLI")
    System(renku, "Renku", $link="../reference/services/services-architecture.html#container-diagram")
    System(gitlab, "Gitlab")
    System(keycloak, "Keycloak")
    System(k8s, "Kubernetes")
    SystemDb(postgres, "PostgreSQL")
    SystemDb(redis, "Redis")
    SystemDb(jena, "Jena")
    SystemDb(s3, "S3")
    SystemDb(azure_blob, "Azure Blob Storage")

    Rel(logged_in, renku, "Uses", "HTTPS")
    Rel(cli, renku, "Uses", "HTTPS/Git+SSH")
    Rel(renku, keycloak, "Auth", "HTTPS")
    BiRel(renku, gitlab, "pull/push/events", "Git+SSH")
    Rel(renku, k8s, "starts sessions", "K8s API")
    Rel(renku, postgres, "read/write")
    Rel(renku, redis, "read/write")
    Rel(renku, jena, "store triples")
    Rel(renku, s3, "Use as storage")
    Rel(renku, azure_blob, "Use as storage")
    @enduml

Container Diagram
-----------------

.. uml::

    @startuml
    !include <C4/C4_Container.puml>

    skinparam linetype ortho

    AddElementTag("kubernetes", $shape=EightSidedShape(), $bgColor="CornflowerBlue", $fontColor="white", $legendText="micro service (eight sided)")

    Person(logged_in, "Logged-In user")
    Person(cli, "Renku CLI")
    System_Boundary(renku, "Renku") {
        Container(ui, "UI", "React", "The homepage")
        Container(ui_server, "UI-Server", "Node", "Backend for Frontend")
        Container(gateway, "Gateway", "Traefik", "API Gateway")
        Container(core_service, "core-service", "Python", "Backend service for project interaction", $link="../reference/services/services-architecture.html#core-service")
        Container(renku_graph, "renku-graph", "Scala", "Backend service for project interaction")
        Container(renku_notebooks, "renku-notebooks", "Python", "Interactive session scheduler")
        Container(amalthea, "Amalthea", "Python", "K8s Operator for scheduling sessions", $tags="kubernetes")
        Container(session, "User Session")
    }
    System(gitlab, "Gitlab")
    System(keycloak, "Keycloak")
    System(k8s, "Kubernetes", $tags="kubernetes")
    SystemDb(postgres, "PostgreSQL")
    SystemDb(redis, "Redis")
    SystemDb(jena, "Jena")

    Rel_D(logged_in, ui, "Uses", "HTTPS")
    Rel(ui, ui_server, "Uses", "HTTPS")
    Rel(ui_server, gateway, "Uses", "HTTPS")
    Rel(gateway, keycloak, "Gets tokens from", "HTTPS")
    Rel(gateway, core_service, "forwards requests", "HTTPS")
    Rel_D(core_service, gitlab, "pushes to repository", "Git+SSH")
    Rel(core_service, redis, "cache projects")
    Rel(gateway, renku_notebooks, "forwards requests", "HTTPS")
    Rel(renku_notebooks, k8s, "creates session resources", "Custom Resource")
    Rel(k8s, amalthea, "watches for session resources", "Custom Resource")
    Rel(k8s, session, "Starts sessions")
    Rel(session, keycloak, "Authenticates users")
    Rel(session, gitlab, "Injects gitlab credentials")
    Rel(amalthea, k8s, "schedules sessions", "K8s API")
    Rel(gateway, renku_graph, "forward requests", "HTTPS")
    Rel_D(cli, gitlab, "pull/push", "Git+SSH")
    Rel_D(cli, gateway, "Authenticate users")
    Rel_D(cli, renku_notebooks, "manage sessions", "HTTPS")
    Rel(gateway, redis, "get tokens for requests")
    Rel_D(gitlab, postgres, "store/retrieve metadata")
    Rel_D(renku_graph, postgres, "keep gitlab eventlog")
    Rel_D(renku_graph, jena, "store/search triples")
    Rel_D(keycloak, postgres, "store settings/auth")

    Lay_D(logged_in, renku)
    Lay_D(cli, renku)
    Lay_D(renku, gitlab)
    Lay_D(renku, keycloak)
    Lay_D(renku, k8s)
    Lay_D(renku, postgres)
    Lay_D(renku, redis)
    Lay_D(renku, jena)
    @enduml

Component Diagrams
------------------

Core Service
~~~~~~~~~~~~

.. uml::

    @startuml
    !include <C4/C4_Dynamic.puml>

    skinparam linetype ortho

    Component_Ext(browser, "Browser")

    Component_Ext(ingress, "Ingress")

    Container_Boundary(gateway, "API Gateway") {
        Component(gateway_traefik, "Traefik")
        Component(gateway_auth, "Gateway Auth")
    }

    Container_Boundary(core_service_boundary, "core-service") {
        Component(traefik, "Traefik")
        Component(core_service, "core-service", "Python")
    }

    Rel_R(browser, ingress, "")
    Rel_R(ingress, gateway_traefik, "")
    Rel(gateway_traefik, traefik, "")
    BiRel(traefik, gateway_auth, "Exchange JWT")
    Rel(traefik, core_service, "")
    Lay_R(gateway_traefik, gateway_auth)
    @enduml

.. _renku: https://github.com/SwissDataScienceCenter/renku
.. _renku-python: https://github.com/SwissDataScienceCenter/renku-python
.. _renku-ui: https://github.com/SwissDataScienceCenter/renku-ui
.. _Amalthea: https://github.com/SwissDataScienceCenter/amalthea
.. _GitLab: https://gitlab.com/
.. _Keycloak: https://www.keycloak.org/
