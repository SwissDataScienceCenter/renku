.. _getting_started_renkulab:

Getting Started on `renkulab.io <https://renkulab.io>`__
========================================================

This tutorial will help you get started working on the Renku platform deployed
at `renkulab.io <https://renkulab.io>`__, it will teach you how to use Renku to:

1. `Create a new project`_
2. `Add input data to your project`_
3. `Install and manage Python packages`_
4. `Work using Jupyter Lab`_
5. Share your results and collaborate with your peers

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
on **Start Jupyter Lab**.

*TODO*: Have a **Start Jupyter Lab** button!!

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

    pip install numpy pandas
    pip freeze > requirements.txt
    git add requirements.txt
    git commit -m"Installed numpy, pandas"
    git push

.. warning::

  Make sure that you update the ``requirements.txt`` file after you install packages.
  This ensures that the packages needed to work on your project will be used by your
  peers when collaborating on a project.

When updating and pushing the ``requirements.txt`` file to your project repository,
the Renku platform will update the Python stack used to launch your Jupyter Lab instance.
The next time you use the **Start Jupyter Lab** button, it will come with these
packages already installed.

Work using Jupyter Lab
^^^^^^^^^^^^^^^^^^^^^^

Notebook: data_preprocess_ipynb_

.. _data_preprocess_ipynb: ../_static/zhbikes/Data%20Preprocess.ipynb
