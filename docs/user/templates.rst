.. _templates:

Templates in Renku
==================

Templates come in handy when you want to be able to use the same kind
of high level project structure, dependencies, and/or scripts layouts over and
over. The two main aspects of the project that can be templated are:

* the directory structure & files
* the Docker image that builds your environment

When you create a project on the RenkuLab platform, you can choose between
a few templates. You should see at least a Python setup (Basic Python Project)
and a R setup (Basic R Project). In many cases, you can use these templates
as-is (see the :ref:`directory_structure` below). If you prefer another
language, you require a different IDE, or the base templates don't meet your
needs for any other reason, you can create new ones!
Check out :ref:`advanced_interfaces`.

Note that you can create projects that are specifically intended to be templates
that others can use by adding to the base Renku template. If you're familiar
with `Cookiecutter <https://cookiecutter.readthedocs.io/en/latest/>`_, you can
also create a cookiecutter template as a Renku project.

.. contents:: :local:

.. _directory_structure:

Directory Structure
^^^^^^^^^^^^^^^^^^^

The default provided directory structure is the following.

``data`` and ``notebooks``
""""""""""""""""""""""""""

The ``data`` and ``notebooks`` directories are where you should keep your...
data (added via ``renku dataset``) and notebooks (by double-clicking on a
python3 or R kernel image from the JupyterLab instance while you're inside
the dir), respectively.
You can add further nesting of directories under these locations to keep your
project organized.

Moreover, you will probably want to create other top-level directories,
like ``src`` for keeping scripts that you create from your notebooks when
your analysis stabilizes, and ``docs`` if you wish to keep your documentation
separate from the analysis.
See :ref:`how_to_documentation` for documentation tips.

The ``.gitkeep`` files in these directories by default are a convention used to
git commit "empty" directories (where normal git behavior is to omit empty
directories).

``environment.yml`` and ``requirements.txt``
""""""""""""""""""""""""""""""""""""""""""""

These two files are where you write in your conda (``environment.yml``) and pip
(``requirements.txt``) library dependencies. This is so that when you kill a
notebook or anyone forks your project, (re)starting an interactive environment
session will pre-install your libraries.

To make it easy to remember to write your dependencies to this file, instead of
``pip install <library>`` in a terminal, for instance, you can get into the
habit of running ``pip install -r requirements.txt``.

``.gitignore``
""""""""""""""

This is your typical .gitignore file that you use for git projects. Write into
here any files that you don't want to be tracked.

``.gitlab-ci.yml``
""""""""""""""""""

This yml file is for "continuous integration" in GitLab. It is configured so
that every time you make a commit, your project's docker image is rebuilt. In
most cases, this build should be successful. If, however, you are making
modifications to the ``Dockerfile``, you should pay attention to the CI/CD tab
in GitLab to check for failing builds. Take a look
at :ref:`advanced_interfaces`.

``Dockerfile``
""""""""""""""

When you run the notebook server, a Docker image is built for your project as
defined by this ``Dockerfile``. The FROM line in this ``Dockerfile`` defines
which Renku Docker image sets up the base of your project; this includes
dependencies for the Renku CLI, JupyterLab, and maybe R kernels & RStudio,
depending on which template you selected upon project creation.

The lines following ``FROM`` define the installation of your own software
dependencies; they are the instructions for conda/pip installations of the
libraries in your ``requirements.txt`` file. If there's nothing special
about the libraries you're installing, you wont have to make changes to this
file. Else, check out :ref:`advanced_interfaces`.

``.dockerignore``
"""""""""""""""""

The ``.dockerignore`` file is just like a ``.gitignore`` file, in that it allows
you to specify which files to ignore in a docker build. If you are not making
changes to the docker build, you can .dockerignore this file.

``README.md``
"""""""""""""

The ``README.md`` file is shown on a project's home page. It's good to have at
least the name of the project and a brief overview of the project for your
intended audience.

``.renku``
""""""""""

The ``.renku`` directory includes a ``renku.ini`` file which contains
project-level configuration for renku, stored using the
`INI format <https://en.wikipedia.org/wiki/INI_file>`_. Currently, it
can be used to specify defaults values for launching interactive environments.

**Interactive Environments**

If your project has specific resources requirements to run, or if it should
default to RStudio or anything other than JupyterLab, then you will want to
provide a configuration for the interactive environments.

Although the file may be modified manually, it is recommended to use the
``renku config --local interactive.<property> <value>`` command.

Here is the list of properties that can be customized in a standard Renkulab
deployment:

* ``default_url [string]``: URL to use when starting a new interactive
  environment (``/lab``, ``/tree``, ...)
* ``cpu_request [float]``: CPUs quota (``0.5``, ``1``, ...)
* ``mem_request [string]``: memory quota (``1G``, ``2G``, ...)
* ``gpu_request [int]``: GPU quota (``0``, ``1``, ...)
* ``lfs_auto_fetch [bool]``: whether to automatically fetch lfs files or not
  (``true``, ``false``)

.. note::

    We use JupyterLab as the default web interface for interactive environments.
    If you work in R, you may prefer to have RStudio. This can be
    achieved by using `/rstudio` as the ``default_url`` instead of `/lab`.

    .. code-block:: console

      > renku config --local interactive.default_url "/rstudio"

    Verify that your ``renku.ini`` file looks like the following.

    .. code-block:: console

      [renku "interactive"]
      default_url = /rstudio

    If you ran this command locally, you will need to push back to the renkulab
    server, e.g.,

    .. code-block:: console

      > git push

    before this change is available (`renku config` automatically creates a
    commit).

    You can now start a new environment against the latest commit and you will
    have RStudio as the default web interface.

.. note::

    Using the same approach as above for RStudio, it is possible to switch the
    interface from JupyterLab to the classic Jupyter Notebook by using `/tree`
    as the ``default_url`` instead of `/lab`.

    .. code-block:: console

      > renku config --local interactive.default_url "/tree"



What can I touch? What should I not touch?
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

What you should or should not touch depends on how well aligned your project is
with the defaults that Renku provides. Here are two example use cases:

User #1: Default everything
"""""""""""""""""""""""""""

You're a python developer and you're ok with JupyterLab and the version of
python provided by the base template. You install all of your libraries with
pip or conda. While you work on this project, you can feel comfortable
modifying the following (as well as creating your own directories and
subdirectories to match your project's structure):

* ``data`` and ``notebooks`` directories
* ``.gitignore``
* ``requirements.txt`` (pip) and ``environment.yml`` (conda)
* ``README.md``

User #2: Extra dependencies
"""""""""""""""""""""""""""

You want a different version of python than the one provided, you want to
install software that requires additional non-python/R dependencies, or you
want to make other changes, and you're comfortable editing Dockerfiles. In
addition to the files above, you might modify the following.
Consult :ref:`advanced_interfaces`.

* ``Dockerfile``
* ``.dockerignore``
* ``.gitlab-ci.yml``

.. warning::

  Modifying these files can result in an image that does not build. Resetting to
  default values and killing and restarting the notebook should bring you back
  to a working state.

You can add any extra directories, sub-directories, and files anywhere without
a problem, but you probably want to leave the dotfiles in the level that
they're in in the default templates. Some of the integrated tools expect to
find these files in the top level of the project and will fail otherwise.

What else could be templated?
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Here are some other aspects of a project that could make use of a template:

* documentation
* subdirectories for keeping different parts of a project (note that you'll
  want to add ``.gitkeep`` files into empty directories to be able to git
  commit them)
* python scripts with ``argparse`` set up for inputs and outputs
* ``Dockerfile`` with installation of alternate IDE

One way to write templates for these aspects of the project is to create them
with `Cookiecutter <https://cookiecutter.readthedocs.io/en/latest/>`_.
Cookiecutter is a CLI that creates projects from project templates. You can
define your own templates, or check out some of the
`curated cookiecutters <https://cookiecutter.readthedocs.io/en/latest/readme.html#data-science>`_.
Note that some of these clash with the `renku` templates (i.e. content in
`Dockerfile`, `.gitignore`, etc.). As long as you read the docs above to
understand which parts are required for `renku`, you should be able to merge
these manually.

.. _create_template_repo:

Create a template repository
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

We maintain an
`official Renku template repository <https://github.com/SwissDataScienceCenter/renku-project-template>`_
that provides a few basic templates you can use to initiate your projects.
That should cover the most common use cases, but we assume users want to
create their own templates to speed up the bootstrap phase of a new project.

The easiest way to create your own templates is to clone our
`Renku template repository <https://github.com/SwissDataScienceCenter/renku-project-template>`_
and modify it as you need.

``manifest.yaml``
"""""""""""""""""

The
`manifest file <https://github.com/SwissDataScienceCenter/renku-project-template/blob/master/manifest.yaml>`_
contains all the specifications needed by the ``renku init`` function to
create a new project. You can specify multiple templates in the same
repository. Each of them requires an entry with the following parameters:

* ``folder``: the target folder inside the repository where the template files
  are stored. Please use a different folder for each template.
* ``name``: a short user friendly name.
* ``description``: a brief description of your template. This will be
  presented to the user when choosing between templates.
* ``variables``: we support the
  `Jinja template engine <https://palletsprojects.com/p/jinja/>`_ in both
  file content and filenames. You can therefore ask users for specific values
  for any number of variables. The syntax is
  ``<variable_name>: <variable_description>``, where the name will be used as
  the variable name provided to the engine and the description will be
  presented to the user to explain the variable's intended use.

Use your repository
"""""""""""""""""""

If you installed the renku command line interface locally, you can provide
your template repository to the ``renku init`` command. We recommend you
**always** specify a tag (or a commit) when creating a new project from a
custom repository. You can find further details in
`renku init docs <https://renku-python.readthedocs.io/en/latest/commands.html#use-a-different-template>`_.

If you are using the UI through a RenkuLab instance, you can ask the
administrators to include your repository in the official repository list
available in the 
`renku-values file <https://renku.readthedocs.io/en/latest/admin/index.html#create-a-renku-values-yaml-file>`_.

We are currently working on adding full support for custom template
repositories in RenkuLab so that you can manually specify external resources
not included in the configuration file.
