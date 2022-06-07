.. _customize_env:

Customize your Renku Project Environment
========================================


Add packages to your environment
--------------------------------

#.  **Add packaged to your project dependencies**

    To add packages to your environment, add the packages to the dependencies file.
    If you're working in Python, this is the ``requirements.txt`` file.
    If you're working in R, it's ``install.R``.

    .. tabbed:: Python

        Add your project's dependencies to ``requirements.txt``, simply list the package names.
        Optionally, you may specify specific package versions, as shown in the two examples below.

        .. code-block:: console

            pandas
            numpy==1.22.0

    .. tabbed:: R

        To specify your project dependencies in ``install.R``, write the R package install commands.
        This ``install.R`` script will be sourced and run to create your requested environment.

        .. code-block:: console

            install.packages(c("dplyr", "ggplot2"))

        Note that the base R ``install.packages`` function can only install the latest version of a package on CRAN.
        To install a specific version, use ``devtools::install_version``.
        If the package's not on CRAN and exists as a github repo, you can use ``install_github`` instead.
        These two scenarios are shown in the example below.

        .. code-block:: console

            devtools::install_version("ggplot2",
                          version = "1.22.0",
                          repos = "http://cran.us.r-project.org",
                          upgrade="always")
            devtools::install_github("thomasp85/patchwork")

#.  **Commit your changes**

    After you've modified your project's dependencies, commit your changes.

    .. code-block:: shell-session

           $ git commit -m "updated dependencies"

#.  **Start a new renku session**

    The next time you start a renku session, renku will see that you've made changes,
    and offer to build the updated Docker image for you.
    In the new Docker image, your added dependencies will be added!


Customize your Dockerfile
--------------------------

If you'd like to further customize your Renku project environment, for example by modifying the Dockerfile,
take a look at :ref:`customizing`.
