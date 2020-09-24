.. _interactive_basics:


Interactive Environment Basics
==============================

What is an Interactive Environment?
-----------------------------------

Interactive environments on RenkuLab are web-based user interfaces (like JupyterLab
and RStudio) that you can launch to develop and run your code and data workflows.
They're commonly used for exploratory analysis because you can try out short blocks
of code before combining everything into a (reproducible) workflow.

You can run JupyterLab or RStudio within a project independently from RenkuLab,
but RenkuLab offers the following advantages:

* environments hosted in the cloud with a configurable amount of resources
  (memory, CPU, and sometimes GPU)

* autosaving of work back to RenkuLab, so you can recover in the event of a
  crash

* environments are defined using Docker, so they can be shared and reproducibly
  re-created

* the functionality provided by the renku-python_ command-line interface (CLI)
  is automatically available


What's in my Interactive Environment?
-------------------------------------

* your project, which is cloned into the environment on startup
* your data (if the option ``Automatically fetch LFS data`` is selected)
  files that are stored in git LFS*)

* all the software required to launch the environment and common tools for
  working with code (``git``, ``git LFS``, ``vim``, etc.)

* any dependencies you specified via conda (requirements.txt), using
  language-specific dependency-management facilities (``requirements.txt``,
  ``install.R``, etc.) or installed in the ``Dockerfile``

* the renku command-line interface renku-python_.

* the amount of CPUs, memory, and (possibly) GPUs that you configured before launch

For adding or changing software installed into your project's interactive environment,
check out :ref:`customizing`


Which Interactive Environment will launch?
------------------------------------------

The template you choose when you create a project on RenkuLab (or locally call
``renku init`` on your project) determines the kind of interactive environment
that is available to launch. Once it is initialized, your project can easily be
modified, for example to install additional libraries into the environment - see
:ref:`customizing`. We provide templates for basic Python, R, and Julia
projects. If you wish to use custom templates for your projects, you can build
your own! Please refer to the :ref:`templating <templates>` documentation.


Starting a new Interactive Environment
--------------------------------------

When starting a new interactive environment, you will be asked to configure it.
The default configuration should work well for most situations. If, however,
you encountered problems with an environment (for example, a crash), you might
want to increase some processing power or memory. Here's the rundown of the
configuration options.

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
| # CPUs                       | the number of CPUs available; resources are shared, so please select the lowest amount    |
|                              | that will work for your use case.                                                         |
+------------------------------+-------------------------------------------------------------------------------------------+
| memory                       | the amount of RAM available; resources are shared, so please select the lowest amount     |
|                              | that will work for your use case.                                                         |
+------------------------------+-------------------------------------------------------------------------------------------+
| # GPUs                       | the number of GPUs available; You might have to wait for GPUs to free up in               |
|                              |                                                                                           |
|                              | order to be able to launch an environment.                                                |
+------------------------------+-------------------------------------------------------------------------------------------+
| Automatically fetch LFS data | Leave off by default. If you find that workflows                                          |
|                              | you used to be able to run have stopped working,                                          |
|                              | check the contents of the file(s) -- if plain text and contains                           |
|                              | strings that are not your data, run ``renku storage pull <filepath>``                     |
|                              | to get the relevant files, or ``git lfs pull`` to get all of the                          |
|                              | files at once.                                                                            |
+------------------------------+-------------------------------------------------------------------------------------------+


What if the Docker image is not available?
------------------------------------------

Interactive environments are backed by Docker images. When launching a new
interactive environment a container is created from the image that matches the
selected ``branch`` and ``commit``.

A GitLab's CI/CD pipeline automatically builds a new image using the project's
``Dockerfile`` when any of the following happens:

  * creating of a project
  * forking a project (in which the new build happens for the fork)
  * pushing changes to the project

(This is defined in the project's ``.gitlab-ci.yml`` file.)

It can sometimes take some time to build an image for various reasons, but if
you've just created the project on RenkuLab from one of the templates it should
take less than  a minute.


The Docker image is still building
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If the Docker image has a "still building" message, you can either wait patiently,
or watch it build by clicking the associated link to see the streaming log messages
on GitLab. This can be useful if you've made changes to the ``Dockerfile`` or added
lines to ``requirements.txt``, ``environment.yml``, or ``install.R``, where something
might have gone wrong.


The Docker image build failed
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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
specify that it should be stored in git LFS, it might take too long to clone. In
this case, read the docs on how to rewrite history and move these files into
git LFS.

Another potential cause is if the project has submodules that are private.

The Docker image is not available
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

RenkuLab uses its internal instance of GitLab to build and store an image in the
registry each time you create a project, push changes, or use the RenkuLab UI to fork
a project. Thus, if you manage to get into a state that skips any of these steps,
the image might be unavailable. It's a workaround, but the easiest way to get out
of this state is to manually trigger a build by adding a new trivial commit through
the GitLab instance, like editing the ``README.md`` file.

.. _renku-python: https://renku-python.readthedocs.org
