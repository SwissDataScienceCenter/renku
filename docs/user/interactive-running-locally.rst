.. _local_interactive_environments:

Running RenkuLab Interactive Sessions on Your Own Machine
=========================================================

Each Renku project contains configuration files (``Dockerfile``,
``requirements.txt`` etc.) for building reproducible session images. We build
these automatically when you push your project to the server and store them in
the image registry connected to the Renku instance that you are using. The
custom-built images are used whenever you launch an interactive session on
RenkuLab.

You can of course also use these same images to run your code in the correct
environment anywhere that `Docker <https://www.docker.com>`_ is available.

.. note::

    All of the instructions below assume that you have cloned your project
    locally and that you have `Docker
    installed <https://docs.docker.com/get-docker/>`_. On Linux systems, Docker
    requires root privileges - if you do not have root access to the host, you
    might consider `rootless
    Docker <https://docs.docker.com/engine/security/rootless/>`_.


Building your own image
-----------------------

If you are working on a project that has not yet been pushed to the server,
or you just prefer to build the image locally, you can certainly do so
with Docker. To build the image, run this command from the root directory
of your project:

.. code-block:: console

    $ docker build -t <imageName>:<tag> .

The ``imageName`` and ``tag`` can be anything you want, but you might want to
try to use something you will remember. To see which images you have in
your local Docker registry, run

.. code-block:: console

    $ docker images


Using the images built on RenkuLab
----------------------------------

If your repository on RenkuLab is public, the easiest way to spin up your
runtime environment locally is by pulling the image that has already been built.
First, make sure that your project has been pushed and that the image build is
complete. If you are not sure, the easiest way to check is to navigate to your
project in RenkuLab and go to launch a new session - if the "Start environment"
button is available, it means the image has been built already.

To pull the image, you need to construct the image name. This is typically the
format to expect: ``registry.<renku-host>/<namespace>/<project>:<commit-sha>``.
Lets break down the meaning of these terms in brackets:

* ``renku-host`` is the hostname of the RenkuLab instance, e.g. ``renkulab.io``
* ``namespace`` is either the username or the group name of the project owner
* ``project`` is the project name
* ``commit-sha`` is the first 7 characters of the ``commit-sha``

``namespace`` and ``project`` are easy - if your repository remote is something
like ``https://renkulab.io/gitlab/rok.roskar/flights-tutorial.git`` then the
``<namespace>/<project>`` is ``rok.roskar/flights-tutorial``.

You can get the ``commit-sha`` by running ``git log`` in your repository, e.g.

.. code-block:: console

    $ git log

    commit 53cbd6a11dc5b0d139b2563b5aad83ae4f676b18 (HEAD -> master, origin/master, origin/HEAD)
    Author: Rok Roskar <rok.roskar@sdsc.ethz.ch>
    Date:   Tue Nov 3 17:15:31 2020 +0100

    renku graph generate

The ``commit-sha`` here is ``53cbd6a11dc5b0d139b2563b5aad83ae4f676b18`` so the full
name of this image, including the tag would be
``registry.renkulab.io/rok.roskar/flights-tutorial:53cbd6a``.


Launching the interactive environment locally
---------------------------------------------

Finally, if we wanted to launch an environment locally with the image that was
built for this commit. From the project's root directory, run

.. code-block:: console

    $ repoName=$(basename -s .git `git config --get remote.origin.url`); \
        docker run --rm -ti -v ${PWD}:/work/$repoName \
        --workdir /work/$repoName -p 8888:8888 \
        registry.renkulab.io/rok.roskar/flights-tutorial:53cbd6a jupyter lab

Replace the image name here with whatever image you derived for your project and
commit above (or if you built your own image, the image/tag combo you used).
This command instructs docker to run the image from the remote registry and to
override its default command with ``jupyter lab``. It also sets the port (``-p``
flag), mounts the current directory into the container (``-v``) and sets that as
the working directory (``--workdir``). Once the image downloads and the
container is created, you will see a series of log messages ending in something
like:

.. code-block:: console


    To access the notebook, open this file in a browser:
    file:///home/jovyan/.local/share/jupyter/runtime/nbserver-23-open.html
    Or copy and paste one of these URLs:
    http://eb6ec2fdfdd0:8888/?token=43ed80d538c4d444ee364b7fa5c0b4df30efcb65df9bca58
    or http://127.0.0.1:8888/?token=43ed80d538c4d444ee364b7fa5c0b4df30efcb65df9bca58

To access the running environment, copy the last of these links (starting with
``https://127.0.0.1``) into your browser and you should drop straight into
the jupyter lab session. The rest should feel rather familiar - your environment
should be identical to what you are used to seeing in your RenkuLab sessions.
