


.. _first_steps:

Getting Started with Renku
==========================

This tutorial will help you get started working on the Renku platform. Feel
free to use  `renkulab.io <https://renkulab.io>`_ or any other instance of
Renku that you can access. Following the steps below, you will  learn how to
use Renku for:

1. `Creating a new project <Create a new project>`_
2. `Adding data to your project <Add input data to your project>`_
3. `Installing and managing Python packages <Install and manage Python packages>`_
4. `Working with Renku within JupyterLab <Work using JupyterLab>`_
5. `Sharing your results and collaborating with your peers <Share your results and collaborate with your peers>`_

Create a new project
^^^^^^^^^^^^^^^^^^^^

First, head to `renkulab.io <https://renkulab.io>`__ (or your own instance of
Renku) and click on the **Login** button located on the top right corner of
the Renku web interface.

On `renkulab.io <http://renkulab.io>`_ you can sign in using with your GitHub
or LinkedIn account by clicking on the corresponding button.

Once logged in, create a new project by going to the **Projects** (1) page
and clicking on the **New Project** button (2).

.. image:: ../_static/images/ui_create_project.png
    :width: 85%
    :align: center
    :alt: Create a new project in UI

Set **tutorial-zhbikes** as your project title, fill-in a short description
and set the project visibility to **Public**.
Click on the **Create** button.

.. warning::

  Be sure to not leave the description field empty. The form will fail
  silently otherwise.

To more easily find your project later, you can give it a star:

.. image:: ../_static/images/ui_star_project.png
    :width: 85%
    :align: center
    :alt: Star a project

Now that we have a project, we can start working on it by clicking
on **Launch JupyterLab**.

Add input data to your project
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In the JupyterLab interface, we can see that a few files already exist.
Let's start by adding data using the `Renku CLI <http://renku-python.readthedocs.io/>`_.

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
    # Adding data to dataset  [                                    ]  1/1  https://data.stadt-zuerich.ch/dataset/verkehrszaehlungen_werte_fussgaenger_velo/resource/d17a0a74-
    # Adding data to dataset  [                                    ]  1/1

Let's take the time to see what happened there. Opening the terminal puts
you inside the project directory with ``git`` already configured.

Next we created a dataset named ``zhbikes`` using the  `Renku CLI <http
://renku-python.readthedocs.io/>`__ and lastly we added a file to the
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

Install and manage Python packages
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Continuing in the same terminal session as in last step, we can install python
packages as usual with ``pip``:

.. code-block:: console

    pip install pandas fastparquet seaborn
    pip freeze > requirements.txt
    git add requirements.txt
    git commit -m"Installed pandas, fastparquet, seaborn"
    git push

.. warning::

  Make sure that you update the ``requirements.txt`` file after you install
  new packages. This ensures that the packages needed to work on your project
  will be available to your peers when collaborating on a project.

When updating and pushing the ``requirements.txt`` file to your project
repository, the Renku platform will update the Python stack used to launch
your JupyterLab instance. If you shut down your notebook server, the next time
you use the **Launch JupyterLab** button, the packages will come already
pre-installed in the new server's environment.


Work using JupyterLab
^^^^^^^^^^^^^^^^^^^^^

Create new notebooks
""""""""""""""""""""

On the JupyterLab interface, use the left-hand bar to go to the **Files** (1)
tab. You can see the list of files and folders from your project.

First, create a folder by clicking on the new folder button (2) and name it
``notebooks`` (3).

.. image:: ../_static/images/jupyterlab-files-notebooks.png
    :width: 85%
    :align: center
    :alt: Files tab and notebooks folder in JupyterLab

To create a new notebook, first double click on the **notebooks** folder (3),
then on the '+' button (4). Select 'Python 3' to create a new notebook (5).

.. image:: ../_static/images/jupyterlab-new-notebook.png
    :width: 85%
    :align: center
    :alt: New notebook in JupyterLab

To rename the notebook, right click on its name (``Untitled.ipynb``) and
select rename.

.. image:: ../_static/images/jupyterlab-rename.png
    :width: 85%
    :align: center
    :alt: Rename notebook in JupyterLab

If you are not familiar with JupyterLab, you can read more on their
`documentation <https://jupyterlab.readthedocs.io/en/latest/>`_. You can take
the time to play with the JupyterLab interface and new notebooks before
continuing.

If you want to save your new notebook(s), go to the console and use ``git`` to
add your work to the repository.

For example, if you want to keep the new notebook(s), run the following.

.. code-block:: console

    git add notebooks # track everything inside the notebooks folder
    git commit -m "Added some notebooks"
    git push


Interactively explore the bicycle data
""""""""""""""""""""""""""""""""""""""

To start working with the bicycle data we have already created a sample
notebook that does some data cleaning and visualization. We will first
download the notebook so you can interactively explore the dataset, much like
you would in a real project. Feel free to execute the cells. When we are ready to generate results, we will
refactor the code from the notebook into a python module and run it with
``renku`` to create a repeatable analysis workflow.

Use the commands below to add the notebook to your project.

.. code-block:: console

    mkdir -p notebooks
    wget -O "notebooks/zhbikes-notebook.ipynb" https://raw.githubusercontent.com/SwissDataScienceCenter/renku/master/docs/_static/zhbikes/ZHBikes.ipynb
    git add notebooks
    git commit -m"Added zuerich bike notebook"
    git push


Refactor the notebook
"""""""""""""""""""""

To make our work here more reusable and easier to maintain we will refactor
the code we have written in the notebook into runnable python scripts.
We will make two scripts: one that preprocesses the data and saves the result
and another that will create the figures.

Here, we have already done the refactoring work for you - to get the scripts,
run:

.. code-block:: console

    mkdir -p src
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
    git commit -m 'added refactored scripts'


Produce a repeatable workflow
"""""""""""""""""""""""""""""

Here we will use ``renku`` and your refactored scripts to quickly create a
"workflow".

First, let's make sure the project repository is clean.
Run:

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
changes to the repository.

To generate the figures, run

.. code-block:: console

    renku run python src/plot_data.py data/preprocessed/zhbikes.parquet


Reuse your own work
"""""""""""""""""""

Here, we will quickly see one of the advantages of using the ``renku`` command
line tool.

Let's begin by adding some more data to the ``zhbikes`` data set:

.. code-block:: console

    renku dataset add zhbikes https://data.stadt-zuerich.ch/dataset/verkehrszaehlungen_werte_fussgaenger_velo/resource/ed354dde-c0f9-43b3-b05b-08c5f4c3f65a/download/2016_verkehrszaehlungen_werte_fussgaenger_velo.csv

This new file corresponds to the year of 2016 and is part of the same bike data set as above.

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

That's it! The intermediate data file ``data/preprocessed/zhbikes.feather``
and the figures in ``figs/``, are recreated by re-running the

Lastly, let's not forget to push our work:

.. code-block:: console

    git push

Share your results and collaborate with your peers
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In this section, we will see how to use Renku to collaborate on projects.

Discussions with Kus
""""""""""""""""""""

Let's start by going back to the `Renku web interface <https://renkulab.io>`__.
Make sure you are logged in, so you can see the list of projects you starred.

Click on your ``tutorial-zhbikes`` project to open it and then go to the
**Kus** tab (1).

As you can see it's empty at the moment, so let's start a new discussion by clicking
on the **New Ku** button (2).

.. image:: ../_static/images/renku-ui-new-ku.png
    :width: 85%
    :align: center
    :alt: New Ku in Renku UI

In the **New Ku** form, fill in the **Title** and **Description** as follows.

* Title: Data source
* Description: Where does the data come from?

Do not change the **Visibility** and click on **Create**.

The **Kus** tab should now list the newly created Ku.

In Renku, Kus are media-rich discussions you can use to help keep track of your work
and to collaborate with others.

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

    cd work
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

    ![Exploration notebook](notebooks/papermill/Explore.ipynb)

As you can see, the content of the notebook is being displayed in the
comment. You can collapse/expand it by clicking on its corresponding title
in blue.

.. image:: ../_static/images/renku-ui-embed-notebook.png
    :width: 85%
    :align: center
    :alt: Embedded notebook in Renku UI

Where to go from here?
^^^^^^^^^^^^^^^^^^^^^^

* Explore the documentation
* Read more about the `Renku CLI <http://renku-python.readthedocs.io/>`_
* `Join us on Gitter <https://gitter.im/SwissDataScienceCenter/renku>`_
