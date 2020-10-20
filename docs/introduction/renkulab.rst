.. _renkulab:

Renkulab
========

Renkulab is a web platform for creating, storing, working on, and sharing your
collaborative data analysis projects. Our public "flagship" deployment of
Renkulab can be found at renkulab.io_ and is free for anyone to use.

Renkulab is the glue that makes it possible to develop and share your research
entirely in the cloud. You can, directly from a project's homepage on Renkulab,
launch JupyterLab and RStudio sessions (or anything else you might run from a
Docker container) using pre-built templates. You can work on your project and
when you are done push the changes back to the repository for safe storage. Our
pre-built base images also contain the ``renku`` command-line tool so you don't
need to worry about installation and benefit from lineage tracking right in the
interactive session.

Renkulab automatically builds images for your interactive sessions so the
environments you or your collaborators use are always up-to-date.

The main components that make up a Renkulab instance are `GitLab
<https://gitlab.com>`_ for repository management and version control,
`JupyterHub <https://jupyter.org>`_ for interactive sessions, a Knowledge Graph
for search and discovery, and a few custom services for all renku-specific tasks
like handling datasets.

Please contact us if you would like to deploy your own instance or see
:ref:`admin_documentation`.

.. _renkulab.io: https://renkulab.io
