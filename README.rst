..
    Copyright 2017 - Swiss Data Science Center (SDSC)
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

RENGA (連歌)
============

The platform allow practitioners to:

* Securely manage, share and process large-scale data across untrusted
  parties operating in a federated environment.

* Capture complete lineage automatically up to original raw data for
  detailed traceability (auditability & reproducibility).


Goal
----

The goal of this project is to provide a simple way to instantiate the
platform using Docker.

Start the platform
------------------

.. code:: bash

    docker-compose up

Exposed ports
~~~~~~~~~~~~~

-  ``80`` all platform APIs through a reverse proxy (Traefik).
-  ``443`` all platform APIs through a reverse proxy (Traefik).
-  ``81`` Traefik monitoring interface.

Additionally, all services expose their port, picked randomly by docker.
Use ``docker-compose ps`` to list them.

Configuration
~~~~~~~~~~~~~

Some variables are defined in the ``.env`` file.

Documentation
-------------

Full documentation is available on https://renga.readthedocs.io/
or it can be build from sources:

::

   pip install -r docs/requirements.txt
   cd docs && make html

Contributing
------------

Please make sure that it is possible to run the integration tests and
build documentation without warnings and errors before creating pull
request.

::

    ./run-tests.sh

Contact
-------

Swiss Data Science Center (SDSC) https://datascience.ch/

