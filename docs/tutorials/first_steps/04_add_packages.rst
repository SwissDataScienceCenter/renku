.. _add_packages:

Installing and managing packages
--------------------------------

Defining your runtime environment is critical if you would like others to be
able to reuse your work. To achieve this, it is necessary to manage the
software libraries that are needed for your code to execute. In Renku, we rely
on existing conventions for specifying the execution environment.

.. tab-set::

    .. tab-item:: Python

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

        .. figure:: ../../_static/images/ui_04.1_jupyterlab-setup-requirements.png
            :width: 85%
            :align: center
            :alt: Configuring Python package dependencies

        Going back to the same terminal session as before, we can now
        install these packages with ``pip``:

        .. code-block:: console

            $ pip install -r requirements.txt

            ...
            Installing collected packages: numpy, pytz, pandas
            Successfully installed numpy-1.20.2 pandas-1.2.3 pytz-2021.1

    .. tab-item:: Julia

        In Julia, the ``Project.toml`` file specifies package dependencies of
        the project. An empty ``Project.toml`` file is already in the project
        directory when creating the project.

        The easiest way to add packages to a project is to use the ``Pkg`` API. You can
        either use the terminal (2) or start a new Julia Jupyter notebook to run the
        following pattern (3):

        .. code-block:: julia

            using Pkg

            Pkg.add("ZipFile")

        This adds the ``ZipFile`` package.

        .. figure:: ../../_static/images/ui_0.11.11_jupyterlab-setup-Pkg.png
            :width: 85%
            :align: center
            :alt: Configuring Julia package dependencies


        This change will be reflected in the ``Project.toml`` file, as below.

        .. figure:: ../../_static/images/ui_0.11.11_jupyterlab-setup-Project.toml.png
            :width: 85%
            :align: center
            :alt: Viewing Julia package dependencies


        You can also specify the version of a particular package, as follows.

        .. code-block:: julia

            Pkg.add(name="Example", version="0.3.1")


    .. tab-item:: R

        In R, users may be familiar with running the ``install.packages``
        function in the console. To create a reproducible environment for each time
        your session opens, we will specify the necessary packages in the ``install.R``
        file. We will demonstrate this with the ``tidyverse`` package. Simply enter
        the following in that file.

        .. code-block:: r

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

  Make sure that you update the *requirements.txt*, *Project.toml* or
  *install.R* file after you install new packages. This ensures that the
  packages needed to work on your project will be available to your peers when
  collaborating on a project.

When an updated *requirements.txt*, *Project.toml* or *install.R* file is
pushed to RenkuLab, RenkuLab will rebuild the software stack used for sessions.
If you shut down a session, the next time you start a new one, the packages
specified in the respective specification file will already be available.
