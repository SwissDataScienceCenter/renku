.. _collaborating:

Collaborating on Renku
======================

Beyond building and maintaining your own data science workflows, there are many
ways to use Renku to share your project with collaborators.

Finding & accessing public projects
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To find and access projects on the Renku platform that you might want to
interact with, you can click the **Projects** link in the menu bar followed by
the **Search** tab. Without entering any search terms, you will see a list of
all public projects. You can click on the project names to be taken to the
project's landing page.

.. warning::

  Note that the search box currently only allows you to search by project title,
  not by username.

By default, the files in public projects created by other people are
"read only" to you. This means you can see an overview, create and read Kus,
browse the filesystem in both text and :ref:`lineage` modes. The
project's **Notebook Servers** tab will inform you that you do not have
permissions to launch a notebook or make modifications to the code or datasets.

To get access to the project, you can:

* ask the project creator to `add you as a developer <added_to_project_>`_ or maintainer of the project through GitLab.

If the project owner has not added you to the project, you can still participate:

* through `Renku platform "Ku"s <make_kus_>`_, where you can post notes and discussion.
* through `forking a project <forks_for_collaboration_>`_ through the Renku platform, which creates your own copy of the current state of the project that you own.

.. _added_to_project:

Add or be added as a developer on a project
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

To allow someone to make changes to your project, you can use the GitLab
interface to add them in Settings > Members, with at either Developer or
Maintainer privileges.

Adding them here will make this project appear in their ``Your Projects`` list
on the Renku platform, where the **Notebook Servers** tab for the project will allow
them to launch an interactive environment to work on the project as usual.

.. warning::

  QUESTION: How are concurrent notebook session edits handled?

.. _make_kus:

Add collaborators to the conversation through "Ku"s
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In Renku, Kus are media-rich discussions you can use to help keep track of
your work and to collaborate with others. To find out more about them and walk
through sample usage, see :ref:`sharing_is_caring`.

.. _forks_for_collaboration:

Fork a project and take it in your own direction
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Forking someone else's project on Renku is just like forking any version-controlled
project. The Renku platform provides you a fork button on the project's home page
that creates a fork of the project (with the same name) under your own namespace,
where you have permissions to launch notebooks and make settings modifications.

One reason you might want to fork someone's project is to use it as a template for
your own analysis.

.. warning::

  Note that if you have a project with an identical name to the one you are
  trying to fork, the fork will fail. We suggest you change the name of your own
  project (which you can do in the Renku interface) before forking.
