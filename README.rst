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

.. image:: https://img.shields.io/travis/SwissDataScienceCenter/renku.svg
   :target: https://travis-ci.org/SwissDataScienceCenter/renku

.. image:: https://readthedocs.org/projects/renku/badge/
    :target: http://renku.readthedocs.io/en/latest/
    :alt: Documentation Status

.. image:: https://pullreminders.com/badge.svg
    :target: https://pullreminders.com?ref=badge
    :alt: Pull Reminders
    :align: right

**Renku** is a software platform designed to enable reproducible and collaborative data science.

The platform:

    * Gives data scientists easy access to configurable, reproducible
    interactive environments

    * Allows data scientists to make their analyses reproducible by
    automatically capturing the lineage of results in a Knowledge Graph

    * Provides tools for working with the Knowledge Graph for the purposes of
    traceability and auditability


If you are a data scientist looking to get started with Renku, head to the
`first steps tutorial
<https://renku.readthedocs.io/en/latest/user/firststeps.html>`_ for a quick look
at how Renku can be used for a data science project. To read up on the concepts
behind Renku, you may go straight to the `Renku concepts
<https://renku.readthedocs.io/en/latest/introduction/index.html#renku-concepts>`_
pages.

If you are a developer or a service provider, have a look at the
`developer documentation
<https://renku.readthedocs.io/en/latest/developer/index.html>`_ for information
about installation, deployment, and architecture.

To try out Renku, you can use our beta deployment at `renkulab.io <https://renkulab.io>`_


Project structure
-----------------

This repository only hosts the deployment charts and the documentation. The magic
happens in these sub-repositories:

- `renku-gateway <https://github.com/SwissDataScienceCenter/renku-gateway>`_: a
simple API gateway

- `renku-graph <https://github.com/SwissDataScienceCenter/renku-graph>`_:
Knowledge Graph services

- `renku-notebooks
<https://github.com/SwissDataScienceCenter/renku-notebooks>`_: a lightweight
service for handling interactive notebooks through JupyterHub

- `renku-jupyter <https://github.com/SwissDataScienceCenter/renku-jupyter>`_:
base images for interactive sessions

- `renku-python <https://github.com/SwissDataScienceCenter/renku-python>`_:
the Python CLI and SDK

- `renku-ui <https://github.com/SwissDataScienceCenter/renku-ui>`_: web frontend


Contributing
------------

We're happy to receive contributions of all kinds, whether it is an idea for a
new feature, a bug report or a pull request!

Please review our `contributing guidelines
<https://github.com/SwissDataScienceCenter/renku/blob/master/CONTRIBUTING.rst>`_
before submitting a pull request.


Contact
-------

To submit a bug report or a feature request, please `open an issue
<https://github.com/SwissDataScienceCenter/renku/issues/new>`_. For other
inquiries contact the Swiss Data Science Center (SDSC) https://datascience.ch/.
