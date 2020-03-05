Renkulab & renku components
===========================

.. note::
    This document is still under construction.

Templates
---------

* create projects from python & R templated directory structures and files that include renku (cli) installation and renkulab config for launching interactive environments
* customize the files inside the templates by adding your own software dependencies to ``requirements.txt`/`install.R``, the ``Dockerfile``, etc.
* create your own directories for organizing your analysis code & use ``renku dataset`` commands to organize your datasets
* choose a template by selecting from the dropdown menu on renkulab during project creation, or applying the `--from template` flag using ``renku init`` locally
* share your templates with others

Dockerfile
----------

* ``Dockerfile``'s base image has installations of ``renku`` (CLI), jupyterhub, and jupyterlab and/or rstudio
* Base image also includes: ``vim``, ``git``, and other utilities
* Write your dependent library installs into the provided ``Dockerfile``, or python in ``requirements.txt``, R in ``install.R``


.. _renkulab.io: https://renkulab.io

.. _`CLI documentation`: https://renku-python.readthedocs.io
