# How to install packages on-the-fly in your session

**Choose your language:**

# Python

Renku can build an environment based on a Python dependencies file for you (no knowledge of Docker required)!

To get started, see [How to create an environment with custom packages installed](How%20to%20create%20an%20environment%20with%20custom%20packages%20%201960df2efafc801b88f6da59a0aa8234.md).

# R

**Add packages to your project dependencies**

To add packages to your environment, add the packages to the dependencies file: `install.R`.

To specify your project dependencies in `install.R`, write the R package install commands.

Note that the base R `install.packages` function can only install the latest version of a package on CRAN. To install a specific version, use `devtools::install_version`. If the package is not on CRAN and exists as a github repo, you can use `install_github` instead. These two scenarios are shown in the example below.

```r
devtools::install_version("ggplot2",
            version = "3.3.6",
            repos = "http://cran.us.r-project.org",
            upgrade="always")
devtools::install_github("thomasp85/patchwork")
```

**Install the packages**

Every time you start or resume the session, install the packages you listed in the dependency file by running the following command in the terminal:

```bash
**$** R -f install.R
```

<aside>
<img src="https://www.notion.so/icons/report_yellow.svg" alt="https://www.notion.so/icons/report_yellow.svg" width="40px" />

 Note: You need to install the dependencies every time you launch or resume your session.

</aside>

# conda

If your image uses conda as the python package management system, follow the instructions.

**Add packages to your project dependencies**

To add packages to your environment, add the packages to the dependencies file: `environment.yml`.

To add your project’s dependencies to `environment.yml`, specify an environment name and then a list of dependencies.

```yaml
name: stats
dependencies:
- numpy
- pandas
```

For more details, see [conda’s documentation](https://docs.conda.io/projects/conda/en/latest/user-guide/tasks/manage-environments.html#create-env-file-manually).

**Install the packages**

Install the packages you listed in the dependency file by running the following command in the terminal:

```bash
**$** conda env update --file environment.yml  --prune
```

<aside>
<img src="https://www.notion.so/icons/report_yellow.svg" alt="https://www.notion.so/icons/report_yellow.svg" width="40px" />

 Note: You need to install the dependencies every time you launch or resume your session.

</aside>