..
    Please see LICENSE file for Copyright and License terms.

.. _renku:

Renku
=====

.. image:: https://readthedocs.org/projects/renku/badge/
    :target: http://renku.readthedocs.io/en/latest/
    :alt: Documentation Status

.. image:: https://img.shields.io/discourse/status?server=https%3A%2F%2Frenku.discourse.group
    :target: https://renku.discourse.group/
    :alt: Discourse

.. image:: https://img.shields.io/gitter/room/SwissDataScienceCenter/renku
    :target: https://gitter.im/SwissDataScienceCenter/renku
    :alt: Gitter

**Renku** is a platform that bundles together various tools for
reproducible and collaborative data analysis projects. It is aimed at
independent researchers and data scientists as well as labs, collaborations, and
courses and workshops. Renku can be used by anyone who deals with data, whether
they are a researcher, data analyst, project owner, or data provider.

Renku promotes **reproducibility** by providing tools to track your analysis
workflows and save them together with your versioned data, code, and
environment specification. Every result can be replayed either to repeat a
calculation or to re-execute on new data or with a different choice of
parameters.

Renku encourages **reusability** by storing and querying the connections between
datasets, code executions, and results in a Knowledge Graph. Producers and
consumers of analysis artifacts can always recover the full provenance of a
result, establishing trust and reducing boilerplate.

Renku stimulates **collaboration** among peers and across disciplines by
guaranteeing that a media-rich discussion space and fully configured, shareable
interactive computational environments are always just a click away.
Collaborators can easily work on projects together or in parallel, combining
their work in a systematic and safe manner.


Getting Started
---------------

Renku consists of `RenkuLab
<https://renku.readthedocs.io/en/latest/introduction/what-is-renku.html#renkulab>`_,
a web-based application and `Renku Client
<https://renku.readthedocs.io/en/latest/introduction/what-is-renku.html#renku-client>`_, a
command-line tool for managing code, data, workflows and making practical use of
the Knowledge Graph.

A public instance of **RenkuLab** is available at https://renkulab.io, and there
are several other deployments at various institutions. To start exploring Renku,
feel free to make an account and try it out! You can follow the `first steps
<https://renku.readthedocs.io/en/latest/tutorials/01_firststeps.html>`_ tutorial
or `continue reading about the Renku project
<https://renku.readthedocs.io/en/latest/introduction/index.html#renku-introduction>`_.

.. _documentation:

Documentation
-------------

* `Tutorials <https://renku.readthedocs.io/en/latest/tutorials.html>`_: how to get your Renku work off the ground
* `Topic Guides <https://renku.readthedocs.io/en/latest/topic-guides/index.html>`_: discussions about concepts central to Renku
* `How-to Guides <https://renku.readthedocs.io/en/latest/how-to-guides/index.html>`_: recipes for common use-cases with Renku for users and administrators
* `Reference <https://renku.readthedocs.io/en/latest/reference/index.html>`_: syntax, structure, architecture, etc.

.. _contributing:

Contributing
------------

We're happy to receive contributions of all kinds, whether it is an idea for a
new feature, a bug report or a pull request!

Please review our `contributing guidelines
<https://github.com/SwissDataScienceCenter/renku/blob/master/CONTRIBUTING.rst>`_
before submitting a pull request.


Getting in touch
----------------

There are several channels you can use to communicate with us; we monitor all of
them, so your messages will always get to us, but communication will be slightly
more streamlined if you pick a channel that most suits your purpose and needs.

* `discourse <https://renku.discourse.group>`_: questions concerning Renkulab or
  Renku CLI usage, release notes

* `github <https://github.com/SwissDataScienceCenter/renku>`_ & `renku
  (CLI) <https://github.com/SwissDataScienceCenter/renku-python>`_: create
  platform-usability and software-bug issues

* `gitter <https://gitter.im/SwissDataScienceCenter/renku>`_: communicate with
  the team

Renku is developed as an open source project by the Swiss Data Science Center in
a team split between EPFL and ETHZ.


Project structure
-----------------

Renku consists of several sub-repositories:

- `renku-gateway <https://github.com/SwissDataScienceCenter/renku-gateway>`_:
  a simple API gateway

- `renku-graph <https://github.com/SwissDataScienceCenter/renku-graph>`_:
  Knowledge Graph services

- `renku-notebooks <https://github.com/SwissDataScienceCenter/renku-notebooks>`_:
  a lightweight service for handling interactive notebooks through Jupyter servers
  and a k8s operator

- `amalthea <https://github.com/SwissDataScienceCenter/amalthea>`_: k8s operator for
  Jupyter servers.

- `renkulab-docker <https://github.com/SwissDataScienceCenter/renkulab-docker>`_:
  base images for interactive sessions

- `renku-python <https://github.com/SwissDataScienceCenter/renku-python>`_:
  the Python CLI, SDK, and core backend service

- `renku-ui <https://github.com/SwissDataScienceCenter/renku-ui>`_: web front-end

- `renku-project-templates <https://github.com/SwissDataScienceCenter/renku-project-template>`_:
  base templates used for instantiating renku projects.

- `renkulab-docker <https://github.com/SwissDataScienceCenter/renkulab-docker>`_: docker
  images used for renku sessions.

Test
