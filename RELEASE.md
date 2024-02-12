# Releases

There are two types of Renku releases: planned and unplanned.

A release consists of: 

* Helm chart changes to reflect the new versions of individual components
* Changes to `CHANGELOG.rst`
* A new tag in this repository, which results in packaging and pushing out new helm charts and corresponding docker images

This procedure should be followed for *any* release:

* Create a release branch (e.g. `0.46.x`), if one does not already exist, with the [release action](https://github.com/SwissDataScienceCenter/renku/actions/workflows/create-release-branch.yml).
* Create a `CHANGELOG` entry for the release and open a PR; create a deployment, this is the reference for the release.
* Note that any PR that should go into the release needs to target the release branch _not_ `master`.
* All release branches should be protected.
* Use the "Rebase and Merge" button to merge release branches into `master`; do not squash commits.

Acceptance tests have to pass on all release branches before merging. 

## Planned release

A release is planned either at the beginning of a build cycle or at the start of the cooldown period. 
Depending on the features being developed, there may be more than one release per build cycle, especially 
if a feature requires significant infrastructure changes. 

For larger features that require the coordination of several teams, consider using 
a feature branch to which the individual component updates can be merged. This feature
should then get merged into the release branch once the feature is complete. 

Note that there might be multiple planned release branches and PRs active at the same time. 

## Bugfix release

The procedure for a bugfix should be more or less the same as for a planned release. If a branch already exists for the 
correct *minor* version, e.g. `0.46.x` for a release `0.46.0`, start from that branch to create `0.46.1`. Care must 
be taken to rebase or cherry-pick any relevant additions from `master` into that branch. Do not rush this and please
communicate with others on the team to make sure we are not a) accidentally releasing new things with a bugfix and b)
slapping together incompatible changes. 

Once the bugfix is merged to `master`, make sure that it can propagate seamlessly to the other currently active release 
branches.

## Maintenance PRs

Maintenance PRs (e.g. library updates) should generally target the next release branch. 

## Make the release [Product team]

🛳️ Once the work on the release branch is completed: 

* The product and yat teams should review the PR, paying attention to the changelog  
* The product team updates the `CHANGELOG.rst` file when needed to make it more user friendly and potentially highlight relevant features or disrupting changes. 
* Once the PR is merged, a GitHub release and corresponding tag should be made.

## Rollout [YAT]

* Before _any_ rollout is done, consider whether any of the upgrades will result in a longer-than-normal outage. If yes, create appropriate maintenance windows marking specific components as affected. Coordinate maintenance windows with external Renku deployments admins (UniFR, SV).
* Roll out the release: one PR per deployment is automatically created in the `terraform-renku` repository to update the version. Make any needed configuration changes to the PR. Approve and merge the PR to deploy. Important: if needed, add the `scheduled maintenance` label to help avoiding it being merged too early. Limited should be rolled out before Renkulab, as a canary release (see below).
* Limited should be rolled out ~1 week before the Renkulab rollout, to give a chance to find bugs early. E.g. Limited on Wednesday and Renkulab on Monday.
* Make the tag in the `renku` repository, monitor the automatic process of chart publishing and deployment on staging.
* Merge terraform-renku PR, monitor Flux upgrading Renku everywhere.
* After the Limited and RenkuLab deployments are completed, notify the Product team that the deployments are live, so they can post the release Highlights.
