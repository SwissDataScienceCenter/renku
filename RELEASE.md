# Releases

There are three types of Renku releases: Sprint release, Hot/bug fix release and Quick release.

## Sprint release

A sprint release is planned on the Tuesday after the end of every sprint
(every three weeks).
The current state of the main branch is released as is, only checking the
wording in the changelog to ensure it is of good quality and adding the version
information.

### Update components [Graph, UI, RP]

🧑‍🏭 Component maintainers can tag a release in their local repositories whenever
they want, the schedule is up to them. They are responsible for updating the
`CHANGELOG.rst` file in the auto-PR in the Renku repository.
Please create a new section above the last release if no one has already done it.
The auto-PRs **must** be merged with an updated changelog/release notes. If upgrading 
a service will knowingly result in an extended outage (e.g. because of a DB migration)
this *must* be clearly noted and highlighted in the release notes. Use emojis freely. 

🧑‍🤝‍🧑 If two components need to coordinate for a release (e.g. new version
of Graph needs new version of RP), they should combine their auto-PRs and
merge them in a single go. Generally, components should strive to keep
dependencies between them to a minimum and coordinated component releases
should not usually be required (using API versioning, feature flags etc.).

⚠️ If a component turns out to be faulty, please revert to the previous version
as soon as possible to prevent accidentally including it in the next Renku release.
The sprint release DOES NOT wait for a fix.
If a regression made it into a release, remember to add an acceptance test to
prevent this from happening again.

🕛 The deadline for this is Tuesday morning after the sprint. Please be on time!


### Tag a release [Product team]

🛳️ The product team updates the `CHANGELOG.rst` file when needed to make it more
user friendly and potentially highlight relevant features or disrupting changes.
This process can start on Tuesday after 12:00. As soon as the release PR is merged, 
the product team tag a release and inform YAT for the rollout.


### Deploy [YAT]

* Roll out the release: one PR per deployment is automatically created in the `terraform-renku` repository to update the version. Make any needed configuration changes to the PR. Approve and merge the PR to deploy. Important: if needed, add the `scheduled maintenance` label to help avoiding it being merged too early. Limited should be rolled out before Renkulab, as a canary release (see below).
* Plan a maintenance, create it in statuspage for all Renku deployments managed by us. Coordinate with external Renku deployments admins (UniFR, SV).
* Limited should be rolled out ~1 week before the Renkulab rollout, to give a chance to find bugs early. E.g. Limited on Wednesday and Renkulab on Monday.
* Make the tag in the `renku` repository, monitor the automatic process of chart publishing and deployment on staging.
* Merge terraform-renku PR, monitor Flux upgrading Renku everywhere.
* After the Limited and RenkuLab deployments are completed, notify the Product team that the deployments are live, so they can post the release Highlights.

### Share Release Highlights [Product team]
* On Wednesday with the Limited release, share release highlights with SDSC (Slack).
* On Monday with the RenkuLab release, highlight prominent changes on Discourse, Twitter, and other channels.


## Hot/bug fix release

This is an unplanned release. It happens when a component has a bug that needs an **immediate** fix and rollout.
In some cases the fix might be deployed in place before a proper release is made.

### Pre-requisites [Graph, UI, RP]

Announce as soon as it is needed.

* Create a Pull Request in the `renku` repository and update the `CHANGELOG.rst` file.
  Ask the Product team for a review on the PR in Renku repository and tag the new release. 
* A Pull Request in the `terraform-renku` repository is automatically created to update the version. Make any needed configuration changes to this PR.
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
* Ask the Product team for a review on the PR in Renku repository and tag the new release.
* Create a Pull Request in the `terraform-renku` repository to update the version and make any needed configuration changes.  

### Process [YAT]

* Plan a maintenance, create it in statuspage for all Renku deployments managed by us. Coordinate with externally managed Renku deployments (UniFR, SV).
* Merge Pull Request in terraform-renku repository.
