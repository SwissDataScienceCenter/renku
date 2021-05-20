.. _python_environment:

Install and manage Python packages
----------------------------------

Defining your runtime environment is critical if you would like others to be
able to reuse your work. To achieve this, it is necessary to manage the
software libraries that are needed for your code to execute.

In Renku, we rely on existing conventions for specifying the execution
environment. In Python, the ``requirements.txt`` file is a standard way to
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

And we need to capture this change in git.

.. code-block:: console

    $ renku save -m 'updated requirements'

    Successfully saved to branch master:
    requirements.txt
    OK

.. warning::

  Make sure that you update the *requirements.txt* file after you install
  new packages. This ensures that the packages needed to work on your project
  will be available to your peers when collaborating on a project.

When an updated *requirements.txt* file is pushed to RenkuLab, RenkuLab will
rebuild the software stack used for the interactive environments. If you shut
down an interactive environment, the next time you start a new one,
the packages specified in ``requirements.txt`` will already be
available in the new environment.
