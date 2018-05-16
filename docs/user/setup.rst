.. _setup:

.. spelling::

    Quickstart

Running the platform
====================

The renku source code is hosted on github:
https://github.com/SwissDataScienceCenter/renku.

Renku is deployed as a collection of microservices (see
:ref:`service_architecture`) running in Docker containers. To run Renku, only
a working version of Docker is required. In the Renku repository, we provide a
`docker-compose` file, which can be used to deploy renku quickly either on a
local machine or with a cloud provider. However, be aware that if you want to
use Renku in production with many users, a more complicated deployment will be
required.

Prerequisites
-------------

* `Docker <http://www.docker.com>`_
* `Docker Compose >=1.14 <https://docs.docker.com/compose/install/>`_
  (**Linux Only**)
* `GNU make <https://www.gnu.org/software/make/>`_
* `sbt <http://www.scala-sbt.org/>`_

.. note::

    On Mac OS X, we recommend that you set the amount of memory available
    to Docker to 6GB (under Docker Preferences --> Advanced).


Getting the code and images
---------------------------

Get the platform
^^^^^^^^^^^^^^^^

.. code-block:: console

    $ mkdir src
    $ cd src
    $ git clone https://github.com/SwissDataScienceCenter/renku.git

``renku`` is the "parent" repository of the collection of microservices, each
of which has their own repository.


.. _quickstart:

Quickstart
^^^^^^^^^^

.. include:: ./development-note.rst

You can get going with Renku in a few minutes by using our pre-built images:

.. code-block:: console

    $ cd renku
    $ make start

Once the script completes, you can go to http://renku.local to see the
browser front-end.

Using the default configuration, you can login to all services using
`demo/demo` as the username/password. See :ref:`user_management` for more
information about handling user accounts.


Building from source
^^^^^^^^^^^^^^^^^^^^

.. include:: ./development-note.rst

.. code-block:: console

    $ cd renku
    $ make

``make`` assumes that the base directory of the platform is the parent
directory of `renku`. If you want to specify a different path, use the ``-e``
option:

.. code-block:: console

    $ mkdir -p /path/to/base/renku/directory
    $ make -e PLATFORM_BASE_DIR=/path/to/base/renku/directory

Once ``make`` completes, you should now have all the service images made:

.. code-block:: console

    $ docker images
    REPOSITORY                      TAG                    IMAGE ID
    renkuhub/gitlab-runner          development            b36beaf93cba
    renkuhub/renku-python           development            0670bbcb22ed
    renkuhub/renku-storage          development            e73374425a1f
    renkuhub/renku-ui               development            3aa6ddac8eee

Use ``docker-compose`` to bring up the platform:

.. code-block:: console

    $ make start
    [Info] Using Docker network: review=8112d474690a
    ...
    renku_reverse-proxy_1 is up-to-date
    renku_ui_1 is up-to-date
    renku_db_1 is up-to-date
    renku_gitlab-runner_1 is up-to-date
    renku_keycloak_1 is up-to-date
    renku_gitlab_1 is up-to-date
    Waiting for keycloak:8080  .  up!
    Waiting for gitlab:80  .  up!
    Waiting for ui:3000  .  up!
    Everything is up

    [Warning] You have not defined a GITLAB_CLIENT_SECRET. Using dummy
              secret instead. Never do this in production!


    [Success] Renku UI should be under http://renku.local and GitLab under http://gitlab.renku.local

    [Info] Register GitLab runners using:
             make register-runners


Identification Management
-------------------------

A default user ``demo`` with password ``demo`` is configured in the identity
manager Keycloak. The administration console of Keycloak is available at
http://localhost/auth/admin, with the user ``admin`` and password ``admin``
(`Keycloak documentation <http://www.keycloak.org/documentation.html>`_).


Platform Endpoint
-----------------

By default, the platform is configured to use ``http://renku.local`` as the
endpoint. You can change this by defining the ``RENKU_ENDPOINT`` environment
variable before starting the platform services.
