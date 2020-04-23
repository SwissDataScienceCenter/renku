.. _lfs:

Data in Renku
=============

Renku uses the `git Large File Storage (LFS) <https://git-lfs.github.com/>`_ for handling data.
Using standard git for large files is difficult because the data itself is
converted into git objects and placed in the repository. Git LFS lets you
include large data files in your repository efficiently while using more
or less just the standard set of git commands. In addition, git LFS places
large files on the server and allows you to work with your repository without
actually having a local copy of the data. You can pull the data from the server
as it becomes needed, saving you time and resources.

Using git LFS responsibly
-------------------------

The default configuration of git LFS is to pull recent data from the server
and into the working copy of the repository. This is fine if the data is
reasonably small (~GB size) but as it becomes larger, the default behavior
can start to pose problems.

Imagine a renku project with 100GB of data in LFS. If a few collaborators all
decide to work on the project at the same time and launch the JupyterLab
environment to iterate over some changes, each might attempt to download 100GB
of data to each of their JupterLab sessions. Not only will this take  a long
time, but it might also eventually lead to resource starvation on the host
node.

Data in JupyterLab sessions
---------------------------

Due to the resource concerns, we therefore do not pull data into user
JupyterLab sessions by default. As a result, you do need to be aware of dealing
with data stored in LFS if you want to use it efficiently in your work with
renku.

Renku LFS configuration
-----------------------
Renku by default only stores files larger than 100kb in LFS, to prevent
littering LFS with files. This limit can be changed by running:

.. code-block:: console

    $ renku config lfs_threshold <size>

where ``<size>`` is a file size formatted like ``10b``, ``100kb``, ``0.5mb`` or
``10gb``.

Additionally, paths can be excluded from LFS storage by renku commands by
editing the ``.renkulfsignore`` file in the project root folder. This file
follows ``.gitignore`` `convention <https://git-scm.com/docs/gitignore#_pattern_format>`
Files matching a pattern in ``.renkulfsignore`` will never be added to git LFS
by a renku command like ``renku run`` or ``renku dataset add``.

Useful git LFS commands
-----------------------

* ``git lfs ls-files``: shows all the files currently in LFS
* ``git lfs pull -I <path> [remote]``: pull a specific path from LFS. It can be a single file or an entire folder.
* ``git lfs migrate import --fixup --include-ref=refs/heads/master``: move files into LFS. Use this command if you accidentally committed large files to a repo.

Note that you can also use wild-cards, e.g. ``git lfs pull -I "data/records_201*.csv"``
but be sure to include quote characters (``"`` or ``'``) when you use wild-cards.

See the `git lfs tutorial <https://github.com/git-lfs/git-lfs/wiki/Tutorial>` for details.
