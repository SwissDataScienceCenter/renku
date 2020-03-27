..
    Copyright 2017-2019 - Swiss Data Science Center (SDSC)
    A partnership between École Polytechnique Fédérale de Lausanne (EPFL) and
    Eidgenössische Technische Hochschule Zürich (ETHZ).

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License... raw:: html

RENKU (連句)
============

.. image:: https://github.com/SwissDataScienceCenter/renku/workflows/Deploy%20and%20Test/badge.svg
   :target: https://github.com/SwissDataScienceCenter/renku/actions?query=workflow%3A%22Deploy+and+Test%22

.. image:: https://readthedocs.org/projects/renku/badge/
    :target: http://renku.readthedocs.io/en/latest/
    :alt: Documentation Status

.. image:: https://img.shields.io/discourse/status?server=https%3A%2F%2Frenku.discourse.group
    :target: https://renku.discourse.group/
    :alt: Discourse

.. image:: https://img.shields.io/gitter/room/SwissDataScienceCenter/renku
    :target: https://gitter.im/SwissDataScienceCenter/renku
    :alt: Gitter

.. image:: https://pullreminders.com/badge.svg
    :target: https://pullreminders.com?ref=badge
    :alt: Pull Reminders
    :align: right


**The Renku Project** provides a platform and tools for reproducible and
collaborative data analysis projects. It is aimed at independent researchers and
data scientists as well as labs, collaborations, and courses and workshops.
Renku can be used by anyone who deals with data, whether they are a researcher,
data analyst, project owner, or data provider.

It consists of:

* `Renkulab <https://renku.readthedocs.io/en/latest/introduction/renkulab.html#renkulab>`_,
  a web-based platform designed to facilitate collaboration, reproducibility, and discovery.

* `Renku <https://renku.readthedocs.io/en/latest/introduction/renku.html#renku>`_,
  a command-line interface (CLI) (`docs <https://renku-python.readthedocs.io/en/latest/>`_,
  `repo <https://github.com/SwissDataScienceCenter/renku-python>`_) for managing code, datasets,
  and workflows that you can use on Renkulab or install locally.

A public instance of **Renkulab** is available at https://renkulab.io, and several
other deployments at various institutions. Go ahead - log in, try it out, and
`let us know what you think <http://bit.ly/renku-feedback>`_! You can follow the
`first_steps <https://renku.readthedocs.io/en/latest/tutorials/firststeps.html>`_ tutorial in python or R or `continue reading about the
Renku project <https://renku.readthedocs.io/en/latest/introduction/index.html#renku-introduction>`_.


If you are looking for detailed **renku** command-line interface (CLI) documentation,
consult the `renku-python docs <https://renku-python.readthedocs.io/en/latest/>`_.



Project structure
-----------------

The Renku project consists of several sub-repositories:

- `renku-gateway <https://github.com/SwissDataScienceCenter/renku-gateway>`_:
  a simple API gateway

- `renku-graph <https://github.com/SwissDataScienceCenter/renku-graph>`_:
  Knowledge Graph services

- `renku-notebooks <https://github.com/SwissDataScienceCenter/renku-notebooks>`_:
  a lightweight service for handling interactive notebooks through JupyterHub

- `renku-jupyter <https://github.com/SwissDataScienceCenter/renku-jupyter>`_:
  base images for interactive sessions

- `renku-python <https://github.com/SwissDataScienceCenter/renku-python>`_:
  the Python CLI, SDK, and core backend service

- `renku-ui <https://github.com/SwissDataScienceCenter/renku-ui>`_: web front-end


Contributing
------------

We're happy to receive contributions of all kinds, whether it is an idea for a
new feature, a bug report or a pull request!

Please review our `contributing guidelines
<https://github.com/SwissDataScienceCenter/renku/blob/master/CONTRIBUTING.rst>`_
before submitting a pull request.


Get in touch
------------

* `discourse <https://renku.discourse.group>`_: questions concerning renkulab or
  renku CLI usage, release notes

* github: `renkulab <https://github.com/SwissDataScienceCenter/renku>`_ & `renku
  (CLI) <https://github.com/SwissDataScienceCenter/renku-python>`_: create
  platform-usability and software-bug issues

* `gitter <https://gitter.im/SwissDataScienceCenter/renku>`_: communicate with
  the team


Renku is developed as an open source project by the Swiss Data Science Center in
a team split between EPFL and ETHZ.
