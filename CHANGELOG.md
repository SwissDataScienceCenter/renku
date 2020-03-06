# 0.6.0 (released 2020-03-06)

This release includes exciting new features and provides an improved user experience, mostly with respect to dataset handling.

## New features

⭐️ Datasets can be created from the UI

⭐️ Files can be added to a dataset from the UI

⭐️ Datasets can now be exported to [Dataverse](https://dataverse.org/)


## Notable improvements

🚄 Support project-level default settings for environments

🚄 Relevant project/namespace information is shown at `/projects/user-groupname/` path

🚄 Cleanup error messages for Renku CLI usage

🚄 Dataset importing is faster with Renku CLI

🚄 Restructured our [documentation](https://renku.readthedocs.io/)

### Miscellaneous

* R-markdown rmd files can be visualized within Renkulab ✔️
* Group avatars are displayed 👤
* Improved presentation for merge request and issues
* A Gitlab IDE link has been made available for working with Renku projects
* Link to see a project's fork information

* Docker images and project templates now use Renku [0.9.1](https://github.com/SwissDataScienceCenter/renku-python/releases)
* A Renku docker image with [Bioconductor](https://github.com/Bioconductor/bioconductor_docker) is now available 📣
* R projects now have the directory structures fixed
* Python now comes with powerline to simplify the command line prompt

* Jupyterhub has been updated to version 1.1.0

* Prometheus metrics available for graph services

## Bug fixes

* LFS data is now retrieved when the checkbox is selected 🐞
* Close the fork dialog after forking
* Various fixes for lineage including performance

### Individual components

For changes to individual components, check:
* renku ui [0.7.3](https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.3) and [0.8.0](https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.8.0)
* renku-gateway [0.7.0](https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.7.0)
* renku-python [0.9.0](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.9.0) and [0.9.1](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.9.1)
* renku-graph [0.48.0](https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/0.48.0)
* renku-notebooks [0.6.2](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.6.2)


## Upgrading from 0.5.2

* No changes required in the values file for this upgrade


# 0.5.2 (released 2020-01-17)

This is a minor update. It contains mostly bug fixes.

## New features

⭐️ Support pagination in display of datasets

## Bug fixes

* Correctly handle nested groups
* Progress bar for Knowledge Graph is not stuck at 0% ([#203](https://github.com/SwissDataScienceCenter/renku-graph/issues/203))
* Knowledge graph supports emails with special chars ([#223](https://github.com/SwissDataScienceCenter/renku-graph/issues/223))

### Individual components

For individual component changes, check:

* renku ui [0.7.3](https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.3)
* renku-graph [0.39.0](https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/0.39.0) through [0.30.0](https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/0.30.0)


# 0.5.1 (released 2019-12-04)

This is a bugfix release that updates the GitLab version required to allow changing the project name when forking (see [#616](https://github.com/SwissDataScienceCenter/renku-ui/issues/616) and [#626](https://github.com/SwissDataScienceCenter/renku-ui/issues/626)).

# 0.5.0 (released 2019-11-27)

## New features

⭐️ Datasets are now displayed inside a Renku project

⭐️ Datasets can now be searched within available Renku projects

## Notable improvements

* Changed project URLs to show namespace and name instead of project ID
* Reworked collaboration view with issues list and collapsing issue pane 👥
* Enabled search by username and group 🔍
* Fork functionality now allows changing the name 🍴
* Better tools to get information about interactive environments 🕹
* Better consistency with project and interactive environment URLs 🎯

### Miscellaneous

* Commit time is local timezone aware 🕖
* Images and project templates now use Renku [0.8.2](https://github.com/SwissDataScienceCenter/renku-python/releases)
* A Renku docker image with CUDA, Tensorflow and Tensorboard is now available 📣
* User profile redirects to Keycloak profile 👤
* Simplified deployment with automatic secrets generation ✔️

### Individual components

For changes to individual components, check:
* renku ui [0.7.2](https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.2), [0.7.1](https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.1), [0.7.0](https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.0) and [0.6.4](https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.6.4)
* renku-gateway [0.6.0](https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.6.0)
* renku-python [0.8.2](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.2), [0.8.1](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.1), [0.8.0](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.8.0), [0.7.2](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.7.2) and [0.7.1](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.7.1)
* renku-graph [0.29.3](https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/0.29.3)
* renku-notebooks [0.6.2](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.6.2), [0.6.1](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.6.1) and [0.6.0](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.6.0)

## Bug fixes

* Lineage visualization bugs addressed 🐞
* Users with developer permissions can now start an interactive environment 🚀

## Upgrading from 0.4.3

* Update values file according to [the values changelog](https://github.com/SwissDataScienceCenter/renku/blob/master/charts/values.yaml.changelog.md#changes-on-top-of-renku-042)
