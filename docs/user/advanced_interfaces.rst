.. _advanced_interfaces:

Advanced Interfaces
===================

Python and R are two of the most common programming languages for data science,
and correspondingly JupyterLab and RStudio are common IDEs (Interactive Development
Environments) for exploratory research. In Renku, we provide project `templates <templating>`_
to easily set up one of these environments.

But what if you want to use a different interface, like MATLAB? Or what if you
want to install software that requires extra non-python or R dependencies on the
machine?

Dockerfile modifications
^^^^^^^^^^^^^^^^^^^^^^^^

In Renku we use Docker for containerization. While we
have a set of defaults that we build into a minimal Python and R images
image, there are several reasons why you might want to build on top of these or
write your own entirely.

Dockerfile structure
""""""""""""""""""""

The project's ``Dockerfile`` lives in the top level of the project directory. In
the default ``Dockerfile`` provided in the template, the first line is a ``FROM``
statement that specifies a `versioned base docker image <https://github.com/SwissDataScienceCenter/renku-jupyter>`_.
We add new versions periodically, but the heart of it is the set of installations
of jupyterlab/rstudio, git, and renku::

  FROM renku/singleuser:0.3.5-renku0.5.2

  # or, for RStudio in the build

  FROM renku/singleuser-r:0.3.5-renku0.5.2

The next two statements install user-specified libraries from ``environment.yml``
and ``requirements.txt``::

  # install the python dependencies
  COPY requirements.txt environment.yml /tmp/
  RUN conda env update -q -f /tmp/environment.yml && \
  /opt/conda/bin/pip install -r /tmp/requirements.txt && \
  conda clean -y --all && \
  conda env export -n "root"

You can add to this ``Dockerfile`` in any way you'd like.

.. _docker_dev:

Dockerfile development
""""""""""""""""""""""

If you're going to be making simple modifications to the ``Dockerfile`` (i.e. changing
the base Docker image version number), you can use the following steps to update
and re-build the image:

#. On the project's landing page, click the **View in GitLab** button in the upper righthand corner (opens a new tab by default).
#. In GitLab, click the **Repository** tab in the lefthand column, which drops you into the **Files** tab.
#. Click the **Dockerfile** out of the list of files that appears, and click **Edit** (top right, near the red Delete button. Don't click the red Delete button.)
#. Make your edits in this window.
#. When you're satisfied with the edits, scroll down and write a meaningful **commit message** (you'll thank yourself later).
#. Click the green **Commit changes** button.

Now you've committed the changes to your ``Dockerfile``. Since you've made a commit,
the CI/CD pipeline will kick off (pre-configured for you as a ``renkulab-runner``
inside the GitLab CI/CD settings). It will attempt to rebuild your project with
the new contents of your ``Dockerfile`` based on the configuration in ``.gitlab-ci.yml``,
a file at the top level of your project directory.

The contents of ``.gitlab-ci.yml`` show you that in the build stage, we pull
the docker image for Renku, build our new image out of our ``Dockerfile``
with a tag relating to the commit, and push it.

Let's monitor this process:

#. Click the **CI/CD** > **Jobs** tab.
#. Click the latest status that corresponds to the changes to the ``Dockerfile`` you just made (probably "running", unless it's already "completed" or "failed").

This is the log file from the build process specified in the ``.gitlab-ci.yml``
file. If it succeeds, there will be a green **passed** status, and the end of
log will be a green **Job succeeded**. If the build instead failed, you can use
the messages in the log to determine why and hint at what you can do to fix it.

.. warning::

  Note that the settings have been configured for this build to time out and fail
  after one hour. While a long running build might be indicative of a bug in your
  ``Dockerfile``, it's possible that your build might take a long time. If this is the
  case, you can change these settings via the lefthand column of the GitLab
  interface at **Settings** > **CI/CD** > **General pipelines** > **Timeout**.

Using your new Dockerfile
"""""""""""""""""""""""""

Passing CI/CD is great, but in order to use the new image you need to
(re)start your interactive environment.

To do this, go back to the Renku platform, and from the project's landing page,
first check in the **Files** tab that your changes to the ``Dockerfile`` are present.
If not, you can force-refresh the page. Then, go to the **Notebook servers** tab.
If you have any running notebooks, those will remain built with the older version(s)
of the ``Dockerfile``. You can **Start new server** and **Launch server** to start
a notebook with the latest image.

If the server launches, test it to make sure that the extra functionality you
added in the ``Dockerfile`` is present in the container. If it isn't, you can go back
to the GitLab interface and continue to make changes until you are satisfied.

Looking to make more extensive modifications? Build running too long? Check out
the `next section <_more_extensive_docker>`_.

.. _more_extensive_docker:

For more extensive modifications
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If you want to make more extensive modifications, say ones that would require
longer build times, you may wish to test the docker build on your own machine.
You can follow the `docker tutorial <https://docs.docker.com/get-started/>`_
to get set up and learn how to build and test local images.

Once you have a local docker setup, you can clone your project locally (if you
haven't set up an SSH key from GitLab you'll need to do this), make modifications
to the ``Dockerfile``, and ``docker build`` and ``docker run`` to test your changes.
To test whether your docker image will work, try running it with::

  docker run --rm -ti -p 8888:8888 <image> jupyter lab --ip=0.0.0.0

.. warning::

  You need to install ``jupyter`` and ``jupyterhub`` into the image to be able to 
  start notebook servers on renkulab.io.

You can commit these changes and push to the
repo. Then, follow the rest of the steps in :ref:`docker_dev`.

Note that by default there are two choices for the ``Dockerfile`` (chosen at project
creation time via "python base" or "R base") for the base image, located here:

* a `JupyterLab base <https://github.com/SwissDataScienceCenter/renku-jupyter/tree/master/docker/base>`_ (with renku installed on top)
* a `rocker (R + RStudio) base <https://github.com/SwissDataScienceCenter/renku-jupyter/tree/master/docker/r>`_ (with conda and renku installed on top)

These two images are pushed into `dockerhub <https://hub.docker.com/r/renku/>`_.

If you can't work with the template ``Dockerfile`` provided, you can pull one of
these base ``Dockerfile`` s and add the ``renku``, ``git``, and ``jupyter`` parts
to another base image that you might have.

Examples
^^^^^^^^

* Matlab via VNC

Coming soon.
