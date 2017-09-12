.. _first_steps:

First Steps
===========

.. meta::
    :description: First steps with Renga
    :keywords: hello world, first steps, starter, primer

To try out Renga, all you need are `docker <http://www.docker.com>`_, `git
<https://git-scm.com/>`_, and `Python 3.6+ <https://www.python.org>`_.

.. code-block:: console

    $ git clone https://github.com/SwissDataScienceCenter/renga.git
    $ cd renga
    $ make start

The first time you run ``make start`` it will take a little while to download
all the images. Once the script completes, you are ready to interact with Renga.

To work with Renga from the command line, you have to install the renga python
package:

.. code-block:: console

    $ pip install renga

First, create a project directory:

.. code-block:: console

    $ mkdir -p ~/renga-projects/test-project
    $ cd ~/renga-projects/test-project

Set up your platform credentials (using the demo ``docker-compose``
configuration, enter ``demo/demo`` for username/password:

.. code-block:: console

    $ renga login
    Username:
    Password:
    Access token has been stored in: ~/Library/Application Support/Renga/config.yml

Register the project with the platform (note that the ``autosync`` option is
mandatory for now):

.. code-block:: console

    $ renga init --autosync

Create an execution context using a docker container:

.. code-block:: console

    $ renga contexts create hello-world
    context-id: 14971456

This creates a context - we can now run it on an engine:

.. code-block:: console

    $ renga contexts run 14971456
    execution-id:

To see all of your existing contexts:

.. code-block:: console

    $ renga contexts list
    ID               VERTEX_ID    IMAGE
    ---------------  -----------  -----------
    16d1491b         4168         hello-world

And the executions of this context:

.. code-block:: console

    $ renga executions list 16d1491b
    ID          CONTEXT_ID   ENGINE    PORTS
    ----------  -----------  --------  -------
    14971456    16d1491b     docker    []

To create a storage bucket for this project:

.. code-block:: console

    $ renga io buckets create test-project-bucket
    4272
    $ renga io buckets list
      ID  NAME                 BACKEND
    ----  -------------------  ---------
    4272  test-project-bucket  local

At this point, we can start a python shell, create some output and put it into a
file in the project bucket:

.. code-block:: python

    $ python
    Python 3.6.2 (default, Jul 17 2017, 16:44:45)
    [GCC 4.2.1 Compatible Apple LLVM 8.1.0 (clang-802.0.42)] on darwin
    Type "help", "copyright", "credits" or "license" for more information.
    >>> import renga
    >>> client = renga.from_config()
    >>> with client.buckets[4272].open('sample-file', 'w') as fp:
        fp.write('Renga enables collaborative data science.')

At this point, you can inspect the knowledge graph using the browser UI at
http://localhost/ui/#/graph and see how all the components relate to each other.
