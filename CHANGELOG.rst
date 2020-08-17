.. _changelog:

0.6.6
-----

This is a release that improves the way images for private projects get pulled, no more GitLab sudo token needed!

Notable improvements
~~~~~~~~~~~~~~~~~~~~

* use user credentials for pulling images for private projects
* user oauth token is removed from repository URL

Breaking changes
~~~~~~~~~~~~~~~~

* kubernetes versions < 1.14 are not supported anymore


Upgrading from 0.6.5
~~~~~~~~~~~~~~~~~~~~

* The version in the welcome page can be updated at `ui.welcomePage.text`

0.6.5
-----

This is a release which only updates the version of the gitlab chart dependency.

Improvements
~~~~~~~~~~~~~~~~~~~~

* More flexibility in configuring the gitlab instance through the values file.

Upgrading from 0.6.4
~~~~~~~~~~~~~~~~~~~~

* No new values required, a `gitlab.extraConfig` block can be used to add settings to the `gitlab.rb` configuration file.

0.6.4
-----

This is primarily a bugfix release.

Bug fixes
~~~~~~~~~

* Fixes a bug which prevented the selection of a non-master branch when launching an environment.

Notable improvements
~~~~~~~~~~~~~~~~~~~~

* Improved display of merge requests in the UI

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, check:

* renku-ui `0.10.3 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.10.3>`__

Upgrading from 0.6.3
~~~~~~~~~~~~~~~~~~~~

* No new values required in the values file
* The version in the welcome page can be updated at `ui.welcomePage.text`

0.6.3
-----

New feature
~~~~~~~~~~~~

⭐️ Project details now include a listing of the commit history

Notable improvements
~~~~~~~~~~~~~~~~~~~~

🚄 Environments: auto-saved branches are filtered per username

🚄 Improve markdown rendering and code highlighting

🚄 Editing markdown files is easier as ``ckeditor`` is partially integrated inside Renku

Bug fixes
~~~~~~~~~

* Dataset contains all folders from unzipped file
* Failing to retrieve metadata for one dataset does not cause the others to fail
* Improved UX for when datasets take too long
* Datasets: no failure when adding ignored files

Miscellaneous
^^^^^^^^^^^^^

- The default R template now uses the latest R (4.0.0). To update it in an existing R project, replace the first line in the Dockerfile with ``FROM renku/renkulab-r:4.0.0-renku0.10.4-0.6.3``
- A Bioconductor image with `bioc 3_11 <https://www.bioconductor.org/news/bioc_3_11_release>`__ is now available. To use it replace the first line in the Dockerfile with ``FROM renku/renkulab-bioc:RELEASE_3_11-renku0.10.4-0.6.3``
- Docker images in project templates use ``renku`` `0.10.4 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v0.10.4>`__

Breaking changes
~~~~~~~~~~~~~~~~

GitLab version: the Renku chart now installs GitLab >= 12.9.0 by default.
GitLab versions < 12.7.0 are supported too, but a ``.gateway.oldGitLabLogout: true`` has to be set explicitly. Note that GitLab versions where `12.7.0 <= version < 12.9.0` are not supported.

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, check:

* renku-ui `0.10.2 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.10.2>`__
* renku-python `0.10.4 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v0.10.4>`__
* renku-gateway `0.8.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.0>`__
* renku-notebooks `0.7.4 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.7.4>`__

* renku-graph `1.0.3 to 0.55.4 <https://github.com/SwissDataScienceCenter/renku-graph>`__

Upgrading from 0.6.2
~~~~~~~~~~~~~~~~~~~~

* No new values required in the values file
* The version in the welcome page can be updated at `ui.welcomePage.text`

0.6.2
-----

New features
~~~~~~~~~~~~

⭐️ Environments: logged-in users without developer access can launch interactive sessions from a project.

⭐️ Environments: interactive sessions can be enabled for logged-out users. Please see the
  `documentation <https://renku.readthedocs.io/en/latest/admin/anonymous-sessions.html>`__ for details.

⭐️ Hiding/showing code cells is now possible from the UI


Notable improvements
~~~~~~~~~~~~~~~~~~~~

🚄 Datasets: dataset creation and import unified in the UI

Bug fixes
~~~~~~~~~

* Datasets now include the folder hierarchy in file listings
* Datasets: avoid recursive addition of the data directory in Renku CLI
* Datasets: fix export to Dataverse
* Datasets: fix metadata commit after `renku dataset unlink`
* Environments: improve styling

Miscellaneous
^^^^^^^^^^^^^

- A maintenance page can now be displayed for when Renkulab is undergoing a scheduled maintenance 🔧
- Help page and dropdown contain links to Renku and Renku CLI documentation 📖
- Easy UI access to GitLab projects, user settings and user profile 👤
- Python environments now include a plugin to monitor memory usage visually 📈
- A new Renku docker image with `Julia <https://julialang.org/>`__ is now available. 📣 To use it just replace the first line of your Dockerfile with ``FROM renku/renkulab:renku0.10.3-julia1.3.1-0.6.2``
- The Tensorflow Renku docker image with Cuda and `Tensorflow 1.14 <https://www.tensorflow.org/>`__ is now available with the latest Renku ``0.10.3``. To use it just replace the first line of your Dockerfile with ``FROM renku/renkulab:renku0.10.3-cuda10.0-tf1.14-0.6.2``
- Docker images in project templates use Renku `0.10.3 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/0.10.3>`__

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, check:

* renku-ui `0.10.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.10.0>`__
* renku-notebooks `0.7.3 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.7.3>`__
* renku-python `0.10.3 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/0.10.3>`__
* renku-gateway `0.7.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.7.1>`__

Upgrading from 0.6.1
~~~~~~~~~~~~~~~~~~~~

* If you want to enable interactive sessions for anonymous users, see the
  `values changelog <https://github.com/SwissDataScienceCenter/renku/blob/master/charts/values.yaml.changelog.md>`__ file and `anonymous sessions documentation <https://renku.readthedocs.io/en/latest/admin/anonymous-sessions.html>`__
* The version in the welcome page can be updated at `ui.welcomePage.text`


0.6.1
-----

**Released 2020-04-01**

New features
~~~~~~~~~~~~

⭐️ Datasets can be imported from data repositories through the UI

⭐️ Datasets allow uploading file hierarchies in zip format

⭐️ CLI: Datasets metadata is editable. Please see the `Dataset documentation <https://renku-python.readthedocs.io/en/latest/commands.html#module-renku.cli.dataset>`__ for details.

⭐️ CLI: enable importing renku datasets

⭐️ CLI: Enable working with data external to the repository `#974 <https://github.com/SwissDataScienceCenter/renku-python/pull/974>`__

Notable improvements
~~~~~~~~~~~~~~~~~~~~

🚄  A file upload can be canceled when creating a dataset

🚄  Environments tab displays information about the resources requested

🚄  Environments tab provides an easy access to the branch/commit file listing

🚄  Improvements to the handling of markdown content

🚄  CLI: starting this version a new migration mechanism is in place, renku command will insist on migrating metadata if its outdated.

Miscellaneous
^^^^^^^^^^^^^

- Various improvements on markdown display for collaboration

- Make help channels more visible

- CLI: wildcard support when adding data from git

- Docker images and project templates use Renku `0.10.2 <https://github.com/SwissDataScienceCenter/renku-python/releases>`__

- A new minimal Renku project template is available on project creation! Use this template if you're using a language other than R or python, or if you're renku-izing an existing python project.

- Newer renkulab docker images also provide interactive environments with a nicer shell (powerline).

Bug fixes
~~~~~~~~~

* Datasets now show file listing with folder hierarchy
* Search uses clearer labeling
* Various fixes to dataset command line bugs

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, check:

* renku-ui `0.9.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.9.1>`__ and `0.9.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.9.0>`__
* renku-python `0.10.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.10.0>`__
* renku-notebooks `0.7.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.7.1>`__

* renku-graph `0.55.4 to 0.49.0 <https://github.com/SwissDataScienceCenter/renku-graph>`__

Upgrading from 0.6.1
~~~~~~~~~~~~~~~~~~~~

* No new values required in the values file
* The version in the welcome page can be updated at `ui.welcomePage.text`



0.6.0
-----

**Released 2020-03-06**

This release includes exciting new features and provides an improved
user experience, mostly with respect to dataset handling.

New features
~~~~~~~~~~~~

⭐️ Datasets can be created from the UI

⭐️ Files can be added to a dataset from the UI

⭐️ Datasets can now be exported to
`Dataverse <https://dataverse.org/>`__

Notable improvements
~~~~~~~~~~~~~~~~~~~~

🚄 Support project-level default settings for environments

🚄 Relevant project/namespace information is shown at
``/projects/user-groupname/`` path

🚄 Cleanup error messages for Renku CLI usage

🚄 Dataset importing is faster with Renku CLI

🚄 Restructured our `documentation <https://renku.readthedocs.io/>`__

Miscellaneous
^^^^^^^^^^^^^

-  R-markdown ``rmd`` files can be visualized within Renkulab ✔️

-  Group avatars are displayed 👤

-  Improved presentation for merge request and issues

-  A Gitlab IDE link has been made available for working with Renku
   projects

-  Link to see a project’s fork information

-  Docker images and project templates now use Renku
   `0.9.1 <https://github.com/SwissDataScienceCenter/renku-python/releases>`__

-  A Renku docker image with
   `Bioconductor <https://github.com/Bioconductor/bioconductor_docker>`__
   is now available 📣

-  R projects now have the directory structures fixed

-  Python now comes with powerline to simplify the command line prompt

-  JupyterHub has been updated to version 1.1.0

-  Prometheus metrics available for graph services

Bug fixes
~~~~~~~~~

-  LFS data is now retrieved when the checkbox is selected 🐞
-  Close the fork dialog after forking
-  Various fixes for lineage including performance

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, check:

* renku-ui:
  `0.7.3 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.3>`__
  and
  `0.8.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.8.0>`__

* renku-gateway
  `0.7.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.7.0>`__

* renku-python
  `0.9.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.9.0>`__
  and
  `0.9.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.9.1>`__

* renku-graph
  `0.48.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/0.48.0>`__

* renku-notebooks
  `0.6.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.6.2>`__

Upgrading from 0.5.2
~~~~~~~~~~~~~~~~~~~~

-  No changes required in the values file for this upgrade


0.5.1
-----

**Released 2019-12-04**

This is a bugfix release that updates the GitLab version required to
allow changing the project name when forking (see
`#616 <https://github.com/SwissDataScienceCenter/renku-ui/issues/616>`__
and
`#626 <https://github.com/SwissDataScienceCenter/renku-ui/issues/626>`__).

0.5.0
-----

**Released 2019-11-27**

New Features
~~~~~~~~~~~~

⭐️ Datasets are now displayed inside a Renku project

⭐️ Datasets can now be searched within available Renku projects

Notable improvements
~~~~~~~~~~~~~~~~~~~~

-  Changed project URLs to show namespace and name instead of project ID
-  Reworked collaboration view with issues list and collapsing issue
   pane 👥
-  Enabled search by username and group 🔍
-  Fork functionality now allows changing the name 🍴
-  Better tools to get information about interactive environments 🕹
-  Better consistency with project and interactive environment URLs 🎯

Miscellaneous
~~~~~~~~~~~~~

-  Commit time is local timezone aware 🕖
-  Images and project templates now use Renku
   `0.8.2 <https://github.com/SwissDataScienceCenter/renku-python/releases>`__
-  A Renku docker image with CUDA, Tensorflow and Tensorboard is now
   available 📣
-  User profile redirects to Keycloak profile 👤
-  Simplified deployment with automatic secrets generation ✔️

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, check:

* renku-ui `0.7.2
  <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.2>`__,
  `0.7.1
  <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.1>`__,
  `0.7.0
  <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.0>`__ and
  `0.6.4
  <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.6.4>`__

* renku-gateway `0.6.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.6.0>`__

* renku-python `0.8.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.2>`__,
  `0.8.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.1>`__,
  `0.8.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.0>`__,
  `0.7.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.7.2>`__
  and
  `0.7.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.7.1>`__

* renku-graph `0.29.3 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/0.29.3>`__

* renku-notebooks `0.6.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.6.2>`__,
  `0.6.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.6.1>`__
  and
  `0.6.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.6.0>`__

Bug fixes
~~~~~~~~~

-  Lineage visualization bugs addressed 🐞
-  Users with developer permissions can now start an interactive
   environment 🚀

Upgrading from 0.4.3
~~~~~~~~~~~~~~~~~~~~

-  Update values file according to `the values
   changelog <https://github.com/SwissDataScienceCenter/renku/blob/master/charts/values.yaml.changelog.md#changes-on-top-of-renku-042>`__


0.4.3
-----

**Released 2019-10-30**

This is a bugfix release that fixes a SPARQL query in the graph service which
was causing Jena to stall and run out of memory (See
`#159 <https://github.com/SwissDataScienceCenter/renku-graph/issues/159>`_ and
`#163 <https://github.com/SwissDataScienceCenter/renku-graph/issues/163>`_).

0.4.2
-----

**Released 2019-08-28**

This is a relatively minor update.

Notable improvements
~~~~~~~~~~~~~~~~~~~~

⭐️ on launching an interactive environment, the user is shown the status of the
  image build - no more guessing whether the Docker image is there!

⭐️ the source of project templates is now configurable so a platform admin can
   provide custom templates if needed

⭐️ data and code nodes are styled differently in the graph view

⭐️ the base user images have been updated, notably the R image is now based on
   Rocker instead of conda

For individual component changes:

* renku-notebooks `version 0.5.1
  <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.5.1>`_

* renku ui: `version 0.6.3
  <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.6.3>`_ and
  PRs `576 <https://github.com/SwissDataScienceCenter/renku-ui/pulls/576>`_ and
  `578 <https://github.com/SwissDataScienceCenter/renku-ui/pulls/578>`_
