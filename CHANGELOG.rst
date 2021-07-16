.. _changelog:

0.8.3
-----

This is a bugfix release that includes fixes to Knowledge Graph. For more details please check the renku-graph `1.36.6 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.36.6>`__ release notes.

0.8.2
-----

This release includes a beta version of the new user interface for Renku. Over the next few releases
we will gradually phase out the old user interface. However, for the time being you can use both. Simply switch 
between the two by clicking the link on the Renku home page.

Bug Fixes
~~~~~~~~~

-  CSS for the Login button on the provider page
   (`#2178 <https://github.com/SwissDataScienceCenter/renku/issues/2178>`__)
   (`d1a0149 <https://github.com/SwissDataScienceCenter/renku/commit/d1a01499622e3dcfc566c942e28eef6e7983be31>`__)

Features
~~~~~~~~

-  **chart:** configure the Renku realm to use the Renku keycloak theme
   (`d527865 <https://github.com/SwissDataScienceCenter/renku/commit/d5278654f4ec13533c3ef3b79b022bef0c66317d>`__),
   closes
   `#2022 <https://github.com/SwissDataScienceCenter/renku/issues/2022>`__
-  **chart:** use keycloak theme with UI 1.0.0 design
   (`35d8980 <https://github.com/SwissDataScienceCenter/renku/commit/35d8980fbd467819ae659fc9239b237bee932135>`__),
   closes
   `#2022 <https://github.com/SwissDataScienceCenter/renku/issues/2022>`__
-  **docs:** new design for renku docs
   (`#2166 <https://github.com/SwissDataScienceCenter/renku/issues/2166>`__)
   (`f2f3985 <https://github.com/SwissDataScienceCenter/renku/commit/f2f398512252fc115f793e41dc4375a3e8bb69c5>`__)

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, please check:

* renku-graph:
  `1.36.5 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.36.5>`__

* renku-core and renku-python:
  `v0.16.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v0.16.0>`__

* renku-ui:
  `0.11.13 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.11.13>`__,
  `1.0.0-beta3 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/1.0.0-beta3>`__

0.8.1
-----

This is a bugfix release that includes a fix of the link on a project forks in Renku UI.

0.8.0
------

This release includes a new version for PostgreSQL and GitLab as well as various improvements and bug fixes to Renku CLI and Environments.
If your PostgreSQL and/or GitLab were deployed as part of Renku, please make sure to backup your volumes before following the upgrade instructions.

Improvements
~~~~~~~~~~~~~

* **PostgreSQL and GitLab upgrade**: We bump the PostgreSQL version from 9.6 to 11 and the GitLab major version from 11 to 13.
* **Project templates**: Community contributed template ``AiiDA`` has been updated. See `a06ab24 <https://github.com/SwissDataScienceCenter/contributed-project-templates/commit/a06ab248e92203343e48854ddc118c4488dd3379>`__.
* **Project templates**: Project templates come with Renku CLI ``v0.15.2`` by default.
* **Renku CLI**: add support to dataset update for detecting changes to local files.
* **Renku CLI**: add support to export `OLOS <https://olos.swiss/>`__ datasets.
* **Renku CLI**: add JSON output format to ``renku dataset ls`` and ``renku dataset ls-files``.
* **Renku CLI**: detect filename from `content-disposition header <https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition>`__ when adding a dataset.

Bug Fixes
~~~~~~~~~~

* **Environments**: remove storage options when launching environments if PVC feature is not enabled.
* **Project templates**: fix project creation to use pinned Renku CLI version.

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, please check:

* renku-notebooks:
  `0.8.12 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.12>`__

* renku-python:
  `v0.15.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/v0.15.1>`__,
  `v0.15.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/v0.15.0>`__

Upgrading from 0.7.13
~~~~~~~~~~~~~~~~~~~~~

**BREAKING CHANGES**

Please follow [these instructions](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart#upgrading-to-080) carefully.

The resulting changes in the values file should be:
* NEW/EDIT *postgresql.persistence.existingClaim* will most likely need to be modified in the course of upgrading your PostgreSQL version. See [these instructions](https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/utils/postgres_migrations/version_upgrades/README.md)
* NEW/EDIT/DELETE *gitlab.image.tag* might have to be adjusted as we do a GitLab major version bump in with this release.

0.7.13
------

Bug Fixes
~~~~~~~~~~

* **Knowledge Graph**: improve lineage visualization by skipping overridden edges
* **Knowledge Graph**: fix rest client to classify failure responses properly

For more details please check renku-graph  `1.36.3 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.36.3>`__

Upgrading from 0.7.12
~~~~~~~~~~~~~~~~~~~~~

A new client application ``renku-cli`` in keycloak has been added. This needs a value for ``global.gateway.cliClientSecret`` which could be generated through ``openssl rand -hex 32``.

0.7.12
------

Features
~~~~~~~~~

* **Notebooks API**: enable endpoint for getting autosave information

Bug Fixes
~~~~~~~~~~

* **Notebooks**: missing annotation handling in marshmallow

0.7.11
------

Features
~~~~~~~~

* **Renku CLI**: support moving files between datasets with ``renku mv`` (`CLI documentation <https://renku-python.readthedocs.io/en/latest/commands.html#module-renku.cli.move>`__).
* **Renku CLI**: ability to update local project from its template and to update the Dockerfile to install the current version of renku-python using renku migrate.

* **Projects**: ability to generate project-creation links, embedding metadata to automatically pre-fill input fields. For more details on how to use this feature please read our `documentation <https://renku.readthedocs.io/en/latest/user/templates.html#create-shareable-project-creation-links-with-pre-filled-fields>`__.

Improvements
~~~~~~~~~~~~~

* **Renku CLI**: support for Unicode paths in renku run (including emojis).

* **Projects**: add preview for common hidden files.

* **Templates**: use Renku CLI 0.14.2


Bug Fixes
~~~~~~~~~~

* **Environments**: If Automatically fetch LFS data enabled, unset LFS auth mode in init container
* **Projects**: restore support for project-level default environments parameters (e.g. CPU and memory requests).

* **Core service**: fix project_clone with git ref specified.

* **Knowledge graph**: event status update process to remove delivery info in a single transaction
* **Knowledge graph**: improvements in lost subscriber node finding algorithm


Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, please check:

* renku-ui:
  `0.11.11 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.11.11>`__,
  `0.11.10 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.11.10>`__

* renku-notebooks:
  `0.8.11 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.11>`__

* renku-python:
  `v0.14.2 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/v0.14.2>`__

* renku-graph:
  `1.36.2 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.36.2>`__,
  `1.36.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.36.1>`__


0.7.10
------

Features
~~~~~~~~

* **Docker images**: a new base-image extension is added that includes a full desktop server, which allows users to run Linux desktop applications directly from their RenkuLab sessions. For more information please refer to this `discourse  post <https://renku.discourse.group/t/feature-virtual-desktop-vnc-for-renkulab/308>`__

Improvements
~~~~~~~~~~~~~

* **Projects**: improve UX when forking a project, and handle up to 1000 namespaces
* **Projects**: allow setting project avatar
* **Environments**: simplify getting the registry image URL for running sessions

* **Docker images**: update all of the base libraries and change the underlying operating system to Ubuntu 20.04 as well as upgrades the python version to 3.8.
* **Templates**: bump Bioconductor version to 3.12
* **Templates**: bump R version to 4.0.4
* **Templates**: use Renku CLI 0.14.1 and above mentioned docker images

* **Knowledge graph**: new service used for routing commits synchronization

Bug Fixes
~~~~~~~~~~

* **Collaboration**: fix issue page not loading properly

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, please check:

* renku-ui:
  `0.11.9 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.11.9>`__

* renku-graph:
  `1.36.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.36.0>`__,
  `1.35.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.35.0>`__

0.7.9
-----

Bug Fixes
~~~~~~~~~~

* **Core**: add error handling if push from temporary branch fails
* **Core**: fix handling of '@' in filenames
* **Core**: fix save and push to correctly handle merge conflicts
* **Service**: sync service cache with remote before operations to prevent cache getting out of sync
* **Datasets**: allow importing a dataset from a non-public project
* **Graph**: fix fail to update event status when triples generation fails

* **Deployment**: fix gateway gitlabClientSecret in minimal-values template

0.7.8
-----

Features
~~~~~~~~~~

* **Datasets**: show a notification when uploading big files
* **Datasets**: improve naming for imported datasets
* **Datasets**: sort by date on the free-text dataset search

* **Projects**: update the project fork flow to match project creation

* **CLI**: add service component management commands

Improvements
~~~~~~~~~~~~~

* **Datasets**: the dataset details returns information about project it belongs to

* **Lineage**: prevent showing the whole graph when displaying a single file's lineage
* **Lineage**: support for committers name changing
* **Knowledge Graph**: improve provisioning flow; re-process stale events sooner, use smaller processes

* **Core**: exclude renku metadata from being added to git lfs

Bug Fixes
~~~~~~~~~~~

* **Datasets**: fix creation date when searching datasets
* **Datasets**: fail gracefully when trying to access a missing dataset
* **Datasets**: dataset import to move temporary files and become more resilient to errors
* **Datasets**: handle datasets with ',' in the name correctly

* **Environments**: image pull secret for pod restart
* **Environments**: support for long project title

* **User interface**: check lfs status properly when previewing a file
* **User interface**: fix broken markdown preview caused by links without a reference
* **User interface**: handle sub-groups on projects list

* **Core**: call git commands for batches of files to prevent hitting argument length limits

* **Core Service**: correctly handle HTTP server errors and ref on project.clone
* **Core Service**: use project_id as part of project filesystem path


Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, please check:

* renku-ui:
  `0.11.8 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/v0.11.8>`__,
  `0.11.7 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/v0.11.7>`__

* renku-core and renku-python:
  `0.14.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v0.14.0>`__

* renku-notebooks:
  `0.8.10 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.10>`__

Upgrading from 0.7.7
~~~~~~~~~~~~~~~~~~~~~


**Breaking change**  Keycloak chart dependency has been upgraded from ``4.10.2`` to ``9.8.1`` which will trigger an irreversible database migration, check out `the upgrade instructions <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/README.rst#upgrading>`__ for more details.

Most notably, keycloak values are less nested, so at the level of the Renku chart values, keycloak.keycloak.X.Y becomes keycloak.X.Y. You can also check out `the instructions <https://github.com/codecentric/helm-charts/tree/master/charts/keycloak#upgrading>`__ on how to upgrade aspects not covered by default in the Renku chart.

If the Renkulab deployment includes keycloak, the values file should be modified as follows:
* DELETE - the section keycloak.keycloak.persistence has been removed. Database connection details are specified through the keycloak.extraEnv and keycloak.extraEnvFrom blocks. See the `Renku values file <https://github.com/SwissDataScienceCenter/renku/blob/0.7.8/helm-chart/renku/values.yaml#L129-L154>`__ for reference.
* EDIT - keycloak.keycloak.username has been moved to global.keycloak.user.

Finally, before applying the helm upgrade, the Keycloak statefulset should be deleted.


0.7.7
-----

Improvements and fixes
~~~~~~~~~~~~~~~~~~~~~~~

- **User interface** Improve UX for non-logged users (`0.11.5 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.11.5>`__)
- **User interactive sessions** Some bug fixes (`0.8.9 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.9>`__)
- **Knowledge graph** Bug fixes and small improvements (`1.27.3 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.27.3>`__ to `1.27.5 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.27.5>`__).
- **Deployment** Helm tests enabled to run our acceptance tests suite (see the `acceptance tests <https://renku.readthedocs.io/en/0.7.7/admin/index.html#acceptance-tests-optional>`__ section of our deployment documentation). A `make-values.sh <https://github.com/SwissDataScienceCenter/renku/blob/0.7.7/charts/example-configurations/make-values.sh>`__ script is available to generate a minimal values file for Proof-of-Concept deployments, for more information please refer to `our deployment documentation <https://renku.readthedocs.io/en/latest/admin/index.html#create-a-renku-values-yaml-file>`__.

0.7.6
-----

This is a bugfix release, it contains fixes for the Knowledge Graph (PRs `#608 <https://github.com/SwissDataScienceCenter/renku-graph/pull/608>`__ and `#609 <https://github.com/SwissDataScienceCenter/renku-graph/pull/609>`__) and user interactive sessions (renku-notebooks `0.8.8 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.8>`__).

0.7.5
-----

New features
~~~~~~~~~~~~

- **Dataset show** Dataset metadata can now also be seen in the Renku CLI using the ``renku dataset show`` command.

- **Knowledge graph** Access control to resources on knowledge graph.

- **RenkuLab** Support for deployments which use TLS certificates issued by a private CA.

Fixes
~~~~~

- **UI** Improve performance of file preview

- **UI** Show project datasets even if user is not logged in

- **Interactive sessions** Fix a bug that made the automatic pull of LFS data on session start fail for private repositories.

- **Interactive sessions** Improve handling of failed session launches.

- **Interactive sessions** Fix status information on session termination.

- **Project migration** Feedback and speed of the recently introduced migration for workflows has been improved to handle very large projects better.


Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, check:

* renku-notebooks:
  `0.8.7 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.7>`__

* renku-core:
  `0.13.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v0.13.0>`__,
  `0.12.3 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v0.12.3>`__

* renku-ui:
  `0.11.4 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/v0.11.4>`__

0.7.4
-----

This is a patch release that includes a notebooks change and some improvements to the Knowledge Graph.

0.7.3
-----

This release contains some very nice improvements to the file and datasets management and visualization as well as project migrations.

New features
~~~~~~~~~~~~

- **Dataset removal** A dataset can be now removed from a project, either from the UI or with the Renku CLI.

- **Pinned environments image** A project can pin interactive environments to a specific image, independent of the content of the project. This can be useful for situations like courses where everyone should use the environment defined by the instructor. To take advantage, see the `renku configuration file documentation <https://renku.readthedocs.io/en/latest/user/templates.html?highlight=.dockerignore#renku>`_.

- **Project migration** Project migration has been improved, allowing users to migrate the template, Dockerfile and Renku version with just one click. Information about the latest and current Renku CLI and template versions are displayed in the Status section of a project. Additionally, a migrationscheck command is available in Renku CLI.

Improvements
~~~~~~~~~~~~

- **Dataset upload** When uploading files, a progress bar is displayed.

- **Dataset visualization** For a better experience on dataset listing inside projects, description and author list have been cropped. The full content of both is still available when accessing the dataset.

- **File preview** Preview of C++ and Fortran files is supported.

- **File download** Files can be downloaded from the Renku UI.

- **Autosaved work** Commits with autosaved content are marked with ``*`` and dialog is more specific.

- **Autosave git LFS** When a session is closed and work is automatically saved, large files are added to LFS according to the project's settings see `renku config documentation <https://renku-python.readthedocs.io/en/v0.12.0/commands.html#available-configuration-values>`_.

- **Core** Renku CLI commit messages are shortened to 100 characters for readability.

- **New projects** Templates to create new projects now use the new Renku CLI version ``0.12.2`` by default.


Fixes
~~~~~

- **Auth credentials**: the way Renku environments handle git credentials has been improved.


Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, check:

* renku-ui
  `0.11.3 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.11.3>`__

* renku-gateway
  `0.9.3 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.9.3>`__

* renku-core
  `0.12.2 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/0.12.2>`__

Upgrading from 0.7.2
~~~~~~~~~~~~~~~~~~~~

-  No changes required in the values file for this upgrade

0.7.2
-----

This release brings several smaller improvements and bug-fixes, the most
notable of which are:

New features
~~~~~~~~~~~~
- **Datasets** Add new fields and the possibility to edit existing fields to
  the web UI.
- **Knowledge Graph** Improve information flow related to KG integration and
  Renku version updates in the UI.

Fixes
~~~~~

- **Project creation**: improve name validation, the handling of non ASCII
  characters and a bug which led to failures when fetching the available templates.
- **Graph processing**: fix a bug in prioritizing events for processing.

For details check out the individual component updates.


0.7.1
-----

This release features an update to the default project templates, bumping
the default `renku CLI version to ``0.12.0`` <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v0.12.0>`_
and some backend bug fixes.

Fixes
~~~~~

- **Graph building**: several improvements to graph building, including a fix
  for metadata compaction in renku-core that caused some entities to not
  get processed.


0.7.0
-----

This release brings a lot of important new features to both Renkulab and the
Renku CLI. It's our best one yet!

New features
~~~~~~~~~~~~

- **Project templates**: you can now create custom templates for your projects and
  use them on project initialization. Great for groups or courses! See the
  `docs <https://renku.readthedocs.io/en/latest/user/templates.html>`_ for more
  details.

- **Datasets**: you can now search for datasets and import them into projects
  directly from the Renkulab web UI. The dataset import and creation flow has
  also been much improved thanks to changes in the core service backend. Note
  that importing datasets from other renku projects from the UI currently works
  only for datasets in public repositories.

- **Data import**: files can be imported into datasets directly from a URL.

- **Project migration**: the metadata of renku projects occasionally changes. In
  order for all the components to work well together, the metadata must be kept
  in sync. Previously you needed to do this manually with the CLI but now it's
  as easy as clicking a button.

- **Template simplification**: We have decoupled the CLI version from the base image and
  made it easier to override in your own environments. See the `project template
  README <https://github.com/SwissDataScienceCenter/renku-project-template/blob/master/README.md>`_
  for details.

- **Improved editor**: All text editing components are using an enhanced editor
  that allows seamless switching between WYSIWYG and markdown.

- **renku save**: the Renku CLI now features a ``save`` command that
  simplifies the process of committing and pushing your project to the server.


Improvements
~~~~~~~~~~~~

- **Dataset deletion**: Datasets used to stick around even after getting deleted
  from the project. This has now been fixed and deleted datasets no longer appear in
  the dataset listings.

- **git credentials**: the interactive sessions now handle your git credentials
  in a way that allows you to seamlessly access any of your private repositories
  on renkulab from within an interactive session.

- **git hooks in interactive sessions**: git hooks were not previously installed
  per default in interactive sessions, which meant that some nice features like
  automatically pushing large files to LFS did not work correctly. This has now
  been corrected and should hopefully save many repositories from improperly
  handled data!

- **graph redesign**: Under the hood, the renku-python library has a completely
  redesigned knowledge graph model. This enormous effort doesn't translate to
  user-visible improvements yet, but they're coming in the next release!


Many other improvements and bug fixes across all of the renku components, which
have significantly improved the stability of the entire platform!


Upgrading user projects
~~~~~~~~~~~~~~~~~~~~~~~

To upgrade your existing renkulab project to the latest images, the easiest is
if you copy/paste a ``Dockerfile`` that suits your project
(python/R/Bioconductor) from the `renku project templates repository
<https://github.com/SwissDataScienceCenter/renku-project-template/blob/master/python-minimal/Dockerfile>`_.

Note that even if you upgrade the image, the project in the repository will still
need to be migrated. You can do this on the command line by running ``renku
migrate`` in your project or follow the instructions in the UI when prompted.


Changes and improvements in the platform deployment
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- **Breaking change: Postgresql chart**: we have switched to the Bitnami version
  of the Postgresql
  helm chart - this requires a manual intervention when upgrading from renku
  ``0.6.8``.

- **Kubernetes**: renku is now compatible with kubernetes >= 1.16 (tested on 1.16)

- **Helm3**: the renku helm charts are now compatible with helm 3


0.6.8
-----

This is a minor release that contains improvements and fixes to the UI.

New features
~~~~~~~~~~~~

⭐️ support for air-gapped deployments: RenkuLab's UI contains all resources necessary to run (no connection to the internet needed).

⭐️ integration with statuspage.io: users get a visual notification for incidents and scheduled maintenance, additionally they can check the status of RenkuLab's components in ``<renkulab>/help/status``.

Improvements
~~~~~~~~~~~~

🚄 anonymous users: can navigate in public projects' collaboration tab

🚄 privacy: add privacy page and cookie consent banner

🚄 markdown: display of relative paths, and improvement in file preview

🚄 Julia: source and project files correctly rendered in the file browser

Bug fixes
~~~~~~~~~

🐛 500 error code is handled at the UI when starting environments

🐛 fix rendering issues with WYSIWYG editor toolbar

Upgrading from 0.6.7
~~~~~~~~~~~~~~~~~~~~

* The version in the welcome page can be updated at ``ui.welcomePage.text`` in the values file.
* The integration with `statuspage.io <https://www.atlassian.com/software/statuspage>`__ can be enabled by adding the unique project ID at ``ui.statuspage.id`` in the values file

0.6.7
-----

This is a bugfix release.

Bug fixes
~~~~~~~~~

🐛 fix pulling of lfs data in the init container of interactive environments

Upgrading from 0.6.6
~~~~~~~~~~~~~~~~~~~~

* The version in the welcome page can be updated at ``ui.welcomePage.text`` in the values file.

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

* The version in the welcome page can be updated at ``ui.welcomePage.text`` in the values file.

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
