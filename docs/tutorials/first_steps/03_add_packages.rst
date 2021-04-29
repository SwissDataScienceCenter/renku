.. _add_packages:

Install and manage Python or R packages
----------------------------------

Defining your runtime environment is critical if you would like others to be
able to reuse your work. To achieve this, it is necessary to manage the
software libraries that are needed for your code to execute. In Renku, we rely
on existing conventions for specifying the execution environment. 

Python packages
^^^^^^^^^^^^^^^

In Python, the ``requirements.txt`` file is a standard way to
specify the required libraries. When you created your project, an empty
``requirements.txt`` was also created --- find it in the file browser of your
JupyterLab session by clicking on the **root** button (1), then double-click
the file (2) to open the editor. For the tutorial, we will need the ``pandas``
and ``seaborn`` libraries. We will require specific versions to ensure that the
same environment can be recreated in the future. Enter the following into the
``requirements.txt`` file on the right (3)

.. code-block:: console

    pandas==1.2.3

and **save** it:

.. image:: ../../_static/images/ui_04.1_jupyterlab-setup-requirements.png
    :width: 85%
    :align: center
    :alt: Configuring package dependencies

Going back to the same terminal session as before, we can now
install these packages with ``pip``:

.. code-block:: console

    $ pip install -r requirements.txt

    ...
    Installing collected packages: numpy, pytz, pandas
    Successfully installed numpy-1.20.2 pandas-1.2.3 pytz-2021.1

R packages
^^^^^^^^^^

In R, users may be familiar with running the ``install.packages``
function in the console. To create a reproducible environment for each time 
your session opens, we will specify the necessary packages in the ``install.R``
file. We will demonstrate this with the ``tidyverse`` package. Simply enter
the following in that file.

.. code-block:: console

    install.packages("tidyverse")

Save the file and return to the console in order to run it with 

.. code-block:: console

    R -f install.R
    
To add more packages for more complex projects, simply add the required
``install.packages`` commands to a new line in the ``install.R`` file.


Saving package additions
^^^^^^^^^^^^^^^^^^^^^^^^

Make sure to save these additions to the repository. This can
be done with the ``renku save`` command from the Terminal:

.. code-block:: console

    $ renku save -m 'updated packages'

    Successfully saved to branch master:
    requirements.txt
    OK


.. warning::

  Make sure that you update the *requirements.txt* or *install.R* file 
  after you install new packages. This ensures that the packages needed
  to work on your project will be available to your peers when 
  collaborating on a project.

When an updated *requirements.txt* or *install.R* file is pushed to RenkuLab,
RenkuLab will rebuild the software stack used for sessions. If you shut
down a session, the next time you start a new one,
the packages specified in ``requirements.txt`` or ``install.R`` will already be
available.
