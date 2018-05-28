..
    Copyright 2017-2018 - Swiss Data Science Center (SDSC)
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

**Renku** is a highly-scalable & secure open software platform designed to
foster multidisciplinary data (science) collaboration.

The platform allows practitioners to:

* Securely manage, share and process large-scale data across untrusted
  parties operating in a federated environment.

* Capture complete lineage automatically up to the original raw data for
  detailed traceability (auditability & reproducibility).


Starting the platform
------------------

.. code:: console

    $ make start

Please follow the output for next instructions. If the script successfully
completes, the platform will be up and running. You may now use standard
docker commandline tools like `docker-compose` to interact with the
platform components.

A python CLI and API client is available in the `renku-python
<https://github.com/SwissDataScienceCenter/renku-python>`_ package.

To stop the platform and clean up all the associated containers and volumes,
you may use

.. code:: console

    $ make wipe

You can find more details about running the platform in the `setup
<http://renku.readthedocs.io/en/development/user/setup.html>`_ documentation.

Where to go next
----------------

The full documentation is available at
https://renku.readthedocs.io/en/latest/.

First-time users should try our `first steps
<https://renku.readthedocs.io/en/latest/user/firststeps.html>`_ tutorial.


Contributing
------------

We're happy to receive contributions of all kinds, whether it is an idea for a
new feature, a bug report or a pull request.

Please make sure that the integration tests pass and the documentation builds
without warnings and errors before creating a pull request:

.. code-block:: console

    $ make start
    $ make test

To build the documentation from source:

.. code-block:: console

    $ pip install -r docs/requirements.txt
    $ cd docs && make html


Contact
-------

To submit a bug report or a feature request, please open an issue. For other
inquiries contact the Swiss Data Science Center (SDSC) https://datascience.ch/
