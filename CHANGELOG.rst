.. _changelog:

0.70.0
------

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Data services**: Fix an issue with prevented sessions with private images from Renku Legacy (v1) to be resumed.
- **Data services**: Fix an issue with data connectors not being correctly removed from Renku when their owning group was deleted.

Internal Changes
~~~~~~~~~~~~~~~~

- **Data services**: Update rclone to v1.69.2+renku-1
- **Data services**: Fix an issue with namespaces in search
- **CSI Rclone**: Update rclone to v1.69.2+renku-1

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.46.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.46.0>`_
- `csi-rclone 0.4.1 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.4.1>`_

Notes for Renku Administrators
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This Renku version includes an update of the CSI Rclone driver which
will result in the unmounting of cloud storage in all running user sessions.


0.69.0
------

This version of Renku introduces the option to use JupyterLab as a frontend for
session environments that are built from code.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Select JupyterLab as an option when creating environments from code.

Internal Changes
~~~~~~~~~~~~~~~~

- **Data services**: Support for JupyterLab as an option when creating environments from code.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.45.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.45.0>`_
- `renku-ui 3.57.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.54.0>`_

0.68.1
------

This version of Renku introduces several bug fixes for the backend services. It also makes DOI
data connectors available in the data services search. Note though that the frontend is not yet using
this new version of the search. The switch will be done most likely in the next release.


User-Facing Changes
~~~~~~~~~~~~~~~~~~~
**🌟 New Features**

- **UI**: Introduce a GUI for creating session launch links.

**🐞 Bug Fixes**

- **Data services**: In some cases data connectors were created and linked to the wrong project.
- **Data services**: The creation of image pull secrets for private images from Gitlab was broken.
- **UI**: Fix a bug that prevented custom launches of V2 sessions from working properly.

Internal Changes
~~~~~~~~~~~~~~~~

- **Data services**: Add global (DOI) data connectors in search.
- **Data services**: Make 404 exception not show a full trace in the logs.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.43.3 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.43.3>`_
- `renku-data-services 0.44.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.44.0>`_
- `renku-ui 3.56.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.56.0>`_

0.68.0
------

This version of Renku introduces several new features. The most notable of these are: 

- using published datasets from Zenodo, Dataverse and similar providers as Data Connectors
- setting environment variables in session launchers
- migrating legacy Renku projects from within the new Renku UI

The release also includes a completely reworked launcher/session interface. 

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Datasets published on platforms such as Zenodo or Dataverse can be linked to
  a project as Data Connectors by using their reference DOI (Digital Object Identifier).
- **UI**: Support declaring environment variables on session launchers.
- **UI**: Legacy projects can now be migrated to Renku 2.0 directly from within Renku 2.0. (`#3613 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3613>`__)


**✨ Improvements**

- **UI**: Improved session launcher and environment views with clear build status indicators and separated launcher, environment, and session UX. (`#3648 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3648>`__)
- **UI**: Enhanced session launcher and session pages with clearer actions, improved environment selector and added session share link. (`#3659 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3659>`__)

**🐞 Bug Fixes**

- **Core Service**: Fix a bug where removing activities wouldn't actually remove them.
- **Data services**: Fix an issue where conflicting mount points would prevent sessions from starting.
- **UI**: Fix an issue where pausing or resuming a session would crash the whole tab in Firefox.

Internal Changes
~~~~~~~~~~~~~~~~

- **Helm chart**: Update the Keycloak theme image to use non-root user by default.
- **Data services**: Added k8s cache service that caches sessions and builds in the data services database.
- **Data services**: Added product metrics tracking.
- **Gateway**: Added product metrics tracking.
- **Data services**: Added data tasks deployment for running basic tasks in the scope of data services.
- **Data services**: Stable sorting when listing sessions.
- **Admin tools**: Add Harbor initialization script to setup a registry for RenkuLab v2.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.39.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.39.0>`_
- `renku-data-services 0.40.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.40.0>`_
- `renku-data-services 0.41.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.41.0>`_
- `renku-data-services 0.42.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.42.0>`_
- `renku-data-services 0.43.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.43.0>`_
- `renku-data-services 0.43.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.43.1>`_
- `renku-data-services 0.43.2 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.43.2>`_
- `renku-gateway 1.5.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.5.0>`_
- `renku-gateway 1.5.1 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.5.1>`_
- `renku-python 2.9.3 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.9.3>`_
- `renku-python 2.9.4 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.9.4>`_
- `amalthea-sessions 0.18.2 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.18.2>`_
- `renku-ui 3.54.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.54.0>`_
- `renku-ui 3.55.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.55.0>`_
- `csi-rclone 0.4.0 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.4.0>`_

Notes for Renku Administrators
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

This Renku version includes an update of the CSI Rclone driver which
will result in the unmounting of cloud storage in all running user sessions.

0.67.2
------

Renku ``0.67.2`` fixes several bugs in the data services backend.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Data services**: Surface more specific message when Git integrations expire.
- **Data services**: Fix a bug where modifying the resource class of a hibernated
  session would cause it to not start back up when resumed.
- **Data services**: Data connectors were failing to copy when copying projects.

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Data services**: Add support for user-defined queries in the search. Note, that the
  search in the data services is still under development and not available to users.

**Bug Fixes**

- **Data services**: Make validation of slugs in responses more lenient to avoid unnecessary validation errors.
- **Data services**: Handle cases where the session pod may be missing when listing logs.
- **Data services**: There are cases when old session secrets may not have been cleaned up properly.
  In this case starting a new would fail, now we can patch and edit these secrets and properly start the session.
- **Data services**: Re-provision the search index in Solr if a migration requires it.
- **Data services**: Properly parse durations with high values in the session specifications.
- **Data services**: Update search documents without optimistic concurrency control.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.38.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.38.0>`_

0.67.1
------

Renku 0.67.1 fixes several issues related to data connectors that are owned by projects.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- **UI**: Creating a project from a group page now has the group as the owner by default.
- **UI**: Show session start link in the session off-canvas (i.e. the sidebar element in the UI).

**🐞 Bug Fixes**

- **UI**: Fix an issue when creating a Data Connector inside a project. The owner field is now correctly populated.
- **UI**: Fix an issue that prevented linking data connectors that are owned by projects.
- **Data services**: Add missing endpoint required to find a data connector owned by a project by its slug.
- **Data services**: Fix an issue that prevented listing data connectors with uppercase letters in their slugs.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.53.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.53.0>`_
- `renku-ui 3.53.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.53.1>`_
- `renku-data-services 0.37.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.37.1>`_

0.67.0
------

Renku ``0.67.0`` simplifies the authorization for data connectors and allows
private images from the Renku Gitlab repository to be used in V2 sessions.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Data connectors can be owned by projects.
- **Data services**: Private images from the Renku Gitlab registry can be used in V2 sessions.
- **UI**: Projects with private images from the Renku Gitlab registry can now be migrated from Renku v1 to Renku 2.0 (`#3603 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3603>`__)

**🐞 Bug Fixes**

- **Data services**: Copy the project documentation when a project is copied.

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Amalthea**: Add image pull policy for the session image.
- **Data services**: Simplify authorization for data connectors.

**Bug Fixes**

- **Data services**: Use "Always" as the image pull policy for session images.
- **Data services**: Remove potential infinite recursion in project slug regex checks.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `amalthea-sessions 0.18.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.18.0>`_
- `amalthea-sessions 0.18.1 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.18.1>`_
- `renku-data-services 0.37.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.37.0>`_
- `renku-ui 3.51.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.51.0>`_
- `renku-ui 3.52.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.52.0>`_


0.66.0
------

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Create a custom environment for your session based on an environment definition in a code repository.
  Current support includes creating python environments defined by an environment.yaml file or similar.
  This feature is only available on RenkuLab.io, learn more in our `documentation <https://renku.notion.site/How-to-create-a-custom-environment-from-a-code-repository-1960df2efafc801b88f6da59a0aa8234>`__.
  (`#3522 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3522>`__)
- **UI**: Surface error messages from validating Git repository URLs
- **UI**: Projects can now be migrated from Renku v1 to Renku 2.0 (`#3527 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3527>`__)
- **Amalthea**: Surface to the user when a session cannot be launched because the quota in the resource pool was exceeded.
- **Amalthea**: Improve error reporting around unschedulable sessions.
- **Amalthea**: Improve error reporting around pulling images.

Internal Changes
~~~~~~~~~~~~~~~~

**New Features**

- **Data services**: Support creating session environments based on code repositories.
- **Data services**: Prepare helm charts to allow data-services to connect to SOLR.
- **Notebooks**: Cache Shipwright BuildRuns and Tekton TaskRuns for image builds.

**Improvements**

- **Data services**: Handle Renku v1 sessions.
- **Gateway**: Route Renku v1 sessions through data services.
- **Notebooks**: Use the correct user labels when caching sessions.
- **Helm chart**: Set the notebooks replica count to zero because it is replaced by the data services.
- **Helm chart**: Add RBAC rules for Shipwright and Tekton resources in the Kubernetes cache.
- **Amalthea**: Support using private images.
- **Amalthea**: Add short name for AmaltheaSessions objects.

**Bug Fixes**

- **Data services**: Better validation for URLs of Git repositories.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.35.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.35.0>`_
- `renku-data-services 0.35.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.35.1>`_
- `renku-data-services 0.35.2 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.35.2>`_
- `renku-data-services 0.36.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.36.0>`_
- `renku-notebooks 1.28.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.28.0>`_
- `renku-notebooks 1.29.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.29.0>`_
- `renku-notebooks 1.29.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.29.1>`_
- `renku-notebooks 1.29.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.29.2>`_
- `renku-notebooks 1.30.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.30.0>`_
- `renku-notebooks 1.30.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.30.1>`_
- `renku-ui 3.48.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.48.0>`_
- `renku-ui 3.49.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.49.0>`_
- `renku-ui 3.50.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.50.0>`_
- `renku-gateway 1.4.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.4.0>`_
- `amalthea-sessions 0.17.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.17.0>`_
- `amalthea-sessions 0.16.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.16.0>`_
- `amalthea-sessions 0.15.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.15.0>`_


0.65.1
------

Renku 0.65.1 refactors the code used to deploy CI installations used to test Pull Requests.
NOTE to administrators: The default values file now specify several resource requests and limits
that were not specified before that are tuned for a small deployment, if the values file
for your deployment does not override these requests and limits (which it should!) then
you might end up with services being killed by Kubernetes (for Out Of Memory errors).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- Many resources requests and limits have been defined in the default values file.
- Now the values file used in the CI deployments is taken from this repository in `minimal-deployment`
  which also serves as a reference for the default values file.
- Removed a check in the Helm template for the ingress that could result in
  dataset search not working properly if disabled.


0.65.0
------

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Projects can now have documentation. (`#3478 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3478>`__).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Jena**: Now the passwords for the admin and Renku Jena users are optional to define in the values file: if the
  secret defining them already exists, it will be maintained, if it does not a set of random passwords will
  be generated.

- **Redis**: Now the password for Redis is optional to define in the values file: if the secret defining it already
  exists, it will be maintained, if it does not a set of random passwords will be generated.

- **OIDC**: Now all OIDC secrets are optional to define in the values file: if the secret defining them already exists,
  it will be maintained, if it does not a set of random passwords will be generated.

- Values for `notebooks.oidc.authUrl`, `notebooks.oidc.tokenUrl`, `notebooks.sessionIngress` have now sensible defaults
  and are optional to define in the values file if Keycloak is deployed through the Renku Helm chart.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.47.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.47.0>`_

0.64.3
------

Renku ``0.64.3`` introduces some bug fixes and minor improvements.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- **Sessions**: Add default global environments. Prior to this there
  was no environments at all in a brand new deployment.

**🐞 Bug Fixes**

- **Sessions**: Create a brand new environment when cloning projects
  instead of linking the environment from the parent project in the new
  project.

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **Data services**: Migrate old cloned projects that had linked environments
  that should have been independent.
- **Gateway**: Do not shut down the load balancer for the core service too early.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.33.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.33.0>`_
- `renku-gateway 1.3.2 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.3.2>`_



0.64.2
------

Renku ``0.64.2`` introduces minor features and various bug fixes.

NOTE to administrators: This Renku version includes an update of the CSI Rclone driver which
will result in the unmounting of cloud storage in all running user sessions.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Sessions**: Refresh the Git credentials when a session is being resumed so
  that users always have valid credentials.
- **Sessions**: Message that "file changed on disk" when editing files
  mounted through Polybox should not occur on every file save now.

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Data services**: Initial support for archiving sessions in the backend.
- **CSI Rclone**: Cleanup old mounted drives on nodes inside the code rather
  than as a Kubernetes lifecycle hook which can fail and cause a restart loop.

**Bug Fixes**

- **Data services**: Update the project ETag when the namespace slug changes.
- **Data services**: Handle HTTP errors when probing Git repositories.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.32.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.32.0>`_
- `csi-rclone 0.3.7 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.3.7>`__


0.64.1
------

NOTE to administrators: This Renku version includes an update of the CSI Rclone driver which
will result in the unmounting of cloud storage in all running user sessions.

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **CSI Rclone**: Update liveness probe image version to fix restart loops.

0.64.0
------

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Early access to Renku 2.0 now available for users to try out. (`#3474 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3474>`__).
- **UI**: Configure disk storage for Renku 2.0 sessions launchers. (`#3463 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3463>`__).

**✨ Improvements**

- **UI**: Cleanup the project and group settings pages (`#3472 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3472>`__).
- **UI**: Hide the edit button when the user does not have permissions (`#3462 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3462>`__).

**🐞 Bug Fixes**

- **UI**: Display all data connector password fields (`#3477 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3477>`__).
- **UI**: Do not set default values for data connector fields (`#3483 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3483>`__).
- **UI**: Redirect properly when changing slug (`#3467 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3467>`__).
- **Data services**: Re-enable S3 customizations for data connectors, which adds back the Switch S3 provider(`#606 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/606>`__)

Internal Changes
~~~~~~~~~~~~~~~~

**New Features**

- **Data services**: Support saving disk storage size for session launchers.

**Bug Fixes**

- **Data services**: Fix patching wrong environment variables when resuming sessions.
- **Data services**: Allow mount and work directories to be reset for session environments.
- **Data services**: Do not call data service through the network from itself.
- **Data services**: Make HEAD responses empty
- **Data services**: Merge all API files correctly.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.30.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.30.0>`_
- `renku-data-services 0.31.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.31.0>`_
- `renku-data-services 0.31.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.31.1>`_
- `renku-ui 3.46.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.46.0>`_
- `renku-ui 3.46.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.46.1>`_


0.63.0
------

This release introduces copying projects. This feature makes it easy for course instructors to distribute course materials to students.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Introduce the ability to make a copy of a project, and to mark a project as a template intended for copying. (`#3427 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3427>`__).

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.45.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.45.0>`_

0.62.1
------

Renku 0.62.1 fixes a bug that prevented users to launch sessions with user secrets in Renku 1.0.


User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Data services**: Correctly map user secrets in Renku 1.0 sessions to use the provided filename.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.29.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.29.0>`_

0.62.0
------

This release introduces session secrets, which make it possible to connect to protected resources, such as databases or
external compute systems, from a Renku session in a standardized and shareable manner.
Collaborating with secrets is easy too: configure a single session secret slot to ensure
that the secret shows up the same way for everyone, and each person enters their own value.

In addition, we have also made it much easier to configure
and use PolyBox and SwitchDrive data connectors.

For administrators: This release removes the Gitlab omnibus Helm chart that we created and used to have as a dependency
of the Renku Helm chart. We have been discouraging anyone from using
this chart in production and we specified this in our documentation as well.

If you are using the internal Gitlab Helm chart then ensure to migrate to a separate
Gitlab deployment as specified in our `documentation <https://renku.readthedocs.io/en/stable/how-to-guides/admin/gitlab.html#migrate-from-renku-bundled-omnibus-gitlab-to-cloud-native-gitlab-helm-chart>`_.
before installing this or any subsequent Renku version. Gitlab publishes an official Helm chart and
that is what should be used for deploying Gitlab with Helm.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Configure and save session secrets in Renku 2.0 projects and use them in sessions (`#3413 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3413>`__).

**Improvements**

- **UI**: Simplify the creation of PolyBox and SwitchDrive data connectors (`#3396 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3396>`__).
- **UI**: Simplify the project and group creation interactions in Renku 2.0 to a simple modal (`#3399 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3399>`__).
- **UI**: Introduce a refreshed design for the dashboard, user, and group pages in Renku 2.0 (`#3407 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3407>`__, `#3428 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3428>`__).

Internal Changes
~~~~~~~~~~~~~~~~

**New Features**

- **Data services**: Support saving session secrets in Renku 2.0 projects and mounting them in sessions.

**Improvements**

- **Infrastructure Components**: ``redis`` has been upgraded from version ``7.0.7`` to ``7.4.1``
- **Helm chart**: remove the custom-made Gitlab Omnibus Helm chart from Renku dependencies
- **Search services**: Add support for sentry


**Bug Fixes**

- **Search services**: Don't return results without linked namespaces


Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.28.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.28.0>`_
- `renku-search 0.7.0 <https://github.com/SwissDataScienceCenter/renku-search/releases/tag/v0.7.0>`_
- `renku-ui 3.43.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.43.0>`_
- `renku-ui 3.44.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.44.0>`_
- `renku-ui 3.44.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.44.1>`_

0.61.2
------

Renku 0.61.2 fixes a bug that prevented users from resuming Renku V2 sessions
after they have been hibernated because they were idle.


User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Sessions**: Correctly resume hibernated sessions.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `amalthea 0.14.7 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.14.7>`_


0.61.1
------

Renku 0.61.1 introduces a few bug fixes for the previous release.


User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Sessions**: Correctly launch sessions that request dedicated resources classes

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **Amalthea**: Add resource requests for the authentication proxy containers
- **Amalthea**: Add support for setting priority classes for sessions
- **Data services**: Use the working directory to mount cloud storage if the mount path is relative
- **Data services**: Use HTTPS in the redirect URL for the authentication proxy
- **Data services**: Use GPU resource limits when GPUs are requested
- **Helm chart**: Do not set the default storage class to empty string if it is not set in the values file
- **Helm chart**: Restart the data services and notebooks pods when the mounted secret changes

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `amalthea 0.14.5 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.14.5>`_
- `amalthea 0.14.6 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.14.6>`_
- `renku-data-services 0.27.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.27.1>`_


0.61.0
------

Renku 0.61.0 introduces a new version of Amalthea that supports running sessions with Docker images
that do not contain Jupyter server.

NOTES to administrators:

- This upgrade introduces a brand new CRD for sessions. All services that support
  sessions for Renku v2 will switch to this new CRD. Renku v1 sessions remain unchanged.
  Therefore any old sessions for Renku v2 will not be visible to users after this upgrade. The sessions
  themselves will not be immediately deleted and as long as users have saved links to their old sessions they
  should be able to access their sessions and save data. However we recommend that administrators
  notify users of the change and allow for enough time so that existing Renku v2 sessions can be saved and
  cleaned up, rather than asking users to save the url to their sessions. In addition to users not being able
  to see old Renku v2 sessions, they will also not be able to pause, resume or delete old Renku v2 sessions.
  Therefore it's best if most sessions are properly saved and cleaned up before this update is rolled out. In order
  to support the new CRD we have also created a new operator that will manage the new `amaltheasession` resources.

- The network policies for Renku have been consolidated and revamped. The most notable change here is the
  removal of the egress policy that prevented egress to internal IP addresses from sessions. Now we disallow
  all ingress in the Renku release namespace by default and explicitly grant permissions to any pods that need
  to access other pods inside the Renku release namespace. Two properties relevant to this have been added to the
  Helm chart values file that allows administrators to grant access to all Renku services from a specific namespace
  or to do the same for specific pods within the Renku namespace. These are not needed for Renku to function and the
  default network policies should be sufficient, they have been added so that administrators can allow ingress for
  other services that may not come with the Renku Helm chart such as logging or monitoring. This change will result in
  the removal of some network policies and the creation of several new policies.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- **UI**: Enable the use of custom images that don’t contain Jupyter, streamlining the image-building process and allowing for the use of “off-the-shelf” images (`#3341 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3341>`__).
- **Sessions**: Enable running session images that do not contain Jupyter in them.

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Data services**: Add support for OAuth storage providers
- **Data services**: Move notebooks code to data services
- **Helm chart**: Consolidate and revamp network policies
- **Data services**: Add support for project documentation
- **Data services**: Add support for cloning projects

**Bug Fixes**

- **Gateway**: Pass on session cookie to data services for anonymous session authentication
- **Data services**: Correct pagination for namespaces
- **Data services**: Add creation date and created_by for namespaces
- **Data services**: Pin RClone version in data services image
- **Data services**: Properly handle multi-architecture docker images when getting working directory
- **Data services**: Make environment working directory and mount directory optional
- **Amalthea**: Add readiness and health checks to sessions.
- **Amalthea**: Do not authenticate the authentication proxy health check
- **Amalthea**: Do not mount the Kubernetes service account in sessions
- **Amalthea**: Do not add Kubernetes specific environment variables in sessions

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 1.3.1 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.3.1>`_
- `renku-ui 3.42.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.42.0>`_
- `renku-data-services 0.26.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.26.0>`_
- `renku-data-services 0.27.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.27.0>`_
- `amalthea 0.13.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.13.0>`_
- `amalthea 0.14.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.14.0>`_
- `amalthea 0.14.1 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.14.1>`_
- `amalthea 0.14.2 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.14.2>`_
- `amalthea 0.14.3 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.14.3>`_
- `amalthea 0.14.4 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.14.4>`_

0.60.0
------

Renku 0.60.0 squashes an issue that has been tripping up many users when connecting their GitHub
account, as well as various UX polish and bug fixes.

NOTE to administrators: Upgrading the `csi-rclone` component will unmount all cloud storage for all
active or hibernated sessions. Therefore, we recommend notifying your users ahead of time when you
deploy this version of Renku and also if possible deploying the upgrade when there are fewer
sessions that use cloud storage or just fewer sessions in general. Once the upgrade is complete
users will be able to mount cloud storage as usual.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- **UI**: Allow for changing the role of members of groups, and hide membership edit buttons for
  users with insufficient permissions on project and group settings pages (`#3374
  <https://github.com/SwissDataScienceCenter/renku-ui/pull/3374>`__).
- **UI**: Improve UX for data connector side sheets and modals (`#3368 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3368>`__).

**🐞 Bug Fixes**

- **UI & Data services**: Direct users to complete the GitHub integration by installing the Renku app in the desired namespace (`#3332 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3332>`__).
- **UI**: Avoid unexpected redirects when clicking on the 2.0 dashboard session buttons (`#3378 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3378>`__).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **UI**: Update the Admin page to set up Connected services (`#3332 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3332>`__).
- **Gateway**: Add support for enabling debug logs from the gateway (`#730 <https://github.com/SwissDataScienceCenter/renku-gateway/pull/730>`__).

**Bug Fixes**

- **csi-rclone**: Do not log potentially sensitive data in error messages.
- **csi-rclone**: Properly handle encrypted secrets with the new annotation-based storage class.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.25.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.25.0>`_
- `renku-gateway 1.3.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.3.0>`_
- `renku-ui 3.41.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.41.0>`_
- `csi-rclone 0.3.4 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.3.4>`__
- `csi-rclone 0.3.5 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.3.5>`__

0.59.2
------

Renku ``0.59.2`` is a bugfix release that fixes a bug in Renku 2.0 where project editors could not edit project information.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Data services**: Allow project editors to send patches with the current namespace (`#483 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/483>`__).
- **Data services**: Allow project editors to send patches with the current visibility (`#484 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/484>`__).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Data services**: Return 409 error when creating a project with a conflicting slug (`#471 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/471>`__).
- **Data services**: Change all serial id columns to be GENERATED AS IDENTITY (`#461 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/461>`__).
- **Data services**: Include ``is_admin`` in the self ``/user`` endpoint (`#472 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/472>`__).

**Bug Fixes**

- **Data services**: Handle spaces in ``provider_id`` for connected services (`#482 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/482>`__).
- **csi-rclone**: Do not log potentially sensitive data in error messages.
- **csi-rclone**: Properly handle encrypted secrets with the new annotation-based storage class.


Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.24.2 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.24.2>`__
- `csi-rclone 0.3.4 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.3.4>`__
- `csi-rclone 0.3.5 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.3.5>`__

0.59.1
------

Renku ``0.59.1`` is a bugfix release that improves stability and performance with renku notebooks when under heavy load.

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Notebooks**: Use gevent methods in notebooks api (`#1996 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1996>`__).

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.27.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.27.1>`_

0.59.0
------

Renku ``0.59.0`` introduces the ability to reuse data connectors in multiple projects!
When you add a data connector (previously called a data source) to your project, you now have the new option to select other data connectors on RenkuLab,
for example those shared in your group, rather than having to re-enter the data connection details.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Add and re-use data connectors in Renku 2.0 projects (`#3323 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3323>`__).

**✨ Improvements**

- **UI**: Add a playful design for the 404 and application error pages (`#3248 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3248>`__).
- **UI**: Update redirect page styles (`#3257 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3257>`__).
- **UI**: Remove Renku 2.0 beta warning alert from dashboard (`#3357 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3357>`__).

**🐞 Bug Fixes**

- **UI**: Fix how permissions are checked in Renku 2.0, notably group members can perform actions according to their role in projects. (`#3351 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3351>`__).
- **UI**: Fix styles for the edit launcher environment list (`#3360 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3360>`__).
- **UI**: Allow opening a project from Renku 2.0 search if the namespace is missing in the result (`#3353 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3353>`__).
- **UI**: Fix update file and download buttons in Renku 1.0 (`#3363 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3363>`__).

Internal Changes
~~~~~~~~~~~~~~~~

**New Features**

- **Data services**: Add support for data connectors (`#407 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/407>`__).
- **Data services**: Do not synchronize blocked users from Keycloak (`#393 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/393>`__).
- **Data services**: Support getting permissions (`#454 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/454>`__).
- **Notebooks**: Add support for data connectors (`#1991 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1991>`__).
- **Notebooks**: Cache Amalthea sessions (`#1983 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1983>`__).

**Improvements**

- **Data services**: Handle errors in background jobs nicely (`#463 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/463>`__).
- **Gateway**: Add support for signing cookies (`#734 <https://github.com/SwissDataScienceCenter/renku-gateway/pull/734>`__).

**Bug Fixes**

- **Data services**: Treat invalid JWT as 401 HTTP error.
- **Data services**: Change user preferences id sequence to proper value.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.23.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.23.0>`__
- `renku-data-services 0.24.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.24.0>`__
- `renku-gateway 1.2.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.2.0>`_
- `renku-notebooks 1.27.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.27.0>`_
- `renku-ui 3.38.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.38.0>`_
- `renku-ui 3.39.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.39.0>`_
- `renku-ui 3.40.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.40.0>`_
- `renku-ui 3.40.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.40.1>`_

0.58.1
------

Renku ``0.58.1`` fixes the correct handling of self-signed certificates in all the pods running OpenJDK as well as a bug
where some groups do not show up in search.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Notebooks**: Fix a bug where some docker images were found to not exist even when they really exist.

Internal Changes
~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Search**: Fix a bug where some groups do not show up in search
- **Notebooks**: Forward authorization header when getting Gitlab tokens
- **Notebooks**: Only patch the jupyter servers once when migrating labels and annotations at startup
- **Helm chart** correctly handle self-signed certificates in all the pods running OpenJDK:
  - `commit-event-service`
  - `event-log`
  - `knowledge-graph`
  - `search-api`
  - `search-provision`
  - `token-repository`
  - `triples-generator`
  - `webhook-service`

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-search 0.6.2 <https://github.com/SwissDataScienceCenter/renku-search/releases/tag/v0.6.2>`_
- `renku-notebooks 1.26.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.26.2>`_

0.58.0
------

Renku ``0.58.0`` fixes several issues related to Renku 2.0 search, and also squashes a bug where the
Renku 2.0 dashboard displayed content not related to you.


User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- **UI**: Polish Renku 2.0 pages and elements according to the latest design changes (`#3254 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3254>`__).

**🐞 Bug Fixes**

- **Search Services**: Resolve issues that caused items to be missing from Renku 2.0 search, including the search for members when adding members to projects and groups.
- **UI**: Resolve an issue where the Renku 2.0 dashboard displayed projects and groups that the user was not a member of (`#3289 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3289>`__)
- **UI**: Fix a bug where clicking on 'Show all my projects' on the Renku 2.0 dashboard redirected to a page displaying not only the user's projects but also others' projects (`#3289 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3289>`__)
- **UI**: Prevent glitches in the new session details sections  (`#3313 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3313>`__).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **UI**: Update Storybook to show Renku 2.0 re-usable elements (`#3254 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3254>`__).
- **UI**: Add and edit connected services from the admin panel (`#3329 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3329>`__).
- **Search Services**: Allow to re-provision the index from data-services and as part of a SOLR schema migration
- **Helm chart**: Add RBAC for K8s cache for new AmaltheaSessions custom resource
- **Gateway**: Add extra credentials for the data service for the new AmaltheaSessions
- **Gateway**: Remove unused Python code
- **Data services**: Support event queue re-provisioning
- **Data services**: Support listing projects and groups by direct membership

**🐞 Bug Fixes**

- **Data services**: Do not use gather() in when listing projects
- **Data services**: Order resource classes by GPU, CPU, RAM and storage
- **Data services**: Following redirects when sending requests to git repositories
- **Data services**: Allow unsetting secrets for cloud storage
- **Helm chart**: Increase the connection timeout for the Authzed database health checks

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-search 0.6.1 <https://github.com/SwissDataScienceCenter/renku-search/releases/tag/v0.6.1>`_
- `renku-ui 3.36.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.36.0>`_
- `renku-ui 3.37.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.37.0>`_
- `renku-ui 3.37.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.37.1>`_
- `renku-gateway 1.1.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.1.0>`_
- `renku-data-services 0.21.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.21.0>`__
- `renku-data-services 0.22.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.22.0>`__

0.57.2
------

Renku ``0.57.2`` fixes several bugs in gateway and the `csi-rclone` driver.

User-facing Changes
~~~~~~~~~~~~~~~~~~~

**Bug Fixes**

- **UI**: show the correct repository access status
- **Sessions**: allow paused sessions with cloud storage secrets to resume normally

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **Gateway**: Fix path rewrite middleware when the path contains escaped characters (`#726 <https://github.com/SwissDataScienceCenter/renku-gateway/pull/726>`__).
- **csi-rclone**: Correctly use OAuth2 tokens for cloud storage to enable mounting.
- **csi-rclone**: Remounting volumes created with older versions did not work.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 1.0.4 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.0.4>`_
- `csi-rclone 0.3.2 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.3.2>`__
- `csi-rclone 0.3.3 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.3.3>`__

0.57.1
------

Renku ``0.57.1`` fixes a bug in renku-ui-server where the service would be stuck in a crash loop when Sentry is enabled.
It also fixes two bugs in Notebooks related to the access token and shared memory in the user-sessions.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **UI**: Access mode defaults to read-only when adding a new data source in Renku 2.0 (`#3275 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3275>`__).
- **Notebooks**: Don't fail clone process if access token doesn't exist (`#1971 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1971>`__).
- **Notebooks**: Fix shared memory attached to the JupyterServer container to be half of the total requested memory (`#1984 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1984>`__).

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **UI**: Fix the UI server being stuck in a crash loop at startup when Sentry is enabled (`#3318 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3318>`__).
- **Gateway**: Fix getting HTTP error 500 when logging in (`#723 <https://github.com/SwissDataScienceCenter/renku-gateway/pull/723>`__).

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.35.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.35.1>`_
- `renku-gateway 1.0.3 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.0.3>`_
- `renku-notebooks 1.26.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.26.1>`_

0.57.0
------

Renku `0.57.0` brings a suite of new features and improvements to the Renku 2.0 beta. As a main
highlight, you can now save and reuse the credentials for data sources. No more copy/paste on every
session launch! We have also made small improvements to sharing, search, and sessions in Renku 2.0.
For a full list of changes, see the list below.


NOTE to administrators: Upgrading the `csi-rclone` component will unmount all cloud storage for all
active or hibernated sessions. Therefore, we recommend notifying your users ahead of time when you
deploy this version of Renku and also if possible deploying the upgrade when there are fewer
sessions that use cloud storage or just fewer sessions in general. Once the upgrade is complete
users will be able to mount cloud storage as usual.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Support saving and managing credentials for Renku 2.0 data sources (`#3266 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3266>`__).

**✨ Improvements**

- **Search Services**: Enable searching by prefix of indexed words
- **UI**: Add members to groups and projects in Renku 2.0 by username instead of email (`#3270 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3270>`__).
- **UI**: Enable sharing search URLs by reflecting the search query in the URL for Renku 2.0 (`#3245 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3245>`__).
- **UI**: Show the status of a session via a dynamic browser tab icon (`#3249 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3249>`__).
- **UI**: Display session details in session page in Renku 2.0 (`#3258 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3258>`__)
- **UI**: Set default namespace when creating a new Renku 2.0 project (`#3264 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3264>`__).

**🐞 Bug Fixes**

- **UI**: Fix issue in Renku 2.0 where launched sessions did not use the default storage size of the selected resource class (`#3295 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3295>`__).
- **UI**: Fix misnomers on the group creation page (`#3276 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3276>`__).
- **Data Services**: Fix connected services showing errors for anonymous users
- **Data Services**: Fix 500 error being raised when modifying a session launcher

Internal Changes
~~~~~~~~~~~~~~~~

**New Features**

- **csi-rclone**: Read credential secrets from PVC annotations
- **csi-rclone**: Update the CSI sidecar container versions
- **csi-rclone**: Add support for decrypting data storage secrets.
- **Gateway**: The API Gateway components have been refactored and simplified (`#709 <https://github.com/SwissDataScienceCenter/renku-gateway/pull/709>`__).
- **Notebooks**: Add a component for liveness detection
- **Notebooks**: Support for saving cloud storage secrets

**Improvements**

- **Search Services**: Reading all data service events from a single Redis stream. Processing from individual streams is kept.
- **Data Services**: Do not show user emails and use usernames instead for all interactions
- **UI**: The UI server has been refactored following the changes in the gateway (`#3271 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3271>`__).

**Bug Fixes**

- **csi-rclone**: Do not crash on unmounting as it might block dependent resources
- **csi-rclone**: Use extra storage class when reading secrets from a PVC annotation
- **Data Services**: Fix group member changes not being sent to search
- **Data Services**: Fix Redis not being able to connect to the master node

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `csi-rclone 0.1.8 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.1.8>`__
- `csi-rclone 0.2.0 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.2.0>`__
- `csi-rclone 0.3.0 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.3.0>`__
- `csi-rclone 0.3.1 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.3.1>`__
- `renku-gateway 1.0.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.0.0>`_
- `renku-gateway 1.0.1 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.0.1>`_
- `renku-gateway 1.0.2 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/1.0.2>`_
- `renku-ui 3.34.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.34.0>`_
- `renku-ui 3.35.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.35.0>`_
- `renku-search 0.5.0 <https://github.com/SwissDataScienceCenter/renku-search/releases/tag/v0.5.0>`_
- `renku-notebooks 1.26.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.26.0>`__
- `renku-data-services 0.20.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.20.0>`__


0.56.3
------

Renku ``0.56.3`` fixes a bug in renku-data-services where strict user email validation
was causing problems with the admin panel and listing users.

Internal Changes
~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Data Services**: do not validate user emails because Keycloak can contain invalid emails

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.19.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.19.1>`__

0.56.2
------

Renku ``0.56.2`` fixes a bug in renku-data-services where a background job would stop working
if a deleted project wasn't correctly removed from the authorization database.

Internal Changes
~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Data Services**: Adds endpoint for saving storage credentials


**🐞 Bug Fixes**

- **Data Services**: Fixes background job not working with Authzed db in inconsistent state
- **Data Services**: Fixes query args validation for /api/data/user/secrets endpoint
- **Data Services**: Splits error into 401 and 403 depending on the error


Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.19.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.19.0>`__


0.56.1
------

Renku ``0.56.1`` fixes a bug where Amalthea would not start when the prometheus metrics or the
audit log export functionality is enabled.

Internal Changes
~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- * **Amalthea**: Fix failing startup when prometheus metrics or audit log is enabled.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `amalthea 0.12.3 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.12.3>`_

0.56.0
------

Renku ``0.56.0`` adds new features and improvements to several components.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Update incidents and maintenance banner and summary (`#3220 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3220>`__)
- **UI**: Add incidents and maintenance section in the admin panel (`#3220 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3220>`__)
- **Data Services**: Add platform configuration

**✨ Improvements**

- Revamp design for Renku 2.0 (`#3214 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3214>`__).

**🐞 Bug Fixes**

- Use standard HTML input fields for secret values (`#3233 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3233>`__).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- * **Amalthea**: Sessions can now run correctly on Kubernetes version 1.29.

**🐞 Bug Fixes**

- * **Amalthea**: Fix the repository for the scheduler image in the Amalthea Helm chart.
- * **Amalthea**: Properly load the namespace configuration when starting the operator.
- * **Amalthea**: Fix the missing health check endpoint for the old operator.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.18.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.18.0>`_
- `renku-data-services 0.18.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.18.1>`_
- `renku-ui 3.32.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.32.0>`_
- `renku-ui 3.33.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.33.0>`_
- `amalthea 0.12.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.12.0>`_
- `amalthea 0.12.1 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.12.1>`_
- `amalthea 0.12.2 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.12.2>`_

0.55.0
------

Renku ``0.55.0`` introduces user and group pages in Renku 2.0, where you can see all projects owned
by those people. In addition, you can now fully take advantage of RenkuLab resources in Renku 2.0 by
setting a resource class for your session launchers.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Renku 2.0: Add user pages that show all projects in the namespace (`#3198 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3198>`__)
- **UI**: Renku 2.0: Extend group pages to show all projects in the namespace (`#3198 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3198>`__)

**✨ Improvements**

- **UI**: Renku 2.0: Provide clickable links between projects and user/group namespace pages on the project page and in search results (`#3198 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3198>`__)
- **Search Services**: Renku 2.0: Show creator name and project namespace in search results,
  where before only the respective ids were included (`#3198 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3198>`__)
- **UI**: Renku 2.0: Support setting a default resource class for a session launcher in Renku 2.0  (`#3196 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3196>`__)

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Search Services**: The search query is now accepted at ``/api/search/query`` url path
  and a ``/api/search/version`` endpoint has been added
- **Data Services**: Change API to provide user and group pages in Renku 2.0

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.17.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.17.0>`_
- `renku-search 0.4.0 <https://github.com/SwissDataScienceCenter/renku-search/releases/tag/v0.4.0>`_
- `renku-ui 3.30.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.30.0>`_
- `renku-ui 3.31.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.31.0>`_

0.54.2
------

Renku ``0.54.2`` fixes a bug with testing the cloud storage connection for WebDAV.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Data Services**: Fix verifying cloud storage connection not working with WebDAV by correctly obscuring RClone values.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.16.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.16.1>`__

0.54.1
------

Renku ``0.54.1`` introduces a few bug fixes in the notebooks and data services components.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Notebooks**: Patch the correct environment variables when a session is resumed after being hibernated
- **Data Services**: Assign the correct project permissions to group members

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.15.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.15.1>`__
- `renku-notebooks 1.25.3 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.25.3>`__


0.54.0
------

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- Test the cloud storage connection before persisting the configuration (`#3194 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3194>`_)
- Prompt for cloud storage credentials on v2 session start (`#3203 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3203>`_)
- Indicate repository permissions in Renku 2.0 (`#3136 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3136>`_)

Internal Changes
~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Secrets**: Allow rotating the private key for secrets storage

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.15.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.15.0>`__
- `renku-notebooks 1.25.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.25.2>`_
- `renku-ui 3.29.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.29.0>`_


0.53.1
------

Internal Changes
~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Search Services**: Set keycloak url into the allow list of JWT
  issuer urls. This setting is now mandatory to the search-api
  service.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-search 0.3.0 <https://github.com/SwissDataScienceCenter/renku-search/releases/tag/v0.3.0>`_


0.53.0
------

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Search Services**: Enable admin to search without restrictions.
  Support for `namespace` search term in user query.

**✨ Improvements**

- **UI**: Convert font-awesome icons to bootstrap icons (`#3173 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3173>`_, `#3161 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3161>`_)
- **UI**: Improve membership maintenance UX in Renku 2.0 (`#3154 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3154>`_)
- **UI**: Small updates to the connected services page (`#3149 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3149>`_)

Internal Changes
~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Search Services**: Adds a `/version` endpoint

**🐞 Bug Fixes**

- **Search Services**: Improve verifying JWT tokens using public key from keycloak
- **UI**: Show project members on the project information page in Renku 2.0 (`#3143 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3143>`_)
- **UI**: Fix project page nav in small view ports in Renku 2.0 (`#3168 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3168>`_, `#3169 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3169>`_)
- **UI**: Update session buttons in Renku 2.0 (`#3172 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3172>`_)
- **UI**: Update session badges on the project page of Renku 2.0 (`#3174 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3174>`_, `#3175 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3175>`_)
- **UI**: Redirect to group page after creation in Renku 2.0 (`#3177 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3177>`_)
- **UI**: Show a full page 404 when a group or project is not accessible in Renku 2.0 (`#3162 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3162>`_, `#3176 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3176>`__, `#3153 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3153>`_)
- **UI**: Fix updating project keywords in Renku 2.0 (`#3187 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3187>`_)
- **Data services**: Fix pagination on the ``/namespaces`` API endpoint
- **Data services**: Silence "Preferences not found for user" exceptions and stack traces

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-search 0.2.0 <https://github.com/SwissDataScienceCenter/renku-search/releases/tag/v0.2.0>`_
- `renku-ui 3.28.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.28.0>`_
- `renku-ui 3.28.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.28.1>`_
- `renku-data-services 0.14.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.14.1>`_

0.52.2
------

Renku ``0.52.2`` fixes a bug in Data Service.


Internal Changes
~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Data services**: Add endpoint for repository permissions for connected services.

**🐞 Bug Fixes**

- **Data service**: Fix typing issue preventing the service from starting when sentry is enabled.
- **Data service**: Prevent removing all owners from Renku 2.0 resources.

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.14.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.14.0>`_

0.52.1
------

Renku ``0.52.1`` fixes a few bugs in Renku 2.0, namely cases where:
- sessions could not start if the parent project listed zero repositories and one or more cloud storages to mount
- long running data migrations for user namespaces would cause the data service to keep restarting and never start

This release also includes minor improvements on the backend that will not be visible to users.

Breaking Changes
~~~~~~~~~~~~~~~~

This release changes the name of the background jobs that synchronize
the data service with Keycloak and also changes the corresponding section for these jobs in the values file.
These jobs have a more general name because they will perform data migrations for the data service in addition to
synchronizing with Keycloak. This requires additional actions from administrators only if you are setting custom
values for ``dataService.keycloakSync`` in the values file, but in most cases the default images set in this
section will be used so no action will be required.


User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Notebooks**: Do not add storage mounts patches when a session has no repository (`#1892 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1892>`_)

Internal Changes
~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Data services**: Configure trusted reverse proxies
- **Data services**: Send message queue events in a background process
- **Data services**: Run asynchronous code in database migrations
- **Data services**: Support PKCE for authentication with connected services
- **Data services**: Send group events to the message queue

**🐞 Bug Fixes**

- **Data service**: Do not perform data migrations for user namespaces at startup
- **Data service**: Remove leading underscores on route names
- **Data service**: Do not crash when a user that is already in a resource pool is added again to the same pool

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.25.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.25.1>`_
- `renku-data-services 0.13.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.13.0>`_

0.52.0
------

Renku ``0.52.0`` introduces a new feature to save and use secrets in RenkuLab sessions.
For example, save your credentials for a database or external computing resource in
RenkuLab to access those external systems from a RenkuLab session. Save secrets via the
new User Secrets page in the account drop down, and choose which secrets to mount in a
session on the Start with Options page. More details on this feature can be found in the
[documentation](https://renku.readthedocs.io/en/stable/topic-guides/secrets/secrets.html).

Administrators can customize the culling times (the length of time before an idle session is paused
or a paused session is deleted) for different resource pools.

This release also contains new features related to Renku 2.0. However, Renku 2.0 is still
in early development and is not yet accessible to users. For more information, see our
[blog](https://blog.renkulab.io/renku-2).

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Data Services**: Add new secrets storage service for managing user session secrets, including
  new endpoints on data-service to manage these secrets.
- **Data Services**: Add the possibility for users to connect Renku 2.0 projects to external
  services, allowing users to clone, pull and push repositories e.g. from GitLab.com or GitHub.com.
- **Notebooks**: Add support for repositories from external services in Renku 2.0 sessions.
- **UI**: Add a new User Secrets page to manage secrets, and extend the session launch pages to
  select secrets to include in the session.
  (`#3101 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3101>`_).
- **UI**: Customize culling times for resource pools
  (`#3113 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3113>`_).
- **UI**: Introduce a new design for Renku 2.0 project pages
  (`#3108 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3108>`_).
- **UI**: Update the user interface to reflect changes to Renku 2.0 sessions (`#3122 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3122>`_).
- **UI**: Add support for Renku 2.0 authorization implementation and roles (`3.27.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.27.0>`_).

**✨ Improvements**

- **Search Services**: Add support for groups, namespaces and project keywords.
- **UI**: Introduce formal navigation for Renku 2.0 pages
  (`#3095 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3095>`_).
- **UI**: Use namespace/slug to identify Renku 2.0 projects
  (`#3103 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3103>`_).


Internal Changes
~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Data Services**: Update the authorization for access to Renku 2.0 projects and groups to work
  with Authzed DB, a 3rd party database dedicated to saving authorization data and making
  authorization decisions
- **Search Services**: Support processing v2 schema messages (alongside with v1). Make the query
  parser more lenient to not raise parsing errors.

**🐞 Bug Fixes**

- **Data Services**: Allow removing tolerations and affinities on resource pools via PATCH requests

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.9.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.9.0>`_
- `renku-data-services 0.10.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.10.0>`_
- `renku-data-services 0.11.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.11.0>`_
- `renku-data-services 0.12.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.12.0>`_
- `renku-notebooks 1.23.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.23.0>`_
- `renku-notebooks 1.24.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.24.0>`_
- `renku-notebooks 1.25.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.25.0>`_
- `renku-search 0.1.0 <https://github.com/SwissDataScienceCenter/renku-search/releases/tag/v0.1.0>`_
- `renku-ui 3.24.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.24.0>`_
- `renku-ui 3.25.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.25.0>`_
- `renku-ui 3.26.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.26.0>`_
- `renku-ui 3.27.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.27.0>`_

0.51.1
------

Renku ``0.51.1`` fixes a bug where sessions were not considering the case (upper or lower) of the
project name that was being cloned when a session is started. This resulted in the working directory
being set to one location and the project cloned in another. This bug only affected projects where
users have manually changed their project paths to include uppercase characters or for projects that
were not created through Renku but were imported after creation.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Notebooks**: Use the case sensitive project name when cloning repositories at startup

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.22.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.22.1>`_

0.51.0
------

Renku ``0.51.0`` introduces new features related to Renku 2.0. However, Renku 2.0 is still
in early development and is not yet accessible to users. For more information, see our
[roadmap](https://github.com/SwissDataScienceCenter/renku-design-docs/blob/main/roadmap.md).

1. This release introduces *groups* to Renku 2.0.
2. Various bug fixes and improvements

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: A new entity, *groups*, has been introduced to Renku 2.0. Groups are a way to organize
  projects in Renku 2.0.
- **UI**: Projects are always in a group -- either the user's implicitly-created group, or a group
  that has been explicitly created.

**✨ Improvements**

- **UI** Add an "email us" button below the session class selector to request more resources (`#3073
  <https://github.com/SwissDataScienceCenter/renku-ui/pull/3073>`_)

**🐞 Bug Fixes**

- **Data service**: Allow proper removal of users from resource pools
- **Data service**: Enable searching for all users when adding users to resource pools

Internal Changes
~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Data services**: Initial support for groups

  **🐞 Bug Fixes**

- **Data service**: Increase timeout for synchronizing Keycloak users

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.8.3 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.8.3>`_
- `renku-data-services 0.8.2 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.8.2>`_
- `renku-data-services 0.8.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.8.1>`_
- `renku-data-services 0.8.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.8.0>`_
- `renku-ui 3.23.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.23.0>`_
- `renku-ui 3.22.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.22.0>`_

0.50.0
------

Renku ``0.50.0`` introduces several new features related to Renku 2.0. However, Renku 2.0 is still
in early development and is not yet accessible to users. For more information, see our
[roadmap](https://github.com/SwissDataScienceCenter/renku-design-docs/blob/main/roadmap.md).

1. This release introduces new sew search functionality for Renku 2.0.
2. Support has been added for interactive sessions in Renku 2.0 projects.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- **UI**: Renku 2.0 Search page with initial support for project and user search (alpha release)
- **UI**: Support for interactive sessions in Renku 2.0 projects, comprising global session
  environments and session launchers (alpha release)
- **Notebooks**: Initial support for Renku 2.0 sessions, supporting mounting multiple repositories
  (alpha release)
- **UI**: Add a new navigation top bar for Renku 2.0 (alpha release)
- **UI**: Add an ad-hoc feature flag for Renku 2.0 (alpha release)

**✨ Improvements**

- **UI** Update the footer links section with Mastodon (`#3081
  <https://github.com/SwissDataScienceCenter/renku-ui/pull/3081>`_, `#3059
  <https://github.com/SwissDataScienceCenter/renku-ui/issues/3059>`_)
- **UI** Improve session scheduling error messages (`#3082
  <https://github.com/SwissDataScienceCenter/renku-ui/pull/3082>`_, `#3036
  <https://github.com/SwissDataScienceCenter/renku-ui/issues/3036>`_)

**🐞 Bug Fixes**

- **UI** Update ``react-pdf`` version and fix it (`#3083
  <https://github.com/SwissDataScienceCenter/renku-ui/pull/3083>`_, `#3036
  <https://github.com/SwissDataScienceCenter/renku-ui/issues/3036>`_)


Internal Changes
~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Data services**: Initial support for project and user search for Renku 2.0 (alpha release)
- **Data services**: Add support for sentry and prometheus
- **Search services**: Initial support for project and user search for Renku 2.0 (alpha release)
- **Data services**: Initial support for Renku 2.0 session environments and session launchers (alpha
  release)

**Improvements**

- **KG**: Jena 5.0.0 upgrade

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.6.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.6.0>`_
- `renku-data-services 0.7.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.7.0>`_
- `renku-gateway 0.24.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/0.24.0>`_
- `renku-graph 2.50.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.50.0>`_
- `renku-notebooks 1.22.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.22.0>`_
- `renku-search 0.0.39 <https://github.com/SwissDataScienceCenter/renku-search/releases/tag/v0.0.39>`_
- `renku-ui 3.21.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.21.0>`_

0.49.1
------

This release contains minor bug fixes.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Data services**: Fix the incomplete synchronization of Keycloak users which caused problems with granting user access to resource pools

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **renku-ui**: Visit the /api/data/user endpoint when a user is logged in (`#3080 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3080>`_).

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.5.2 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.5.2>`_
- `renku-ui 3.20.2 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.20.2>`_

0.49.0
------

The release contains bug fixes to renku core service related to project migration.

This release also contains initial support for next generation 'Renku 2.0' functionality. However,
Renku 2.0 is still in early development and is not yet accessible to users. For more information,
see our [roadmap](https://github.com/SwissDataScienceCenter/renku-design-docs/blob/main/roadmap.md).

**Note for administrators**: this release includes breaking changes due to upgrading PostgreSQL to 16.1.0.
This requires modifying the values file to work with the new PostgreSQL Helm chart.
Please check (`the helm chart values changelog <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/values.yaml.changelog.md>`_)
for detailed instructions.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Core Service**: Fix issue with having to run project migration twice to migrate the Dockerfile/project template.

Internal Changes
~~~~~~~~~~~~~~~~

**New Features**

- **Data services**: Initial support for Renku 2.0 projects (alpha release)

**Improvements**

- **csi-rclone**: added rclone logs to regular node-plugin logs.
  (`#11 <https://github.com/SwissDataScienceCenter/csi-rclone/pull/11>`_).


Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-python 2.9.2 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.9.2>`_
- `renku-data-services 0.5.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.5.0>`_
- `csi-rclone 0.1.7 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.1.7>`_


0.48.1
------

Renku ``0.48.1`` only changes how the Terms of Use and Privacy Policy sections
can be customized by administrators.

0.48.0
------

Renku ``0.48.0`` introduces the ability to add a Terms of Use and Privacy Policy to
RenkuLab, as well as an assortment of small improvements and bug-fixes.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 📜 **UI**: Show terms of use and privacy policy in the help section
  (`#2954 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2954>`_).

**✨ Improvements**

- 🖌 **UI**: Improve appearance of templates on new project page
  (`#2999 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2999>`_).
- 🛑 **UI**: Unify appearance of project settings alerts
  (`#3001 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3001>`_).

**🐞 Bug Fixes**

- **UI**: Restore logged in/out notifications
  (`#3014 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3014>`_).
- **UI**: Hide button to add storage on deployments not supporting external storages
  (`#3001 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3001>`_).
- **UI**: Fix landing page parallax background (`#3010 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3010>`_).
- **UI**: Fix search bar styles (`#3019 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3019>`_).
- **UI**: Handle ``jsonrpc`` improper redirects (`#3017 <https://github.com/SwissDataScienceCenter/renku-ui/pull/3017>`_, `#2966 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2966>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Infrastructure**: Upgrade the version of PostgreSQL to 16.1.0.
- **UI**: Add initial alpha implementation of Renku 2.0 projects
  (`#2875 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2875>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.20.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.20.1>`_
- `renku-ui 3.20.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.20.0>`_

0.47.1
------

This release only includes changes to the documentation and updates to the acceptance tests.
It doesn't bring any new features or bug fixes.


0.47.0
------

This release expands Renku's cloud storage functionality in two key ways: First, mounted storages
are now read **and write**, so you can use mounted storage as an active workspace for your data in a RenkuLab
session. Second, we have expanded the cloud storage services you can integrate with RenkuLab. You can now
mount not only S3 buckets, but also WebDAV-based storages and Azure Blobs.

If you use SSH sessions via the CLI, you can use cloud storage there too! Configure cloud storage for your
project on RenkuLab.io, and those storages will be mounted in your remote session. Support for cloud
storage in local Renku sessions is still on our roadmap.

This release also adds the ability to change which resource class your session uses when you unpause the
session, in case the original resource class is now full.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 🖋 **Notebooks,Data Services,CSI**: Support for read and write storage mounting in sessions using a new rclone based storage driver
  (`#1707 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1707>`_,
  `#92 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/92>`_,
  `#1 <https://github.com/SwissDataScienceCenter/csi-rclone/pull/1>`_).
- 🔌 **UI**: add support for more storage services
  (`#2908 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2908>`_,
  `#2915 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2915>`_).

**✨ Improvements**

- 🖌️ **UI**: Improve the look and feel of the home page
  (`#2968 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2968>`_,
  `#2937 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2937>`_,
  `#2927 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2927>`_).
- 🔐 **UI**: Use password fields for credentials
  (`#2920 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2920>`_).
- 🔧 **UI**: Allow users to modify non running sessions
  (`#2942 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2942>`_).
- 🛑 **UI**: Improve feedback when starting sessions on outdated projects
  (`#2985 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2985>`_).
- 🖌️ **UI**: Update the Renku logo and Renku browser icons
  (`#2848 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2848>`_).

**🐞 Bug Fixes**

- **UI**: Resize the feedback badge on the session settings page
  (`#2953 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2953>`_).
- **UI**: Fix the environment dropdown on the Start session page
  (`#2949 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2949>`_).
- **UI**: Improve string validation when trying to upload a dataset file by URL
  (`#2834 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2834>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **UI**: RenkuLab admins can now add tolerations and node affinities to resource classes
  (`#2916 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2916>`_).
- **UI**: RenkuLab admins can add multiple users to a resource pool at once via a list of emails
  (`#2910 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2910>`_).
- **UI**: Use the renku-core API for session options
  (`#2947 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2947>`_).
- **UI**: Specify a branch every time a renku-core API is invoked
  (`#2977 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2977>`_).

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `amalthea 0.11.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.11.0>`_
- `csi-rclone 0.1.5 <https://github.com/SwissDataScienceCenter/csi-rclone/releases/tag/v0.1.5>`_
- `renku-data-services 0.4.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.4.0>`_
- `renku-notebooks 1.21.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.21.0>`_
- `renku-ui 3.18.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.18.0>`_
- `renku-ui 3.18.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.18.1>`_
- `renku-ui 3.19.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.19.0>`_


0.46.0
------

Renku ``0.46.0`` contains a bugfix for issues some users are facing when migrating projects to the newest metadata version.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- **UI**: Improve feedback when starting sessions on outdated projects
  (`#2985 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2985>`_).
- **CLI**: Allow specifying storage to mount when launching Renkulab sessions from the CLI
  (`#3629 <https://github.com/SwissDataScienceCenter/renku-python/pull/3629>`_).
- **KG**: Remove the Free-Text Dataset Search API as improved functionality is offered by the Entities Search.
  (`#1833 <https://github.com/SwissDataScienceCenter/renku-graph/pull/1833>`_).
- **KG**: Add support for specifying ``templateRef`` and ``templateParameters`` on the Project Create API.
  (`#1837 <https://github.com/SwissDataScienceCenter/renku-graph/pull/1837>`_).

**🐞 Bug Fixes**

- **Core Service**: Fix migrations not working when the Dockerfile needs to be migrated as well
  (`#3687 <https://github.com/SwissDataScienceCenter/renku-python/pull/3687>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**✨ Improvements**

- **Core Service**: Allow passing commit sha on config.show endpoint for anonymous users
  (`#3685 <https://github.com/SwissDataScienceCenter/renku-python/pull/3685>`_).

Individual Components
~~~~~~~~~~~~~~~~~~~~~
- `renku-python 2.9.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.9.1>`_
- `renku-python 2.9.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.9.0>`_
- `renku-ui 3.17.3 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.17.3>`_
- `renku-graph 2.49.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.49.1>`_
- `renku-graph 2.49.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.49.0>`_


0.45.2
------

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

- **Core Service**: Removed support for metadata v9 projects in the UI. Migration to v10 is now required.
- **Core Service**: Fixed a bug where projects weren't cloned shallowly, leading to large projects not working properly on the platform.

Internal Changes
~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **KG**: The process removing expiring Project Access Tokens not to be locked on the date of rollout.
- **UI**: Use the default branch on all the core datasets API to prevent cache conflicts
  resulting in broken or missing datasets
  (`#2972 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2972>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~

- `renku-python 2.8.2 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/2.8.2>`_
- `renku-ui 3.17.2 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.17.2>`_
- `renku-graph 2.48.2 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.48.2>`_


0.45.1
------
This is a bugfix release that updates the helm chart to work with new
prometheus metrics in the renku core service, which was preventing it from
starting properly if metrics were enabled. In addition this release
also addresses problems with expiring Gitlab access tokens when sessions
are paused and resumed which caused resumed session to not be able to push to Gitlab
or also it caused some sessions to not be able to resume after they have been paused.

Internal Changes
~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Helm Chart**: update core-service deployment to allow service and rq
  metrics to run side-by-side (`#3303
  <https://github.com/SwissDataScienceCenter/renku/pull/3303>`_).
- **Notebooks**: use a larger /dev/shm folder in sessions
  (`#1723 <https://github.com/SwissDataScienceCenter/renku-notebooks/issues/1723>`_)
- **Notebooks**: properly renew expiring Gitlab tokens when hibernated session are resumed
  (`#1734 <https://github.com/SwissDataScienceCenter/renku-notebooks/issues/1734>`_)
- **Gateway**: properly renew expiring Gitlab tokens for hibernating sessions
  (`#692 <https://github.com/SwissDataScienceCenter/renku-gateway/issues/692>`_)

Individual components
~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.20.3 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.20.3>`_
- `renku-gateway 0.23.1 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/0.23.1>`_

0.45.0
------

Renku ``0.45.0`` adds support for pausing and resuming sessions from the CLI. You can now also specify a
project image when initializing a project from the CLI. Additionally, this release brings coherent usage
of Dataset `name` and `slug` across all Renku APIs.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- **Core Service, CLI**: Add support for specifying a project image during
  project initialization
  (`#3623 <https://github.com/SwissDataScienceCenter/renku-python/issues/3623>`_).
- **CLI**: Add support for pausing & resuming remote sessions from the cli
  (`#3633 <https://github.com/SwissDataScienceCenter/renku-python/issues/3633>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**✨ Improvements**

- **Core Service, CLI**: Make slug and name consistent with rest of platform
  (`#3620 <https://github.com/SwissDataScienceCenter/renku-python/issues/3620>`_).
- **Core Service**: Add prometheus metrics
  (`#3640 <https://github.com/SwissDataScienceCenter/renku-python/issues/3640>`_).
- **UI**: Adapt dataset APIs to the new naming convention used in the backend
  (`#2854 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2854>`_).
- **KG**: All APIs to return Dataset ``slug`` and ``name`` and no ``title`` property
  (`#1741 <https://github.com/SwissDataScienceCenter/renku-graph/pull/1741>`_).
- **KG**: Clean up process removing project tokens close to their expiration date
  (`#1812 <https://github.com/SwissDataScienceCenter/renku-graph/pull/1812>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.17.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.17.1>`_
- `renku-python 2.8.0 <https://github.com/SwissDataScienceCenter/renku-python/tree/v2.8.0>`_
- `renku-graph 2.48.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.48.1>`_
- `renku-graph 2.48.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.48.0>`_

0.44.0
------

Renku ``0.44.0`` introduces the ability to pin your favorite projects to the dashboard
in RenkuLab for easy access. Additionally, it features a redesigned landing page that
provides information about Renku, its key features, and the development team behind the
platform, plus entry points for getting started with the platform.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 📌️ **UI**: Users can now pin projects to the dashboard, up to a maximum of
  5 projects (`#2898 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2898>`_).
- 🎨 **UI**: Introduce a redesigned landing page to enhance the user experience for new users exploring the platform for the first time
  (`#2925 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2925>`_).


**✨ Improvements**

- 🖼 **UI**: [Keycloak] Enhance UX for registration and authentication in the platform (`#26 <https://github.com/SwissDataScienceCenter/keycloak-theme/pull/26>`_).

**🐞 Bug Fixes**

- **UI**: Correctly update progress of project indexing (`#2833 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2833>`_).
- **UI**: Change icons in the Nav bar to use Bootstrap icons (`#2882 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2882>`_).
- **UI**: Fixed bug that caused Dashboard to reload frequently by handling errors from the ``getSessions`` query in the Dashboard (`#2903 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2903>`_).
- **UI**: Adjust dropdown menus with anchors nested in buttons (`#2907 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2907>`_).
- **UI**: Update the workflows documentation link (`#2917 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2917>`_).
- **UI**: Add whitespace after author name in session commit details (`#2921 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2921>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**✨ Improvements**

- **Data services**: New API endpoints to store and retrieve user
  preferences have been added to support the projects pins (`#85 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/85>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.17.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.17.0>`_
- `renku-data-services 0.3.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.3.0>`_

0.43.0
------

Renku ``0.43.0`` brings improvements to the KG API, addresses a few bugs in the UI
and in the data services API.

**A note to Renku administrators**: this release includes breaking changes in our Helm chart values file.
For more details on the Helm chart values changes please refer to the explanation in ``helm-chart/values.yaml.changelog.md``.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- **KG**: Performance improvements to the Cross-Entity Search API.
  (`#1666 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1666>`_).
- **KG**: The Cross-Entity Search API to allow filtering by a ``role``.
  (`#1486 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1486>`_).
- **KG**: Improved search to return results where the search keyword is separated by underscores.
  (`#1783 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1783>`_).
- **KG**: A new ``GET /knowledge-graph/version`` API.
  (`#1760 <https://github.com/SwissDataScienceCenter/renku-graph/pull/1760>`_).
- **KG**: Token service and Webhook service can now accept an AES token that is not base64 encoded.
  (`#1774 <https://github.com/SwissDataScienceCenter/renku-graph/pull/1774>`_).

**🐞 Bug Fixes**

- 🔽 **UI**: Prevent showing wrong options on the Session dropdown menu when the project
  namespace includes uppercase letters
  (`#2874 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2874>`_).
- 🔲 **UI**: Restore the switch between creating and importing a dataset, and restyle the
  buttons
  (`#2857 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2857>`_).
- 🔨 **UI**: Address visual glitches on many pages
  (`#2883 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2883>`_).
- 🧑‍🤝‍🧑 **UI**: Prevent occasionally duplicating last visited projects on the Dashboard
  (`#2892 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2892>`_).
- **KG**: Prevent CLI's Dataset Import from failing when the Dataset belongs to a project with more than 20 datasets.

Internal Changes
~~~~~~~~~~~~~~~~~~~

**Improvements**

- **KG**: KG services to work with both ``Base64`` encoded and ASCII secrets read from configuration.
- **KG**: Java upgraded to 21.0 and Jena to 4.10.0.

**Bug Fixes**

- **UI**: Correctly handle Statuspage down
  (`#2871 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2871>`_).
- **CRC**: Do not create new quotas when updating existing ones
- **CRC**: Use one database connection pool with limited number of connections

Individual Components
~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.47.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.47.1>`_
- `renku-graph 2.47.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.47.0>`_
- `renku-graph 2.46.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.46.0>`_
- `renku-graph 2.45.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.45.0>`_
- `renku-graph 2.44.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.44.0>`_
- `renku-ui 3.15.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.15.1>`_
- `renku-data-services 0.2.3 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.2.3>`_


0.42.1
------

Renku ``0.42.1`` is a bugfix release that addresses the following bugs in a few services:

- creating new resources in the ``data-services`` API
- properly enforcing access controls to the default resource pool
- accidentally removing the git repository directory from hibernated sessions
- properly templating node affinities and tolerations from the ``data-services`` into user sessions

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.2.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.2.1>`_
- `renku-data-services 0.2.2 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.2.2>`_
- `renku-notebooks 1.20.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.20.1>`_
- `renku-notebooks 1.20.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.20.2>`_

0.42.0
------

Renku ``0.42.0`` allows RenkuLab administrators to easily manage user resource pools via an Admin Panel built into RenkuLab.
User resource pools are a way to manage the compute resources accessible to groups of RenkuLab users for interactive sessions.
From the new Admin Panel, admins can create resource pools, set their max resource quotas, customize the session classes
available within pools, and add users to pools. Admins can access the new Admin Panel by navigating to the account icon
in the top right in RenkuLab and selecting 'Admin Panel'. To access the Admin Panel, a user must have the `renku-admin` role
delegated to them in Keycloak.

In addition, the login screen was updated to better space components on smaller screens and fix minor
visual glitches.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 🖼 **UI**: Admins can configure compute resources available to groups of users for interactive sessions.
  (`#2752 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2752>`_).

**✨ Improvements**

- 💾 **UI**: Show a confirmation text when saving a session
  (`#2856 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2856>`_).

Internal Changes
~~~~~~~~~~~~~~~~~~~

- ``renku-gateway`` can now proxy to Keycloak endpoints

Individual components:
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.23.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.23.0>`_
- `renku-ui 3.15.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.15.0>`_

0.41.1
------

Renku ``0.41.1`` is a bugfix release to patch a bug found in the data service which prevented
new users from being created due to a db migration problem.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.2.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.2.1>`_

0.41.0
------

Renku ``0.41.0`` adds new functionality for configuring external storage in projects! Users can now
configure external storage to be mounted automatically in their sessions. The settings are persisted for the project,
but access control is managed by the provider of the storage, not by Renku. This means that for restricted
data sources, users must enter credentials separately. This first implementation only supports S3-compatible storage,
but we will add support for additional providers soon.

Lastly, with this release administrators can configure the RenkuLab homepage to highlight chosen projects.

**A note to Renku administrators**: this release includes breaking changes in our Helm chart values file.
Refer to the ``Internal Changes`` section below for more details.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 🖼 **UI**: Admins can designate projects to be showcased on the home page, which will show them
  in the showcase section of the home page
  (`#2799 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2799>`_).

**✨ Improvements**

- 💾 **UI**: Add support for cloud storage configuration per project. There are now more options
  to customize to support external S3 and S3 compatible storage better
  (`#2760 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2760>`_).
- 🌈 **UI**: Improve color contrast and other UX elements
  (`#2846 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2846>`_).

Internal Changes
~~~~~~~~~~~~~~~~~~~

This release is a breaking change to the Helm values file and it requires minor edits to the following field:

- ``ui.homepage`` removed the unused ``projects`` field and added the ``showcase`` field.
- ``amalthea.scheduler.*`` deprecates all existing child fields and adds new child fields. If you are not defining these fields
  in your values file then you are using the default Kubernetes scheduler and this requires no action. But if you are
  defining a custom scheduler in your deployment's values file then this requires additional edits to your values file
  so that you can retain the same functionality as before.
- the ``crc`` field in the values file has been renamed to ``dataService``, all child fields remain the same
  functionally and by name.

For more details on the Helm chart values changes please refer to the explanation in ``helm-chart/values.yaml.changelog.md``.

In addition to this, other notable changes include:

- add node affinities and tolerations for resource classes
- persist cloud storage configurations at the project level
- validation of Rclone cloud storage configuration by the backend
- update the Amalthea scheduler to work with newer versions of Kubernetes
- ``renku-notebooks`` now get S3 cloud storage configuration from ``renku-data-services``
- ``renku-gateway`` now provides credentials for the cloud storage potion of ``renku-data-services``
- UI shows prominent banners during major outages
- various bug fixes across many components
- users can be prevented from accessing the default resource pool in ``renku-data-services``

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.1.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.1.1>`_
- `renku-data-services 0.2.0 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.2.0>`_
- `renku-gateway 0.22.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.22.0>`_
- `renku-notebooks 1.20.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.20.0>`_
- `renku-ui 3.14.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.14.0>`_
- `amalthea 0.10.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.10.0>`_

0.40.2
------

Renku ``0.40.2`` fixes a bug in the Renku data services where the web server consumed a lot of database connections.

**🐞 Bug Fixes**

- **Data services**: Run the server with only 1 worker so that fewer database connections are consumed

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services v0.0.3 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.0.3>`_

0.40.1
------

Renku ``0.40.1`` reverts recent changes to Lucene configuration in the Triples Store preventing users from searching by keywords.

**🐞 Bug Fixes**

- **KG**: Use the `StandardTokenizer` to allow searching by keywords containing underscore signs.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.43.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.43.1>`_

0.40.0
------

Renku ``0.40.0`` introduces UI performance improvements and fixes internal KG and Triples Store performance issues.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- 🚀 **UI**: Reduce the compiled bundle size to improve performance of the UI (`#2818 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2818>`_,
  `#2827 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2827>`_, `#2832 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2832>`_)
- 🚀 **UI**: [Dashboard] Speed up showing the warning for non-indexed projects (`#2824 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2824>`_)
- 🛠️ **UI**: [Projects] Use the KG API to update a project's metadata for the following cases: visibility, keywords and description (`#2793 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2793>`_)

Internal Changes
~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- 🚀 **KG**: reduces the number of update queries run against the Triples Store causing its performance degradation.
- 🛠️ **UI**: [Datasets] Use versioned URL of `renku-core` when uploading files to a dataset (`#2831 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2831>`_)

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.43.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.43.0>`_
- `renku-ui 3.13.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.13.1>`_

0.39.3
------

Renku ``0.39.3`` fixes various bugs.


Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **Helm chart**: fix problem with missing network policies preventing access to sessions
- **Helm chart**: use the session specific affinity, node selector and tolerations and not the general configuration reserved for Renku services
- **Helm chart**: use the correct default value for the Renku OAuth client in Gitlab

0.39.2
------

Renku ``0.39.2`` fixes a bug when pausing sessions.

**🐞 Bug Fixes**

- **Renku Notebooks**: fix a bug in session hibernation (`#1645 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1645>`_)

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.19.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.19.1>`_

0.39.1
------

Renku ``0.39.1`` fixes bugs in the Helm chart introduced by ``0.39.0``.


Internal Changes
~~~~~~~~~~~~~~~~

This introduces changes in the templates and values file of the Helm chart from 0.39.0 that were causing the Helm upgrade
operation to fail.

0.39.0
------

Renku ``0.39.0`` moves all renku component Helm charts to one single chart that now resides in this repository.

After initial testing we have noticed a bug in this version of the Helm chart. If you have already deployed this version simply
upgrading to ``0.39.1`` will fix things. If you have not yet deployed this version then skip it and go straight to ``0.39.1``.
The reason for the bug is that we replaced the ``spec.selector.matchLabels`` field of two important deployments in the Gateway
because of this the two components do not upgrade and the whole Helm upgrade operation fails.

Also, with the next releases we will adopt a specific way of versioning the helm chart. Namely:

- Patch changes (i.e. ``0.50.1`` -> ``0.50.2``) indicate that there are NO changes in the Helm chart and that
  only application level bug fixes are present in the new release.
- Minor version changes (i.e. ``0.50.2`` -> ``0.51.0``) indicate that there are NO changes in the Helm chart and that
  only application level new features and/or application level breaking changes are present.
- Major version changes (i.e. ``0.50.0`` -> ``1.0.0``) will be reserved for changes in the Helm chart, either when the
  values file changes or when the Helm templates change.

Please note that this is a breaking change to the values file and it requires three minor edits to the following fields:

- ``graph.jena.*`` moved to ``jena.*``
- ``notebooks.amalthea.*`` moved to ``amalthea.*``
- ``notebooks.dlf-chart.*`` moved to ``dlf-chart.*``

For more details please refer to the explanation in ``helm-chart/values.yaml.changelog.md``.

Internal Changes
~~~~~~~~~~~~~~~~

There are now no more separate Helm charts for the core, notebooks, graph, UI and gateway components. All the Helm
templates have been moved into the main Renku Helm chart in this repository.

0.38.0
------

Renku ``0.38.0`` improves the Knowledge Graph API, with a new Project Creation functionality and a Project Update enhancement.
There is also a new version of the core service with multiple bug fixes and a few new features.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- **CLI**: allow disabling automated parameter detection in renku run
  (`#3548 <hhttps://github.com/SwissDataScienceCenter/renku-python/issues/3548>`_).

**🌟 New Features**

- 🖼️ **Knowledge Graph**: New `Project Create API <https://renkulab.io/swagger/?urls.primaryName=knowledge%20graph#/default/post_projects>`_
  to create a project in GitLab and Knowledge Graph
  (`#1635 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1635>`_).

**🐞 Bug Fixes**

- **Knowledge Graph**: Improves quality of the results returned by the Cross-Entity Search API.
- **Knowledge Graph**: The `Project Update API <https://renkulab.io/swagger/?urls.primaryName=knowledge%20graph#/default/patch_projects__namespace___projectName_>`_ to work for non-public projects
  (`#1695 <https://github.com/SwissDataScienceCenter/renku-graph/pull/1695>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Core Service**: replace/refactor internal repository cache
  (`#3534 <hhttps://github.com/SwissDataScienceCenter/renku-python/issues/3534>`_).

**Bug Fixes**

-  **CLI:** do not freeze/unfreeze plan view model
   (`#3599 <https://github.com/SwissDataScienceCenter/renku-python/issues/3599>`__)
   (`3c48cff <https://github.com/SwissDataScienceCenter/renku-python/commit/3c48cffe116db5c246beca2003c2f282fc38b465>`__)
-  **CLI:** simplify ssh setup and key usage
   (`#3615 <https://github.com/SwissDataScienceCenter/renku-python/issues/3615>`__)
   (`3fa737a <https://github.com/SwissDataScienceCenter/renku-python/commit/3fa737ab6cd6126047098957ff2e5f179e939339>`__)
-  **Core Service:** setting a non-existing config property to null more than once results in error
   (`#3595 <https://github.com/SwissDataScienceCenter/renku-python/issues/3595>`__)
   (`e0ff587 <https://github.com/SwissDataScienceCenter/renku-python/commit/e0ff587f507d049eeeb873e8488ba8bb10ac1a15>`__)
-  **Core Service:** skip fast cache migrations check for anonymous users
   (`#3577 <https://github.com/SwissDataScienceCenter/renku-python/issues/3577>`__)
   (`9ee3176 <https://github.com/SwissDataScienceCenter/renku-python/commit/9ee3176ce379dd80d2955e858f5e11e1fb32b464>`__)
-  **Core Service:** normalize git url to avoid duplicate cache entries
   (`#3606 <https://github.com/SwissDataScienceCenter/renku-python/issues/3606>`__)
   (`19142c6 <https://github.com/SwissDataScienceCenter/renku-python/commit/19142c6f58713cb9990b71f9ed738990987c3e16>`__)
-  **CLI:** adapt to changes in Knowledge Graph API for importing datasets
   (`#3549 <https://github.com/SwissDataScienceCenter/renku-python/issues/3549>`__)
   (`020434a <https://github.com/SwissDataScienceCenter/renku-python/commit/020434a7dd6449755644a2e9ca849b8821900f72>`__)
-  **Core Service:** add branch to service cache path
   (`#3562 <https://github.com/SwissDataScienceCenter/renku-python/issues/3562>`__)
   (`3800a38 <https://github.com/SwissDataScienceCenter/renku-python/commit/3800a3823515763c207b1b15f348df3b0cdd9831>`__)
-  **Core Service:** add support for using default values in template parameters
   (`#3550 <https://github.com/SwissDataScienceCenter/renku-python/issues/3550>`__)
   (`d162392 <https://github.com/SwissDataScienceCenter/renku-python/commit/d162392b3dc20dd3433be78b08f101e7f268ed7d>`__)
-  **Knowledge Graph**: Various issues preventing Grafana dashboards not working.
   (`#1717 <https://github.com/SwissDataScienceCenter/renku-graph/pull/1717>`_)
   (`#1719 <https://github.com/SwissDataScienceCenter/renku-graph/pull/1719>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.42.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.42.0>`_
- `renku-graph 2.42.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.42.1>`_
- `renku-python 2.7.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/2.7.0>`_


0.37.0
------

Renku ``0.37.0`` introduces a new feature to pause sessions and later resume them exactly where you left off. All of your work in progress, including files, data, and environment changes not saved to git, are resumed right as you left them.

This feature replaces RenkuLab's branch-based auto-save mechanism. Most users do not have to do anything to transition from auto-saves to persistent sessions. However, if your last session went into an auto-save, you can still retrieve that work by using Start with Options and selecting your most recent auto-save branch. If your project contains auto-save branches that you do not need anymore, you can safely delete them.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- ⏸️ **Renku Notebooks** and **UI**: Support for pausing (i.e. hibernating) and resuming sessions
  (`#1518 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1518>`_)
  (`#2686 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2686>`_).

**🐞 Bug Fixes**

- **UI**: restore adding files by URL to datasets
  (`#2800 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2800>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**New Features**

- **Renku Notebooks**: Use a new version of Amalthea which adds fields for culling
  hibernating sessions in the CRD.
- **Renku Notebooks** - **Helm chart breaking change**: ``notebooks.culling.idleThresholdSeconds`` in the values file
  was renamed to ``notebooks.culling.idleSecondsThreshold``.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `amalthea 0.9.1 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.9.1>`_
- `renku-notebooks 1.19.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.19.0>`_
- `renku-ui 3.13.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.13.0>`_


0.36.3
------

Renku ``0.36.3`` is a bug-fix release that solves a few issues with creating new
projects and datasets.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **UI**: fix render loops in creating new projects; they were occasionally creating
  problems based on the specific fields filled in by the user.
  (`#2788 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2788>`_).
- **UI**: restore adding files to datasets on an outdated but supported metadata version
  (`#2788 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2788>`_).
- **UI**: do not error on dataset thumbnails pointing to an external URL
  (`#2791 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2791>`_).
- **UI**: prevent failures when unzipping files with a large number of elements on dataset
  creation
  (`#2786 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2786>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.12.2 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.12.2>`_


0.36.2
------

Renku ``0.36.2`` is a bug-fix release that fixes a bug with running ``renku save`` from JupyterServer
session sidecars.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Renku Notebooks**: Fix a problem that prevented users from saving data via the UI in a session
  (`#1620 <https://github.com/SwissDataScienceCenter/renku-notebooks/issues/1620>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.18.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.18.1>`_


0.36.1
------

Renku ``0.36.1`` is a bug-fix release that includes the fixes from ``0.35.2``.

It also includes a few changes behind the scenes on how the UI interacts with backend components.

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **UI**: Take advantage of Core Service API versions
  (`#2764 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2764>`_).

**Bug Fixes**

- **UI**: Fix bootstrap icons
  (`#2772 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2772>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.12.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.12.1>`_


0.36.0
------

Renku ``0.36.0`` introduces an updated landing page layout to help new users explore
the platform for the first time. Thus, it implements a few bug fixes for the UI and Knowledge Graph.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 🖼️ **Knowledge Graph**: Project Update API can update description, keywords and image
  (`#1631 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1631>`_).

**✨ Improvements**

- 📐 **UI**: Update the landing page for non-logged users to simplify discovering
  the platform
  (`#2741 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2741>`_).

**🐞 Bug Fixes**

- **UI**: Fix the Renku version on the footer
  (`#2776 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2776>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **UI**: Prevent using different repository URLs for the same project
  (`#2766 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2766>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.12.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.12.0>`_
- `renku-graph 2.41.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.41.0>`_


0.35.2
------

Renku ``0.35.2`` introduces a UI bug-fix to prevent overloading backend components
when using pre-filled template links.

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **UI**: Handle embedded template variables in project creation links
  (`#2789 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2789>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.11.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.11.1>`_


0.35.1
------

Renku ``0.35.1`` introduces bug fixes in the compute resource control (CRC) service
and the gateway.

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **Compute resource control**: Fix erroneous validation errors with quotas
  (`#22 <https://github.com/SwissDataScienceCenter/renku-data-services/pull/22>`__)
- **Gateway**: Sticky sessions null de-reference causing crashes
  (`#673 <https://github.com/SwissDataScienceCenter/renku-gateway/issues/673>`__)

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services v0.0.2 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.0.2>`_
- `renku-gateway 0.21.3 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.21.3>`_


0.35.0
------

Renku ``0.35.0`` introduces new features in the UI and bug fixes in various components.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 🫥 **UI**: Add a new section in the project settings to change visibility
  (`#2648 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2648>`_).
- 🔢 **UI**: Show the Renku version on the footer and add a new page to list
  the backend components versions.
  (`#2703 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2703>`_).

**✨ Improvements**

- 🫥 **UI**: Use the description from the project metadata in the project pages.
  This means the description set at project creation won't go lost.
  (`#2631 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2631>`_).
- ❓ **UI**: Use a more generic and user-friendly concept instead of "Knowledge Graph"
  when referring to the project metadata processing
  (`#2709 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2709>`_).

**🐞 Bug Fixes**

- **UI**: Allow navigating back during and after the login without ending in a
  corrupted state
  (`#2711 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2711>`_).
- **UI**: Sort project datasets by the user-friendly title instead of the hidden name
  (`#2702 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2702>`_).
- **UI**: Update the loader spinner to fix DOM nesting errors
  (`#2750 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2750>`_).
- **UI**: Prevent fake warnings from memory constraints when starting sessions
  (`#2757 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2757>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **KG**: New graph for storing auth data
  (`#1661 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1661>`_).
- **UI**: Restore maintenance page
  (`#2715 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2715>`_).

**Bug Fixes**

- **Core Service**: Fix issue with cache.migrations_check timing out.
  (`#3597 <https://github.com/SwissDataScienceCenter/renku-python/issues/3597>`__)
  (`20b5589 <https://github.com/SwissDataScienceCenter/renku-python/commit/20b5589ea2639b4ff017fc390a9b685842c9685d>`__)
- **Core Service**: Fix dataset image IDs for datasets imported from Zenodo
  (`#3596 <https://github.com/SwissDataScienceCenter/renku-python/issues/3596>`__)
  (`f624b2b <https://github.com/SwissDataScienceCenter/renku-python/commit/f624b2bf261d97b07c88243f674f544613753e28>`__)
- **Core Service**: Fix issue on workflows UI with badly formatted IDs
  (`#3594 <https://github.com/SwissDataScienceCenter/renku-python/issues/3594>`__)
  (`c418c17 <https://github.com/SwissDataScienceCenter/renku-python/commit/c418c178d03a5caac126d14cc089064ee13f2747>`__)
- **Gateway:** properly use Redis sentinel client
  (`#668 <https://github.com/SwissDataScienceCenter/renku-gateway/issues/668>`__)
  (`5ab4447 <https://github.com/SwissDataScienceCenter/renku-gateway/commit/5ab44475c9f7a516ddb8865c8f70db9bdb0ba5ec>`__)
- **Gateway:** properly redirect from /gitlab urls
  (`#669 <https://github.com/SwissDataScienceCenter/renku-gateway/issues/669>`__)
  (`2fac96f <https://github.com/SwissDataScienceCenter/renku-gateway/commit/2fac96f5c6141f4e57ae5cc77877670156bceae5>`__)
- **Gateway:** return 404 if the core service metadata version does not exist instead of redirecting
  to the endpoint that is using the latest metadata version
  (`#667 <https://github.com/SwissDataScienceCenter/renku-gateway/issues/667>`__)
  (`2753d07 <https://github.com/SwissDataScienceCenter/renku-gateway/commit/2753d0773e26cb1c74e4be4dd44fe5e77f428657>`__
- **UI**: Use a common project URL when invoking renku-core APIs
  (`#2722 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2722>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.21.2 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.21.2>`_
- `renku-graph 2.40.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.40.0>`_
- `renku-python 2.6.2 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.6.2>`_
- `renku-ui 3.11.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.11.0>`_

0.34.1
------

Renku ``0.34.1`` fixes Project names in the UI and the Knowledge Graph API.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Knowledge Graph**: The Knowledge Graph APIs return wrong names, especially for Projects that are forks.
  (`#1662 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1662>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.39.2 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.39.2>`_

0.34.0
------

Renku ``0.34.0`` comes with improvements in the Infrastructure.

Internal Changes
~~~~~~~~~~~~~~~~

**New Features**

- **Infrastructure**: add support for ingress class name

**Improvements**

- **Documentation**: refactor certificate management part to show how to make use of
  cert-manager and manually created certificates in both development and production
  contexts.


0.33.1
------

Renku ``0.33.1`` introduces bug fixes and addition of a warning field when listing servers in the Notebook service.
The minor change in the Notebook service API are fully backwards compatible.
It also brings some improvements and bug fixes in the Knowledge Graph.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- 🔎 **Knowledge Graph**: all the APIs return a new Project `slug` property.
  The `path` property although still available will be removed in the future.
  (`#1641 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1641>`_).

**🐞 Bug Fixes**

- **Knowledge Graph**: Cross-Entity Search cannot find projects by creator.
  (`#1656 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1656>`_).

Internal Changes
~~~~~~~~~~~~~~~~

- **Renku Notebooks:** add session warnings to jupyter server API responses
  (`#1482 <https://github.com/SwissDataScienceCenter/renku-notebooks/issues/1482>`__)
  (`b463980 <https://github.com/SwissDataScienceCenter/renku-notebooks/commit/b46398032e6361ef1b69fb4909d2ed87afc583eb>`__)

**Bug Fixes**

- **Renku Notebooks:** parse old server options in request to start sessions
  (`#1570 <https://github.com/SwissDataScienceCenter/renku-notebooks/issues/1570>`__)
  (`8b3e5c0 <https://github.com/SwissDataScienceCenter/renku-notebooks/commit/8b3e5c091507446080fd468d84c4bd4b8d134b60>`__)

- **Renku Notebooks:** properly recover LFS files from autosave branches
  (`#1568 <https://github.com/SwissDataScienceCenter/renku-notebooks/issues/1568>`__)
  (`8f34b09 <https://github.com/SwissDataScienceCenter/renku-notebooks/commit/8f34b09ab73913bfbba4acbe28b00c53ad576367>`__)

- **Knowledge Graph**: fix for an infinite retry loop while sending certain types of internal events.
  (`#1650 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1650>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.18.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.18.0>`_
- `renku-graph 2.39.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.39.0>`_
- `renku-graph 2.39.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.39.1>`_

0.33.0
------

Renku ``0.33.0`` introduces improvements and bug fixes in the UI and Knowledge Graph.

The UI benefits from better error handling and overall behavior, including improved
handling of common R file extensions. Regarding the Knowledge Graph, the
Cross-Entity Search improves significantly its performance and project visibility
can be changed through a dedicated API.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 🫣 **Knowledge Graph**: Add a new Project Update API for changing Project visibility
  (`#1611 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1611>`_).

**✨ Improvements**

- 🔎 **Knowledge Graph**: The Cross-Entity Search returns a new ``dateModified`` property for
  Project and Dataset entities
  (`#1612 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1612>`_) and
  (`#1595 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1595>`_).
- 〽️ **Knowledge Graph**: Improved performance of the Cross-Entity Search while searching for
  datasets
  (`#1591 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1591>`_).
- 🔙 **UI**: Add a global error page for fatal errors preventing blank pages
  (`#2604 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2604>`_).
- 📄 **UI**: Support previewing additional common R file extensions
  (`#2639 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2639>`_).

**🐞 Bug Fixes**

- **UI**: Prevent showing endless loader when migration errors occur
  (`#2650 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2650>`_).
- **UI**: Do not expand folders by default in dataset view when dataset contains
  large number of elements
  (`#2628 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2628>`_).
- **UI**: Render Rstudio components correctly -- sometimes columns were collapsed
  (`#2660 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2660>`_).
- **UI**: Prevent losing work when editing datasets
  (`#2628 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2628>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**New Features**

- **Renku Notebooks**: Use a new version of Amalthea which adds fields for hibernating sessions in the CRD.

**Bug Fixes**

- **Knowledge Graph**: Fixed the problem of concurrent writes to the Triples Store causing data
  integrity violations
  (`#1577 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1577>`_).


Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.38.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.38.0>`_
- `renku-ui 3.10.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.10.0>`_
- `renku-notebooks 1.17.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.17.0>`_
- `amalthea 0.8.0 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.8.0>`_

0.32.0
------

Renku ``0.32.0`` introduces improvements in the KG services, enhancing KG overall performance.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- 🔁 **KG**: ``updatedAt`` renamed to ``dateModified`` on the Project Details API (`#1582 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1582>`_).
- 📖 **KG**: Improved performance of the Project Status API (`#1554 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1554>`_).

Internal Changes
~~~~~~~~~~~~~~~~

** Improvements**

- **KG**: A new process to synchronize various project properties between GitLab and Triples Store (`#1569 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1569>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.37.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.37.0>`_


0.31.0
------

Renku ``0.31.0`` introduces the compute resource control (CRC) service, enabling
Renku administrators to manage access to specific computing resources. The
service exposes an HTTP API for the administrators to interact with. In this
way, Renku administrators can create, update or delete resource pools, and can
add to or remove users from resource pools. Please note that a user interface
for the CRC has not been added yet but it will be added in a future release.
Currently, the only way for administrators to interact with the CRC service is
through the swagger page which can be found at the path
``/swagger/?urls.primaryName=crc%20service`` appended to the base URL of a Renku
deployment. A Renku administrator is any user who has the `renku-admin` realm
role. Assigning users to this role can be performed by the Keycloak
administrator via the Keycloak UI or API.

The CRC service also brings changes to the user interface for launching
sessions, specifically when it comes to selecting compute resources for a
specific session. With this version we have grouped different pre-set
configurations of memory, CPU, RAM and GPU in resource classes. Resource classes
are further grouped in resource pools and users are asked to select the resource
pool and class they wish to use when they launch a session rather than
separately specify memory, CPU, RAM and GPU requirements. The selection for the
amount of disk storage required is also changed but now has more freedom than
before. Users can now select disk storage with a slider that only has a maximum
limit and no pre-set steps. For projects where the users have specified resource
requests in the project settings the UI will provide hints as to which resource
classes are suitable based on the settings. When the quick launch button is used
to start a session the closest equal or greater resource class based on the
project settings will be automatically selected.

Apart from the changes needed to support compute resource access features,
support has also been added for common R file extensions.

This release also includes a hotfix for an issue with the horizontal scaling of
the core-service where users could get redirected to the wrong service instance
and subsequent requests to the core-service would fail due to partial cache
misses.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 🧑‍💻 **UI**: Update session start options and project settings to use compute resource pools
  (`#2484 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2484>`_).

**🐞 Bug Fixes**

- 〽️ **UI**: Support common R file extensions
  (`#2638 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2638>`_).
- 🛠 **Renku Core Service**: Isolates core-service cache per instance, improves cache cleanup.
  (`#3555 <https://github.com/SwissDataScienceCenter/renku-python/pull/3555>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Infrastructure**: Add the compute resource control service.
- **Renku Notebooks**: Use the compute resource control (CRC) service.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-data-services 0.0.1 <https://github.com/SwissDataScienceCenter/renku-data-services/releases/tag/v0.0.1>`_
- `renku-notebooks 1.16.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.16.0>`_
- `renku-ui 3.9.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.9.0>`_
- `renku-python 2.6.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.6.1>`_


0.30.1
------

Renku ``0.30.1`` is a small bugfix release that addresses a problem with the gateway Helm chart.

Internal Changes
~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **Gateway**: Remove duplicate environment variables in reverse proxy container
  (`#660 <https://github.com/SwissDataScienceCenter/renku-gateway/issues/660>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.21.1 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.21.1>`_


0.30.0
------

Renku ``0.30.0`` adds the ability for the core service to horizontally scale and for the gateway to provide sticky sessions
for the core service. In addition, improvements and bug fixes are also included on the UI, as well as required changes for
enabling sticky sessions for the core service.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 🔭 **UI**: Surface backend error message on dataset list page
  (`#2629 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2629>`_).

**🐞 Bug Fixes**

- **UI**: Do not query for datasets if no backend is available for the project version
  (`#2636 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2636>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**🌟 New Features**

- **Gateway**: Add sentry to reverse-proxy
  (`#654 <https://github.com/SwissDataScienceCenter/renku-gateway/issues/654>`__)
- **Gateway**: Core-service sticky sessions
  (`#646 <https://github.com/SwissDataScienceCenter/renku-gateway/issues/646>`__)
- **Renku Core Service**: Horizontal scaling
  (`#3178 <https://github.com/SwissDataScienceCenter/renku-python/issues/3178>`_).
- **UI**: Handle responses from the new core versions endpoint
  (`#2134 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2134>`_).

**🐞 Bug Fixes**

- **Renku Core Service**: Fixes importing private datasets in deployments with external gitlab
  (`#3523 <https://github.com/SwissDataScienceCenter/renku-python/issues/3523>`_).
- **UI**: Prevent API failures for projects on older metadata versions
  (`#2627 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2627>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.21.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.21.0>`_
- `renku-python 2.6.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.6.0>`_
- `renku-ui 3.8.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.8.0>`_
- `renku-ui 3.8.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.8.1>`_

0.29.0
------

Renku ``0.29.0`` introduces new UI features and a PostgreSQL DB for triples-generator.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 📝 **UI**: Add a customizable message to the dashboard page
  (`#2592 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2592>`_).

**🐞 Bug Fixes**

- **UI**: Restore deleting datasets
  (`#2607 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2607>`_).
- **UI**: Improve showing the error details for sessions
  (`#2611 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2611>`_).
- **UI**: Fix sessions not showing on dashboard
  (`#2608 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2608>`_).
- **UI**: Allow up to 100 namespaces and sort them
  (`#2606 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2606>`_).
- **UI**: Prevent crashes when handling markdown files
  (`#2597 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2597>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**✨ Improvements**

- **KG**: A PostgreSQL DB is added for the triples-generator (`values changelog <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/values.yaml.changelog.md#upgrading-to-renku-0290>`_).

**Bug Fixes**

- **Infrastructure**: properly generate PostgreSQL secrets on upgrade (`#3137 <https://github.com/SwissDataScienceCenter/renku/issues/3137>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.7.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.7.0>`_

0.28.1
------

Renku ``0.28.1`` resolves a minor bug that occurs when launching sessions when git submodules are used.

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- 🛠 **Renku Notebooks**: do not fail session launch if Git submodules couldn't be pulled.


Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-notebooks 1.15.3 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.15.3>`_


0.28.0
------

Renku ``0.28.0`` simplifies the project status update by making it available in the project settings tab and improves the information in the UI regarding the indexing processes. Thus, it enables new features for the command line interface related to sessions and exporting dataset keywords.

Read on for a full breakdown of all new features, improvements and bug fixes included in this release.


User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 🐳️ **CLI**: Pass docker run args to session start (`#3487 <https://github.com/SwissDataScienceCenter/renku-python/issues/3487>`_).
- 👩‍💻️ **CLI**: Shell completion for sessions (`#3450 <https://github.com/SwissDataScienceCenter/renku-python/issues/3450>`_).
- 📎️ **CLI**: Export dataset keywords (`#3454 <https://github.com/SwissDataScienceCenter/renku-python/issues/3454>`_).
- **KG**: Dataset Details API response enhanced with info about project specific dataset identifier (`#1546 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1546>`_).
- **KG**: ``slug`` as a copy of ``name`` on responses from all Dataset APIs (`#1544 <https://github.com/SwissDataScienceCenter/renku-graph/issues/1544>`_).
- 🗑️ **UI**: Added a delete project button on the settings tab (`#2416 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2416>`_).

**✨ Improvements**

- 👩‍💻️ **UI**: Major rework and simplification of project status page (moved to Settings -> General) (`#2315 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2315>`_).

**🐞 Bug Fixes**

- 📎️ **CLI**: Fixed dataset update with external files (`#3379 <https://github.com/SwissDataScienceCenter/renku-python/issues/3379>`_).
- 🛠️ **CLI**: Fixed special paths in workflow files and bump ``toil/cwltool`` (`#3489 <https://github.com/SwissDataScienceCenter/renku-python/issues/3489>`_).
- 🖼️ **UI**: Made text not selectable in entity cards (`#2546 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2546>`_).
- 📎️ **UI**: Fixed the "add dataset to project" feature (`#2549 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2549>`_).
- 🔗️ **UI**: Fixed clicking on a file name on the dataset view wrongly leading to lineage and not contents (`#1270 <https://github.com/SwissDataScienceCenter/renku-ui/issues/1270>`_).
- 🔗️ **UI**: Fixed getting a 404 page when switching from lineage view to contents (`#2571 <https://github.com/SwissDataScienceCenter/renku-ui/issues/2571>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**✨ Improvements**

- 🛠️ **Gateway**: Added new path to knowledge-graph webhooks
  (`#639 <https://github.com/SwissDataScienceCenter/renku-gateway/issues/639>`_).

**🐞 Bug Fixes**

- 🛠️ **Core**: Fixed working on branches in the ``core-svc`` (`#3472 <https://github.com/SwissDataScienceCenter/renku-python/issues/3472>`_).
- 🛠️ **Core**: Return proper errors on migrations check (`#3334 <https://github.com/SwissDataScienceCenter/renku-python/issues/3334>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-python 2.5.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.5.0>`_
- `renku-ui 3.6.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.6.0>`_
- `renku-graph 2.36.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.36.0>`_

0.27.0
------

Renku ``0.27.0`` upgrades the Keycloak version that is shipped with the project.

NOTE: make sure to check `helm-chart/README.rst` and `helm-chart/values.yaml.changelog.md` for instructions on how to
upgrade to this version of Renku.

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Infrastructure**: (BREAKING CHANGE) Use a new Keycloak Helm chart (codecentric/keycloakx) and upgrade Keycloak to 20.0.3. This requires modifying your current values file to work with the new Keycloak Helm chart, see (`the helm chart values changelog <https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/values.yaml.changelog.md>`_) for instructions.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.20.1 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.20.1>`_

0.26.2
------

Renku ``0.26.2`` resolves bugs and adds minor features in renku-graph.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- **KG**: Improved performance when searching for Projects.

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **KG**: Excessive Project Access Tokens creation when calling Project Status API for non-activated projects.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.35.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.35.0>`_

0.26.1
------

Renku ``0.26.1`` comes with a fix for KG services failing on startup when longer than 16 chars secrets are configured.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.34.2 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.34.2>`_

0.26.0
------

Renku ``0.26.0`` resolves bugs and adds minor features in the core-service, CLI, renku-graph, and the UI.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

- 🚢 **Renku CLI**: Allow force-building local images and setting local port on docker session provider.
- **KG**: A new Recently Viewed Entities API giving information about entities the user viewed.
- 🔁 **UI**: Add a clone button to the project overview
  (`#2495 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2495>`_).

**✨ Improvements**

- ☝ **Renku CLI**: Removed nagging about new Renku CLI versions when running commands.
- 🪣 **Renku CLI**: Added support for storing dataset S3 credentials per bucket.
- **KG**: Improved quality of search results returned by the Cross-Entity Search API.

**🐞 Bug Fixes**

- 🛠 **Renku Core Service**, **Renku CLI**: Fixed an issue with v10 metadata migration regarding datasets.
- 🍎 **Renku CLI**: Fixed compatibility with Python 3.11 on MacOS.
- 🍎 **Renku CLI**: Fixed properly exporting triples for deleted datasets.
- 🛠 **Renku Core Service**, **Renku CLI**: Improved handling of Dockerfile changes when updating project template.

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- 📜 **Renku Core Service**, **Renku CLI**: Added code contracts in key places to ensure metadata consistency.

**Bug Fixes**

- **KG**: Fix KG services failing on startup when longer than 16 chars secrets configured.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-python 2.4.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.4.0>`_
- `renku-python 2.4.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.4.1>`_
- `renku-graph 2.34.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.34.0>`_
- `renku-graph 2.34.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.34.1>`_
- `renku-ui 3.5.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.5.0>`_

0.25.6
------

Renku ``0.25.6`` fixes a bug in the Renku release process and does not bring any functional changes.

0.25.5
------

Renku ``0.25.5`` comes with a few KG bug-fixes.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **KG**: Prevent presenting misleading information about Knowledge Graph integration status on projects where the user is not one of the members.

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **KG**: Resolve token decryption issues preventing Knowledge Graph integration activation.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.33.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.33.0>`_


0.25.4
------

Renku ``0.25.4`` introduces several KG and UI bug-fixes.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

- **UI**: Prevent flashing a spinning wheel when loading workflows
  (`#2493 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2493>`_).
- **UI**: Restore the flag to show inactive workflows
  (`#2502 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2502>`_).
- **UI**: Prevent infinite spinning wheel when accessing projects by numeric ID
  without the required user's permissions
  (`#2476 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2476>`_).
- **UI**: Fix an issue where a notebook would not automatically open in a session for anonymous users
  as an anonymous user
  (`#2479 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2479>`_).
- **UI**: Prevent content layout shift when selecting a template
  (`#2482 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2482>`_).
- **UI**: Update broken documentation link
  (`#2497 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2497>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **KG**: Resolve issues with synchronization of project user access authorization data.
- **KG**: Ensure that the Delete Project API works when there is no data for the project in the Triples Store.
- **KG**: Ensure that the Lineage API works in cases of implicit parameters, inputs and outputs.
- **UI**: Correct handling of notebook search parameter with autostart
  (`#2469 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2469>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.32.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.32.0>`_
- `renku-ui 3.4.3 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.4.3>`_


0.25.3
------

Renku ``0.25.3`` introduces a bug fix in the Helm chart for the gateway.

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **Gateway**: **chart:** properly template horizontal pod auto-scaler for reverse proxy
  (`#643 <https://github.com/SwissDataScienceCenter/renku-gateway/issues/643>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.19.2 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.19.2>`_


0.25.2
------

Renku ``0.25.2`` introduces a few bug fixes and improvements in the UI.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

- 🧾 **UI**: Use drop-downs for session options with many elements
  (`#2461 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2461>`_).

**🐞 Bug Fixes**

- **UI**: Prevent dropping valid pinned images when starting new sessions
  (`#2466 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2466>`_).
- **UI**: Ignore deleted projects on the dashboard
  (`#2465 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2465>`_).
- **UI**: Fix fork suggestion when trying to start sessions on non-owned projects
  (`#2465 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2465>`_).
- **UI**: Remove conspicuous commas in the add dataset screen
  (`#2472 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2472>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **UI**: Do not query workflows on outdated projects
  (`#2460 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2460>`_).
- **UI**: Fix broken warning component on sessions pages
  (`#2474 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2474>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-ui 3.4.2 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.4.2>`_


0.25.1
------

Renku ``0.25.1`` introduces a small bug fix in the Gateway internal components.

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

- **Gateway**: correct path for reaching Gitlab from the CLI, it should be ``/repos`` instead of ``/api/repos``.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.19.1 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.19.1>`_

0.25.0
------

Renku ``0.25.0`` introduces an update to the base images and templates, as well as changes in internal components and the Helm chart.

User-facing Changes
~~~~~~~~~~~~~~~~~~~

**Improvements**

- New base images correct a ``PATH`` misconfiguration that occurs when connecting to a session with SSH. If using sessions through SSH, please update your project!

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

- **Gateway**: replace the Traefik reverse proxy with a custom solution based on the Echo library in Go.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-gateway 0.19.0 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.19.0>`_
- `renkulab-docker 0.16.0 <https://github.com/SwissDataScienceCenter/renkulab-docker/releases/tag/0.16.0>`_


0.24.4
------

Renku ``0.24.4`` resolves bugs in the Knowledge Graph backend processes.

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

* **KG**: Collect info about users who showed interest in projects and datasets.

**Bug Fixes**

* **KG**: Resolve problems causing flows accessing GitLab API to go into deadlock.


Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.31.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.31.0>`_


0.24.3
------

Renku ``0.24.3`` resolves bugs in the UI and in the Knowledge Graph backend processes.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

* 🔗 **UI**: Resolve an issue where the Connect button on the Dashboard leads to a broken link
  (`#2444 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2444>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

* **UI**: Retrieve project metadata from the KG on project access
  (`#2414 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2414>`_).

**Bug Fixes**

* **KG**: Resolve problems causing particular flows to terminate prematurely,
  as well as improve the quality of logging within the Activation API.


Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.30.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.30.0>`_
- `renku-graph 2.30.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.30.1>`_
- `renku-ui 3.4.1 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.4.1>`_


0.24.2
------

Renku ``0.24.2`` introduces bug fixes. We squashed a bug where you may have had trouble
finding your projects in other namespaces. Now, all projects show up in the Knowledge Graph
activation page so you can add them to your dashboard.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

* ❌ **Knowledge Graph**: Add a new API to delete projects.
* 🔘 **UI**: Provide the same options on the session dropdown buttons across the
  dashboard and the project page.
  (`#2393 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2393>`_).
* 📖 **UI**: Make the browser column and file content column sticky when scrolling
  long files on the file viewer.
  (`#2412 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2412>`_).
* 🧾 **UI**: Improve the feedback for sessions ending in an error state.
  (`#2411 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2411>`_).

**🐞 Bug Fixes**

* **Core Service**: Fix a crash when migrating to v10 metadata
  (`#3359 <https://github.com/SwissDataScienceCenter/renku-python/pull/3359>`__).
* **Knowledge Graph**: Expand the Knowledge Graph Project Activation page to list all
  projects where the user is a member, not only owned projects.
* **Sessions**: Fixed bug where sessions from one project would appear under another project
  (`#1423 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1423>`_).
* **UI**: Prevent listing projects twice on the dashboard
  (`#2408 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2408>`_).
* **UI**: Start sessions with base images when pinned images are missing
  (`#2410 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2410>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

* **Knowledge Graph**: A new functionality to capture Project viewing events

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.28.4 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.28.4>`_
- `renku-graph 2.29.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.29.0>`_
- `renku-notebooks 1.15.2 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.15.2>`_
- `renku-python 2.3.2 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.3.2>`_
- `renku-ui 3.4.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.4.0>`_

0.24.1
------

Renku ``0.24.1`` introduces bug fixes.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🐞 Bug Fixes**

* **Core Service**: Correctly update Dockerfile on migration
  (`#3351 <https://github.com/SwissDataScienceCenter/renku-python/issues/3351>`__).
* **Renku CLI**: Fix git credentials helper setup in ``renku login``
  (`#3348 <https://github.com/SwissDataScienceCenter/renku-python/issues/3348>`__).
* **Sessions**: Sessions crashing when automated token refresh runs in background
  (`#1416 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1416>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Bug Fixes**

* **Knowledge Graph**: Fixes and improvements to the schema v10 migration

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.28.1 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.28.1>`_
- `renku-graph 2.28.2 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.28.2>`_
- `renku-graph 2.28.3 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.28.3>`_
- `renku-notebooks 1.15.1 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.15.1>`_
- `renku-python 2.3.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.3.1>`_


0.24.0
------

Renku ``0.24.0`` introduces two new features: SSH-access to RenkuLab sessions
and a new RenkuLab Dashboard!

Would you like to work on your Renku project from the comfort of your own local
computer? Use the Renku CLI to start an SSH session on RenkuLab, and open that
session in your local terminal or even your IDE, such as VSCode. Check out the
"Connect with SSH" option in the Session Start menu to get started, or see our
`docs <https://renku.readthedocs.io/en/stable/how-to-guides/renkulab/ssh-into-sessions.html>`__.
*Note that SSH functionality must be enabled by your administrator and may not be
available on all RenkuLab deployments.*

And, your RenkuLab Dashboard now has a snazzy new look that puts your most used
projects, sessions, and datasets at your fingertips. Quickly pick up where you
left off: connect to your already-running sessions, start a new session on a
recently visited project, or access your datasets- all from a single page!

Read on for a full breakdown of all improvements and bug fixes included in this
release.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

* ⌨️ **Renku CLI**: Enable connecting to RenkuLab sessions via SSH
  (`#3318 <https://github.com/SwissDataScienceCenter/renku-python/pull/3318>`_).
* 📑 **UI**: Show instructions on how to connect to RenkuLab sessions via SSH from the
  Session Start menu
  (`#2376 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2376>`_).
* 🧑‍💻 **UI**: Add a new dashboard for logged-in users, showing running sessions, last
  accessed projects, and own datasets
  (`#2332 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2332>`_).


**✨ Improvements**

* **Sessions**: Fail when injecting env vars that already exist in session
  (`#1396 <https://github.com/SwissDataScienceCenter/renku-notebooks/issues/1396>`_)
* 📜 **UI**: Show commit messages in the commit selection dropdown when
  starting a session from a specific commit
  (`#2362 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2362>`_).
* 🔗 **UI**: Make searches shareable by storing parameters in the URL
  (`#2351 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2351>`_).
* 📸 **UI**: Customize avatars when creating a project
  (`#2331 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2331>`_).

**🐞 Bug Fixes**

* **Gateway**: Use offline access tokens for automated access from within sessions.
  (`#632 <https://github.com/SwissDataScienceCenter/renku-gateway/pull/632>`_).
* **Sessions**: Propagate environment variables for R-Studio sessions
  (`#1339 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1339>`_).
* **UI**: Fix markdown problems with underscores in links and math formulas
  (`#2374 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2374>`_).
* **UI**: Restore session autostart when connecting from the notebook preview page
  (`#2344 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2344>`_).
* **UI**: Improve dataset pages
  (`#2318 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2318>`_,
  `#2357 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2357>`_).
* **UI**: Sort commits by date to prevent random order
  (`#2347 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2347>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

* **Core Service**: Metadata v10 support
* **Knowledge Graph** Add support for the new Renku Metadata Schema v10.
* **Knowledge Graph** Enable the Cross-Entity Search API to allow multiple sort parameters.
* **Knowledge Graph** Remove deprecated GraphQL API
* **Knowledge Graph**: Upgrade Jena to 4.7.0
* **Sessions**: Show if ssh is enabled in /version of notebook service
  (`#1407 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1407>`_).
* **Sessions**: Introduce experimental Azure Blob storage support
  (`#1374 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1374>`_).
* **Sessions**: Enable SSH access via jump host
  (`#1389 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1389>`_).

**Bug Fixes**

* **Sessions**: Cloning the correct SHA for anonymous user sessions
  (`#1406 <https://github.com/SwissDataScienceCenter/renku-notebooks/pull/1406>`_).


Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `amalthea 0.6.1 <https://github.com/SwissDataScienceCenter/amalthea/releases/tag/0.6.1>`_
- `renku-gateway 0.18.1 <https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.18.1>`_
- `renku-graph 2.28.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.28.0>`_
- `renku-notebooks 1.15.0 <https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/1.15.0>`_
- `renku-python 2.0.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.0.0>`_
- `renku-python 2.0.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.0.1>`_
- `renku-python 2.1.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.1.0>`_
- `renku-python 2.2.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.2.0>`_
- `renku-python 2.3.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v2.3.0>`_
- `renku-ui 3.2.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.2.0>`_
- `renku-ui 3.3.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.3.0>`_

0.23.0
------

Renku ``0.23.0`` introduces the Renku Workflow File, a friendlier way to encode and run your data analysis pipelines
on Renku. You can write out your workflow in this easy-to-use YAML file, and execute it with `renku run workflow.yml`. Add a workflow
to your Renku project with our docs `here <https://renku.readthedocs.io/en/stable/topic-guides/workflows/workflow-file.html>`_!

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**🌟 New Features**

* 📝 **Renku CLI**: Add support for workflow files which can be executed directly, greatly improving the UX
  around workflows
  (`#3176 <https://github.com/SwissDataScienceCenter/renku-python/pull/3176>`_).

**✨ Improvements**

* ⚙️ **Renku CLI**: Made `toil` the default workflow backend instead of `cwl`
  (`#3220 <https://github.com/SwissDataScienceCenter/renku-python/issues/3220>`_).
* 💨 **Knowledge Graph**: Performance improvements for Cross-Entities Search and Project Details APIs.
* 🔌 **UI**: Change text on the quick-start session button from "start" to
  "connect" when a session is running
  (`#2268 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2268>`_).

**🐞 Bug Fixes**

* **Renku CLI**: Fixed ``toil`` dependency not being installed correctly.
* **Renku core service**: Fix issue with project templates being cached and users being unable to create projects
  based on the newest version of a template
  (`#3243 <https://github.com/SwissDataScienceCenter/renku-python/issues/3243>`_).
* **UI**: Restrict visibility options to be compatible with namespace and
  parent project in fork dialog
  (`#2299 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2299>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

* **Infrastructure Components**: ``redis`` has been upgraded from version ``6.0.5`` to ``7.0.7``
* **Knowledge Graph**: New provisioning process managing data to be used in the future for further performance improvements of the search APIs.

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.27.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.27.0>`_
- `renku-python 1.11.0 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.11.0>`_
- `renku-python 1.11.1 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.11.1>`_
- `renku-python 1.11.2 <https://github.com/SwissDataScienceCenter/renku-python/releases/tag/v1.11.2>`_
- `renku-ui 3.1.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.1.0>`_

0.22.0
------

Renku ``0.22.0`` introduces a simplified and more powerful search, now
powered by the Renku Knowledge Graph. Use the single integrated search bar
to discover projects and datasets across Renku.

Plus, after a few months away, Project and Dataset images are back! Upload an
image to your project or dataset (via the Settings menu) to add some pop to your
Renku project. You'll notice this change comes with a more compact look to
Project and Dataset page headers, too.

Read on for more detail on UI and Knowledge Graph improvements and bug-fixes
included in this release.

User-Facing Changes
~~~~~~~~~~~~~~~~~~~

**✨ Improvements**

* 🔎 **UI**: Introduce a new cross-entity search page for searching Projects and
  Datasets simultaneously. This search page replaces the separate Projects and
  Datasets pages
  (`#1894 <https://github.com/SwissDataScienceCenter/renku-ui/pull/1894>`_).
* 🔲 **UI**: Improve styling of buttons with menu and group buttons
  (`#2243 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2243>`_,
  `#2284 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2284>`_).
* 🖼️ **UI**: Restore projects and datasets avatars, and add a simple image
  editor.
  (`#2231 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2231>`_,
  `#2246 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2246>`_).
* 📊 **UI**: Update real-time the project's indexing status
  (`#2255 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2255>`_).
* 📑 **UI**: Update documentation links and target the stable release
  (`#2276 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2276>`_).

**🐞 Bug Fixes**

* **UI**: Restore per-language source highlighting in the file preview
  (`#2233 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2233>`_,
  `#2265 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2265>`_).
* **UI**: Limit the available namespaces in which new projects may be created
  to the ones owned by the user to prevent failures
  (`#2187 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2187>`_).
* **UI**: Prevent interface from freezing when interacting with broken sessions
  (`#2269 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2269>`_).
* **UI**: Remove idle time when moving to step two on the starting session page.
  (`#2282 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2282>`_).

Internal Changes
~~~~~~~~~~~~~~~~

**Improvements**

* **Gitlab**: Modify embedded `renku-gitlab` `helm` chart to use internal `redis`.
* **Knowledge Graph**: List project images on responses from Cross-entity search and Project details APIs.
* **Knowledge Graph**: Return more accurate processing details from the Project status API. The payload was updated
  and `contains breaking changes <https://github.com/SwissDataScienceCenter/renku-graph/tree/2.26.0/webhook-service#get-projectsideventsstatus>`_).
* **Knowledge Graph**: Accept `project-id` query parameter on the Event log API.
* **Knowledge Graph**: Allow for greater control on the re-provisioning triggering conditions.
* **UI**: Reduce unnecessary components re-rendering.
* **UI**: Refresh the documentation for developers and external contributors
  (`#2275 <https://github.com/SwissDataScienceCenter/renku-ui/pull/2275>`_).

Individual components
~~~~~~~~~~~~~~~~~~~~~~

- `renku-graph 2.26.0 <https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/2.26.0>`_
- `renku-ui 2.15.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.15.0>`_
- `renku-ui 2.16.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/2.15.0>`_
- `renku-ui 3.0.0 <https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/3.0.0>`_


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

* **Renku CLI**: support moving files between datasets with ``renku mv`` (`CLI documentation <https://renku-python.readthedocs.io/en/stable/reference/commands/move.html>`__).
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

⭐️ CLI: Datasets metadata is editable. Please see the `Dataset documentation <https://renku.readthedocs.io/en/stable/renku-python/docs/reference/commands/dataset.html>`__ for details.

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
