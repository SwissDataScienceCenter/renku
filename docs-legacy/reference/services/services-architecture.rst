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
    !define DEVICONS https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/v2.4.0
    !include DEVICONS/devicons2/bash.puml
    !include DEVICONS/devicons2/kubernetes.puml
    !include DEVICONS/devicons2/gitlab.puml

    HIDE_STEREOTYPE()

    skinparam defaultFontName "Calcutta","DejaVu Sans Condensed","SansSerif"
    skinparam linetype ortho

    Person(logged_in, "Logged-In user")
    Person(cli, "Renku CLI", $sprite="bash")
    System(renku, "", $link="../reference/services/services-architecture.html#container-diagram", $sprite="img:https://renku.readthedocs.io/en/latest/_static/icons/renku_logo.png{scale=0.2}")
    System(gitlab, "Gitlab", $sprite="gitlab")
    System(keycloak, "Keycloak", $sprite="img:https://renku.readthedocs.io/en/latest/_static/icons/keycloak_logofinal_1color.png{scale=0.2}")
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
    !define DEVICONS https://raw.githubusercontent.com/tupadr3/plantuml-icon-font-sprites/v2.4.0
    !include DEVICONS/devicons2/bash.puml

    skinparam linetype ortho
    skinparam defaultFontName "Calcutta","DejaVu Sans Condensed","SansSerif"

    HIDE_STEREOTYPE()

    AddElementTag("kubernetes", $shape=EightSidedShape(), $bgColor="CornflowerBlue", $fontColor="white", $legendText="micro service (eight sided)")

    Person(cli, "Renku CLI", $sprite="bash")
    Person(logged_in, "Logged-In user")
    System_Boundary(renku, "Renku") {
        Container(ui, "UI", "React", "Web Frontend")
        Container(ui_server, "UI-Server", "ExpressJs", "Backend for Frontend service")
        Container(gateway, "Gateway", "Go", "API Gateway")
        Container(core_service, "core-service", "Python", "Backend service for project interaction", $link="../reference/services/services-architecture.html#core-service")
        Container(renku_graph, "renku-graph", "Scala", "Backend service for Knowledge Graph data")
        Container(renku_notebooks, "renku-notebooks", "Python", "Interactive session management service")
        Container(amalthea, "Amalthea", "Python", "K8s Operator for scheduling session CRDs", $tags="kubernetes")
        Container(session, "User Session")
        Container(renku_crc, "CRC Service", "Python", "Manages compute resource access")
    }
    System(gitlab, "Gitlab")
    System(keycloak, "Keycloak")
    System(k8s, "Kubernetes", $tags="kubernetes")
    SystemDb(postgres, "PostgreSQL")
    SystemDb(redis, "Redis")
    SystemDb(jena, "Jena")

    Rel(logged_in, ui, "")
    Rel(logged_in, session, "")
    Rel(ui, ui_server, "")
    Rel(ui_server, gateway, "")
    Rel(gateway, keycloak, "")
    Rel(gateway, core_service, "")
    Rel(gateway, renku_graph, "")
    Rel(gateway, renku_notebooks, "")
    Rel(gateway, renku_crc, "")
    Rel(renku_notebooks, renku_crc, "")
    Rel(core_service, gitlab, "", "")
    Rel(core_service, redis, "")
    Rel(k8s, amalthea, "", "")
    Rel(k8s, session, "")
    Rel(session, keycloak, "")
    Rel(session, gitlab, "")
    Rel(amalthea, k8s, "", "")
    Rel(cli, gitlab, "", "")
    Rel(cli, gateway, "")
    Rel(cli, renku_notebooks, "")
    Rel(cli, renku_graph, "")
    Rel(renku_notebooks, amalthea, "")
    Rel(gateway, redis, "")
    Rel(gitlab, postgres, "")
    Rel(renku_graph, postgres, "")
    Rel(renku_graph, jena, "")
    Rel(renku_graph, gitlab, "")
    Rel(keycloak, postgres, "")

    Lay_D(cli, renku)
    Lay_D(redis, k8s)
    Lay_R(redis, renku)
    Lay_R(k8s, renku)

    @enduml

UI
~~

- Web Frontend
- Using Nodejs, Typescript and React

UI-Server
~~~~~~~~~

- Backend-for-Frontend Server for the UI
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
- uses `python-rq` for long-running background jobs
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
- Built with python and the `kopf` library
- Watches for custom resources created by renku-notebooks and creates K8s objects for user sessions


CRC Service
~~~~~~~~~~~

- Manages resource pools
- Determines which user has access to which compute resources

.. _renku: https://github.com/SwissDataScienceCenter/renku
.. _renku-python: https://github.com/SwissDataScienceCenter/renku-python
.. _renku-ui: https://github.com/SwissDataScienceCenter/renku-ui
.. _Amalthea: https://github.com/SwissDataScienceCenter/amalthea
.. _GitLab: https://gitlab.com/
.. _Keycloak: https://www.keycloak.org/
