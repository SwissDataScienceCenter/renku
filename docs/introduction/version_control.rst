.. _version_control:

Version Control
===============

Data and code change frequently in a typical project. Knowing which *exact*
version of code and data produced a particular result is critical for ensuring
the robustness and veracity of your work. In Renku, version control is the
base upon which everything else is built.

We rely on the currently most widespread version control system, `git <https
://git-scm.com/>`_. If you are unfamiliar with ``git`` it would not hurt to
read at least some of their `excellent tutorials <https://git-
scm.com/docs/gittutorial>`_. In Renku we try to take care of most of the
boiler plate ``git`` commands for you, but you should still be aware that it
is being used under the hood.

The added benefit of using a version control system like ``git`` is that it also
automatically encourages you to be creative, explore new ideas, and break things.
"Branching" is extremely light-weight in ``git`` and allows you to freely
experiment with complete peace-of-mind that you can always simply restore your
last sane version of your work if everything happens to go off the rails. This
is a fantastic advantage in data science, where experimentation is
a critical part of the discovery process.

Note that in Renku we make use of `git LFS <https://git-lfs.github.com>`_ which
allows to keep not only the code but also the data related to an analysis under
version control while keeping the git repository itself small.
