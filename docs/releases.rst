.. _releases:

Releases
=========

Next planned releases
---------------------

* Sprint June 10 - June 30 2022 release: July 5th

* Sprint July 1 - July 21 2022 release: July 26th

* Sprint July 22 - August 11 2022 release: August 16th

There are three types of Renku releases: Sprint release, Hot/bug fix release and Quick release.

Sprint release
---------------

A new release is planned after the end of every sprint (every three weeks).
Specifically the **Tuesday after**, to give enough time for preparing component releases and their pipelines.

Pre-requisites [Graph, UI, RP]
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* Release corresponding Renku component

* Adapt tests/documentation in renku repository as needed. Contribute to the Release Notes in the dedicated open Pull Request.

* Verify the new release has been merged into repository renku main branch

Process [YAT]
~~~~~~~~~~~~~

* Make a Pull Request in renku repository with updates relevant -and adapted- to USERS and ADMINS in the CHANGELOG.md file. Ask ORDES for review when necessary.

* Rollout updates

    * For non-breaking changes: create a pull request in terraform-renku repository with the changes. Important: add the scheduled maintenance label to help avoiding it being merged before time.

    * For breaking changes: create more than one PR to upgrade at different times depending on the case.

* Plan a maintenance, create it in statuspage for all Renku deployments managed by us. Coordinate with external Renku deployments admins (UniFR, SV).

* Make the tag in renku repository, follow process of chart publishing and deployment on staging.

* Merge terraform-renku PR, monitor Flux upgrading renku everywhere.

* If relevant, notify users on discourse, twitter using visual tools and short sentences. Coordinate with ORDES.

Hot/bug fix release
-------------------

This is an unplanned release. It happens when a component has a bug that needs an **immediate** fix and rollout.
In some cases the fix might be deployed in place before a proper release is made.

Pre-requisites [Graph, UI, RP]
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Announce as soon as it is needed.

* Create a Pull Request in renku repository with updates relevant -and adapted- to USERS and ADMINS in the CHANGELOG.md file.

* Create a Pull Request in terraform-renku repository with the changes. See `this example <https://github.com/SwissDataScienceCenter/terraform-renku/pull/557>`__.

Process [YAT]
~~~~~~~~~~~~~

* Approve and merge release Pull Request, tag new release.

* Plan a maintenance, create it in statuspage for all Renku deployments managed by us. Coordinate with externally managed Renku deployments (Champak UniFR, Nicolas SV).

* Merge terraform-renku PR.

Quick release
-------------

A non-urgent release is requested to be made in between sprints by any of the teams.

Pre-requisites [Graph, UI, RP]
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* Component update should not incur in downtime, other than the restart of the updated component.

* Announce half a day in advance.

* Create a Pull Request in renku repository with updates relevant -and adapted- to USERS and ADMINS in the CHANGELOG.md file.

* Create a Pull Request in terraform-renku repository with the changes. See `this example <https://github.com/SwissDataScienceCenter/terraform-renku/pull/557>`__. Important: add the scheduled maintenance label to help avoiding it being merged before time.

Process [YAT]
~~~~~~~~~~~~~

* Approve and merge release Pull Request in Renku repository, tag new release.

* Plan a maintenance, create it in statuspage for all Renku deployments managed by us. Coordinate with externally managed Renku deployments (UniFR, SV).

* Merge Pull Request in terraform-renku repository.
