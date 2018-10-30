.. _first_steps:

Getting Started with Renku
==========================

This tutorial will help you get started working on the Renku platform. Feel
free to use renkulab.io_ or any other instance of
Renku that you can access. Following the steps below, you will  learn how to
use Renku for:

1. `Creating a new project <create_project_>`_
2. `Adding data to your project <add_data_>`_
3. `Installing and managing Python packages <python_environment_>`_
4. `Working with Renku within JupyterLab <jupyterlab_>`_
5. `Interactively exploring the bicycle counting data <interactive_exploration_>`_
6. `Producing a repeatable analysis <create_workflow_>`_
7. `Sharing your results and collaborating with your peers <sharing_is_caring_>`_

.. _create_project:

Create a new project
^^^^^^^^^^^^^^^^^^^^

First, head to renkulab.io_ (or your own instance of
Renku) and click on the **Login** button located on the top right corner of
the Renku web interface.

On renkulab.io_ you can sign in using with your GitHub
or LinkedIn account by clicking on the corresponding button.

Once logged in, create a new project by going to the **Projects** (1) page
and clicking on the **New Project** button.

.. image:: ../_static/images/ui_create_project.png
    :width: 100%
    :align: center
    :alt: Create a new project in UI

Set **Zürich bikes tutorial** as your project title, fill-in a short
description and set the project visibility to **Public**. Click on the
**Create** button.

To more easily find your project later, you can give it a star:

.. image:: ../_static/images/ui_star_project.png
    :width: 100%
    :align: center
    :alt: Star a project

Now that we have a project, we can start working on it by clicking
on **Launch JupyterLab**.

.. _add_data:

Add input data to your project
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In the JupyterLab interface, we can see that a few files already exist.
Let's start by adding data using the `Renku CLI <https://renku-python.readthedocs.io/en/latest/cli.html>`__.

From JupyterLab, start a terminal.

.. image:: ../_static/images/jupyterlab-open-terminal.png
    :width: 85%
    :align: center
    :alt: Open terminal in JupyterLab

If your JupyterLab interface does not have the launcher tab open, you can
find it in the top bar menu in File > New Launcher.

.. note::

  To paste commands to the JupyterLab console, use ``Cmd+V`` on MacOS or
  ``Ctrl+Shift+V`` on Linux.

When you start the terminal, you will already be inside your project
directory.  Use the following commands to add data to your project.

.. code-block:: console

    renku dataset create zhbikes
    # Output:
    # Creating a dataset ... OK

    renku dataset add zhbikes https://data.stadt-zuerich.ch/dataset/verkehrszaehlungen_werte_fussgaenger_velo/resource/d17a0a74-1073-46f0-a26e-46a403c061ec/download/2017_verkehrszaehlungen_werte_fussgaenger_velo.csv
    # Output:
    # Adding data to dataset  [     ]  1/1  https://data.stadt-zuerich.ch/dataset/verkehrszaehlungen_werte_fussgaenger_velo/resource/d17a0a74-
    # Adding data to dataset  [     ]  1/1

Let's take the time to see what happened there. Opening the terminal puts
you inside the project directory with ``git`` already configured.

Next we created a dataset named ``zhbikes`` using the  `Renku CLI <http
://renku-python.readthedocs.io/>`__ and finally we added a file to the
``zhbikes`` data set. Here, we can see the preferred method of referencing a
file to be added which is to use a permanent URL. By doing so, we create a
reference to the source of the file in the metadata of the project.

The data file we added is about bike traffic in the City of Zürich, and its
description can be found `here <https://data.stadt-
zuerich.ch/dataset/verkehrszaehlungen_werte_fussgaenger_velo>`_. As the file
name suggests, this file covers the year of 2017.

We can see that the two ``renku`` commands make use of the underlying git
repository:

.. code-block:: console

    git log
    # Output similar to:
    # commit ef542b5ec5a44fdbb16afc3de413308a7daff32f
    # Author: John Doe <john.doe@example.com>
    # Date:   Thu Aug 23 11:58:34 2018 +0000
    #
    #     renku dataset add zhbikes https://data.stadt-zuerich.ch/dataset/verkehrszaehlungen_werte_fussgaenger_velo/resource/d17a0a74-1073-46f0-a26e-46a403c061ec/download/2
    # 017_verkehrszaehlungen_werte_fussgaenger_velo.csv
    #
    # commit 38ac3261e8b2964c4608a6ca6d30a4f907dc6930
    # Author: John Doe <john.doe@example.com>
    # Date:   Thu Aug 23 11:58:30 2018 +0000
    #
    #     renku dataset create zhbikes
    #
    # commit 3f74a2dfdf5e27c1dc124f6455931089023253b8
    # Author: John Doe <john.doe@example.com>
    # Date:   Thu Aug 23 11:55:41 2018 +0000
    #
    #     init renku repository

    git status
    # Expected output:
    # On branch master
    # Your branch is ahead of 'origin/master' by 2 commits.
    #   (use "git push" to publish your local commits)
    # nothing to commit, working directory clean

Let's push the two fresh commits by running:

.. code-block:: console

    git push

The data file can be opened from JupyterLab by going to the **Files** tab
and traversing the ``data`` folder.

Opening the file, we can see it contains some data in CSV format.

.. image:: ../_static/images/jupyterlab-data-open-csv.png
    :width: 85%
    :align: center
    :alt: Files tab and notebooks folder in JupyterLab


.. _python_environment:

Install and manage Python packages
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Defining your runtime environment is critical if you would like for others to
reuse your work. The basic step to achieve this is to manage the software
libraries that are needed for your code to execute. In Renku, we use common
conventions  for the environment definitions. In python, the
``requirements.txt`` is one such conventional way to specify the required
libraries. When  you created your project, an empty ``requirements.txt`` was
created - find it in the file browser of your JupyterLab session and double-
click to open the editor.  We will need ``pandas``, ``fastparquet`` and ``seaborn``
libraries, so enter those  on separate lines in the ``requirements.txt`` file
and save it:

.. image:: ../_static/images/jupyterlab-setup-requirements.png
    :width: 85%
    :align: center
    :alt: Configuring package dependencies

Going back to the same terminal session as before, we can now
install these packages with ``pip``:

.. code-block:: console

    pip install -r requirements.txt
    git add requirements.txt
    git commit -m "Installed pandas, fastparquet, seaborn"
    git push

.. warning::

  Make sure that you update the ``requirements.txt`` file after you install
  new packages. This ensures that the packages needed to work on your project
  will be available to your peers when collaborating on a project.

When updating and pushing the ``requirements.txt`` file to your project
repository, the Renku platform will rebuild the software stack used to launch
your JupyterLab notebook server. If you were to shut it down, the next time
you use the **Launch JupyterLab** button, the packages you will come already
pre-installed in the new server's environment.

.. _jupyterlab:

Work using JupyterLab
^^^^^^^^^^^^^^^^^^^^^

Create new notebooks
""""""""""""""""""""

On the JupyterLab interface, use the left-hand bar to go to the **Files** (1)
tab. You can see the list of files and folders from your project. To create a
new notebook, first double click on the **notebooks** folder (2), then on the
'+' button (3). Select 'Python 3' to create a new notebook (4).

.. image:: ../_static/images/jupyterlab-files-notebooks.png
    :width: 85%
    :align: center
    :alt: Files tab and notebooks folder in JupyterLab

To rename the notebook, right click on its name (``Untitled.ipynb``) and
select rename.

.. image:: ../_static/images/jupyterlab-rename.png
    :width: 85%
    :align: center
    :alt: Rename notebook in JupyterLab

If you are not familiar with JupyterLab, you can read more on their
documentation_. You can take
the time to play with the JupyterLab interface and new notebooks before
continuing.

If you want to save your new notebook(s), you can go to the console and use
``git`` to add your work to the repository.

For example, if you want to keep the new notebook(s), run the following in the
terminal:

.. code-block:: console

    git add notebooks # track everything inside the notebooks folder
    git commit -m "Added some notebooks"
    git push

Alternatively, you can click on the git icon on the left-hand side of your
JupyterLab session (1) to open the git panel. Here you can add untracked files
by selecting them and clicking (2). Finally, you can enter the commit message
in (3) and click the check mark to finish the commit. Currently there is no
push option in this panel, so you will need to go back to the terminal
and type ``git push`` to sync with the server.

.. image:: ../_static/images/jupyterlab-git-panel.png
    :width: 85%
    :align: center
    :alt: Commit notebook in JupyterLab


.. _interactive_exploration:

Interactively explore the bicycle data
""""""""""""""""""""""""""""""""""""""

To start working with the bicycle data we have already created a sample
notebook that does some data cleaning and visualization. We will first
download the notebook so you can interactively explore the dataset, much like
you would in a real project. Feel free to execute the cells. When we are ready
to generate results, we will refactor the code from the notebook into a python
module and run it with ``renku`` to create a repeatable analysis workflow.

Use the commands below to add the notebook to your project.

.. code-block:: console

    wget -O "notebooks/zhbikes-notebook.ipynb" https://raw.githubusercontent.com/SwissDataScienceCenter/renku/master/docs/_static/zhbikes/ZHBikes.ipynb
    git add notebooks
    git commit -m "Added zuerich bike notebook"
    git push


Refactor the notebook
"""""""""""""""""""""

To make our work here more reusable and easier to maintain we will refactor
the code we have written in the notebook into runnable python scripts. We will
make two scripts: one that does some initial preprocessing of the data and
saves the result, and another that will create the figures.

Here, we have already done the refactoring work for you - to get the scripts,
run:

.. code-block:: console

    mkdir src
    wget -O "src/clean_data.py" https://raw.githubusercontent.com/SwissDataScienceCenter/renku/master/docs/_static/zhbikes/clean_data.py
    wget -O "src/plot_data.py" https://raw.githubusercontent.com/SwissDataScienceCenter/renku/master/docs/_static/zhbikes/plot_data.py

Feel free to inspect the code in the file viewer in your JupyterLab session.
Note that in the scripts, we are saving first the intermediate output with
the cleaned ``DataFrame`` and finally also the two figures.

In addition, the scripts must be run with parameters -- to the
``clean_data.py`` script, we must give an input directory and an output path
for saving the cleaned dataset. The ``plot_data.py`` script takes as input the
location of the cleaned dataset.

When you are satisfied with the code you can commit it to your repository:

.. code-block:: console

    git add src
    git commit -m "added refactored scripts"


.. _create_workflow:

Produce a repeatable workflow
"""""""""""""""""""""""""""""

Here we will use ``renku`` and the refactored scripts to quickly create a
"workflow". A workflow consists of a series of steps, each of which consumes
some inputs, executes code based on those inputs and produces outputs. The
outputs of one step are frequently the inputs of another - this creates a
dependency between the code executions and results. When workflows become more
complex, the bookkeeping can become tedious. That is where ``Renku`` comes in
-- it is designed to keep track of these dependencies for you. We will
illustrate some of these concepts with a simple example (see also the
:ref:`lineage` in the documentation_).

First, let's make sure the project repository is clean. Run:

.. code-block:: console

    git status
    # Expected output:
    # On branch master
    # Your branch is up-to-date with 'origin/master'.
    # nothing to commit, working directory clean

Make sure the output ends with ``nothing to commit, working tree clean``.
Otherwise, use ``git add``, ``git commit`` and ``rm`` to cleanup your project repository.

To run the ``clean_data.py`` script, we would normally do

.. code-block:: console

    python src/clean_data.py data/zhbikes data/preprocessed/zhbikes.parquet

The only change required to execute the script with ``renku`` is

.. code-block:: console

    renku run python src/clean_data.py data/zhbikes data/preprocessed/zhbikes.parquet

Go ahead and run this command -- it will create the preprocessed file for you
including the specification of *how* this file was created, and commit all the
changes to the repository. See the `renku command line docs <https://renku-
python.readthedocs.io/en/latest/cli.html>`_ for more information on this and
other commands.

To generate the figures, run

.. code-block:: console

    renku run python src/plot_data.py data/preprocessed/zhbikes.parquet

.. warning::

   Do *not* make any edit to the code before the renku command ``run`` is finished.
   In oder to keep track of all changes that your runing script is doing renku will 
   automatically add the changes to Git LFS. If you want to keep working while runing 
   a renku command, you should create a new branch.

Reuse your own work
"""""""""""""""""""

Here, we will quickly see one of the advantages of using the ``renku`` command
line tool.

Let's begin by adding some more data to the ``zhbikes`` data set:

.. code-block:: console

    renku dataset add zhbikes https://data.stadt-zuerich.ch/dataset/verkehrszaehlungen_werte_fussgaenger_velo/resource/ed354dde-c0f9-43b3-b05b-08c5f4c3f65a/download/2016_verkehrszaehlungen_werte_fussgaenger_velo.csv

This new file corresponds to the year of 2016 and is part of the same bike
data set as above.

We can now see that ``renku`` recognizes that output files like
``data/preprocessed/zhbikes.parquet`` and the figures are outdated:

.. code-block:: console

    renku status
    # Expected output similar to:
    # On branch master
    # Files generated from newer inputs:
    #   (use "renku log [<file>...]" to see the full lineage)
    #   (use "renku update [<file>...]" to generate the file from its latest inputs)

    #         figs/grid_plot.png: data/zhbikes#57c66586
    #         data/preprocessed/zhbikes.parquet: data/zhbikes#57c66586
    #         figs/cumulative.png: data/zhbikes#57c66586

To update all the outputs, we can run the following.

.. code-block:: console

    renku update

That's it! The intermediate data file ``data/preprocessed/zhbikes.parquet``
and the figures in ``figs/``, are recreated by re-running the necessary steps.
See the `renku update documentation <https://renku-python.readthedocs.io/en/latest/cli.html
#renku-update>`_ for a detailed explanation of how the workflow is re-
executed.

.. note::

    A very similar command is `renku rerun <https://renku-
    python.readthedocs.io/en/latest/cli.html#module-renku.cli.rerun>`__,
    except that it also allows you to modify the inputs. This is useful, for
    example, to vary hyper-parameters or input data sources.

Lastly, let's not forget to push our work:

.. code-block:: console

    git push


.. _sharing_is_caring:

Share your results and collaborate with your peers
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In this section, we will see how to use Renku to collaborate on projects.

Discussions with Kus
""""""""""""""""""""

Let's start by going back to the Renku web interface on renkulab.io_ or
whichever instance you are using for this tutorial. Make sure you are logged
in, so you can see the list of projects you starred.

Click on your ``tutorial-zhbikes`` project to open it and then go to the
**Kus** tab (1).

As you can see it's empty at the moment, so let's start a new discussion by
clicking on the **New Ku** button (2).

.. image:: ../_static/images/renku-ui-new-ku.png
    :width: 85%
    :align: center
    :alt: New Ku in Renku UI

In the **New Ku** form, fill in the **Title** and **Description** as follows.

* Title: Data source
* Description: Where does the data come from?

Do not change the **Visibility** and click on **Create**.

The **Kus** tab should now list the newly created Ku.

In Renku, Kus are media-rich discussions you can use to help keep track of
your work and to collaborate with others.

To participate in a given Ku and add comments, click on the title.

.. image:: ../_static/images/renku-ui-kus-list.png
    :width: 85%
    :align: center
    :alt: Kus list in Renku UI

This will display the thread of comments from the selected Ku.
To write something and add it to the discussion, use the text
box and click submit.

.. image:: ../_static/images/renku-ui-new-ku-comment.png
    :width: 85%
    :align: center
    :alt: Participate in a Ku in Renku UI

The comments are displayed using the Markdown format (`cheatsheet here <https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet>`_),
with the powerful addition that you can embed notebook files and markdown files.
The syntax is as follows:

.. code-block:: console

    ![description](file-location)


Let' try this with our question about where the data is coming from.
Copy and paste the following text in the text box and hit **Submit**.

.. code-block:: console

    The readme should be updated with information about the data source:

    ![Readme](README.md)

.. image:: ../_static/images/renku-ui-comment-1.png
    :width: 85%
    :align: center
    :alt: Ku example 1 in Renku UI

Now, you can use **Launch JupyterLab** to open and edit the ``README.md`` file.
You can mention that the data comes from the city of Zürich, with the following
link to the `bike data set <https://data.stadt-zuerich.ch/dataset/verkehrszaehlungen_werte_fussgaenger_velo>`__.

To save the changes to the ``README.md`` file, open a console by click on the '+' button
and then selecting **Terminal**.

Use ``git`` to save your changes:

.. code-block:: console

    git add README.md
    git commit -m "Added data information in the Readme"
    git push

Now that the ``README.md`` file has been updated, we can **Close** the Ku (1).

.. image:: ../_static/images/renku-ui-close-ku.png
    :width: 85%
    :align: center
    :alt: Close Ku in Renku UI

Doing so indicates that the corresponding discussion is closed.
This can be useful to sort discussions and find out what is currently work in progress
within the project.

Now, let's create another Ku and embed a notebook in the discussion.

* Title: General data exploration
* Description: First look at the data set

Add a comment with the following content.

.. code-block:: console

    Let's explore the dataset! Here is what we know:

    ![Exploration notebook](notebooks/zhbikes-notebook.ipynb)

As you can see, the content of the notebook is being displayed in the
comment. You can collapse/expand it by clicking on its corresponding title
in blue.

.. image:: ../_static/images/renku-ui-embed-notebook.png
    :width: 85%
    :align: center
    :alt: Embedded notebook in Renku UI

Where to go from here?
^^^^^^^^^^^^^^^^^^^^^^

* Create your own project on renkulab.io!
* Explore the documentation_
* Read more about the `Renku CLI <http://renku-python.readthedocs.io/>`__
* Join us on `Gitter <https://gitter.im/SwissDataScienceCenter/renku>`_ or `GitHub <https://github.com/SwissDataScienceCenter/renku>`_.

.. _renkulab.io: https://renkulab.io
.. _documentation: https://jupyterlab.readthedocs.io/en/latest/
