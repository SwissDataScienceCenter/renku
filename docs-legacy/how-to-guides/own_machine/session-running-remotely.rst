.. _remote_sessions:

Run a Renku Session on a Remote Machine
=======================================

Prerequisites
-------------

.. note::

     **Note about Windows:** Renku does not officially support Windows.
     If you would like to try to use Renku on Windows, we recommend using WSL.
     However, we do not promise that all Renku functionality will work.

Connect to the remote machine using SSH
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Connect to the remote machine using ssh, e.g. ``ssh user@my.vm.io``

Is the CLI installed?
~~~~~~~~~~~~~~~~~~~~~

Make sure the Renku CLI is installed: :ref:`cli_installation`.

Have you cloned your project?
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To work on your project on the remote machine, you'll need to have cloned your project.
If you haven't done that yet, check out :ref:`clone_renku_project`.

Do you have Docker?
~~~~~~~~~~~~~~~~~~~

To run a Renku session, you need to have `Docker installed <https://docs.docker.com/get-docker/>`_.

On Linux systems, Docker requires root privileges. If you do not have root access to the host, you might
consider `rootless Docker <https://docs.docker.com/engine/security/rootless/>`_. If the VM does not
support Docker, you might need to ask an administrator to install it for you.

If the remote machine you want to use is itself a Docker container, you'll need to enable
``Docker in Docker``, either using the ``docker:dind`` image in ``priviledged`` mode or
by mounting the host docker socket into your container.


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

    **Running under a specific port**

    By default, Renku will assign a random port to the session it starts. If you'd rather
    have a consistent experience with it running on the same port, use the ``--port`` flag for
    ``renku session start`` to start with a fixed port.


Port-forward to the Remote Renku Session
----------------------------------------

When the session was started above, it printed the URL where the session is running,
``http://0.0.0.0:56674/?token=910ca732ef574049a22d41d0f1109f56`` in the above example.

Note the port, ``56674`` in the example, and port-forward to it from your local machine
using SSH, e.g.:


.. code-block:: shell-session

    $ ssh -L 56674:localhost:56674 user@my.vm.io


You can then open the session on your local machine by going to ``http://localhost:56674``.

Stop the session
----------------

When you're done with your session, use :meth:`renku session stop ID <renku.ui.cli.session>`
on the remote machine.

.. code-block:: shell-session

    $ renku session stop <session_id>

to shut down the session.


Clean up unused Docker objects
------------------------------
As you run renku sessions, the docker images used in each session will accumulate on the remote machine.
We suggest you occasionally prune docker containers you don't need anymore so they don't take up space on your machine.

For example, you can use the following commands to remove all docker images created more than 24 hours ago:

.. code-block:: shell-session

    $ docker image prune -a --filter "until=24h"

and all stopped docker containers older than 24 hours:

.. code-block:: shell-session

    $ docker container prune --filter "until=24h"

Take a look at the `Docker documentation <https://docs.docker.com/config/pruning/>`_ if you'd like to learn more.
