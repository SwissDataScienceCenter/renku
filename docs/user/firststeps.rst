.. _first_steps:

Getting Started on `renkulab.io <https://renkulab.io>`__
========================================================

This tutorial will help you get started working on the Renku platform deployed
at `renkulab.io <https://renkulab.io>`__, it will teach you how to use Renku to:

1. `Create a new project`_
2. `Add input data to your project`_
3. `Install and manage Python packages`_
4. `Work using Jupyter Lab`_
5. `Share your results and collaborate with your peers`_

Create a new project
^^^^^^^^^^^^^^^^^^^^

First, head to `renkulab.io <https://renkulab.io>`__ and click on the **Login**
button located on the top right corner of the Renku web interface.

You can sign in using with your GitHub or LinkedIn account by
clicking on the corresponding button.

Once logged in, create a new project by going to the **Projects** (1) page
and clicking on the **New Project** button (2).

.. image:: ui_create_project.png
    :width: 85%
    :align: center
    :alt: Create a new project in UI

Set **tutorial-zhbikes** as your project title, fill-in a short description
and set the project visibility to **Public**.
Click on the **Create** button.

.. warning::

  Be sure to not leave the description field empty. The form will fail
  silently otherwise.

Now that we have a project, we can start working on it by clicking
on **Launch Jupyter Lab**.

Add input data to your project
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

On the Jupyter Lab interface, we can see that a few files already exist.
Let's start by adding data using the `Renku CLI <http://renku-python.readthedocs.io/>`_.

From Jupyter Lab, start a terminal.

.. code-block:: console

    cd work
    git lfs install --local
    renku dataset create zhbikes
    renku dataset add zhbikes https://data.stadt-zuerich.ch/dataset/verkehrszaehlungen_werte_fussgaenger_velo/resource/d17a0a74-1073-46f0-a26e-46a403c061ec/download/2017_verkehrszaehlungen_werte_fussgaenger_velo.csv

.. note::

  Running ``git lfs install --local`` before ``renku dataset add ...`` ensures that
  the data files will tracked using `Git LFS <https://git-lfs.github.com/>`_.

We can see that the two ``renku`` commands manipulate the git repository:

.. code-block:: console

    git log
    git status

Let's push the two fresh commits by running:

.. code-block:: console

    git push

The data file can be opened from Jupyter Lab by going to the **Files** tab
and traversing the ``data`` folder.

Opening the file, we can see it contains *todo todo todo*.

Install and manage Python packages
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Continuing on the same terminal session as in last step, we can install python
packages as usual with ``pip``:

.. code-block:: console

    pip install papermill numpy pandas feather-format
    pip freeze > requirements.txt
    git add requirements.txt
    git commit -m"Installed papermill, numpy, pandas, feather-format"
    git push

.. warning::

  Make sure that you update the ``requirements.txt`` file after you install packages.
  This ensures that the packages needed to work on your project will be used by your
  peers when collaborating on a project.

When updating and pushing the ``requirements.txt`` file to your project repository,
the Renku platform will update the Python stack used to launch your Jupyter Lab instance.
The next time you use the **Launch Jupyter Lab** button, it will come with these
packages already installed.

Work using Jupyter Lab
^^^^^^^^^^^^^^^^^^^^^^

Create new notebooks
""""""""""""""""""""

On the Jupyter Lab interface, use the left bar to go to the **Files** tab.
You can see the list of files and folders from your project.

To create a new notebook, first double click on the **notebooks** folder (1), then
on the '+' button (2). Select 'Python 3' to create a new notebook (3).

TODO: images

To rename the notebook, right click on its name (``Untitled.ipynb``) and select rename.

TODO: image

If you are not familiar with the Jupyter Lab, you can read more on their `documentation <https://jupyterlab.readthedocs.io/en/latest/>`_.
You can take the time to play with the Jupyter Lab interface and new notebooks before continuing.

If you want to save your new notebook(s), go to the console and use ``git`` to
add your work to the repository.

Record you work and make it repeatable
""""""""""""""""""""""""""""""""""""""

First, let's make sure the project repository is clean.
Run:

.. code-block:: console

    git status

Make sure the output ends with ``nothing to commit, working tree clean``.
Otherwise, use ``git add``, ``git commit`` and ``rm`` to cleanup your project repository.

In this section, we will use two pre-existing notebooks to demonstrate how you can
use the `Renku CLI <http://renku-python.readthedocs.io/>`__ to record you work and make it repeatable.
You can view the content of the notebooks, use the following links: `DataPreprocess.ipynb <http://example.com>`_
and `Explore.ipynb <http://example.com>`_. (TODO: link to github)

Use the commands below to add the two notebooks to your project.

.. code-block:: console

    wget -O "notebooks/DataPreprocess.ipynb" <todo>
    wget -O "notebooks/Explore.ipynb" <todo>
    git add notebooks
    git commit -m"Added Data Preprocess and Explore notebooks"
    git push

Now, let's run the two notebooks:

.. code-block:: console

    mkdir notebooks/papermill
    renku run papermill notebooks/DataPreprocess.ipynb notebooks/papermill/DataPreprocess.ipynb \
        -p input_folder data/zhbikes \
        -p output_file data/preprocessed/zhbikes.feather
    renku run papermill notebooks/Explore.ipynb notebooks/papermill/Explore.ipynb \
        -p zhbikes_data data/preprocessed/zhbikes.feather
    git push

`Papermill <https://papermill.readthedocs.io/en/latest/>`_ is a tool useful for
running Jupyter notebooks as python scripts.

Here you can see that we wrapped our command line with ``renku run``.
By doing so, you have created and recorded recipes which will help everyone
including you to rerun and reuse your work.

Reuse your own work
"""""""""""""""""""

Here, we will quickly see one of the advantages of using the ``renku`` command line
tool.

Let's begin by adding some data to the ``zhbikes`` data set:

.. code-block:: console

    renku dataset add zhbikes https://data.stadt-zuerich.ch/dataset/verkehrszaehlungen_werte_fussgaenger_velo/resource/ed354dde-c0f9-43b3-b05b-08c5f4c3f65a/download/2016_verkehrszaehlungen_werte_fussgaenger_velo.csv

We can now see that ``renku`` sees that output files like ``data/preprocessed/zhbikes.feather`` are outdated:

.. code-block:: console

    renku status
    # Output:
    # On branch master
    # Files generated from newer inputs:
    #   (use "renku log [<file>...]" to see the full lineage)
    #   (use "renku update [<file>...]" to generate the file from its latest inputs)
    #
    #         data/preprocessed/zhbikes.feather: data/zhbikes#ac8d549b
    #         notebooks/papermill/DataPreprocess.ipynb: data/zhbikes#ac8d549b
    #         notebooks/papermill/Explore.ipynb: data/zhbikes#ac8d549b

To update all the outputs, we can run the following.

.. code-block:: console

    renku update

Share your results and collaborate with your peers
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
