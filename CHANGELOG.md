# Renku changes

## 0.5.1 (released 2019-12-04)

This is a bugfix release that updates the GitLab version required to allow changing the project name when forking (see [#616](https://github.com/SwissDataScienceCenter/renku-ui/issues/616) and [#626](https://github.com/SwissDataScienceCenter/renku-ui/issues/626)).

## 0.5.0 (released 2019-11-27)

## New Features

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
