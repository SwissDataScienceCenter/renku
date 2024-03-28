.. _upgrading_renku:

Upgrade your Renku project
============================

Renku is in constant flux! This means that often there are changes
to RenkuLab (e.g. the web platform (UI) and the knowledge graph (KG)), and the
``renku`` command-line interface (CLI). These docs will explain how to benefit from
changes to the ``renku CLI`` when you're using a session on RenkuLab.
You can find the changes `in the renku-python docs <https://renku-python.readthedocs.io/en/latest/changes.html>`_.

If you're instead looking for a list of new features available in RenkuLab, they
are announced in the :ref:`release_notes`. When we update components of the
RenkuLab platform, you won't usually have to do anything special. E.g. when we
added the ability for anonymous (not logged in) users to launch sessions, 
each project was immediately accessible without the project owner
needing to change any settings.

.. _renku_cli_upgrade:

Upgrading the renku CLI version used in your sessions
-----------------------------------------------------

.. note::

  This section is for upgrading the version of ``renku`` CLI installed into
  the Sessions on RenkuLab for your project. See :ref:`upgrading_local`
  for upgrading your local machine's version of ``renku``.

When we release a new version of the ``renku`` CLI, you do have to make some
(minimal) changes to the ``Dockerfile`` in your project to use it in your interactive
sessions on RenkuLab.

The version of the ``renku`` CLI is defined in the base image specified in the
``ARG RENKU_VERSION`` line of the ``Dockerfile`` in your project repo (if you 
don't see this line in your ``Dockerfile`` your project was made using an older template).
All you need to do to have a different version of the Renku CLI installed is modify the version 
on that line and push the change. Once that is done, the image will automatically
be built as usual and include the new version. 

.. _renku_base_image_upgrade:

Upgrading the base image used for interactive sessions
------------------------------------------------------

In addition to updating the CLI version, you may also want to update the base image 
used for sessions in your project. We periodically release new base images with 
upgrades to underlying libraries and packages. The base image is specified 
on the ``ARG RENKU_BASE_IMAGE`` line in your ``Dockerfile``, for example it might
read ``renku/renkulab-py:3.9-0.11.0``. This means that it is a python-based image 
using Python 3.9 and image release ``0.11.0``. To change it, simply modify the base image
referenced on that line. For a list of base images see
`current images <https://github.com/SwissDataScienceCenter/renkulab-docker#current-images>`_.
When choosing an updated image, try to stick to released versions like `renku/renkulab-py:3.9-0.11.0` 
and not ones that include a commit hash like `renku/renkulab-py:3.9-14f93c5`. Those are 
to be considered "development" versions. 
Note: changing the base image does not automatically mean that the Renku CLI version will 
also change - see the section above for details on how to update it. 
