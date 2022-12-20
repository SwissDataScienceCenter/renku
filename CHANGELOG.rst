.. _changelog:

0.21.0
------

Renku ``0.21.0`` brings tidings of tweaks and bug fixes to make your Renku experience a little bit smoother.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

* 🎨 **UI**: Improve the layout of the project creation, session start, and file browser pages.

**🐞 Bug Fixes**

* 📃 **UI**: Update broken links to local projects and documentation, and add more links to useful resources
  (`#2199 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2199>`_,
  `#2207 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2207>`_,
  `#2209 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2209>`_).
* 🚀 **UI**: Fix glitches with autosave and improve layout of session pages
  (`7fbda29 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2199/commits/7fbda299f6e2a956abc541565e3680160f09609d>`_,
  `#2211 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2211>`_).
* 🔦 **UI**: Prevent flashing inputs when forking a project
  (`#2157 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2157>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

* **Gateway**: Add endpoint for refreshing expiring GitLab tokens
* **Knowledge Graph**: Improve functionality to refresh access tokens before expiration
* **Knowledge Graph**: Enhance migration functionality to restore missing CompositePlans 
* **Knowledge Graph**: Fix to prevent data corruption which could previously occur due to duplicate Project creation dates
* **Sessions**: Check LFS size and available disk space before cloning
* **UI**: Receive notifications through WebSocket when session state changes
  (`#2145 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2145>`_,
  `#2189 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2189>`_).

**Bug fixes**

* **Gateway**: Do not remove Redis clients on logout
* **Gateway**: Address security vulnerabilities
* **Knowledge Graph**: Switched to the latest Alpine Linux to address docker image vulnerabilities identified by Snyk
* **Knowledge Graph**: Handle cases when Plan Invalidation Time is wrong
* **Knowledge Graph**: Make the Cross Entity search API results sorting case-insensitive
* **Knowledge Graph**: Escape Lucene keywords from Cross-Entity Search `query` parameter
* **Knowledge Graph**: Return all inactive Projects from the User's-Projects API (previously only returned 20)
* **Sessions**: Handle expiring GitLab tokens

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.18.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.18.0>`_
- `renku-notebooks 1.14.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.14.0>`_
- `renku-graph 2.25.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.25.0>`_
- `renku-ui 2.14.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.14.0>`_

0.20.0
------

Renku ``0.20.0`` tidies up the RenkuLab session start sequence, as well as an assortment of improvements and bug fixes. 

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

* 🚀 **UI**: We tidied up the session launch sequence to have a unified appearance, regardless of where on RenkuLab you start your session from. Sessions also have better logs and error handling.
* 🎨 **UI**: The RenkuLab login and logout pages have been updated with the latest styling.
* 💬 **Renku CLI**: When you run ``renku save`` in a clean but unpushed repository, ``renku`` now informs you that it has pushed changes to the remote, rather than just saying that there were no changes to save.  

**🐞 Bug Fixes**

* 💔 **Renku CLI**: Fixed an issue where ``renku workflow compose`` would break ``renku workflow list``.
* 🍴 **UI**: Fixed the Project name field getting reset when forking a project. 
* 📃 **UI**: Fixed issues with downloading session logs.

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

* **Knowledge Graph**: Read lineage data from the new dataset in the Triples Store
* **Knowledge Graph**: Added info about Composite Plans to the Cross-Entity Search API
* **Knowledge Graph**: Adopted ``renku-python 1.10.0`` with significantly improved graph export performance
* **Renku core service**: Extended the workflow API, adding an ``export`` endpoint that returns the workflow definition.
* **Sessions**: Added support for running sessions in a separate namespace
* **Sessions**: Added caching for Jupyter servers

**Bug fixes**

* **Gateway**: Removed trailing slash from redirect links
* **Knowledge Graph**: Fixed wrong Plans creation dates through migration
* **Knowledge Graph**: Switched to using Project Access Tokens for accessing GitLab API
* **Renku core service**: Fixed Plans creation dates from preceding their corresponding Activities
* **Sessions**: Fixed properly accept getting session logs without a limit

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.17.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.17.0>`_
- `renku-graph 2.22.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.22.1>`_
- `renku-graph 2.22.2 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.22.2>`_
- `renku-graph 2.22.3 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.22.3>`_
- `renku-graph 2.23.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.23.0>`_
- `renku-graph 2.24.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.24.0>`_
- `renku-python 1.9.3 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.9.3>`_
- `renku-python 1.10.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.10.0>`_
- `renku-ui 2.12.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.12.0>`_
- `renku-ui 2.13.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.13.0>`_
- `renku-notebooks 1.13.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.13.0>`_


0.19.1
------
Version 0.19.1 is a minor bugfix release to the UI. In certain situations, the session *save* and *refresh* buttons would
report that they were not supported, when in fact they would have worked. This problem has been fixed.

Bug fixes
~~~~~~~~~~

* **UI**: improve robustness of sessions save 💾 and refresh 🔄 buttons (`#2100 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2100>`_)


Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 2.11.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.11.1>`_

0.19.0
------
This release adds support
`for showing workflows in the UI <https://renku.readthedocs.io/en/latest/topic-guides/workflows.html>`_.
You can visualize workflows in project pages by clicking on the new `Workflows` tab.
Support for editing workflows and searching them through multiple projects will come in a feature release.

This also brings changes to the way data is organized in the Triples Store and bug-fixes to improve
the core service stability.

Features
~~~~~~~~

* **UI**: browse and visualize workflows in projects 🔀 (`#2038 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2038>`_)
* **UI**: add refresh button to update live sessions ↪️
* **Renku core service**: add ``/workflow_plans.list`` and ``/workflow_plans.show`` for listing and showing workflows in a project 🔀
* **Knowledge Graph**: use Named Graphs dataset in the Triples Store for provisioning processes and APIs 💾

Bug fixes
~~~~~~~~~~

* **UI**: fill values correctly in new project form links 🔗 (`#2026 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2026>`_)
* **UI**: handle corrupted autosave information and improve warnings when starting a session ⚠️
* **Renku core service**: fix intermittent issue with project cache concurrency 🏃
* **Renku core service**: fix import of private datasets with some cloud-native github instances ⤵️

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-python 1.9.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.9.1>`_
- `renku-graph 2.22.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.22.0>`_
- `renku-ui 2.11.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.11.0>`_

0.18.5
------

This release fixes a bug in the core-service and includes improvements for the renku gateway service

Features
~~~~~~~~

* **Gateway**: enable ``/gitlab`` path even when external GitLab is configured, and forward traffic to external GitLab

Bug fixes
~~~~~~~~~~

* **Renku Core Service**: fix temporary working directory in service getting accidentally removed by other threads
* **Project templates**: fix broken ``nbconvert`` package needed by jupyter notebooks and server

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-python 1.8.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.8.1>`_
- `renku-project-template 0.3.5 <https://github.com/SwissDataScienceCenter/renku-project-template/releases/tag/0.3.5>`_
- `renku-gateway 0.16.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.16.0>`_

0.18.4
-------

This release fixes a bug in sessions.

Bug fixes
~~~~~~~~~~

* **UI**: prevent occasionally flashing a loader when working with sessions

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 2.10.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.10.1>`_


0.18.3
------
This release brings improvements and bug fixes to Renku UI.

Features
~~~~~~~~
* **UI**: Save session button (`#1957 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1957>`_, `#1985 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1985>`_, `#2040 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2040>`_)
* **UI**: Add environment variables when starting a session (`#2058 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2058>`_, `#2066 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2066>`_)
* **UI**: Add progress indicator when starting a session (`#1879 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1879>`_, `#2054 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2054>`_)
* **Sessions**: Detailed session start status breakdown (`#1289 <https://github.com/SwissDataScienceCenter/renku-notebooks/issues/1289>`_)

Bug fixes
~~~~~~~~~

* **Renku Core Service**, **Renku CLI**: Fixes Dockerfile Renku version when force setting a template to update an old project
* **Renku CLI**: Allow passing in multiple custom metadata entries for Project and Dataset entities
* **Knowledge Graph**: fixes improving stability
* **UI**: Restore notebook rendering (`#2052 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2052>`_)

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-core 1.8.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.8.0>`_
- `renku-ui 2.10.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.10.0>`_
- `renku-notebooks 1.12.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.12.0>`_
- `amalthea 0.6.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.6.0>`_
- `renku-graph 2.21.2 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.21.2>`_

0.18.2
------

This release includes fixes and improvements for the renku gateway service that deals with authentication
It also includes improvements to the renku documentation about CLI plugins

Features
~~~~~~~~

* **Documentation**: add CLI plugins section

Bug fixes
~~~~~~~~~~

* **Gateway**: re-initialize keycloak server-side client if needed
* **Gateway**: remove anonymous user ID creation because it is handled by the ui-server
* **Tests**: Modify acceptance test wait duration for locating a terminal in a session

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.15.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.15.0>`_


0.18.1
------

This release comes with the first step towards a new data organization inside the Renku Knowledge Graph. Although completely transparent for the users at this point, the change brings substantial additions to the internal processes within the Knowledge Graph as well as adds a migration which copies all the data in the old format to the new one. For the time being, data in both the new and the old format will be kept in sync, however the API will still use the data in the old format.

Features
~~~~~~~~

* **Knowledge Graph**: new parallel processes to provision data to Named Graphs datasets in the Triples Store
* **Knowledge Graph**: a new Triples Store migration that creates transformed version of all the data in the new Named Graphs dataset

Bug fixes
~~~~~~~~~~
* **Knowledge Graph**: Jena upgraded to 4.6.1 to address an issue that in certain circumstance makes Jena unresponsive
* **Sessions**: various bug fixes and improvements

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.21.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.21.1>`_
- `renku-notebooks 1.11.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.11.1>`_

0.18.0
------

This release brings improvements and bug fixes to renku-ui and renku-notebooks.

Features
~~~~~~~~

* **UI**: reduce unnecessary 'project locked' notifications (`#1982 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1982>`_, `#2025 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2025>`_)
* **UI**: expose project metadata as JSON-LD (`#1867 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1867>`_, `#2022 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2022>`_)


Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 2.9.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.9.0>`_

Upgrading from 0.17.x
~~~~~~~~~~~~~~~~~~~~~~

BREAKING CHANGES!
The renku-ui helm chart was restructured:
* the field ``ui.baseUrl`` was moved to ``ui.client.url``
* the field ``ui.gatewayUrl`` was moved to ``ui.gateway.url``

Consult `the values changelog file <helm-chart/values.yaml.changelog.md>`_ for details.

Upgrading from 0.16.x
~~~~~~~~~~~~~~~~~~~~~~

The way anonymous sessions are handled has changed, and this means that an anonymous session
that was *started before the upgrade* will no longer be accessible afterwards. You may want
to notify users in advance and check that the number of running anonymous sessions is small
before performing the upgrade.


0.17.2
------

Fixes a bug in the Graph preventing the provisioning process to hang for projects using template in a certain state.

Features
~~~~~~~~

* **Knowledge Graph**: ``visibility`` property added to the response of the Dataset Details API (`#1085 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1085>`_)

0.17.1
------

Fixes a bug in the UI with anonymous sessions that was caught after tagging 0.17.0,
but before deploying that version.

Features
~~~~~~~~

* **UI** New styling of project and datasets (`#1984 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1984>`_, `#2001 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2001>`_, `#1964 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1964>`_, `#1978 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1978>`_, `#2005 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2005>`_)
* **UI** New full-screen session view  (`#1988 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1988>`_, `#2009 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2009>`_)

Bug fixes
~~~~~~~~~~
* **Sessions** allow usernames starting with numbers/symbols (`#1213 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1213>`_)

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 2.8.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.8.1>`_
- `renku-notebooks 1.11.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.11.0>`_

Upgrading from 0.16.x
~~~~~~~~~~~~~~~~~~~~~~

The way anonymous sessions are handled has changed, and this means that an anonymous session
that was *started before the upgrade* will no longer be accessible afterwards. You may want
to notify users in advance and check that the number of running anonymous sessions is small
before performing the upgrade.

0.17.0
------

This release brings improvements and bug fixes to Renku Graph, Renku CLI, Amalthea, Sessions and Renku UI.

Features
~~~~~~~~~

* **Knowledge Graph**: New ``GET /knowledge-graph/ontology`` providing documentation for ontology used in KG
* **Knowledge Graph**: Cross-Entity Search resource to filter by namespaces (#1075)
* **Knowledge Graph**: Dataset Details resource to indicate if Dataset was imported from a tag (#1074)
* **Knowledge Graph**: A new Project Dataset's Tags API (#1071)
* **Knowledge Graph**: Link to Dataset's Tags on the Dataset Details and Project's Datasets endpoints (#1072)
* **Knowledge Graph**: Details about namespaces added to the Cross-Entity Search response (#1070)
* **Knowledge Graph**: Info about creator's affiliation added to the Project Details response (#1069)
* **Knowledge Graph**: A new User's Projects resource (#1066)
* **Renku CLI**: Changed dataset logic to put all files into a dataset's data directory. Allow customizing a dataset's data directory
* **UI** improve session start flow (`#1990 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1990>`_, `#2003 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2003>`_)

Bug fixes
~~~~~~~~~~

* **Knowledge Graph**: A process to retry all the events failed due to CLI version mismatch
* **Renku CLI**: Show SSH password prompt when using an SSH key with a password
* **UI** fix file tree display when changing to original project from fork (`#1907 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1907>`_, `#2015 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2015>`_)
* **UI** prevent repeated queries to projects API (`#2017 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2017>`_)
* **UI** render star project button for anon users (`#2014 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2014>`_)
* **UI** restore back button navigation on projects for anon users (`#2017 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2017>`_)

Misc
~~~~~~~~~~

* **Knowledge Graph**: Renku CLI upgraded to 1.7.1
* **Knowledge Graph**: Jena upgraded to 4.6.0

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.18.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.18.0>`_
- `renku-graph 2.17.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.17.0>`_
- `renku-ui 2.8.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.8.0>`_


0.16.0
------

This release brings improvements and bug fixes to Renku Graph, Amalthea, Sessions and Renku UI.

⚠️ Please mind that Renku Graph contains changes requiring intervention from an administrator (see below for further details).

Features
~~~~~~~~~

* **Knowledge Graph**: new ``GET /knowledge-graph/ontology`` providing documentation for ontology used in KG
* **Knowledge Graph**: ``GET /projects/:namespace/:name`` to honor ``Accept: application/ld+json`` header
* **Sessions**: standardized error responses from the API
* **Renku UI**: update forms style and layout

Bug fixes
~~~~~~~~~~

* **Knowledge Graph**: triples store provisioning to support Command Parameters without position
* **Amalthea**: fixed an issue where some metrics were published more than once
* **Amalthea**: set sensible values for Prometheus histogram metric buckets
* **Sessions**: avoid flashing "Failed" status when starting a new session
* **Renku UI**: show the correct dialog when starting a session from an autosave

Misc
~~~~~~~~~~

* **Knowledge Graph**: Jena upgraded to 4.5.0

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.16.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.16.0>`_
- `renku-graph 2.15.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.15.0>`_
- `renku-graph 2.14.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.14.0>`_
- `renku-notebooks 1.10.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.10.0>`_
- `renku-ui 2.7.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.7.0>`_
- `amalthea 0.5.2 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.5.2>`_


Upgrading from 0.15.0
~~~~~~~~~~~~~~~~~~~~~~

BREAKING CHANGES!
Renku Graph upgrades Jena to 4.5.0 as well as introduces a new Helm chart for Jena. This change requires manual removal of the old Jena StatefulSet. The removal command is:

``kubectl delete statefulset <helm-release-name>-jena-master -n <k8s_namespace>``

The change also makes the ``graph.jena.dataset`` property from the ``values.yaml`` obsolete so it can be removed as described in `the values changelog file <helm-chart/values.yaml.changelog.md>`_.


0.15.0
------

This release features improvements and bug fixes to Renku CLI, UI, and User Sessions.

⚠️ Please mind that Renku UI contains changes requiring intervention from an administrator (see below for further details).

Features
~~~~~~~~~

* **Knowledge Graph**: expose OpenAPI documentation for the lineage endpoint 📃
* **Renku CLI**: improve UX around renku login when errors occur 👤
* **Renku CLI**: use existing remote image when starting sessions 💽
* **Renku CLI**: add an option to skip metadata update when executing workflows 🏃
* **Renku UI**: add support for Mermaid format in Markdown files 🧜‍♀️
* **Renku UI**: update layout, font, and colors 🎨
* **Renku UI**: add social links 🔌

Bug fixes
~~~~~~~~~~

* **User sessions**: prevent logging unnecessary errors when starting new sessions ✏️
* **Renku CLI**: fix merge-tool issues 🧰
* **Renku CLI**: prevent deleting plans still in use and using already deleted plans 🗑

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 2.6.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.6.0>`_
- `renku-python 1.6.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.6.0>`_
- `renku-notebooks 1.9.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.9.1>`_
- `amalthea 0.5.1 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.5.1>`_
- `renku-graph 2.13.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.13.1>`_


Upgrading from 0.14.1
~~~~~~~~~~~~~~~~~~~~~~

BREAKING CHANGES!
Mind the changes to the structure of the values file for `ui` and `uiserver`.

More details available in `the values changelog file <helm-chart/values.yaml.changelog.md>`_.


0.14.1
------

This release updates a minor GitLab version to ``14.10.5``.

Upgrading from 0.14.1
~~~~~~~~~~~~~~~~~~~~~~

BREAKING CHANGES!
We advise admins to make a backup of their GitLab and PostgreSQL volumes before going through this upgrade.


0.14.0
------

This release updates a minor GitLab version to ``14.9.5``.

Upgrading from 0.13.0
~~~~~~~~~~~~~~~~~~~~~~

BREAKING CHANGES!
We advise admins to make a backup of their GitLab and PostgreSQL volumes before going through this upgrade.

0.13.0
-------

This release introduces important CLI features as well as improvements around dataset upload, user sessions support and knowledge graph.
There are also chart gateway-related updates, we advice admins to please look at the ``Upgrading`` section.

Features
~~~~~~~~~~

* **Dataset**: improve upload performance and robustness
* **Renku CLI**: add a command to revert workflows
* **Renku CLI**: allow exporting datasets to a local directory
* **Renku CLI**: add support for listing dataset files for a specific dataset version
* **Renku Core Service**: allow partial updates on dataset and project edit
* **Renku Core Service**: support chunked file uploads
* **User sessions**: support for injecting environment variables through the API
* **User sessions**: support for storing detailed metrics in S3 buckets
* **Authentication**: improvements in the organization and setup of internal components
* **Knowledge Graph**: support for project path changes in GitLab

Bug fixes
~~~~~~~~~~

* **Dataset**: fix intermittent bug in importing datasets
* **Knowledge Graph**: fixes improving services stability and data correctness
* **Renku Core Service**: fix project id generation from the project's namespace
* **User sessions**: fix bug that could lead to endless spinner when autosave information exists

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 2.5.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.5.0>`_
- `renku-python 1.5.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.5.0>`_
- `renku-notebooks 1.9.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.9.0>`_
- `amalthea 0.5.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.5.0>`_
- `renku-gateway 0.14.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.14.0>`_
- `renku-graph 2.12.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.12.0>`_


Upgrading from 0.12.17
~~~~~~~~~~~~~~~~~~~~~~

If using self-signed CA certificates additional values are required when upgrading to ``0.13.0`` so that the
Traefik Helm chart in ``renku-gateway`` can trust these certificates. The values that will need to be added are ``gateway.traefik.additionalArguments``
and ``gateway.traefik.volumes``. Refer to the `values file <https://github.com/SwissDataScienceCenter/renku-gateway/blob/0.14.0/helm-chart/renku-gateway/values.yaml>`_
in the ``renku-gateway`` repo for more details.

0.12.17
-------

This release introduces improvements and fixes bugs related to user sessions.

Features
~~~~~~~~~~

* **User sessions**: allow specifying files in auto start links

Bug fixes
~~~~~~~~~~

* **User sessions**: fix a situation where the session start gets stuck in an endless progress spinner
* **User sessions**: fix handling of S3 buckets that are not hosted on AWS

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.8.3 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.8.3>`_
- `renku-ui 2.4.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.4.1>`_


0.12.16
-------

This release fixes bugs in the user session service.

Bug fixes
~~~~~~~~~~

* **User sessions**: include information about s3 bucket functionality in the server_options endpoint
* **User sessions**: improve the parsing of messages from k8s that explain why a session is unschedulable

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.8.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.8.2>`_

0.12.15
-------

This release mostly aims to fix the data in the Knowledge Graph. It will start the re-provisioning process
which is about wiping out all the data and generating it again.

Bug fixes
~~~~~~~~~~

* **Knowledge Graph**: fix for the problems where datasets were not present in the Knowledge Graph
* **Knowledge Graph**: fix for the Lineage REST endpoint to match the API specification

Features
~~~~~~~~~~

* **Knowledge Graph**: a new process to speed up provisioning Knowledge Graph with basic project info
* **Knowledge Graph**: the Cross-Entity search to allow filtering on the creator in a case-insensitive way

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.11.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.11.1>`_
- `renku-graph 2.11.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.11.0>`_
- `renku-graph 2.10.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.10.0>`_

0.12.14
-------

This is a minor release that fixes a bug in the renku notebook service that caused
existing sessions launched by older renku versions to not be recognized.

Bug fixes
~~~~~~~~~~

* **User sessions**: successfully list and manage sessions launched by older renku versions

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.8.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.8.1>`_

0.12.13
-------

This is a minor release featuring improvements and bug-fixes to Renku CLI, core, graph and UI components.

Improvements
~~~~~~~~~~~~~

* **Renku Python API**: add `Activity <https://github.com/SwissDataScienceCenter/renku-rfc/blob/main/design/006-renku-api-activities-plans/006-renku-api-activities-plans.md#using-plans-and-activities-through-renkuapi>`__ support in Renku Python API
* **Renku CLI**: add support to start remote sessions from the CLI
* **User sessions**: provide better feedback when a session cannot be scheduled due to lack of resources or when initializing a session fails
* **Knowledge Graph**: add support for identification with `ORCID <https://orcid.org/>`__

Bug fixes
~~~~~~~~~~

* **Renku UI**: prevent errors when working on datasets with images
* **Knowledge Graph**: fixes of broken data in the Triples Store

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `amalthea 0.4.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.4.0>`_
- `renku-notebooks 1.8.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.8.0>`_
- `renku-python 1.4.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.4.0>`_
- `renku-ui 2.4.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.4.0>`_
- `renku-graph 2.9.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.9.0>`_

0.12.12
-------

A minor bugfix release fixing data problems in the Triples Store.

- `renku-graph 2.8.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.8.1>`_

0.12.11
-------

A minor release adding some features and bug-fixes to the renku components.

The main changes are:

- graph: a new lineage resource to replace current GraphQL endpoint
- graph: cross-entity search resource to allow filtering on since and until
- graph: various fixes related to both corrupted data in Triples Store as well as issues in the Provisioning flow preventing users from finding their data in the Knowledge Graph
- graph: an improvement to the internal processes to detect a lost project re-provisioning event
- graph: other stability improvements
- UI: UX improvements around project and dataset creation
- UI: polish and speedup the logic to start new sessions
- UI: correct bugs affecting the project's dataset page
- UI: improvements to non-logged-in user experience
- renku-python: add Plan and project status support in Renku Python API
- renku-python: add a custom git merge tool for merging renku metadata
- renku-notebooks: switch git proxy sidecar to golang.

More info can be found in release notes of Renku components:

- `renku-graph 2.6.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.6.0>`_
- `renku-graph 2.7.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.7.0>`_
- `renku-graph 2.8.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.8.0>`_
- `renku-ui 2.3.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.3.0>`_
- `renku-python 1.3.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.3.0>`_

Upgrading from 0.12.10
~~~~~~~~~~~~~~~~~~~~~~

This release does contain potentially breaking changes in renku-notebooks where we previously
deprecated `securityContext.enabled`, but are now setting `securityContext` directly. Simply
delete `securityContext` and `securityContext.enabled` from your `values.yaml` to resolve this.

0.12.10
-------

A minor release adding some features and non-critical bug-fixes to the core service and renku-python CLI.

- `renku-python 1.2.3 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.2.3>`_
- `renku-python 1.2.4 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.2.4>`_

0.12.9
------

A release containing new features and bug fixes for CLI, UI and Graph. The main changes are:

- CLI: SHACL validation fixes and improvements
- UI: improvements around starting new sessions
- UI: fixes for projects where the default branch is not called `master`.
- graph: migration mechanism of the data in the Triples Store
- graph: the Cross-entity search to allow multiple values on `type`, `visibility` and `creator` parameters

More info can be found in release notes of Renku components:

- `renku-python 1.2.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.2.1>`_
- `renku-python 1.2.2 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.2.2>`_
- `renku-ui 2.2.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.2.0>`_
- `renku-graph 2.5.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.5.0>`_
- `renku-graph 2.5.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.5.1>`_

0.12.8
------

Bugfix release that re-introduces a part of the Renku config that creates auto-saves when sessions crash.
This is required only for sessions launched prior to 0.12.6 which still may exist in some deployments.
This part of the config will be fully retired in a later subsequent release.

- `renku-notebooks 1.6.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.6.2>`_

0.12.7
------

Bugfix release fixing an issue where cloning user repositories was failing during session
startup.

- `renku-notebooks 1.6.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.6.1>`_

0.12.6
------

Bugfix release fixing an issue where image availability was incorrectly reported if a pinned image
was used for interactive sessions.

- `renku-notebooks 1.6.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.6.0>`_
- `renku-ui 2.1.2 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.1.2>`_

0.12.5
------

Minor release with a bugfix for the core service.

- `renku-python 1.1.3 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.1.3>`_

0.12.4
------

Minor release bumping the renku project templates version to ``0.3.1``.

Includes minor updates to component versions:

- `renku-notebooks 1.5.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.5.1>`_
- `renku-python 1.1.2 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.1.2>`_
- `renku-ui 2.1.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.1.1>`_

0.12.3
------

Minor release coming with:

* several new features on renku-ui,
* new ``/knowledge-graph/entities`` (cross-entity search) API,
* fixes around Cross-Origin Resource Sharing,
* better messaging and reporting on renku-notebooks
* fixes for various bugs in renku-notebooks, renku-ui, renku-gateway and renku-graph services.

More info can be found on release notes of specific components:

* `renku-ui 2.1.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.1.0>`_

* `renku-gateway 0.13.1 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.13.1>`_

* `renku-graph 2.4.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.4.1>`_

* `renku-graph 2.4.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.4.0>`_

* `renku-graph 2.3.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.3.0>`_

* `renku-notebooks 1.5.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.5.0>`_

0.12.2
------

Minor release fixing various bugs in `renku-python` `renku-core` and `graph` services.
The main fixes are addressing issues in migration and workflow functionality of renku CLI
and security and stability bugs in `renku-graph`.

Please note that the `renku-core` metrics should be disabled in this release. Recent changes
made to Redis are incompatible with the `renku-core` metrics and cause the whole Renku deployment
to not function properly. The `renku-core` metrics will be fixed in a subsequent release.

More info can be found on release notes of specific components:

* `renku-python 1.1.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.1.1>`_

* `renku-python 1.1.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.1.0>`_

* `renku-graph 2.2.4 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.2.4>`_

* `renku-graph 2.2.3 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.2.3>`_

0.12.1
------

Minor release to align Sentry configurations across the components.
It includes a few minor bug-fixes.


0.12.0
------

This is an important upgrade that enables v1.0 of `renku-python` (renku CLI) to work with the
renku web interface, renkulab. The upgrade requires regenerating the metadata in the knowledge
graph, which means that for a period of time, searching and accessing datasets and lineage
information will be limited and incomplete. The metadata regeneration is done automatically
and doesn't require any action from you.

Updating projects
~~~~~~~~~~~~~~~~~

You might be prompted to update your project to the new version of Renku. As a general rule,
this update should take less than 30 seconds, and we will provide an estimate of the time
required. The update will migrate the metadata for your project and, for most projects,
update the base image and the version of the Renku CLI used in interactive sessions. For
large projects with thousands of commits or many renku workflows, we recommend doing the
migration manually. Please don't hesitate to `reach out <https://renku.discourse.group>`_
or `open an issue <https://github.com/SwissDataScienceCenter/renku/issues>`_ if you
encounter problems or are unsure how to proceed.

Most importantly, from this point on, new projects created on renkulab will use renku
CLI ``>= v1.0``. We therefore strongly urge all users to have a look at the myriad of
excellent new features that this major release enables by checking out the
`renku-python release notes <https://github.com/SwissDataScienceCenter/renku-python/blob/1.0.0-release-notes/renku-release-notes-1.0.0.md>`_.
This release completely changes the storage and generation of the knowledge graph metadata,
with vastly improved performance and functionality. We have gone to great lengths to
ensure a smooth transition for older projects and for continuity in the CLI, but
please report issues on `discourse <https://renku.discourse.group>`_ or
`GitHub <https://github.com/SwissDataScienceCenter/renku/issues>`_.

We hope you like the new features - if you have further questions suggestions for improvements, let us know!

Detailed release notes follow below.

Users
~~~~~

* Support for ``renku-python >= 1.0`` - plugins, workflows, speed 🥳 `read all
  about it!
  <https://github.com/SwissDataScienceCenter/renku-python/blob/1.0.0-release-notes/renku-release-notes-1.0.0.md>`_
* Improved backwards compatibility for older projects - this means fewer
  mandatory project updates and interruptions 🎯
* (UI) Streamlined update dialog in project status view 🚀
* (UI) Estimates of project update duration for peace-of-mind 😯
* (UI) improved display of math formulas in markdown preview
* (UI/sessions) more robust autosave infrastructure
* (UI/core) support for project-template-defined parameter validation
* (UI/core) support for icons and description for project templates
* (bug fix) fix problems with dataset "add to project" button
* (UI/sessions) experimental support for cloud storage in user sessions

For full release notes of individual components see:

* UI: https://github.com/SwissDataScienceCenter/renku-ui/releases
* Graph: https://github.com/SwissDataScienceCenter/renku-graph/releases
* CLI: https://github.com/SwissDataScienceCenter/renku-python/releases
* Notebooks: https://github.com/SwissDataScienceCenter/renku-notebooks/releases


Administrators
~~~~~~~~~~~~~~

BREAKING CHANGES: carefully plan the outage for this upgrade. Because of the
underlying changes to the knowledge graph structure, the entire KG has to be
rebuilt. Based on our experience, this is orders of magnitude faster than in
earlier iterations, but depending on the number of projects it could still take
some time. The platform will be usable during this time, but KG features may not
fully work (e.g. dataset search)

* (gateway/notebooks) partial support for custom CA certificates

0.11.3
------

Minor release fixing a bug in the acceptance tests.


0.11.2
------

Minor release fixing a bug in the Renku template for Keycloak.


0.11.1
------

This is a minor release featuring improvements to the Renku UI.

New Feature highlights:
~~~~~~~~~~~~~~~~~~~~~~~

* **UI**: updates to style of alerts, menus, and tables

* **User sessions**: allow stopping a session that is not fully started (`example stopping session <https://github.com/SwissDataScienceCenter/renku/blob/0.11.2/docs/_static/changelog-images/renku-ui-1.3.0-session-shutdown.gif>`_)

* **User sessions**: support links for sharing sessions with additional options (`example sharing a link <https://github.com/SwissDataScienceCenter/renku/blob/0.11.2/docs/_static/changelog-images/renku-ui-1.3.0-launch-links.gif>`_)

Note: this release also updates the version of the keycloak chart to ``16.0.4``,
but no manual admin action should be needed.

For full release notes see:

* `renku-ui 1.3.0 <https://github.com/SwissDataScienceCenter/renku-ui/blob/master/CHANGELOG.md#130-2022-01-07>`_
* `renku-graph 1.37.7 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.37.7>`_
* `renku-gateway 0.10.3 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.10.3>`_

0.11.0
------

This is a major release that includes an important upgrade to GitLab and PostgreSQL components: `14` and `12` major versions respectively.

Upgrading from 0.10.3
~~~~~~~~~~~~~~~~~~~~~

**BREAKING CHANGES**

Please follow `these instructions <https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart#upgrading-to-0110>`_ carefully.

The resulting changes in the values file should be:

* NEW/EDIT *postgresql.persistence.existingClaim* will most likely need to be modified in the course of upgrading your PostgreSQL version. See `postgres migration instructions <https://github.com/SwissDataScienceCenter/renku/tree/master/helm-chart/utils/postgres_migrations/version_upgrades/README.md>`_
* NEW/EDIT/DELETE *gitlab.image.tag* might have to be adjusted as we do a GitLab major version bump in with this release.

0.10.3
------

This is a bugfix release that includes various fixes to user sessions and some improvements to the UI.

Improvements
~~~~~~~~~~~~

* **Datasets**: allow canceling a search before it completes.
* **User sessions**: rearrange session menu options.
* **User sessions**: update Renku commands cheat sheet.
* **UI**: notify user when a new renkulab version is available.
* **File display**: highlight code syntax in markdown files.
* **File display**: support preview of Matlab files.
* **File display**: add PDF file viewer.
* **File display**: render LaTeX math.


Bug fixes
~~~~~~~~~~
* **User sessions**: CPU limit enforcement is now configurable. Admins should refer to the `values documentation <https://github.com/SwissDataScienceCenter/renku-notebooks/blob/1.2.1/helm-chart/renku-notebooks/values.yaml#L155-L160>`__ to configure this in a Renku deployment.
* **User sessions**: keep auto-saved branches after restoring a session with a newer commit.
* **User sessions**: a different package is used to decode sessions authorization token.
* **Anonymous sessions**: not crash anonymous sessions if these are disabled in a deployment.

Individual components
~~~~~~~~~~~~~~~~~~~~~

For a full list of improvements and bug fixes in individual components, please check:

* renku-ui:
  `1.2.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/1.2.1>`__,
  `1.2.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/1.2.0>`__ and
  `1.1.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/1.1.0>`__

* renku-notebooks:
  `1.2.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/1.2.1>`__

* renku-gateway:
  `0.10.2 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/0.10.2>`__

0.10.2
------

This is a bugfix release that includes various fixes and improvements to user sessions.
See `renku-notebooks 1.2.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.2.0>`__ and `amalthea 0.2.1 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.2.1>`__ for more details.

Improvements
~~~~~~~~~~~~

* **Chart**: Add ``tolerations``, ``affinity`` and ``nodeSelector`` for user sessions.

Bug fixes
~~~~~~~~~~

* **User sessions**: checkout the correct alternative branch.
* **User sessions**: use correct fallback renku image.
* **Anonymous sessions**: fix failing probes.

0.10.1
------

This is a bugfix release that contains a fix for launching R sessions with our newest component that manages user sessions (Amalthea).
See `renku-notebooks 1.1.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.1.1>`__ and `amalthea 0.1.3 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.1.3>`__ for more details.

Improvements
~~~~~~~~~~~~

Our documentation has been restructured, now articles are reorganized into ``Tutorials``, ``How-to guides``, ``Topic Guides`` and ``Reference`` (see `#2191 <https://github.com/SwissDataScienceCenter/renku/pull/2191>`__).

0.10.0
------

This release includes a new user session controller replacing Jupyterhub. The new controller is not compatible with user sessions created by Jupyterhub, therefore all user sessions need to be terminated prior to upgrading to ``0.10.0``.

Improvements
~~~~~~~~~~~~

* **Documentation**: updated documentation on proper teaching etiquette and steps to use renkulab for teaching.
* **User sessions**: use `Amalthea <https://github.com/SwissDataScienceCenter/amalthea>`__ to control sessions through a k8s operator.

Bug fixes
~~~~~~~~~~

* **Authentication**: log out from GitLab when logging out from Renku.
* **Authentication**: fix Keycloak token authentication.

Individual components
~~~~~~~~~~~~~~~~~~~~~

For a full list of improvements and bug fixes in individual components, please check:

* renku-notebooks:
  `1.0.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/1.0.0>`__

* renku-gateway:
  `0.10.1 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/0.10.1>`__ and
  `0.10.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/0.10.0>`__

Upgrading from 0.9.3
~~~~~~~~~~~~~~~~~~~~

**BREAKING CHANGES!!** The admin should plan and warn users ahead of time that their sessions will be terminated when doing the upgrade. The new ``Loud`` statuspage component introduced in `0.9.3` can help get  the message across.

* The use of Amalthea and removal of Jupyterhub will require some changes. Namely:
  - All references to Jupyterhub in the ``values.yaml`` have been removed and are not required anymore.
  - Amalthea is installed from a separate helm chart which is now a dependency of the ``renku-notebooks`` helm chart.
  - Several new sections have been added to the ``values.yaml`` file which are required by Amalthea. Please refer to the renku values file for more details.
* Some older images with Rstudio will open Rstudio in a directory one level above the repository. This can be fixed by upgrading to a newer version of the base image in the Dockerfile in the relevant renku project.
* This version is not backward compatible with the user sessions from older versions. During the deployment the admin should clean up all remaining user sessions and then deploy this version.
* Anonymous sessions do not require a separate namespace and renku-notebooks deployment, if enabled in the values file they now run in the same namespace as regular user sessions.

0.9.3
-----

This is a very minor release that allows messages about maintenance and downtime to be displayed more prominently in the UI. This way the interruptions from upcoming releases can be more effectively communicated to users.

Improvements
~~~~~~~~~~~~

* **UI**: possibility to make maintenance/downtime notifications more prominently shown. To use this feature, the admin needs to create a new statuspage component called Loud and thick when wanting the message to appear more prominently.

0.9.2
-----

This is a bugfix release that includes various minor fixes: templates and core use a new bugfix CLI version, as well as other fixes for external to SDSC deployments and improved login style.

Improvements
~~~~~~~~~~~~

* **Sessions**: make enforced limits configurable when using ``emptyDir`` disk space.

Bug Fixes
~~~~~~~~~

* **Templates**: Renku and custom templates updated to use Renku ``0.16.2`` (should fix `pyshacl and renku conflicting dependencies <https://renku.discourse.group/t/failing-image-build/429/6>`__).
* **Renku core / CLI**: pin pyshacl to version ``0.17.0.post1``.
* **Login**: make social identity providers style match internal ones.
* **UI**: configurable welcome page for external deployments.

Individual components
~~~~~~~~~~~~~~~~~~~~~

For a full list of improvements and bug fixes in individual components, please check:

* renku-ui:
  `1.0.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/1.0.1>`__

* renku-core:
  `0.16.2 <https://github.com/SwissDataScienceCenter/renku-python/releases/v0.16.2>`__

* renku-notebooks:
  `0.8.20 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/0.8.20>`__ and
  `0.8.19 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/0.8.19>`__

* renku-graph:
  `1.37.5 <https://github.com/SwissDataScienceCenter/renku-graph/releases/1.37.5>`__

0.9.1
-----

This bugfix release includes fixes to the Knowledge Graph component, see `1.37.2 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.37.2>__` and `1.37.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.37.1>__`.

0.9.0
-----

This release switches to the **new UI**  🎉 by default.
The biggest changes compared to the earlier UI version are explained on the RenkuLab home page
and include:

* New aesthetics, look and feel
* Sessions (formerly "interactive environments") shown in the UI within their RenkuLab context
* Issues and Merge Requests shown in the UI within RenkuLab context

Improvements
~~~~~~~~~~~~

* **Collaboration**: add ``Fork`` tab and ``Open in Tab`` buttons to collaboration pages.
* **Datasets**: support for dataset marquee image in projects
* **Sessions**: improve functioning and experience of sessions in iframes
* **File Browser**: allow resizing of file-system navigation view

Bug Fixes
~~~~~~~~~

* **Projects**: handle primary branches named other than master
* **Templates**: template updating issue with  (see this `forum post <https://renku.discourse.group/t/error-during-environment-creation/407/7>`__).
* **Renku core / CLI**: update rdflib 6 and remove rdflib-jsonld which could not be installed with setuptools ``>58.0.2``.
* **CLI**: fix `renku rm` failure in specific cases.


Individual components
~~~~~~~~~~~~~~~~~~~~~

For a full list of improvements and bug fixes in individual components, please check:

* renku-ui:
  `1.0.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/1.0.0>`__

* renku-core:
  `0.16.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/0.16.1>`__

* renku-graph:
  `1.37.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/1.37.0>`__

Upgrading from 0.8.7
~~~~~~~~~~~~~~~~~~~~

Although no special changes are needed in your values file for upgrading to Renku ``0.9.0``, we want to bring a couple of configurations to your attention:

* To configure and customize the welcome page you have some options, please read the related `values file section <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/values.yaml#L476>`__.
* To enable the new Keycloak renku-theme, you can login to the admin console of ``<renku-deployment-url>/auth``, go to Realm settings, theme and choose ``renku-theme``.
* The ingress should now include a configuration snippet to support showing sessions in iframes (automatically added by our chart templates).

0.8.7
-----

This is a small release that contains mainly bug fixes to the user sessions and to the UI.

Improvements
~~~~~~~~~~~~~

* **UI**: redesign header to take less vertical space.
* **Knowledge Graph**: dataset free-text search performance improvements.
* **Authentication**: enable ``renku login`` support for CLI so that users can interact with private repositories without using a GitLab password or an ssh key.

Bug Fixes
~~~~~~~~~

* **Environments**: listing orphaned user sessions tied to a deleted project/branch/namespace.
* **Environments**: bugs with mistyped variable and missing branches in autosave.
* **UI**: prevent values duplication on session enumerations.

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, please check:

* renku-notebooks:
  `0.8.18 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.18>`__

* renku-gateway:
  `0.9.5 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.9.5>`__

* renku-ui:
  `1.0.0-beta5 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/1.0.0-beta5>`__

* renku-graph:
  `1.36.7 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/1.36.7>`__

0.8.6
-----

This is just a bugfix release that addresses a problem in the notebook service caused by different naming conventions for user session PVCs.

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, please check:

* renku-notebooks:
  `0.8.17 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.17>`__

0.8.5
-----

This is just a bugfix release that addresses a problem in the notebook service where project names were causing the creation of PVCs in k8s to fail because of characters that k8s does not allow in PVC names (i.e. uppercase letters and underscores).

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, please check:

* renku-notebooks:
  `0.8.16 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.16>`__

0.8.4
-----

This version of Renku introduces the ability to use persistent volumes for user sessions. This is optional and can be enabled in the values
file for the helm chart. In addition to enabling this feature users have the ability to select the storage class used by the persistent
volumes. We strongly recommend that a storage class with a `Delete` reclaim policy is used, otherwise persistent volumes from all user
sessions will keep accumulating.

Also, unlike previous versions, with 0.8.4 the amount of disk storage will be **strongly enforced**,
regardless of whether persistent volumes are used or not. With persistent volumes users will simply run out of space. However,
when persistent volumes are not used, going over the amount of storage that a user has requested when starting their session
will result in eviction of the k8s pod that runs the session and termination of the session. Therefore, admins are advised
to review and set proper options for disk sizes in the `notebooks.serverOptions` portion of the values file.

Improvements
~~~~~~~~~~~~~

* **UI**: Add banner advertising new version to logged-in users and various improvements in the new canary deployment itself.
* **Environments**: Ability to use persistent volumes for user sessions.

Bug Fixes
~~~~~~~~~

-  **CI/CD:** CI action entrypoint typo
   (`3858df0 <https://github.com/SwissDataScienceCenter/renku/commit/3858df02182abeab26e324914fd7bcae7e7226ff>`__)
-  **Acceptance Tests:** flaky FreeTextDatasetSearchSpec
   (`a872504 <https://github.com/SwissDataScienceCenter/renku/commit/a872504becb41c1a761cbe02525cae3ebdb6ebea>`__)
-  **Acceptance Tests:** retry when EOF occurs on the Rest Client
   (`#2211 <https://github.com/SwissDataScienceCenter/renku/issues/2211>`__)
   (`e81a212 <https://github.com/SwissDataScienceCenter/renku/commit/e81a21229621463b4be4759f8c4b16714de097c4>`__)
-  **Acceptance Tests:** Wait for the dataset search results
   (`#2210 <https://github.com/SwissDataScienceCenter/renku/issues/2210>`__)
   (`132ec8b <https://github.com/SwissDataScienceCenter/renku/commit/132ec8b813ad6777ae309699d1769cdf07380571>`__)

Features
~~~~~~~~

-  **CI/CD:** parametrize rancher API endpoint
   (`46a5155 <https://github.com/SwissDataScienceCenter/renku/commit/46a51551da48225156f7e6c3a526a310574e674f>`__)

Individual components
~~~~~~~~~~~~~~~~~~~~~

For changes to individual components, please check:

* renku-ui:
  `1.0.0-beta4 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/1.0.0-beta4>`__
  `0.11.14 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.11.14>`__
* renku-notebooks:
  `0.8.15 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.15>`__

Upgrading from 0.8.3
~~~~~~~~~~~~~~~~~~~~

When upgrading from 0.8.3 the following steps should be taken based on whether you would like to use persistent volumes for user sessions or not:

**Use persistent volumes:**

  1. Edit the `notebooks.userSessionPersistentVolumes` section of the `values.yaml file <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/values.yaml#L527>`_ changing the `enabled` flag to true and selecting a storage class to be used with every user session. It is strongly recommended to select a storage class with a `Delete` retention policy to avoid the accumulation of persistent volumes with every session launch.
  2. Review and modify (if needed) `the disk request options in the values.yaml file <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/values.yaml#L506>`_.
  3. Review and modify (if needed) the `the server defaults in the values.yaml file <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/values.yaml#L479>`_. These will be used if a specific server options is omitted in the request to create a session and should be compatible with any matching values in the `serverOptions` section. It also allows an administrator to omit an option from the selection menu that is defined in the `serverOptions` section and have renku always use the default from the `serverDefaults` section.

**Do not use persistent volumes:**

  1. Review and modify (if needed) `the disk request options in the values.yaml file <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/values.yaml#L506>`_. Please note that if a user consumes more disk space than they requested (or more than what is set in the `serverDefaults` of the values file) then the user's session will be evicted. This is necessary because if a user consumes a lot of space on the node where their session is scheduled k8s starts to evict user sessions on that node regardless of whether they are using a lot of disk space or not. This sometimes results in the eviction of multiple sessions and not the session that is consuming the most storage resources.
  2. Review and modify (if needed) the `the server defaults in the values.yaml file <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/renku/values.yaml#L479>`_. These will be used if a specific server options is omitted in the request to create a session and should be compatible with any matching values in the `serverOptions` section. It also allows an administrator to omit an option from the selection menu that is defined in the `serverOptions` section and have renku always use the default from the `serverDefaults` section.

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

* **Renku CLI**: support moving files between datasets with ``renku mv`` (`CLI documentation <https://renku.readthedocs.io/en/latest/renku-python/docs/reference/commands.html#module-renku.cli.move>`__).
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

⭐️ CLI: Datasets metadata is editable. Please see the `Dataset documentation <https://renku.readthedocs.io/en/latest/renku-python/docs/reference/commands.html#module-renku.cli.dataset>`__ for details.

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
