.. _local_sessions:

Run a Renku Session on your Own Machine
=======================================

.. note::
    **Have you cloned your project?**
    To work on your project on your own machine, you'll need to have cloned your project.
    If you haven't done that yet, check out :ref:`clone_renku_project`.


.. note::
    **Do you have Docker?**
    To run a Renku session, you need to have `Docker installed <https://docs.docker.com/get-docker/>`_.

    On Linux systems, Docker requires root privileges. If you do not have root access to the host, you might
    consider `rootless Docker <https://docs.docker.com/engine/security/rootless/>`_.


Start a renku session
---------------------

First, navigate into your renku project directory:

.. code-block:: shell-session

   $ cd <project_name>

Then, start a session using :meth:`renku session start <renku.ui.cli.session>`:

.. code-block:: shell-session

   $ renku session start
   ID          STATUS    URL
   ----------  --------  ------------------------------------------------------------
   f1693c198e  running   http://0.0.0.0:56674/?token=910ca732ef574049a22d41d0f1109f56

.. note::

   Note: Renku may inform you that the container image for the current commit
   does not yet exist on your machine, and Renku will offer to build it for you. Say yes!

.. note::

    **Building too many images?**
    By default, Renku builds a new image every time you start from a new commit.
    If you would prefer to reuse an image already built for your project (not build a new image every time),
    you can :ref:`pin a specific Docker image <pin_docker_image>` that every session will use.


Open the session in your browser
--------------------------------
When the session starts, it will print out a url where the session is running.
Copy this url into your browser to access your Renku project running inside it's containerized environment.

If you need to find this url again later, you can find all running Renku sessions by running :meth:`renku session list <renku.ui.cli.session>`.

.. code-block:: shell-session

    $ renku session list
    ID          STATUS    URL
    ----------  --------  ------------------------------------------------------------
    f1693c198e  running   http://0.0.0.0:56674/?token=910ca732ef574049a22d41d0f1109f56

You can also use the session ID to call :meth:`renku session open ID <renku.ui.cli.session>`, which opens your browser window for you.

.. code-block:: shell-session

    $ renku session open <session_id>

.. note::

    **Looking for a shell?**

    Would you like to enter your containerized project environment on a shell, rather than via the browser?

    Since Renku uses Docker to manage your project's computational environment, you can use Docker commands to enter the
    container directly and use the shell.


    First, find your renku session's container ID by listing your running sessions:

    .. code-block:: shell-session

       $ renku session list
       ID          STATUS    URL
       ----------  --------  ------------------------------------------------------------
       f1693c198e  running   http://0.0.0.0:56674/?token=910ca732ef574049a22d41d0f1109f56


    Note the value in the ID field.

    Then, open a shell in a running container by providing the Container ID:

    .. code-block:: console

        $ docker exec -it <ID> /bin/bash
        base ▶ ~ ▶ work ❯ project_name ▶ master ▶ $ ▶

Stop the session
----------------

When you're done with your session, use :meth:`renku session stop ID <renku.ui.cli.session>`.

.. code-block:: shell-session

    $ renku session stop <session_id>

to shut down the session.


Clean up unused Docker objects
------------------------------
As you run renku sessions, the docker images used in each session will accumulate on your machine.
We suggest you occasionally prune docker containers you don't need anymore so they don't take up space on your machine.

For example, you can use the following commands to remove all docker images created more than 24 hours ago:

.. code-block:: shell-session

    $ docker image prune -a --filter "until=24h"

and all stopped docker containers older than 24 hours:

.. code-block:: shell-session

    $ docker container prune --filter "until=24h"

Take a look at the `Docker documentation <https://docs.docker.com/config/pruning/>`_ if you'd like to learn more.
