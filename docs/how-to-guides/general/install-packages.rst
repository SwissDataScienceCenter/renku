.. _install_packages:

Add Packages to your Renku Project Environment
==============================================

The process for adding packages to your Renku project is a little different depending on which language you are using-
for example Python or R.
Alternatively, for both R and Python, you may use conda's environment management system.
All three of these options work with Renku.

Select the tab below for the documentation relevant to you.


.. tab-set::

    .. tab-item:: Python

        **Add packages to your project dependencies**

        To add packages to your environment, add the packages to the dependencies file.
        When you're working in Python, this is the ``requirements.txt`` file.

        To add your project's dependencies to ``requirements.txt``, simply list the package names.
        Optionally, you may specify specific package versions, as shown in the example below.

        .. code-block:: console

            pandas
            numpy==1.22.0

        **Install the packages**

        Install the packages you listed in the dependency file by running the following command in the terminal:

        .. code-block:: console

            $ pip install -r requirements.txt

    .. tab-item:: R

        **Add packages to your project dependencies**

        To add packages to your environment, add the packages to the dependencies file.
        When you're working in R, it's ``install.R``.

        To specify your project dependencies in ``install.R``, write the R package install commands.
        This ``install.R`` script will be sourced and run to create your requested environment.

        .. code-block:: console

            install.packages(c("dplyr", "ggplot2"))

        Note that the base R ``install.packages`` function can only install the latest version of a package on CRAN.
        To install a specific version, use ``devtools::install_version``.
        If the package is not on CRAN and exists as a github repo, you can use ``install_github`` instead.
        These two scenarios are shown in the example below.

        .. code-block:: console

            devtools::install_version("ggplot2",
                        version = "3.3.6",
                        repos = "http://cran.us.r-project.org",
                        upgrade="always")
            devtools::install_github("thomasp85/patchwork")

        **Install the packages**

        Install the packages you listed in the dependency file by running the following command in the terminal:

        .. code-block:: console

            $ R -f install.R

    .. tab-item:: conda

        **Add packages to your project dependencies**

        To add packages to your environment, add the packages to the dependencies file.
        When you're using conda, it's ``environment.yml``.

        To add your project's dependencies to ``environment.yml``, specify an environment name and then a list of dependencies.

        .. code-block:: console

            name: stats
            dependencies:
            - numpy
            - pandas

        For more details, see `conda's documentation <https://docs.conda.io/projects/conda/en/latest/user-guide/tasks/manage-environments.html#create-env-file-manually>`_.

        **Install the packages**

        Install the packages you listed in the dependency file by running the following command in the terminal:

        .. code-block:: console

            $ conda env update --file environment.yml  --prune

Save your changes
-----------------

After you've modified your project's dependencies, make sure to save your changes so that the packages will
automatically be installed the next time you start a Renku session.

.. code-block:: shell-session

    $ renku save -m "updated dependencies"

The next time you start a renku session, the packages will already be installed for you.



Looking for more options?
-------------------------

If you'd like to further customize your Renku project environment, take a look at :ref:`customizing` and :ref:`docker`.
