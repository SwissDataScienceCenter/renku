# Renku

[![Discourse](https://img.shields.io/discourse/status?server=https%3A%2F%2Frenku.discourse.group)](https://renku.discourse.group/)
[![Gitter](https://img.shields.io/gitter/room/SwissDataScienceCenter/renku)](https://gitter.im/SwissDataScienceCenter/renku)

**Renku** is an open-source platform that connects the ecosystem of
data, code, and compute to empower researchers to build collaborative
communities. It is aimed at independent researchers and data scientists
as well as labs, collaborations, and courses and workshops.

Once a Renku project is configured, anyone using it can take advantage
of convenient access to data and code within pre-configured
computational sessions. Projects are assembled by piecing together:

- **Data connectors**: connect virtually any cloud-based or on-premise
data source, like S3 buckets, WebDav or SFTP servers. Once you create a
data connector, it can be reused across all of your projects and even be
made available to your group or community.

- **Source code**: link the source code repositories you are used to, like
GitHub or GitLab and easily work on it within your project sessions.

- **Compute environments**: use one of our pre-configure environments with
VSCode, RStudio or Jupyter inside or have Renku build an image to match
your repository\'s requirements. Alternatively, bring your own docker
image and share it easily with others to really make your code and data
sing.

> [!NOTE]  
> Renku is currently being rewritten; the new version, Renku 2.0 will
> replace the \"legacy\" functionality fully in late Spring 2025. All of
> the material linked below refers to the new version.

## Getting Started

A public instance of **RenkuLab** is available at
https://renkulab.io/v2. To start exploring Renku, feel free to make an
account and try it out! You can follow the [hands-on
tutorial](https://renku.notion.site/Hands-On-Tutorial-1a50df2efafc800f8554e30fd7458fa6)
or visit our [Community
Portal](https://renku.notion.site/Renku-Community-Portal-2a154d7d30b24ab8a5968c60c2592d87).

## Documentation

-   [Tutorials](https://renku.notion.site/Renku-2-0-Tutorials-1460df2efafc80c2b27acd221aa34a24):
    how to get your Renku work off the ground
-   [How-to
    Guides](https://renku.notion.site/Renku-2-0-How-To-Guides-900f417fc205439789a9fbdc5cadcec8):
    recipes for common use-cases with Renku for users and administrators
-   [Reference](https://renku.notion.site/Renku-2-0-Reference-874b6f7b83a044598f5bdbf1193cb150):
    various concepts explained in detail.
-   [Renku 2.0 blog post](https://blog.renkulab.io/deep-dive-2-0/): discover what is new in Renku 2.0
-   [\"Legacy\" documentation:](https://renku.readthedocs.org): the
    documentation pages for the previous version of the platform.

## Contributing

We\'re happy to receive contributions of all kinds, whether it is an
idea for a new feature, a bug report or a pull request!

Please review our [contributing
guidelines](https://github.com/SwissDataScienceCenter/renku/blob/master/CONTRIBUTING.rst)
before submitting a pull request.

## Getting in touch

There are several channels you can use to communicate with us; we
monitor all of them, so your messages will always get to us, but
communication will be slightly more streamlined if you pick a channel
that most suits your purpose and needs.

-   [discourse](https://renku.discourse.group): questions concerning
    Renkulab, your feature requests or feedback
-   [github](https://github.com/SwissDataScienceCenter/renku):
    create platform-usability and software-bug issues
-   [gitter](https://gitter.im/SwissDataScienceCenter/renku):
    communicate with the team

Renku is developed as an open source project by the Swiss Data Science
Center in a team split between EPFL and ETHZ.

## Project structure

Renku is built from several sub-repositories:

-   [renku-data-services](https://github.com/SwissDataScienceCenter/renku-data-services):
    backend services providing the majority of the platform user-facing
    functionality.
-   [renku-ui](https://github.com/SwissDataScienceCenter/renku-ui): web
    front-end.
-   [renku-gateway](https://github.com/SwissDataScienceCenter/renku-gateway):
    a simple API gateway.
-   [amalthea](https://github.com/SwissDataScienceCenter/amalthea): k8s
    operator for user session servers.
-   [renkulab-docker](https://github.com/SwissDataScienceCenter/renkulab-docker):
    base images for interactive sessions.
