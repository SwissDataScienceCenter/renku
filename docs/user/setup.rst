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

* python 3.6
* `pip <https://pypi.org/project/pip/>`_ (or `pipenv <https://github.com/pypa/pipenv>`_)
* `Docker <http://www.docker.com>`_
* `Docker Compose >=1.14 <https://docs.docker.com/compose/install/>`_
  (**Linux Only**)
* `GNU make <https://www.gnu.org/software/make/>`_

.. note::

    On Mac OS X, we recommend that you set the amount of memory available
    to Docker to 6GB (under Docker Preferences --> Advanced).

For Kubernetes deployments:

* a `Kubernetes <https://kubernetes.io/>`_ cluster or `minikube <https://kubernetes.io/docs/getting-started-guides/minikube/>`_
* `helm <https://helm.sh/>`_ (>= 2.9.1)


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
----------


Docker
^^^^^^

You can get going with Renku in a few minutes by using our pre-built images:

.. code-block:: console

    $ cd renku
    $ make .env
    $ ./scripts/renku-start.sh

Once the script completes, you can go to http://renku.build to see the
browser front-end.

`make .env` creates a `.env` file which contains environment variables used
as configuration for various platform components. You can override these
values either by specifying them on the command line *when calling `make .env`*,
or you can modify them in `.env` after it has been created. Note that once
the `.env` file is in place, the values specified there take precedent.

Using the default configuration, you can login to all services using
`demo/demo` as the username/password. See :ref:`user_management` for more
information about handling user accounts.


Kubernetes
^^^^^^^^^^

Please follow the instructions in ``charts/renku/README.rst``.


Building from source
^^^^^^^^^^^^^^^^^^^^

.. note::
    Unless you are developing Renku components or trying a bleeding edge
    version of a service, you should not need to build from source.

.. code-block:: console

    $ cd renku
    $ make

This will build the images of *all* Renku services. To build a single service,
you can simply use, for example

.. code-block:: console

    $ make renku-ui

``make`` assumes that  the base directory of the platform is the parent
directory of `renku`. If you want to specify a different path, use the ``-e``
option:

.. code-block:: console

    $ mkdir -p /path/to/base/renku/directory
    $ make -e PLATFORM_BASE_DIR=/path/to/base/renku/directory

Once ``make`` completes, you should now have all the service images made:

.. code-block:: console

    $ docker images
    REPOSITORY                  TAG             IMAGE ID
    renku/gitlab-runner         latest          b36beaf93cba
    renku/renku-python          latest          0670bbcb22ed
    renku/renku-storage         latest          e73374425a1f
    renku/renku-ui              latest          3aa6ddac8eee

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

    ...

    [Success] Renku UI should be under http://renku.build and GitLab under http://gitlab.renku.build


Identity Management
-------------------------

A default user ``demo`` with password ``demo`` is configured in the identity
manager Keycloak. The administration console of Keycloak is available at
http://localhost/auth/admin, with the user ``admin`` and password ``admin``
(`Keycloak documentation <http://www.keycloak.org/documentation.html>`_).


Platform Endpoint
-----------------

By default, the platform is configured to use ``http://renku.build`` as the
endpoint. You can change this by defining the ``RENKU_ENDPOINT`` environment
variable before starting the platform services.
