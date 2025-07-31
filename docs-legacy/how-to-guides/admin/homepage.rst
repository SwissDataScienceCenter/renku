.. _admin_homepage:

Configure the RenkuLab Homepage
-------------------------------

Homepage
~~~~~~~~

The homepage is shown at the root URL of a RenkuLab instance to a non-logged-in visitor.
(A logged-in visitor sees the ``Welcome`` page.)
By default, this page shows an explanation of the features of RenkuLab, but its content
is configurable by altering the ``renku/values.yaml`` file.

Configuring the content
~~~~~~~~~~~~~~~~~~~~~~~

There are two pieces of information that most deployments will want to customize: the
link to the tutorial and the list of projects to highlight on the homepage.

ui.homepage.tutorialLink
^^^^^^^^^^^^^^^^^^^^^^^^

``ui.homepage.tutorialLink`` contains the URL that is used when a user navigates to
the tutorial. The default is to take the user to the tutorial in the documentation:
https://renku.readthedocs.io/en/latest/tutorials/01_firststeps.html. If your
deployment has a custom tutorial, for example, one hosted on the deployment itself,
you can replace the link to the tutorial.


ui.homepage.showcase
^^^^^^^^^^^^^^^^^^^^

A deployment may want to highlight certain projects that are hosted on it.
The field ``ui.homepage.showcase`` makes that possible. To activate the showcase
section on the homepage, set ``ui.homepage.showcase.enabled`` to ``true``.

There are several configuration settings for the section header.

- ``ui.homepage.showcase.title`` - The title shown for the section
- ``ui.homepage.showcase.description`` - The Markdown description shown for the section

The list of projects shown in the showcase is controlled by the
``ui.homepage.showcase.projects`` field. This is a list, and each element needs
to include the following required field:

- ``identifier`` - The fully qualified name of the project.

By default, the title, description, and image for the showcase card are taken
from the project.

If you wish to override these values, you can use the following optional fields:

- ``overrideTitle`` -- text for the title of the card
- ``overrideDescription`` -- Markdown for the description of the card
- ``overrideImageUrl`` - An URL resolving to an image of any format
  supported by the ``img`` tag, including PNG, JPG, and SVG.

E.g.,::

  showcase:
    enabled: true
    title: Showcase
    description: "A selection of **projects** hosted on this instance."
    projects:
    - identifier: renku-tutorials/e-rum-2020
    - identifier: covid-19/covid-19-forecast
      overrideTitle: "COVID Forcasting"
      overrideDescription: |
        This project contains an attempt to _forecast_ COVID-19 infection rates.
      overrideImageUrl: https://github.com/SwissDataScienceCenter/renku/blob/master/docs/_static/icons/logo.svg


Here is an example of what the showcase section could look like:

.. image:: ../../_static/images/ui_homepage_showcase.png
    :width: 85%
    :align: center
    :alt: Homepage showcase example

Replacing the content
~~~~~~~~~~~~~~~~~~~~~

If the default content is not appropriate for your deployment, it is also possible
to replace what is shown on the homepage.

There are three values that control the main content on the homepage: ``ui.homepage.custom.enabled``,
``ui.homepage.custom.main.contentMd``, and ``ui.homepage.custom.main.backgroundImage.url``

ui.homepage.custom.enabled
^^^^^^^^^^^^^^^^^^^^^^^^^^

``ui.homepage.custom.enabled`` needs to be set to ``true`` to enable overriding the homepage content. If left empty
or set to something else, the customization settings will be ignored and the standard
RenkuLab homepage will be shown.

ui.homepage.custom.main.contentMd
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This variable stores the text that will be shown. Content should be provided in
`Markdown syntax <https://en.wikipedia.org/wiki/Markdown>`_.
If ``ui.homepage.custom.enabled == true``, but ``ui.homepage.custom.main.contentMd`` is empty, then dummy
content is shown indicating that something should be provided here.

ui.homepage.custom.main.backgroundImage.url
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

By default, the text is presented over a standard background image. If you wish
to provide a different background, you may do so by setting this variable to the URL
for the background you want to show. Any format supported by the ``background-image``
property in CSS is allowed, including PNG, JPG, and SVG.

.. note::

  The background color for the surrounding content is ``#01192D``, ``rgb(1, 25, 45)``.
  To achieve a seamless look, you should probably should use this color in at least border
  of your image.

.. note::

   A convenient place to store an image you want to use would be in a Git repository
   hosted in the GitLab used by RenkuLab. The repo can be a plain Git repo (it does not
   need to be a Renku repo).
