.. _autosave:

Autosave in JupyterLab
======================

When you stop a JupyterLab notebook, an automatic check looks for any work
that has not been pushed to the remote repository, including untracked and
modified files. If something is found, a new "autosave" branch is created
and pushed to GitLab.

.. note::

  You cannot start a notebook server from these branches but you can
  still see them listed in GitLab. You can easily identify them because
  their name starts with `renku/autosave/`


Restore unsaved work
--------------------

The next time you start a new JupyterLab server from the same branch/commit
combination, you will be notified and the autosaved data will be automatically
loaded in your session. Please note that nothing will be pushed automatically
to the origin, therefore you won't see any changes in your project's files
in Renku, nor will the :ref:`lineage` be updated.

Local branches you created but never pushed to the origin will not be saved.


Discard autosaved content
-------------------------

If you don't need the autosaved content, you can easily discard it using the
following commands:

::

    $ git reset --hard origin/<branch-name> # remove commits and modified files
    $ git clean -f -d # remove untracked files

If you are working on the ``master`` branch, the command looks like this:

::

    $ git reset --hard origin/master && git clean -f -d


Keep pushing to the origin
--------------------------

This feature is intended to prevent losing your work but you should not rely
on it. Server or network failures can prevent the webhook to trigger, along
with a number of other events that could still cause a permanent loss of your
non-pushed work. Furthermore, :ref:`knowledge-graph` indexing will not be
triggered by the autosave function. Therefore, you should still commit your
work regularly and push it to the origin.
