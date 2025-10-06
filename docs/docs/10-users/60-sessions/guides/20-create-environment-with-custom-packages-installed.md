# Create an environment with custom packages installed

If you’d like a set of custom packages to be installed and ready to go when you (or anyone else) launches a session in your project, you can take advantage of Renku’s **code based environments**.

With Renku code based environments, you can point Renku to a code repository that contains an environment definition file, such as a `environment.yml`, `requirements.txt`, or `pyproject.toml`, and Renku will build a custom environment for your session for you!

This guide has 2 parts:

- First, we will walk through what kinds of files you can use to [define code based
  environments](#what-kinds-of-environment-definitions-are-supported) in Renku.
- Second, we’ll show you how to [create a code-based
  environment](#how-to-create-a-code-based-environment-for-your-renku-session) for your project.

## What kinds of environment definitions are supported?

RenkuLab’s code-based environments currently supports creating **Python** environments. Support for more languages is coming soon!

:::tip

Are you working in R? Support for R is coming soon. For now, for working with R please see
[installing packages on the fly in your
session](/docs/users/sessions/guides/install-packages-on-the-fly-in-your-session).

:::

### Defining a Python Environment

There are multiple ways you can define a python environment for your Renku session:

- [Miniconda (`environment.yml`) (recommended)](#miniconda-environmentyml-recommended)
- [Pip (`requirements.txt`)](#pip-requirementstxt)
- [Poetry (`pyproject.toml`)](#poetry-pyprojecttoml)

See below for more details on how to use each of these systems.

:::info
If you’d like to learn more about the system Renku uses to create python environments, check
out [Paketo Buildpacks](https://paketo.io/docs/howto/python/#use-a-package-manager).
:::

#### Miniconda (`environment.yml`) (recommended)

Include an `environment.yml` file located at the root (top level) of the code repository.

- Here’s an example `environment.yml`:

    ```yaml
    # Note: name can be changed
    name: "base"
    channels:
    	# important: do not use 'defaults', but always include 'nodedefaults'
      - conda-forge
      - nodefaults
    dependencies:
      - python=3.9 # set your python version here
      - tensorflow-gpu
      - numba
      - scikit-learn
      - pandas
      - seaborn
      - matplotlib
      - jupyterlab
      - xarray
      - pip
      - pip:
    	  # put packages to be installed by pip here
        - deepsmiles
        - rdkit
    # Note: prefix can be changed
    prefix: "/opt/conda"
    ```


Important usage notes:

- Regarding conda channels:
    - `nodefaults` must be included. See example above.
    - We recommend using the `conda-forge` channel or other non-anaconda channels.
    - The `defaults` channel is not recommended and often results in failed builds (due to rate limits imposed by Anaconda).
- Please note that miniconda can only be used at this time to create Python environments, not R environments.
- Environments defined with one of these files will be created via miniconda. Configuring a version of miniconda is not supported.

#### Pip (`requirements.txt`)

Include a valid `requirements.txt` file at the root (top level) of your code repository. Renku will create an environment from this file using `pip`.

:::info

Defining a python environment via a requirements.txt file will create a python environment with
python version `3.10`. It is not currently possible to specify a different python version.

:::

- Here is an example `requirements.txt`:

    ```
    numpy==2.2.2
    pandas==2.2.3
    jupyterlab==4.3.5
    ```

#### Poetry (`pyproject.toml`)

Including a `pyproject.toml` file at the root of your code repository triggers the poetry installation process. The buildpack will invoke `poetry` to install the application dependencies defined in `pyproject.toml` and set up a virtual environment.

Note that poetry version `1.8.3` will be used.

- Here is an example `pyproject.toml`:

    ```toml
    [tool.poetry]
    name = "python-poetry-1"
    version = "0.1.0"
    description = ""
    authors = ["Flora Thiebaut <flora.thiebaut@sdsc.ethz.ch>"]
    readme = "README.md"
    # Important: use `package-mode = false` if the repository
    # is not an installable package.
    package-mode = false
    
    [tool.poetry.dependencies]
    python = "^3.12"
    numpy = "^2.2.2"
    pandas = "^2.2.3"
    jupyterlab = "^4.3.5"
    scipy = "^1.15.1"
    torch = "^2.6.0"
    pytorch-lightning = "^2.5.0.post0"
    
    [build-system]
    requires = ["poetry-core"]
    build-backend = "poetry.core.masonry.api"
    ```

## How to create a code-based environment for your Renku session

:::info

This functionality only works with **public code repositories**. If your code repository is
private, please see [Creating a custom environment from a private code
repository](/docs/users/sessions/guides/create-environment-with-custom-packages-private-code-repository).

:::

1. Make sure the code repository that contains your environment definition file is added to your Renku project.
2. Create a **new session launcher**
3. Select the  **Create from code** option

    ![image.png](./create-environment-with-custom-packages-installed-10.png)

4. Select the **Code repository**

    :::info

    Note: The code repository must be public. If your code repository is private, please see
    [Creating a custom environment from a private code
    repository](/docs/users/sessions/guides/create-environment-with-custom-packages-private-code-repository)

    :::

    :::info

    Note: The code repository must be already linked to the Renku project

    :::

5. Select the **Environment** **type** (Python, *more coming soon*)
6. Select the **User interface** you’d like your session to have (VSCodium or Jupyterlab, *more coming soon*).
7. Click **Next**
8. Define the **name** of the Session Launcher
9. Select the default **compute resources**
10. Click on **Add session launcher**

The environment is now being built by RenkuLab. You can see the status on the session launcher.

![image.png](./create-environment-with-custom-packages-installed-20.png)

When the environment is built, you can launch your session.

## Updating a code-based environment

1. When you want to make changes to your environment (add new packages), first update the environment definition file in the code repository where the environment is defined.
2. Then, rebuild the environment in RenkuLab:
    1. Click on the session launcher to open the session launcher side panel.
    2. Navigate to the **Session Environment** section.
    3. Click on **Rebuild**.

## [experimental] Using a dashboard with a code-based environment

:::warning

Temporary and experimental! The description below is a current work-around but we will streamline
this workflow in the near future!

:::

Your project might have a nice dashboard inside, which you would want others to see. If your
repository’s requirements include a dashboard tool (e.g. streamlit or plotly dash), it is relatively
simple to have Renku build the image, and convert it to show the dashboard instead of VSCodium. This
way, you can have, for example, one launcher for development that you use and another to show others
the results.

To set up a dashboard with an environment built from your repository, you can follow these steps:

1. Follow the steps for creating a [code-based environment](/docs/users/sessions/guides/create-environment-with-custom-packages-installed) above.
2. Once the image is done building, edit the environment and change it to a “Custom Environment”
3. Edit the `Command` to be `["bash", "-c"]` and `Args` to correspond to your app - see common examples [here](/docs/users/sessions/guides/use-your-own-docker-image-for-renku-session).

Once you are done, your environment configuration should look something like this:

![image.png](./create-environment-with-custom-packages-installed-30.png)

And your launcher set up could be, for example:

![image.png](./create-environment-with-custom-packages-installed-40.png)

## Creating a code-based environment from a private code repository

Please see [Creating a custom environment from a private code
repository](10-users/60-sessions/guides/25-create-environment-with-custom-packages-private-code-repository.md).
