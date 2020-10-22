.. _add_data:

Add data to your project
------------------------

In the JupyterLab interface, we can see that a few files already exist.
Let's start by adding data using the `Renku CLI <https://renku-python.readthedocs.io/en/latest/commands.html>`__.

From JupyterLab, start a terminal by clicking the **Terminal** icon (1)
on the bottom right of the **Launcher** page.

.. image:: ../../_static/images/jupyterlab-open-terminal.png
    :width: 85%
    :align: center
    :alt: Open terminal in JupyterLab

If your JupyterLab interface does not have the launcher tab open, you can
find it in the top bar menu in *File* > *New Launcher*.

.. note::

  To paste commands to the JupyterLab console, use ``Cmd+V`` on MacOS or
  ``Ctrl+Shift+V`` on Linux.

When you start the terminal, you will already be inside your project
directory. So you are ready to create a dataset.

Renku can create datasets containing data from a variety of sources. Renku
supports adding data from the local file system or a URL. Renku can also
import data from a data repository like the
`Dataverse <https://dataverse.harvard.edu>`_ or `Zenodo <https://zenodo.org>`_.
The advantage of data in a data repository is that it can contain metadata that
can be used to help interpret it. Another advantage is that data repositories
assign `DOIs <https://www.doi.org>`_ to data which can be used to
succinctly identify it and guarantee that the data will be findable and
accessible for a longer period of time (usually at least 20 years).

The DOI for the
dataset we want to import is `10.7910/DVN/WTZS4K <https://www.doi.org/10.7910/DVN/WTZS4K>`_.

Execute the following line and when prompted if you really want to download the
data, answer yes.

.. code-block:: console

    renku dataset import 10.7910/DVN/WTZS4K

    # Output:
    # CHECKSUM    NAME                       SIZE (MB)  TYPE
    # ----------  -----------------------  -----------  ---------------
    #             2019-01-flights.csv.zip       7.9301  application/zip
    # Do you wish to download this version? [y/N]: y
    # OK

Let us take a moment to understand what happened there. Opening the terminal
puts you inside the project directory with ``git`` already configured.

Then we imported a dataset  using the  `Renku CLI <http
://renku-python.readthedocs.io/>`__, Here, we can see the method of
referencing a dataset in a data repository by DOI. By doing so,
we capture a reference to the source of the data in the metadata of the
project.

You can list the datasets in a project by running the following:

.. code-block:: console

        renku dataset ls

        # ID                                    DISPLAY_NAME         VERSION    CREATED              CREATORS
        # ------------------------------------  -------------------  ---------  -------------------  ---------------
        # c5c74efd-5982-4c75-a1eb-4be870c51cc5  201901_us_flights_1  1          2020-01-16 16:51:12  [your name]

The file we added contains data on flight take-offs and landings at US airports, and it
comes originally from `here <https://www.transtats.bts.gov>`_. As the file
name suggests, this file covers data for January, 2019.

We can see that the two ``renku`` commands make use of the underlying git
repository:

.. code-block:: console

    git log

    # Output similar to:
    # commit ef542b5ec5a44fdbb16afc3de413308a7daff32f
    # Author: John Doe <john.doe@example.com>
    # Date:   Mon Apr 29 11:58:34 2019 +0000
    #
    #     renku dataset import 10.7910/DVN/WTZS4K
    #
    # commit 3809ce796933bd554ec65df0737b6ecf00b069e1
    # Author: John Doe <john.doe@example.com>
    # Date:   Mon Apr 29 11:58:33 2019 +0000
    #
    #     renku dataset: committing 1 newly added files
    #
    # commit 3f74a2dfdf5e27c1dc124f6455931089023253b8 (origin/master, origin/HEAD)
    # Author: John Doe <john.doe@example.com>
    # Date:   Mon Apr 29 11:53:41 2019 +0000
    #
    #     init renku repository

.. code-block:: console

    git status

    # Output similar to:
    # On branch master
    # Your branch is ahead of 'origin/master' by 2 commits.
    #   (use "git push" to publish your local commits)
    #
    # nothing to commit, working directory clean

Let us push the two fresh commits by running:

.. code-block:: console

    git push

    # Output similar to:
    # Locking support detected on remote "origin". Consider enabling it with: [...]
    # Uploading LFS objects: 100% (1/1), 7.9 MB | 0 B/s, done
    # Counting objects: 15, done.
    # Delta compression using up to 8 threads.
    # Compressing objects: 100% (12/12), done.
    # Writing objects: 100% (15/15), 2.26 KiB | 463.00 KiB/s, done.
    # Total 15 (delta 2), reused 0 (delta 0)
    # To https://renkulab.io/gitlab/john.doe/flights-tutorial.git
    #     b55aea9..91b226b  master --> master

.. _renkulab.io: https://renkulab.io
.. _documentation: https://renku.readthedocs.io/
.. _papermill: https://papermill.readthedocs.io/en/latest/
