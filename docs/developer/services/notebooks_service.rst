.. _notebooks_service:

Notebooks service
=================

The notebooks service provides an interactive computing environment
for every commit in a project's history. The notebooks are provided by
the JupyterHub server. A new "named" server is spawned for every unique
request. A notebook server launch is initiated by accessing the
``<PLATFORM_URL>/<JUPYTERHUB_PREFIX>/services/notebooks/<namespace>/<project-name>/<commit-sha>``
URL. This means that two users can be directed to the same URL, but each
will receive their own notebook server.

The notebook spawner assumes that the user's project contains a CI job  that
builds a unique image on each push to the server (see the
:ref:`notebooks_image_builds` section below). If such a job exists,  it waits
for the job to complete and then launches a notebook server using  that image.
If the job is not there or there is a problem with the image pull,  a notebook
server is launched with the default notebook image as specified in the
platform configuration optinos.

The diagram below illustrates the sequence of events that take place in order
to launch a new notebook using the notebook service:

.. _fig-uml_notebooks_service:

.. uml:: ../../_static/uml/notebook-sequence.uml
   :alt: Sequence diagram of notebook launch from the UI via the Notebooks Service.


.. _notebooks_image_builds:

Image builds
------------

If the Renku repository contains a ``.gitlab-ci.yml`` file the GitLab instance
that the repository is pushed to will try to execute the commands inside this
file. By default, when a ``renku`` project is initialized, ``.gitlab-ci.yml``,
``Dockerfile``, and ``requirements.txt`` are added to the project. On push to the
server, the GitLab runner (if it is configured) will then build the image
with the name ``<gitlab-registry>/<namespace>/<project-name>:<commit-sha>``.
This way we guarantee that the user will have an image available for every
point in the project's history. In future iterations of these services, the
build process will be optimized to avoid superflous builds and reduce launch
latency to improve the user experience.
