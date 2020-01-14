.. _templates:

Templates in Renku
==================

Templates come in handy when you want to be able to use the same kind
of high level project structure, dependencies, and/or scripts layouts over and
over. The two main aspects of the project that can be templated are:

* the directory structure & files
* the Docker image that builds your environment

When you create a project on the Renku platform, you can choose between
two base templates: a minimal python setup, and a minimal R setup. In many
cases, you can use these templates as-is (see
the :ref:`directory_structure` below). If the base template for either of
these languages is not sufficient for you, or you require a different IDE,
you can make modifications! Check out :ref:`advanced_interfaces`.

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
python3 or R kernel image from the Jupyterlab instance while you're inside
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
dependencies for the Renku CLI, Jupyterlab, and maybe R kernels & RStudio,
depending on which template you selected upon project creation.

The lines following ``FROM`` define the installation of your own software
dependencies; they are the instructions for conda/pip installations of the
libraries in your ``requirements.txt`` file. If there's nothing special
about the libraries you're installing, you wont have to make changes to this
file. Else, check out :ref:`advanced_interfaces`.

``.dockerignore``
"""""""""""""""""

The ``.dockerignore`` file is just like a ``.gitignore`` file, in that it
allows you to specify which files to ignore in a docker build. If you aren't
making changes to the docker build, you can .dockerignore this file.

``README.md``
"""""""""""""

The ``README.md`` file is shown on a project's home page. It's good to have at
least the name of the project and a brief overview of the project for your
intended audience.

``.renku``
""""""""""

The ``.renku`` directory contains a ``renku.ini`` file which has a set of
configurations for interactive environments. It's a good idea to define
default values if your project has specific resources requirements or if it
needs to start on a different url.

Although the file may be modified manually, it's a better idea to use the
``renku config --local interactive.<key> <value>`` command.

Here is the list of properties you can customize in a standard Renkulab
deployment:

* ``default_url [string]``: url to use when starting a new interactive
  environment
* ``cpu_request [float]``: CPUs quota
* ``mem_request [float]``: memory quota (in GBs)
* ``gpu_request [int]``: GPU quota
* ``lfs_auto_fetch [bool]``: whether to automatically fetch lfs files or not

.. note::

    We use Jupyterlab as the web interface. You may be interested in launching
    the Jupyter default instead. You can achieve this by using `/tree` as a
    ``default_url`` instead of `/lab`.

    .. code-block:: console

      > renku config --local interactive.default_url "/tree"

    Verify that your renku.ini file looks like the following.

    .. code-block:: console

      [renku "interactive"]
      default_url = /tree

    If you did this locally, please remember to push back to origin
    (`renku config` automatically creates a commit ).
    You can now start a new environment for the latest commit to get
    the default Jupyter web interface.



What can I touch? What shouldn't I touch?
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

What you should or shouldn't touch depends on how well aligned your project is
with the defaults that Renku provides. Here are two example use cases:

User #1: Default everything
"""""""""""""""""""""""""""

You're a python developer and you're ok with jupyterlab and the version of
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

  Modifying these files can result in an image that doesn't build. Resetting to
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
