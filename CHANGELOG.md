# Renku changes


## 0.5.0 (released 2019-11-07)

## New Features

⭐️ Datasets are now displayed inside a Renku project

## Notable improvements

* Changed project URLs to show namespace and name instead of project ID 
* Reworked collaboration view with issues list and collapsing issue pane 
* Enabled search by username and group 🔍
* Fork functionality now allows changing the name 🍴
* Better tools to get information about interactive environments 🕹
* Better consistency with project and interactive environment URLs 🎯


### Miscellaneous
* Commit time is local timezone aware
* Images and project templates now use Renku [0.7.1](https://github.com/SwissDataScienceCenter/renku-python/releases)
* User profile redirects to Keycloak profile
* Simplified deployment with automatic secrets generation


### Individual components

For changes to individual components, check:
* renku ui [0.6.4](https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.6.4), [0.7.0](https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.0) and [0.7.1](https://github.com/SwissDataScienceCenter/renku-ui/releases/tag/0.7.1)
* renku-gateway [0.6.0](https://github.com/SwissDataScienceCenter/renku-gateway/releases/tag/0.6.0)
* renku-python [0.7.1](https://github.com/SwissDataScienceCenter/renku-notebooks/releases/tag/0.7.1)
* renku-graph [0.24.3](https://github.com/SwissDataScienceCenter/renku-graph/releases/tag/0.24.3)

## Bug fixes
* Lineage visualization bugs addressed
* Users with developer permissions can now start an interactive environment 🚀

## Upgrading from 0.4.3
* Update values file according to [the values changelog](https://github.com/SwissDataScienceCenter/renku/blob/master/charts/values.yaml.changelog.md#changes-on-top-of-renku-042)
