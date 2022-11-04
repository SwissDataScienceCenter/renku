# Releases

There are three types of Renku releases: Sprint release, Hot/bug fix release and Quick release.

## Sprint release

A sprint release is planned on the Tuesday after the end of every sprint at 12:00
(every three weeks).
The current state of the main branch is released as is, only checking the
wording in the changelog to ensure it is of good quality and adding the version
information.

### Pre-requisites [Graph, UI, RP]

Component maintainers can create a release whenever they want, the schedule is up to them. They are
responsible for updating the changelog in the auto-PR in the Renku repository,
in a section above the one for the most recent release.
The auto-PR is only merged with an updated changelog/release notes.

If two components need to bee coordinated for a release (e.g. new version
of Graph needs new version of RP), they should combine their auto-PRs and
merge them in a single go. Generally, components should strive to keep
dependencies between them to a minimum and coordinated component releases
should not usually be required (using API versioning, feature flags etc.).

If a merged auto-PR that has not yet been included in a Renku release turns
out to be faulty, it has to be reverted in the Renku repository so it is not
accidentally included in a release. A new auto-PR with the fix can then be
created. The sprint release DOES NOT wait for a fix.

If a regression made it into a release, the fix must add an acceptance test
that would catch it, within reason.

Each sprint one of the teams takes on the responsibility of pushing out the release and makes a PR with updates 
relevant _and adapted_ to USERS and ADMINS in the `CHANGELOG.rst` file.  Before tagging a release, ask the 
Product Manager to review the release notes for communication about user-facing changes. 

### Process [YAT]

* Roll out the release: create one PR per deployment in the `terraform-renku` repository to update the version and make any needed configuration changes. Important: if needed, add the `scheduled maintenance` label to help avoiding it being merged too early. Limited should be rolled out before Renkulab, as a canary release (see below).
* Plan a maintenance, create it in statuspage for all Renku deployments managed by us. Coordinate with external Renku deployments admins (UniFR, SV).
  * Limited should be rolled out ~1 week before the Renkulab rollout, to give a chance to find bugs early. E.g. Limited on Wednesday and Renkulab on Monday.
* Make the tag in the `renku` repository, monitor the automatic process of chart publishing and deployment on staging.
* Merge terraform-renku PR, monitor Flux upgrading Renku everywhere.
* If relevant, notify users on Discourse, Twitter using visual tools and short sentences. Coordinate with the Product Manager.

## Hot/bug fix release

This is an unplanned release. It happens when a component has a bug that needs an **immediate** fix and rollout.
In some cases the fix might be deployed in place before a proper release is made.

### Pre-requisites [Graph, UI, RP]

Announce as soon as it is needed.

* Create a Pull Request in the `renku` repository with updates relevant -and adapted- to USERS and ADMINS in the `CHANGELOG.rst` file.
* Create a Pull Request in the `terraform-renku` repository to update the version and make any needed configuration changes. 
* Approve and merge release Pull Request, tag new release.

### Process [YAT]

* Plan a maintenance, create it in statuspage for all Renku deployments managed by us. Coordinate with externally managed Renku deployments (Champak UniFR, Nicolas SV).
* Merge terraform-renku PR.

## Quick release

A non-urgent release is requested to be made in between sprints by any of the teams.

### Pre-requisites [Graph, UI, RP]

* Component update should not incur in downtime, other than the restart of the updated component.
* Announce half a day in advance.
* Create a Pull Request in the `renku` repository with updates relevant -and adapted- to USERS and ADMINS in the `CHANGELOG.rst` file.
* Approve and merge release Pull Request in Renku repository, tag new release.
* Create a Pull Request in the `terraform-renku` repository to update the version and make any needed configuration changes.  

### Process [YAT]

* Plan a maintenance, create it in statuspage for all Renku deployments managed by us. Coordinate with externally managed Renku deployments (UniFR, SV).
* Merge Pull Request in terraform-renku repository.
