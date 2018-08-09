.. _getting_started_renkulab:

Getting Started on `renkulab.io <https://renkulab.io>`__
========================================================

This tutorial will help you get started working on the Renku platform deployed
at `renkulab.io <https://renkulab.io>`__, it will teach you how to use Renku to:

1. `Create a new project`_
2. `Add input data to your project`_
3. Share your results and collaborate with your peers

Create a new project
^^^^^^^^^^^^^^^^^^^^

First, head to `renkulab.io <https://renkulab.io>`__ and click on the **Login**
button located on the top right corner of the Renku web interface.

You can sign in using with your GitHub or LinkedIn account by
clicking on the corresponding button.

Once logged in, create a new project by going to the **Projects** (1) page
and clicking on the **New Project** button (2).

*TODO:* Insert UI screenshot with (1) next to Projects and (2) next to New Project.

Set **tutorial-weather** as your project title, fill-in a short description
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
    renku dataset create weather-ch
    renku dataset add weather-ch http://www.meteoschweiz.admin.ch/product/output/climate-data/homogenous-monthly-data-processing/data/homog_mo_SMA.txt

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

Opening the file, we can see it contains monthly historical data about the
temperature and precipitations around Zürich, Switzerland.
