.. upgrading_renku:

Upgrading a Renku Project
=========================

The Renku Project is in constant flux! This means that often there are changes
to RenkuLab (e.g. the web platform (UI) and the knowledge graph (KG)), and ``renku``
(the command-line interface (CLI), which is installed via ``pipx`` in the
interactive environments).

Using new features on RenkuLab
------------------------------

New features in the UI and KG on RenkuLab are announced in the :ref:`release_notes`.

When we update components of the RenkuLab platform, usually you will have access
to these new features without doing anything special. E.g. when we added the ability
for anonymous (i.e. not logged in) users to launch interactive environments, all
projects were immediately accessible to anonymous users without the project owner
needing to change any settings.

.. _renku_cli_upgrade:

Using new features ``renku`` CLI features
-----------------------------------------

Note: This section is for upgrading the version of ``renku`` CLI installed into
the Interactive Environments on RenkuLab for your project. See :ref:`upgrading_local`
for upgrading your local machine's version of ``renku``.

When we release a new version of ``renku``, the CLI, you do have to make some
(minimal) changes to the ``Dockerfile`` in your project to ensure that the
Interactive Environment on RenkuLab will use the image with the correct version.

The version of the ``renku`` CLI is defined in the base image specified in the
``FROM`` line (usually line 1) of the ``Dockerfile`` in your repo.

1. Replace this line with the latest available image for the "flavor" you're using,
   which you can find by:

* checking the `renkulab-docker repo README.md <https://github.com/SwissDataScienceCenter/renkulab-docker/blob/master/README.md>`_
  for the naming conventions of the "flavor" of image you're updating (e.g. if you're using
  the "minimal python project", you will look in the py3.7 section)
* visiting the dockerhub page linked in the naming conventions section in the above ``README``
* choosing the latest available tagged image

Note that the version of the ``renku`` CLI is defined in the image name. For example,
``renku/renkulab-py:3.7-renku0.10.3-0.6.2`` has CLI version ``0.10.3``. Please choose
an image with the renku version specified (e.g. ``-renku0.10.3-`` and not ``-renku-``),
unless you absolutely need the latest development version of ``renku``, since it makes
debugging and reproducibility much simpler.

2. Push your changes (either from the GitLab editor, or in a running Interactive
   Environment), and start up a new Interactive Environment to use this new image.

3. When you start to run ``renku`` commands, you might be asked to call ``renku migrate``.
   This command ensures that the metadata in your project is updated.

Note: ``renku`` is installed in the image with ``pipx``. You can also upgrade ``renku``
in a running Interactive Environment by following the docs for upgrading your local
installation of ``renku``, however these changes will not hold through the next
time you launch an Interactive Environment. Thus, we recommend updating the image.
