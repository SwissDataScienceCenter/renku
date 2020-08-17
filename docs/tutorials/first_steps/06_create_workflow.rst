.. _create_workflow:

Build a reproducible workflow
-----------------------------

Create a workflow step
^^^^^^^^^^^^^^^^^^^^^^

Now we will use ``renku`` to create a reproducible, reusable "workflow". A
workflow consists of a series of steps, each of which may consume some input
files, execute code, and produce output files. The outputs of one step are
frequently the inputs of another --- this creates a dependency between the code
execution and results. When workflows become more complex, the bookkeeping can
be tedious. That is where Renku comes in --- it is designed to keep
track of these dependencies for you. We will illustrate some of these concepts
with a simple example (see also the :ref:`lineage` in the documentation_).

First, let us make sure the project repository is clean. Run:

.. code-block:: console

    git status

    # On branch master
    # Your branch is up-to-date with 'origin/master'.
    #
    # nothing to commit, working directory clean

Make sure the output ends with ``nothing to commit, working tree clean``.
Otherwise, you have to clean up your project repository by either removing
the changes or committing them to the repository.

.. note::

    You can undo your changes with:

    .. code-block:: console

        git checkout .
        git clean -fd

    Or, if you want to keep your changes, commit with:

    .. code-block:: console

        git add -A
        git commit -m "My own changes"
        git push

The ``00-FilterFlights.py`` script takes two input parameters: 1. a file to
process as an input 2. a path for storing the output. So to run it, we would
normally execute the following:

.. code-block:: console

    mkdir -p data/output        # Create the output directory
    python src/00-FilterFlights.py data/201901_us_flights_1/2019-01-flights.csv.zip data/output/2019-01-flights-filtered.csv

For renku to capture information about the execution, we need to make a small
change: we prepend ``renku run`` to the python command.

.. code-block:: console

    mkdir -p data/output        # Create the output directory
    renku run python src/00-FilterFlights.py data/201901_us_flights_1/2019-01-flights.csv.zip data/output/2019-01-flights-filtered.csv

Go ahead and run this command: it will create the preprocessed data file,
including the specification of *how* this file was created, and commit all the
changes to the repository. See the `renku command line docs <https://renku-
python.readthedocs.io/en/latest/commands.html>`_ for more information on this
and other commands.

.. note::

    Did you accidentally run the python command first? You would get
    an error like this

    .. code-block:: console

        # Error: The repository is dirty. Please use the "git" command to clean it.
        # On branch master
        # Your branch is up to date with 'origin/master'.
        # Untracked files:
        # (use "git add <file>..." to include in what will be committed)
        #         data/output/

    Remove the untracked files and this time execute ``only`` the renku command

    .. code-block:: console

        rm data/output/*
        renku run python src/00-FilterFlights.py data/201901_us_flights_1/2019-01-flights.csv.zip data/output/2019-01-flights-filtered.csv

.. note::

    Did you get an error like this instead?

    .. code-block:: console

        # Traceback (most recent call last):
        # File "src/00-FilterFlights.py", line 26, in <module>
        #     df.to_csv(output_path, index=False)
        # File "/opt/conda/lib/python3.7/site-packages/pandas/core/generic.py", line 3228, in to_csv
        #     formatter.save()
        # File "/opt/conda/lib/python3.7/site-packages/pandas/io/formats/csvs.py", line 183, in save
        #     compression=self.compression,
        # File "/opt/conda/lib/python3.7/site-packages/pandas/io/common.py", line 399, in _get_handle
        #     f = open(path_or_buf, mode, encoding=encoding, newline="")
        # FileNotFoundError: [Errno 2] No such file or directory: 'data/output/2019-01-flights-filtered.csv'
        # Error: Command returned non-zero exit status 1.

    If in the process of working through the tutorial, you stopped the
    interactive environment and started a new one along the way, this may
    happen. Why?
    `Under the hood <https://renku.readthedocs.io/en/latest/user/lfs.html>`_,
    we use
    `git-lfs <https://git-lfs.github.com/>`_
    to save large files, and these files may not be fetched when a new
    environment is started. We try to retrieve them automatically when needed
    for a renku command, but that may not always work.

    If you check the ``data/201901_us_flights_1/2019-01-flights.csv.zip`` file you
    will see only a few lines of metadata starting with
    ``version https://git-lfs.github.com/spec/v1``. You can easily
    fetch the data manually from the console by running

    .. code-block:: console

      git lfs pull

      # Downloading LFS objects: 100% (1/1), 66MB | 22 MB/s

    Another way to verify that your lfs files have been fetched is running the
    ``ls-files`` command and check if every file has a "*" (pulled) or a "-"
    (not pulled)

    .. code-block:: console

      git lfs ls-files

      # 2b1851ab60 * data/201901_us_flights_1/2019-01-flights.csv.zip


.. warning::

   Do *not* make any edits to the code before the ``renku run``
   command is finished. In order to keep track of the outputs of
   your script, renku will automatically add the changes to
   ``git``. If you want to modify your project while a ``renku`` command
   is executing, you should create a new branch.

**Aside: looking at data in JupyterLab**

The original zip file is not easy to visualize in Jupyter,
but the csv output of filtering can be opened from JupyterLab by navigating to
the **File** tab on the top left (1), then clicking ``data``
folder (2) and ``output`` (3).

.. image:: ../../_static/images/ui_04.2_jupyterlab-file-data.png
    :width: 85%
    :align: center
    :alt: File tab and data folder

Opening the file
``2019-01-flights-filtered.csv`` (1),
we can see its contents (2).

.. image:: ../../_static/images/ui_04.3_jupyterlab-data-open-csv.png
    :width: 85%
    :align: center
    :alt: Files tab and notebooks folder in JupyterLab

Add a second workflow step
^^^^^^^^^^^^^^^^^^^^^^^^^^

We will now develop a notebook to count the flights in the filtered data file.
As before, we will fast-forward through this step by downloading the solution.

.. code-block:: console

    wget -O notebooks/01-CountFlights.ipynb https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/01-CountFlights.ipynb

    # --2019-04-29 14:45:31--  https://renkulab.io/gitlab/renku-tutorial/renku-tutorial-flights/raw/master/.tutorial/meta/templates/01-CountFlights.ipynb
    # Resolving renkulab.io (renkulab.io)... 86.119.40.77
    # Connecting to renkulab.io (renkulab.io)|86.119.40.77|:443... connected.
    # HTTP request sent, awaiting response... 200 OK
    # Length: 1909 (1.9K) [text/plain]
    # Saving to: ‘notebooks/01-CountFlights.ipynb’
    #
    # notebooks/01-CountFlights.ipynb        100%[==============================================================================>]   1.86K  --.-KB/s    in 0s
    #
    # 2019-04-29 14:38:03 (105 MB/s) - ‘notebooks/01-CountFlights.ipynb’ saved [1909/1909]

Whenever we make changes, we need to record our work in git.

.. code-block:: console

    git add notebooks
    git commit -m"Created notebook to count flights"
    git push

    # [...]
    # To https://dev.renku.ch/gitlab/john.doe/flights-tutorial.git
    #     0fb9ac1..d0c4d1f  master -> master

You can look at the notebook to see how the logic works:
notebooks/01-CountFlights.ipynb.

We want to use this notebook to make a second step in the workflow.
For this, we are going to use papermill_.

Though Jupyter notebooks are very useful tools for interactively working with
data, they create some difficulties for reproducibility. A notebook that has
been manually executed may not be reproducible because the cells are not
required to be run in a fixed order. And notebooks are difficult to reuse
and apply to new data because they cannot be easily parametrized.

The tool papermill_ solves both these problems, and we will use it to create
the second step of our workflow.

First, let us make sure the project repository is clean. Run:

.. code-block:: console

    git status

    # On branch master
    # Your branch is up-to-date with 'origin/master'.
    #
    # nothing to commit, working directory clean

If the output does not end with ``nothing to commit, working tree clean``,
cleanup the project repository by either removing the changes or
committing them.

.. note::

    You can undo your changes with:

    .. code-block:: console

        git checkout .
        git clean -fd

    Or, if you want to keep your changes, commit with:

    .. code-block:: console

        git add -A
        git commit -m "My own changes"
        git push

Using papermill, we can run the notebook in a reproducible and
parameterizable way. Running a notebook with papermill produces
a new notebook containing the executed cells as output.

.. code-block:: console

    renku run papermill \
        notebooks/01-CountFlights.ipynb \
        notebooks/01-CountFlights.ran.ipynb \
        -p input_path data/output/2019-01-flights-filtered.csv  \
        -p output_path data/output/2019-01-flights-count.txt
    git push

    # Output similar to:
    # Input Notebook:  notebooks/01-CountFlights.ipynb
    # Output Notebook: notebooks/01-CountFlights.ran.ipynb
    # Executing: 100%|█████████████████████████████████████████████████████| 11/11 [00:01<00:00,  5.70cell/s]


Update your results
^^^^^^^^^^^^^^^^^^^

Here, we will quickly see one of the advantages of using the ``renku`` command
line tool.

Open the notebook `notebooks/01-CountFlights.ran.ipynb`, which contains the
output of running the notebook in the last step. In it, you should see that
there were 23078 flights to Austin, TX in Jan 2019.

.. image:: ../../_static/images/ui_04.4_jupyterlab-results_1.png
    :width: 85%
    :align: center
    :alt: First run results

This does not seem quite right. Austin, TX is not a very large airport, but
that number would mean that it had a flight landing on average
every two minutes, around the clock, during the entire month of January 2019.

Go back and take a look at the file ``src/00-FilterFlights.py`` file: it
contains an error! In the code block

.. code-block:: console

    # Select only flights to Austin (AUS)
    df = df[df['DEST'] == 'DFW']

we want to select flights to Austin-Bergstrom (AUS), but mistakenly select
flights to a different airport, ``DFW``. This would explain the discrepancy
we found. Dallas/Fort Worth is a much larger airport.

Let us fix this. Change ``DFW`` to ``AUS`` and save the file. Now when you
execute ``git status`` you should see something like the following:

.. code-block:: console

    git status

    # Output:
    # On branch master
    # Your branch is up to date with 'origin/master'.
    #
    # Changes not staged for commit:
    #   (use "git add <file>..." to update what will be committed)
    #   (use "git checkout -- <file>..." to discard changes in working directory)
    #
    #         modified:   src/00-FilterFlights.py
    #
    # no changes added to commit (use "git add" and/or "git commit -a")

Since we have made a change to our code, we need to commit the updated file to
the repository.

.. code-block:: console

    git add src/00-FilterFlights.py
    git commit -m"Fixed filter to use AUS, not DFW."
    git push

    # [...]
    # To https://dev.renku.ch/gitlab/john.doe/flights-tutorial.git
    #     a40f192..7922ee1  master -> master

**Reflection**

Now that we have made this change, how would you update everything *without*
Renku? Without Renku, you would need to think back and remember what files
would be affected by this change and what commands were run to initially
create them. To effect an update, you would manually carry out those steps
again, while being careful to do so in the correct order.

So without Renku, updating a project in response to a change can be tedious and
error-prone. But *with* Renku, it is very easy. We can just ask the system
what changed and what needs to be updated.

.. code-block:: console

    renku status

    # On branch master
    # Files generated from newer inputs:
    #   (use "renku log [<file>...]" to see the full lineage)
    #   (use "renku update [<file>...]" to generate the file from its latest inputs)
    #
    #         data/output/2019-01-flights-count.txt: src/00-FilterFlights.py#10d92afb
    #         data/output/2019-01-flights-filtered.csv: src/00-FilterFlights.py#10d92afb
    #         notebooks/01-CountFlights.ran.ipynb: src/00-FilterFlights.py#10d92afb
    #
    # Input files used in different versions:
    #   (use "renku log --revision <sha1> <file>" to see a lineage for the given revision)
    #
    #         src/00-FilterFlights.py: 10d92afb, 9630da17

Renku is telling us that ``src/00-FilterFlights.py`` was changed and
``data/output/2019-01-flights-filtered.csv``, ``01-CountFlights.ran.ipynb``,
``data/output/2019-01-flights-count.txt`` all need to be updated as a result.
We do not need to remember how to update them: Renku already knows this. We can
just ask it to make the update by running ``renku update``.

.. code-block:: console

    renku update

    # Resolved '.renku/workflow/2fd4341a00c945fbaf00cb3f0942c674.cwl' to 'file:///work/flights-tutorial/.renku/workflow/2fd4341a00c945fbaf00cb3f0942c674.cwl'
    # [workflow ] start
    # [workflow ] starting step step_2
    # [step step_2] start
    # [job step_2] /tmp/tmpawwugtz3$ python \
    #     /tmp/tmpawwugtz3/src/00-FilterFlights.py \
    #     /tmp/tmpawwugtz3/data/flights/2019-01-flights.csv.zip \
    #     data/output/2019-01-flights-filtered.csv
    # [job step_2] completed success
    # [step step_2] completed success
    # [workflow ] starting step step_1
    # [step step_1] start
    # [job step_1] /tmp/tmp5djthljs$ papermill \
    #     /tmp/tmp5djthljs/notebooks/01-CountFlights.ipynb \
    #     notebooks/01-CountFlights.ran.ipynb \
    #     -p \
    #     input_path \
    #     /tmp/tmp5djthljs/data/output/2019-01-flights-filtered.csv \
    #     -p \
    #     output_path \
    #     data/output/2019-01-flights-count.txt
    # Input Notebook:  /tmp/tmp5djthljs/notebooks/01-CountFlights.ipynb
    # Output Notebook: notebooks/01-CountFlights.ran.ipynb
    # Executing: 100%|█████████████████████████████████████████████████████| 11/11 [00:03<00:00,  3.67cell/s]
    # [job step_1] completed success
    # [step step_1] completed success
    # [workflow ] completed success

**Wasn't that easy!?**

Now, if you look at notebooks/01-CountFlights.ran.ipynb, you should see that
there were 4951 flights to Austin, TX in Jan 2019, which sounds plausible.

.. image:: ../../_static/images/ui_04.5_jupyterlab-results_2.png
    :width: 85%
    :align: center
    :alt: Second run results

Before calling it a day, we should not forget to push our work:

.. code-block:: console

    git push

    # [...]
    # Uploading LFS objects: 100% (7/7), 69 MB | 25 MB/s, done
    # Counting objects: 39, done.
    # Delta compression using up to 8 threads.
    # Compressing objects: 100% (36/36), done.
    # Writing objects: 100% (39/39), 4.59 KiB | 1.15 MiB/s, done.
    # Total 39 (delta 14), reused 0 (delta 0)
    # To https://dev.renku.ch/gitlab/lorenzo.cavazzi.tech/deleteme.git
    #    8892173..8d00b71  master -> master

.. _renkulab.io: https://renkulab.io
.. _documentation: https://renku.readthedocs.io/
.. _papermill: https://papermill.readthedocs.io/en/latest/
