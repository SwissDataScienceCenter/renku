.. _renkulab:

Renkulab
--------

Renkulab is a web platform for creating, storing, working on, and sharing your
data science projects. Our public "flagship" deployment of Renkulab can be found
at renkulab.io_ and is free for anyone to use.

Renkulab is the glue that makes it possible to develop and share your research entirely
in the cloud. You can, directly from a project's homepage on Renkulab, launch Jupyterlab
and RStudio sessions (or anything else you might run from a Docker container) using
pre-built templates. You can work on your project and when you are done
push the changes back to the repository for safe storage. Our pre-built base images
also contain the ``renku`` command-line tool so you don't need to worry about installation
and benefit from lineage tracking right in the interactive session.

Renkulab automatically builds images for your interactive sessions so the environments
you or your collaborators use are always up-to-date.

Please contact us if you would like to deploy your own instance or see
:ref:`admin_documentation`.


Gitlab
~~~~~~

* storing project repositories
* storing each project's large files in a common git LFS object store
* running CI/CD to rebuild Docker images and send them to the image registry when new commits are pushed
* managing groups & permissions

Jupyterlab & RStudio
~~~~~~~~~~~~~~~~~~~~

* Interactive environments that you can launch from a project's home page on renkulab
* push changes back to renkulab's gitlab
* project-level configurations: i.e. container #CPU/GPU, memory
* user-level configurable environment: i.e. .bashrc

Knowledge Graph
~~~~~~~~~~~~~~~

* projects search: public projects and private projects for which you have access rights
* datasets search: datasets that are created/added to a renku project are made discoverable
* (behind the scenes) knowledge graph database that ingests commits from a project when they are pushed to renkulab's gitlab
* Lineage visualization uses this knowledge graph to display a lineage for each file that has been touched by a renku command


.. _renkulab.io: https://renkulab.io
