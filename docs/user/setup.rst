.. _setup:

Running the platform
====================

The renga source code is hosted on github:
https://github.com/SwissDataScienceCenter/renga.

Renga is deployed as a collection of microservices (see
:ref:`service_architecture`) running in Docker containers. To run Renga, only a
working version of Docker is required. In the Renga repository, we provide a
`docker-compose` file, which can be used to deploy renga quickly either on a
local machine or with a cloud provider. However, be aware that if you want to
use Renga in production with many users, a more complicated deployment will be
required.

Prerequisites
-------------

* `Docker <http://www.docker.com>`_
* `GNU make <https://www.gnu.org/software/make/>`_
* `sbt <http://www.scala-sbt.org/>`_


Getting the code and images
---------------------------

Get the platform
^^^^^^^^^^^^^^^^

.. code-block:: console

    $ mkdir src
    $ cd src
    $ git clone https://github.com/SwissDataScienceCenter/renga.git

``renga`` is the "parent" repository of the collection of microservices, each of
which has their own repository.


Quickstart
^^^^^^^^^^

You can get going with ``renga`` in a few minutes by using our pre-built images:

.. code-block:: console

    $ cd renga
    $ docker-compose pull
    $ docker-compose up && ./wait-for-services.sh

Once the script completes, you can go to http://localhost/ui to see the browser
front-end or http://localhost/admin/swagger/ to see the Swagger REST API.

A default user ``demo`` with password ``demo`` is configured in the identity
manager Keycloak.


Building from source
^^^^^^^^^^^^^^^^^^^^

.. code-block:: console

    $ cd renga
    $ make

``make`` assumes that the base directory of the platform is the parent directory
of `renga`. If you want to specify a different path, use the ``-e`` option:

.. code-block:: console

    $ mkdir -p /path/to/base/renga/directory
    $ make -e PLATFORM_BASE_DIR=/path/to/base/renga/directory

Once ``make`` completes, you should now have all the service images made:

.. code-block:: console

    $ docker images
    REPOSITORY                       TAG                 IMAGE ID
    renga-deployer                   latest              883bada01727
    renga-storage                    latest              2fd5a8585e69
    renga-projects                   latest              28616eddc51f
    renga-graph-typesystem-service   latest              a4c3d21acc28
    renga-graph-navigation-service   latest              11fc7285e580
    renga-graph-mutation-service     latest              2515cc934f1d
    renga-graph-init                 latest              4bda6baad1b0
    renga-explorer                   latest              47fb4ec835ec
    renga-authorization              latest              8ca37566b674

Use ``docker-compose`` to bring up the platform:

.. code-block:: console

    $ make start
    ...
    Creating renga_graph-init_1 ... done
    Creating renga_deployer_1
    Creating renga_storage_1 ... done
    Waiting for keycloak:8080  .....................................  up!
    Waiting for deployer:5000  .  up!
    Waiting for explorer:9000  .  up!
    Waiting for graph-mutation:9000  .......................  up!
    Waiting for graph-navigation:9000  .  up!
    Waiting for graph-typesystem:9000  ........  up!
    Waiting for resource-manager:9000  ...........  up!
    Waiting for storage:9000  .  up!
    Everything is up


To check on the status of the services, use standard ``docker-compose``
commands:

.. code-block:: console

    $ docker-compose ps
    Name                        Command               State
    --------------------------------------------------------------
    renga_apispec_1            uwsgi --http :5000 --wsgi- ...   Up
    renga_db_1                 docker-entrypoint.sh postgres    Up
    renga_deployer_1           ./docker-entrypoint.sh fla ...   Up
    renga_explorer_1           bin/renga-explorer               Up
    renga_graph-mutation_1     bin/renga-graph-mutation-s ...   Up
    renga_graph-navigation_1   bin/renga-graph-navigation ...   Up
    renga_graph-typesystem_1   bin/renga-graph-typesystem ...   Up
    renga_keycloak_1           /opt/jboss/docker-entrypoi ...   Up
    renga_projects_1           bin/renga-projects               Up
    renga_resource-manager_1   bin/renga-authorization          Up
    renga_reverse-proxy_1      /traefik --web --web.addre ...   Up
    renga_storage_1            bin/docker-entrypoint.sh b ...   Up
    renga_swagger_1            sh /usr/share/nginx/docker ...   Up
    renga_ui_1                 python3 /app/server/run.py       Up

You can now point your browser to http://localhost/ui for the web front-end, or
to http://localhost/admin/swagger for the swagger REST API spec.


Default behaviour
^^^^^^^^^^^^^^^^^

A default user ``demo`` with password ``demo`` is configured in the identity
manager Keycloak. The administration console of Keycloak is available at
http://localhost/auth/admin, with the user ``admin`` and password ``admin``
(`Keycloak documentation <http://www.keycloak.org/documentation.html>`_).
The storage backend uses the folder ``./services/storage/data`` to store
the buckets and files. The deployer backend uses the local docker instance
to execute containers.
