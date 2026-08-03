# Install packages on-the-fly in your session

You can install packages directly in a running session's terminal to try them out right away, without editing project files or rebuilding your environment.

:::note

Packages installed this way:

- Are only available to you and they are lost when you stop and restart the session, so you'll need to reinstall them every time you launch or resume the session.
- Are not shared with anyone else who launches a session from the same launcher.

If you'd like a package to persist and be available to everyone who uses the launcher, add it to your project's environment definition file, rebuild the launcher and restart the session. See [How to create an environment with custom packages installed](../../environment/guides/create-environment-with-custom-packages-installed).

:::

**Choose your language:**

## Python

Open a terminal in your session and run:

```bash
pip install <package-name>
```

## R {#r}

**Add packages to your project dependencies**

To add packages to your environment, add the packages to the dependencies file: `install.R`.

To specify your project dependencies in `install.R`, write the R package installation commands.

Note that the base R `install.packages` function can only install the latest version of a package on CRAN. To install a specific version, use `devtools::install_version`. If the package is not on CRAN and exists as a GitHub repo, you can use `install_github` instead. These two scenarios are shown in the example below.

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

## conda

If your image uses conda as the python package management system, follow the instructions.

**Add packages to your project dependencies**

To add packages to your environment, add the packages to the dependencies file: `environment.yml`.

To add your project’s dependencies to `environment.yml`, specify an environment name and then a list of dependencies.

```yaml
name: stats
dependencies:
  - numpy
  - pandas
```

For more details, see [conda’s documentation](https://docs.conda.io/projects/conda/en/latest/user-guide/tasks/manage-environments.html#create-env-file-manually).

**Install the packages**

Install the packages you listed in the dependency file by running the following command in the terminal:

```bash
**$** conda env update --file environment.yml  --prune
```
