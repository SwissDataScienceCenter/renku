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
    !define DEVICONS https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/master/devicons2
    !include DEVICONS/bash.puml
    !include DEVICONS/kubernetes.puml
    !include DEVICONS/gitlab.puml

    HIDE_STEREOTYPE()

    skinparam defaultFontName Calcutta
    skinparam linetype ortho

    Person(logged_in, "Logged-In user")
    Person(cli, "Renku CLI", $sprite="bash")
    System(renku, "", $link="../reference/services/services-architecture.html#container-diagram", $sprite="img:https://renku.readthedocs.io/en/add-architecture-diagram/_static/icons/renku_logo.png{scale=0.1}")
    System(gitlab, "Gitlab", $sprite="gitlab")
    System(keycloak, "Keycloak", $sprite="img:https://renku.readthedocs.io/en/add-architecture-diagram/_static/icons/keycloak_logofinal_1color.png{scale=0.2}")
    System(k8s, "Kubernetes", $sprite="kubernetes")
    SystemDb(s3, "S3")
    SystemDb(azure_blob, "Azure Blob")

    Rel(logged_in, renku, "Uses")
    Rel(cli, renku, "Uses")
    Rel(renku, keycloak, "Auth")
    BiRel(renku, gitlab, "pull/push/events")
    Rel(renku, k8s, "sessions")
    Rel(renku, s3, "Storage")
    Rel(renku, azure_blob, "Storage")
    @enduml

Container Diagram
-----------------

.. uml::

    @startuml
    !include <C4/C4_Container.puml>

    skinparam linetype ortho
    skinparam defaultFontName Calcutta

    HIDE_STEREOTYPE()

    AddElementTag("kubernetes", $shape=EightSidedShape(), $bgColor="CornflowerBlue", $fontColor="white", $legendText="micro service (eight sided)")

    Person(cli, "Renku CLI")
    Person(logged_in, "Logged-In user")
    System_Boundary(renku, "Renku") {
        Container(ui, "UI", "React", "The homepage")
        Container(ui_server, "UI-Server", "ExpressJs", "Backend for Frontend")
        Container(gateway, "Gateway", "Traefik", "API Gateway")
        Boundary(services, "Backend Services") {
            Container(core_service, "core-service", "Python", "Backend service for project interaction", $link="../reference/services/services-architecture.html#core-service")
            Container(renku_graph, "renku-graph", "Scala", "Backend service for project interaction")
            Container(renku_notebooks, "renku-notebooks", "Python", "Interactive session scheduler")
        }
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
    Rel(gateway, services, "forwards requests", "HTTPS")
    Rel_D(core_service, gitlab, "pushes to repository", "Git+SSH")
    Rel(core_service, redis, "cache projects")
    Rel(k8s, amalthea, "watches for session resources", "Custom Resource")
    Rel(k8s, session, "Starts sessions")
    Rel(session, keycloak, "Authenticates users")
    Rel(session, gitlab, "Injects gitlab credentials")
    Rel(amalthea, k8s, "schedules sessions", "K8s API")
    Rel_D(cli, gitlab, "pull/push", "Git+SSH")
    Rel_D(cli, gateway, "Authenticate users")
    Rel_D(cli, renku_notebooks, "manage sessions", "HTTPS")
    Rel(gateway, redis, "get tokens for requests")
    Rel_D(gitlab, postgres, "store/retrieve metadata")
    Rel_D(renku_graph, postgres, "keep gitlab eventlog")
    Rel_D(renku_graph, jena, "store/search triples")
    Rel_D(keycloak, postgres, "store settings/auth")

    Lay_L(cli, logged_in)
    Lay_R(k8s, renku)
    Lay_D(logged_in, renku)
    Lay_D(cli, renku)
    Lay_D(renku, gitlab)
    Lay_D(renku, keycloak)
    Lay_D(services, gitlab)
    Lay_D(services, keycloak)
    Lay_D(gitlab, postgres)
    Lay_D(gitlab, redis)
    Lay_D(gitlab, jena)
    Lay_D(keycloak, postgres)
    Lay_D(keycloak, redis)
    Lay_D(keycloak, jena)
    @enduml

UI
~~

- Web Frontend
- Using Nodejs, Typescript and React

UI-Server
~~~~~~~~~

- Backend-for-frontend Server for the UI
- Using Nodejs, Typescript and ExpressJs

Gateway
~~~~~~~

- API gateway for backend services
- Handles/injects access tokens and credentials
- Based on Traefik with a Flask application as forward-auth middleware

Core-Service
~~~~~~~~~~~~

- API for interacting with metadata stored in user repositories (Project, Datasets, Workflows)
- Built with Python as a Flask app
- uses python-rq for long-running background jobs
- caches project repositories for fast access

Renku-Graph
~~~~~~~~~~~

- Knowledge graph metadata store for storing metadata for all renku projects
- Built with Scala and backed by Jena and Elasticsearch
- Used for queries across projects and datasets

Renku-Notebooks
~~~~~~~~~~~~~~~

- API for scheduling user sessions
- Built with Python as a Flask app
- Provides information on existing sessions
- Creates K8s custom resources to schedule new sessions

Amalthea
~~~~~~~~

- Custom K8s operator for running user sessions
- Built with python and the kopf library
- Watches for custom resources created by renku-notebooks and creates K8s objects for user sessions

Component Diagrams
------------------

Core Service
~~~~~~~~~~~~

.. uml::

    @startuml
    !include <C4/C4_Dynamic.puml>

    skinparam linetype ortho
    skinparam defaultFontName Calcutta

    HIDE_STEREOTYPE()

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
