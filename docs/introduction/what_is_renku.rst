.. _what_is_renku_verbose:

What is the Renku Project?
==========================

The Renku Project is a web platform (renkulab) and a commandline interface (renku)
built on top of open source components for researchers, data scientists, educators,
and students to help manage:

* code (e.g. script.py, script.R, script.bat, ...),
* data (e.g. data.csv, data.h5, data.zip, ...),
* execution environments (e.g. requirements.txt, Dockerfile, ...), and
* workflows (e.g. workflow.cwl, ...)

in a way that helps make research:

* repeatable,
* reproducible,
* reusable, and
* shareable

It is developed as an open source project by the Swiss Data Science Center in a
team split between EPFL and ETHZ.

Renku Project components
^^^^^^^^^^^^^^^^^^^^^^^^

The two parts to the Renku Project are:

* **renkulab**: a web platform for creating, storing, working on, saving, and sharing
  your projects and project templates.

* **renku**: a commandline interface (CLI) for annotating datasets and running workflows.

Renkulab
--------

Renkulab is a web platform for creating, storing, working on, and sharing your
data science projects. Our public "flagship" deployment of Renkulab can be found
at renkulab.io_ and is free for anyone to use.

Renkulab is the glue that makes it possible to develop and share your research entirely
in the cloud. You can, directly from a project's homepage on Renkulab, launch Jupyterlab
and RStudio sessions (or anything else you might run from a Docker container) using
pre-built templates. You can work on your project and when you are done
push the changes back to the repository for safe storage. Our pre-built base images
also contain the ``renku`` command-line tool so you don't need to worry about installation
and benefit from lineage tracking right in the interactive session.

Renkulab automatically builds images for your interactive sessions so the environments
you or your collaborators use are always up-to-date.

Renkulab can be accessed publicly at renkulab.io_, but there are also several
institutional deployments of the platform. Contact us if you would like to
deploy your own instance or see :ref:`admin_documentation`.

For more details, see the :ref:`renkulab` page and the :ref:`FAQ`.

renku
-----

The `renku` part of the Renku Project is a commandline interface (CLI) for annotating
datasets and wrapping workflows with metadata for aiding iterative development and
building a lineage of results. You can use this CLI from within an interactive environment
launched from Renkulab or locally by installing the library onto your machine.

`renku` aims to be "git for data science" by applying version control to executed
code tied together with the input files that were dependencies and the output files
that result.

For more details, see the :ref:`renku` page and the `CLI documentation`_.


From both Renkulab & renku
--------------------------

Templates
~~~~~~~~~

* create projects from python & R templated directory structures and files that include renku (cli) installation and renkulab config for launching interactive environments
* customize the files inside the templates by adding your own software dependencies to `requirements.txt`/`install.R`, the Dockerfile, etc.
* create your own directories for organizing your analysis code & use `renku dataset` commands to organize your datasets
* choose a template by selecting from the dropdown menu on renkulab during project creation, or applying the `--from template` flag using `renku init` locally
* share your templates with others

Dockerfile
~~~~~~~~~~

* Base images has installations of renku (CLI), jupyterhub, and jupyterlab and/or rstudio
* Write your dependent library installs into the provided Dockerfile, or python in `requirements.txt`, R in `install.R`

The Philosophy of the Renku Project
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The Renku Project is as useful for independent researchers and data scientists as
it is for labs, collaborations, and courses and workshops.

Use Cases
~~~~~~~~~

+ share published work
+ create work to be published
+ create a link between the source of your data and
+ visualize the connectivity between data from your own project and others
+ create template containers with

.. _renkulab.io: https://renkulab.io

.. _`CLI documentation`: https://renku-python.readthedocs.io
