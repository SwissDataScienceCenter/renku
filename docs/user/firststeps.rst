.. _first_steps:

First Steps
===========

.. meta::
    :description: First steps with Renga
    :keywords: hello world, first steps, starter, primer

To try out Renga, you first need a platform to connect to: see :ref:`setup`
for instructions on how to get one running on your own machine in a few minutes.

Interaction with the platform happens via the python-based command-line-
interface (CLI) and the python API. You can get both via pip:

.. code-block:: console

    $ pip install renga

Our first Renga project
---------------------------

First, create a project directory:

.. code-block:: console

    $ mkdir -p ~/renga-projects/test-project
    $ cd ~/renga-projects/test-project

Set up your platform credentials (using the demo ``docker-compose``
configuration, enter ``demo/demo`` for username/password:

.. code-block:: console

    $ renga login http://localhost
    Username:
    Password:
    Access token has been stored in: ~/Library/Application Support/Renga/config.yml

Register the project with the platform (note that the ``autosync`` option is
mandatory for now):

.. code-block:: console

    $ renga init --autosync

Creating a project deployment
-----------------------------

Renga can be used to launch computational tasks that run inside docker
containers. For this, you must first create an execution "context" which defines
the task to be carried out. This is then followed by executing the context on
one of the platform deployment engines.

Create an execution context using a docker container:

.. code-block:: console

    $ renga contexts create hello-world
    14971456

This creates a context - we can now run it on an engine:

.. code-block:: console

    $ renga contexts run 14971456 docker
    16d1491b

To see all of your existing contexts:

.. code-block:: console

    $ renga contexts list
    ID               VERTEX_ID    IMAGE
    ---------------  -----------  -----------
    16d1491b         4168         hello-world

And the executions of this context:

.. code-block:: console

    $ renga executions list 14971456
    ID          CONTEXT_ID   ENGINE    PORTS
    ----------  -----------  --------  -------
    16d1491b    14971456     docker    []


Creating and populating a storage bucket
----------------------------------------

To create a storage bucket for this project:

.. code-block:: console

    $ renga io buckets create project-bucket
    4272
    $ renga io buckets list
      ID  NAME                 BACKEND
    ----  -------------------  ---------
    4272  project-bucket       local

At this point, we have created a project, linked it to a storage bucket and a
container deployment. However, our "hello-world" container didn't really do
much. A more interesting container to run is an interactive `jupyter notebook
<http://jupyter.org>`_ and if we launch it using ``renga``, we can automatically
link the creation of any derived data to our project:

.. code-block:: console

    $ renga notebooks launch
    beedcadb-4ae0-4678-ab02-9f567c866076
    http://0.0.0.0:32956/?token=8514bb62

You can use this link to open the notebook in your browser - at any later point
you can see your current notebooks with

.. code-block:: console

    $ renga notebooks list
        ENGINE    URL
    --  --------  ------------------------------------
     1  docker    http://0.0.0.0:32956/?token=8514bb62

Once inside the notebook, start a new python notebook and install ``renga``:

.. code-block:: ipython

    In [1]: !pip install renga

Now we can import the ``renga`` python API and interact with the platform:

.. code-block:: ipython

    In [2]: import renga
    In [3]: client = renga.from_env()

We can check that the bucket we created earlier for our project is available:

.. code-block:: ipython

    In [4]: for bucket in client.buckets.list():
                print(bucket)
    <Bucket 4272>

And we can write data to a file stored within this bucket:

.. code-block:: ipython

    In [5]: with client.buckets[4272].open('sample-file', 'w') as fp:
        fp.write('Renga enables collaborative data science!')

This command created a new file, linked it to the running notebook, which in
turn is linked to the project - we have begun to populate our project's
knowledge graph. You can inspect the knowledge graph using the browser UI at
http://localhost/ui/#/graph.
