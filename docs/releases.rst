.. _releases:

Releases
=========

There are three types of Renku releases: Sprint release, Hot/bug fix release and Quick release.

Sprint release
---------------

A new release is planned on the Tuesday after the end of every sprint at 12:00
(every three weeks).
The current state of the main branch is released as is, only checking the
wording in the CHANGELOG to ensure it is of good quality and adding the version
information.

Pre-requisites [Graph, UI, RP]
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Components release whenever they want, the schedule is up to them. They are
responsible for updating the changelog in the auto-PR in the renku repository,
in a section above the one for the most recent release.
The auto-PR is only merged with an updated changelog/release notes.

If two components need to bee coordinated for a release (e.g. new version
of Graph needs new version of RP), they should combine their auto-PRs and
merge them in a single go. Generally, components should strive to keep
dependencies between them to a minimum and coordinated component releases
should not usually be   using API versioning, feature flags etc.).

If a merged auto-PR that has not yet been included in a Renku release turns
out to be faulty, it has to be reverted in the renku repository so it's not
accidentally included in a release. A new auto-PR with the fix can then be
created. The main release DOES NOT wait for a fix.

If a regression made it into a release, the fix must add an acceptance test
that would catch it, within reason.

Process [YAT]
~~~~~~~~~~~~~

* Make a Pull Request in renku repository with updates relevant -and adapted- to USERS and ADMINS in the CHANGELOG.md file. Ask the product manager for review when necessary.

* Rollout updates

    * For non-breaking changes: create one PR per deployment in terraform-renku repository with the changes. Important: add the scheduled maintenance label to help avoiding it being merged before time. Limited should be rolled out before Renkulab, as a canary release (see below).

    * For breaking changes: create more than one PR to upgrade at different times depending on the case.

* Plan a maintenance, create it in statuspage for all Renku deployments managed by us. Coordinate with external Renku deployments admins (UniFR, SV).

    * Limited should be rolled out at ~1 week before the Renkulab rollout, to give a change to find bugs early. E.g. Limited on Wednesday and Renkulab on Monday.

* Make the tag in renku repository, follow process of chart publishing and deployment on staging.

* Merge terraform-renku PR, monitor Flux upgrading renku everywhere.

* If relevant, notify users on discourse, twitter using visual tools and short sentences. Coordinate with the product manager.

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
