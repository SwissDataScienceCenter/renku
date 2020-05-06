.. upgrading_renku:

Upgrading a Renku Project
=========================

The Renku Project is in constant flux! This means that often there are changes
to RenkuLab (e.g. the web platform (UI) and the knowledge graph (KG)), and ``renku``
(the command-line interface (CLI), which is installed via ``pipx`` in the
interactive environments).

Using new features on RenkuLab
------------------------------

Often, changes to the RenkuLab UI and KG don't require you to do anything special
to benefit from the new features. For instance, when we added the ability for
anonymous (i.e. not logged in) users to launch interactive environments, it
automatically applied for all projects.

Other changes to the RenkuLab UI do
require you to update project settings to make use of the features.

You can read the CHANGELOG for more details.

Using new features in the ``renku`` CLI
---------------------------------------

When we release a new version of ``renku``, the CLI, you do have to take actions
if you want to update your project to the latest version.

On RenkuLab, e.g. for Interactive Environments
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In an Interactive Environment, the version of ``renku`` is controlled by the image
built from the ``Dockerfile`` in the project. By default, the first line defines
a pre-built RenkuLab compatible image (e.g. an image that can launch JupyterLab
and/or RStudio, with a bunch of other convenient software installs). This image
defines a fixed version of the ``renku`` CLI, indicated in the name.

For example, ``renku/renkulab-py3.7:renku0.10.3-0.6.2`` has ``renku`` version 0.10.3.
If a new release is available, you can check https://github.com/SwissDataScienceCenter/renkulab-docker
for the latest version of the flavor you're using (e.g. python, cuda, R, Biocondutor, etc.).
Then, replace the ``FROM`` line with the new image name, and commit & push the changes.
Pushing the changes causes gitlab to build the new image. Then, you can launch a new
interactive environment from the latest commit, and you'll have the latest version
of renku CLI.

You can read the CHANGELOG for the ``renku`` CLI here.


Locally
~~~~~~~

If you are working locally (e.g. running ``renku`` commands in a terminal on your
laptop), and you have installed renku with ``pipx`` (recommended), follow these
instructions.

To upgrade renku to the latest stable version:

::

    $ pipx upgrade renku

To upgrade to the latest development version:

::

    $ pipx upgrade --pip-args=--pre renku
