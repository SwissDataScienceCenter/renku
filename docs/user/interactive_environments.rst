.. _interactive_environments:

Interactive Environments on Renkulab
====================================

What is an Interactive Environment?
-----------------------------------

Interactive environments on Renkulab are web-based user interfaces (like JupyterLab
and RStudio) that you can launch to develop and run your code and data workflows.
They're commonly used for exploratory analysis because you can try out short blocks
of code before combining everything into a (reproducible) workflow.

You can run your own Interactive Environments independently from Renkulab, but
on Renkulab, you get the bonus capabilities:

* a configurable amount of resources (memory, CPU, and sometimes GPU) is
  available on launch

* you can save your work from within the environment back to Renkulab

* it's a sharable, reproducible, :ref:`customizing` container defined by the ``Dockerfile``

* you have all the extensibility provided by the `renku-python <https://renku-python.readthedocs.io/en/latest/>`_ command-line interface (CLI)

Which Interactive Environment will launch?
------------------------------------------

The template you choose when you create a project on Renkulab (or locally call
``renku init`` on your project) determines the kind of interactive environment
that is available to launch. Here's a guide to the templates provided:

* ``Basic Python Project``: JupyterLab
* ``Basic R Project``: RStudio AND JupyterLab with R kernel
* ``Minimal Renku`` (language-agnostic): JupyterLab

This can be customized -- see :ref:`customizing`.

Starting a new Interactive Environment
--------------------------------------

When you choose to start a new interactive environment, you will need to select
some configurations. If you're not sure what options to select, it's best to keep
the defaults. If, however, you've tried launching a project with the defaults and
it crashed on you, you might want to increase some processing power or memory.

Here's the run down for the configuration options.

+------------------------------+-------------------------------------------------------------------------------------------+
| Option                       | Description                                                                               |
+==============================+===========================================================================================+
| branch                       | default master, but if you're doing work on another branch, switch!                       |
+------------------------------+-------------------------------------------------------------------------------------------+
| commit                       | default latest, but you can launch the environment from an earlier commit;                |
|                              |                                                                                           |
|                              | also useful if your latest commit's build failed (see below).                             |
+------------------------------+-------------------------------------------------------------------------------------------+
| environment                  | ``lab``: JupyterLab; ``rstudio``: RStudio; if you're using a python template,             |
|                              |                                                                                           |
|                              | the ``rstudio`` endpoint will not work.                                                   |
+------------------------------+-------------------------------------------------------------------------------------------+
| # CPUs                       | the number of CPUs available; select the lowest # available unless you have reason not to |
+------------------------------+-------------------------------------------------------------------------------------------+
| memory                       | the amount of RAM available; select the lowest # available unless you have reason not to  |
+------------------------------+-------------------------------------------------------------------------------------------+
| # GPUs                       | the number of GPUs available; You might have to wait for GPUs to free up in               |
|                              |                                                                                           |
|                              | order to be able to launch an environment.                                                |
+------------------------------+-------------------------------------------------------------------------------------------+
| Automatically fetch LFS data | Leave de-selected by default. If you find that workflows                                  |
|                              | you used to be able to run have stopped working,                                          |
|                              | check the contents of the file(s) -- if plaintext and contains                            |
|                              | strings that are not your data, run ``renku storage pull <filepath>``                     |
|                              | to get the relevant files, or ``git lfs pull`` to get all of the                          |
|                              | files at once.                                                                            |
+------------------------------+-------------------------------------------------------------------------------------------+


What if the Dockerfile isn't available?
---------------------------------------

When you select the configurations for ``branch`` and ``commit``, a check is run
to see if there's an image for this state available in the project's image registry,
which is stored in Renkulab's instance of GitLab. The container is then launched
from that built image.

An image build from the ``Dockerfile`` in the project is kicked off automatically
using GitLab's CI/CD pipelines configured by the project's ``.gitlab-ci.yml`` when you:

 * create the project
 * fork a project (in which the new build happens for the fork)
 * push changes to the project

It can sometimes take a long time to build for various reasons, but if you've just
created the project on Renkulab from one of the templates it should take less than
a minute.

The Dockerfile is still building
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If the ``Dockerfile`` has a "still building" message, you can either wait patiently,
or watch it build by clicking the associated link to see the streaming log messages
on GitLab. This can be useful if you've made changes to the ``Dockerfile`` or added
lines to ``requirements.txt``, ``environment.yml``, or ``install.R``, where something
might have gone wrong.

The Dockerfile build failed
~~~~~~~~~~~~~~~~~~~~~~~~~~~

If this happens, it's best to click the link to view the logs on GitLab so you
can see what happened. Here are some common reasons for build failure:

* Software installation failure

**problem** You added a new software library to ``requirements.txt``, ``environment.yml``,
or ``install.R``, but something was wrong with the installation (e.g. typo in
the name, extra dependencies required for the library but unavailable).

**how to fix this**
You can use the GitLab editor or clone your project locally to fix the installation,
possibly by adding the extra dependencies it asks for into the ``Dockerfile``
(the commented out section in the file explains how to do this). As an alternative,
you can start an interactive environment from an earlier commit.

**how to avoid this** First try installing into your running interactive environment,
e.g. by running ``pip install -r requirements.txt`` in the terminal on JupyterLab.
You might not have needed to install extra dependencies when installing on your
local machine, but the operating system (OS) defined in the ``Dockerfile`` has
minimal dependencies to keep it lightweight.

* The build timed out

By default, image builds are configured to time out after an hour. If your build
takes longer than that, you might want to check out the section on :ref:`customizing`
interactive environments before increasing the timeout.

* Your project could not be cloned

If you accidentally added 100s of MBs or GBs of data to your repo and didn't
specify that it should be stored in gitLFS (see: :ref:`gitLFS`), it might take
too long to clone. In this case, read the docs on how to rewrite history and move
these files into gitLFS.

Another potential cause is if the project has submodules that are private.

The Dockerfile is unavailable
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Renkulab uses its internal instance of GitLab to build and store an image in the
registry each time you create a project, push changes, or use the Renkulab UI to fork
a project. Thus, if you manage to get into a state that skips any of these steps,
the image might be unavailable. It's a workaround, but the easiest way to get out
of this state is to manually trigger a build by adding a new trivial commit through
the GitLab instance, like editing the ``README.md`` file.

What's in my Interactive Environment?
-------------------------------------

* your project, which is cloned into the environment (but by default *without
  files that are stored in git LFS*. :ref:`what does this mean?`)
* all the default software required to launch the environment and common software
  for code development (``git``, ``git LFS``, ``vim``, etc.)
* any software you added via ``requirements.txt``, ``environment.yml``, ``install.R``,
  or directly into the ``Dockerfile``
* the renku command-line interface :ref:`docs`
* the amount of CPUs, memory, and (possibly) GPUs that you configured before launch

For adding or changing software installed into your Interactive Environment,
check out :ref:`Customizing`


Saving your work
----------------

Interactive environments persist for 24 hours. If left unattended, they might shut
down without warning. They also might crash if you run a process that eats more
memory than you've allocated. Thus, it's best to save often.

There are two ways to save your work back to Renkulab from an Interactive Environment
(both available in JupyterLab and RStudio), and behind the scenes both are using ``git``
staging (``add``), ``commit``, and ``push``. You can type these commands directly
into the available terminal interface of your Interactive Environment, or click
some buttons via the git plugins.

When you push your changes back to Renkulab, the GitLab CI/CD is triggered to build
a new image out of the ``Dockerfile``, which will be available the next time you
start a new environment.

Saving via Terminal
~~~~~~~~~~~~~~~~~~~

In the Terminal interface inside the Interactive environment, call the following
three ``git`` commands to stage, commit, and push your changes:

1. ``git add *``
2. ``git commit -m "my short but descriptive message of the changes I made"``
3. ``git push``

If you are new to git, these resources might be useful:

* [git documentation](https://git-scm.com/doc)
* [A great interactive cheatsheet](http://ndpsoftware.com/git-cheatsheet.html)
* [An intro to git](https://rogerdudler.github.io/git-guide/)

Saving via Git Plugin
~~~~~~~~~~~~~~~~~~~~~

Find the git plugin interface (Jupyterlab: branched-dots icon on lefthand vertical
menu; RStudio: top right box). Add the changed files you want to save to staging,
write a message to commit the changes, and don't forget to hit the icon or button
to push those changes.

Stopping an Interactive Environment
-----------------------------------

If you know you're not going to be actively working on your project, it's good
manners to :ref:`Saving your work` and stop the interactive environment, so that
you can release the resources you were consuming. You can do this from the Environments
tab on the Renkulab UI.

Sometimes an environment will get stuck pending launch (e.g. because you requested
GPUs and they are not yet available). In this case you can view the status and
logs to see if there's a useful message. In the case of pending GPUs, you can
safely wait for the resources to become available. However in other cases, more
commonly when you are trying to customize your environment, you might notice an
issue via the logs, and want to stop the launch so that you can fix the problem
and try again. However, for these stuck notebooks it is not yet possible.
You can reach out to us on `discourse <https://renku.discourse.group>`_ in this
case.

.. _customizing:

How can I customize my Interactive Environment?
-----------------------------------------------

The launch is enabled by the content in the following files in your project:

* ``Dockerfile``: defines the type of interactive environment and other software
  installed in the environment, including the ``renku`` command-line installation.
* ``.gitlab-ci.yml``: controls the docker build of the image based on the project's
  ``Dockerfile``.



Modifying the template's Dockerfile
-----------------------------------

The `Dockerfile` in the project is what defines the environment. In the template
provided, the `Dockerfile`

Renku projects use Docker for containerization. While we
have a set of defaults that we build into a minimal Python and R image
image, there are several reasons why you might want to build on top of these or
write your own entirely.


Dockerfile structure
~~~~~~~~~~~~~~~~~~~~

The project's ``Dockerfile`` lives in the top level of the project directory. In
the default ``Dockerfile`` provided in the template, the first line is a ``FROM``
statement that specifies a `versioned base docker image <https://github.com/SwissDataScienceCenter/renku-jupyter>`_.
We add new versions periodically, but the heart of it is the set of installations
of jupyterlab/rstudio, git, and renku::

  FROM renku/singleuser:0.3.5-renku0.5.2

  # or, for RStudio in the build

  FROM renku/singleuser-r:0.3.5-renku0.5.2

The next two statements install user-specified libraries from ``environment.yml``
and ``requirements.txt``::

  # install the python dependencies
  COPY requirements.txt environment.yml /tmp/
  RUN conda env update -q -f /tmp/environment.yml && \
  /opt/conda/bin/pip install -r /tmp/requirements.txt && \
  conda clean -y --all && \
  conda env export -n "root"

You can add to this ``Dockerfile`` in any way you'd like.

.. _docker_dev:

Dockerfile development
~~~~~~~~~~~~~~~~~~~~~~

If you're going to be making simple modifications to the ``Dockerfile`` (i.e. changing
the base Docker image version number), you can use the following steps to update
and re-build the image:

#. On the project's landing page, click the **View in GitLab** button in the upper righthand corner (opens a new tab by default).
#. In GitLab, click the **Repository** tab in the lefthand column, which drops you into the **Files** tab.
#. Click the **Dockerfile** out of the list of files that appears, and click **Edit** (top right, near the red Delete button. Don't click the red Delete button.)
#. Make your edits in this window.
#. When you're satisfied with the edits, scroll down and write a meaningful **commit message** (you'll thank yourself later).
#. Click the green **Commit changes** button.

You may find the [official docker documentation](https://docs.docker.com/engine/reference/builder/) useful
during this process.

Now you have committed the changes to your ``Dockerfile``. Since you have made a commit,
the CI/CD pipeline will kick off (pre-configured for you as a ``renkulab-runner``
inside the GitLab CI/CD settings). It will attempt to rebuild your project with
the new contents of your ``Dockerfile`` based on the configuration in ``.gitlab-ci.yml``,
a file at the top level of your project directory.

The contents of ``.gitlab-ci.yml`` show you that in the build stage, we pull
the docker image for Renku, build our new image out of our ``Dockerfile``
with a tag relating to the commit, and push it.

Let's monitor this process:

#. Click the **CI/CD** > **Jobs** tab.
#. Click the latest status that corresponds to the changes to the ``Dockerfile`` you just made (probably "running", unless it's already "completed" or "failed").

This is the log file from the build process specified in the ``.gitlab-ci.yml``
file. If it succeeds, there will be a green **passed** status, and the end of
log will be a green **Job succeeded**. If the build instead failed, you can use
the messages in the log to determine why and hint at what you can do to fix it.

.. warning::

  Note that the settings have been configured for this build to time out and fail
  after one hour. While a long running build might be indicative of a bug in your
  ``Dockerfile``, it's possible that your build might take a long time. If this is the
  case, you can change the limits in the project settings via the lefthand column of the GitLab
  interface at **Settings** > **CI/CD** > **General pipelines** > **Timeout**.

Using your new Docker image
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Passing CI/CD is great, but in order to use the new image you need to
(re)start your interactive environment.

To do this, go back to the Renku platform, and from the project's landing page,
first check in the **Files** tab that your changes to the ``Dockerfile`` are
present. If not, you can force-refresh the page. Then, go to the **Notebook
servers** tab. If you have any running notebooks, those will keep running the image which was built with
the older version(s) of the ``Dockerfile``. You can **Start new server** and
**Launch server** to start a notebook with the latest image.

If the server launches, test it to make sure that the extra functionality you
added in the ``Dockerfile`` is present in the container. If it is not, you can
go back to the GitLab interface and continue to make changes until you are
satisfied.

Looking to make more extensive modifications? Build running too long? Check out
the `next section <_more_extensive_docker>`_.

.. _more_extensive_docker:

for more extensive modifications.

More extensive modifications
--------------------------------

If you want to make more extensive modifications, say ones that would require
longer build times, you may wish to test the docker build on your own machine.
You can follow the `docker tutorial <https://docs.docker.com/get-started/>`_ to
get set up and learn how to build and test local images.

Once you have a local docker setup, you can clone your project locally (if you
haven't set up an SSH key from GitLab you'll need to do this), make
modifications to the ``Dockerfile``, and ``docker build`` and ``docker run`` to
test your changes. To test whether your docker image will work, try running it
with::

  docker run --rm -ti -p 8888:8888 <image> jupyter lab --ip=0.0.0.0

.. warning::

  You need to install ``jupyter`` and ``jupyterhub`` into the image to be able to
  start notebook servers on renkulab.io.

You can commit these changes and push to the repo. Then, follow the rest of the
steps in :ref:`docker_dev`.

Note that by default there are two choices for the ``Dockerfile`` (chosen at
project creation time via "python base" or "R base") for the base image, located
here:

* a `JupyterLab base <https://github.com/SwissDataScienceCenter/renku-jupyter/tree/master/docker/base>`_ (with renku installed on top)
* a `rocker (R + RStudio) base <https://github.com/SwissDataScienceCenter/renku-jupyter/tree/master/docker/r>`_ (with conda and renku installed on top)

These two images are available on `dockerhub <https://hub.docker.com/r/renku/>`_.

If you can't work with the template ``Dockerfile`` provided, you can pull one of
these base ``Dockerfile`` s and add the ``renku``, ``git``, and ``jupyter``
parts to another base image that you might have.
