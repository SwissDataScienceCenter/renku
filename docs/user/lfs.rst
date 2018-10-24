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

Useful git LFS commands
-----------------------

* ``git lfs ls-files``: shows all the files currently in LFS
* ``git lfs pull <remote> -I <path>``: pull a specific file from LFS
