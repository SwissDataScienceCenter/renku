# How to create an environment with custom packages installed

If you’d like a set of custom packages to be installed and ready to go when you (or anyone else) launches a session in your project, you can take advantage of Renku’s **code based environments**.

With Renku code based environments, you can point Renku to a code repository that contains an environment definition file, such as a `environment.yml`, `requirements.txt`, or `pyproject.toml`, and Renku will build a custom environment for your session for you!

This guide has 2 parts: 

- First, we will walk through what kinds of files you can use to [define a code based environment](#what-kinds-of-environment-definitions-are-supported) in RenkuLab.
- Second, we’ll show you [how to create a code-based environment](#how-to-create-a-code-based-environment-for-your-renku-session) for your project.

## What kinds of environment definitions are supported?

RenkuLab’s code-based environments currently supports creating **Python** environments. Support for more languages is coming soon!

:::info
Do you need to install R packages in your Renku session? See [R](https://www.notion.so/R-ac30903cc2784066b16cc35a2b15d2d5?pvs=21). 

:::

# Defining a Python Environment

There are multiple ways you can define a python environment for your Renku session:

- [Miniconda (`environment.yml`) (recommended)](#miniconda-environmentyml-recommended)
- [Pip (`requirements.txt`)](#pip-requirementstxt)
- [Poetry (`pyproject.toml`)](#poetry-pyprojecttoml)

See below for more details on how to use each of these systems.


:::info
If you’d like to learn more about the system Renku uses to create python environments, check out [Paketo Buildpacks](https://paketo.io/docs/howto/python/#use-a-package-manager).
:::


### Miniconda (`environment.yml`) (recommended)

Include an `environment.yml` file located at the root (top level) of the code repository.

- Here’s an example `environment.yml`:

<details>
<summary>Here’s an example `environment.yml`:</summary>

```
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
</details>
      

Important usage notes:

- Regarding conda channels:
    - `nodefaults` must be included. See example above.
    - We recommend using the `conda-forge` channel or other non-anaconda channels.
    - The `defaults` channel is not recommended and often results in failed builds (due to rate limits imposed by Anaconda).
- Please note that miniconda can only be used at this time to create Python environments, not R environments.
- Environments defined with one of these files will be created via miniconda. Configuring a version of miniconda is not supported.

### Pip (`requirements.txt`)

Include a valid `requirements.txt` file at the root (top level) of your code repository. Renku will create an environment from this file using `pip`.

:::info
Defining a python environment via a requirements.txt file will create a python environment with python version `3.10`. It is not currently possible to specify a different python version. 
:::

<details>
<summary>Here is an example `requirements.txt`:</summary>

```
    numpy==2.2.2
    pandas==2.2.3
    jupyterlab==4.3.5
```
</details>        

### Poetry (`pyproject.toml`)

Including a `pyproject.toml` file at the root of your code repository triggers the poetry installation process. The buildpack will invoke `poetry` to install the application dependencies defined in `pyproject.toml` and set up a virtual environment.

Note that poetry version 1.8.3 will be used.

<details>
<summary>Here is an example `pyproject.toml`:</summary>

```
toml
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
</details>  
    

## Defining an R Environment

:::info

This feature is coming soon. For now, please see [R](https://docs.renkulab.io/en/latest/docs/users/sessions/guides/install-packages-on-the-fly-in-your-session). 

:::

## How to create a code-based environment for your Renku session

:::info

**Important**: This functionality only works with **public code repositories**. If your code repository is private, please see [](https://www.notion.so/1c40df2efafc80de9bbffbf61ea4b2f7?pvs=21). 

:::

1. Make sure the code repository that contains your environment definition file is added to your Renku project. 
2. Create a **new session launcher**
3. Select the  **Create from code** option
    
    ![image.png](attachment:03c43222-4864-48b6-8a62-3650f7c0feba:image.png)
    
4. Select the **Code repository**
    
    <aside>
    <img src="/icons/info-alternate_blue.svg" alt="/icons/info-alternate_blue.svg" width="40px" />
    
    Note: The code repository must be public
    
    </aside>
    
    <aside>
    <img src="/icons/info-alternate_blue.svg" alt="/icons/info-alternate_blue.svg" width="40px" />
    
    Note: The code repository must be already linked to the Renku project
    
    </aside>
    
5. Select the **Environment** **type** (Python, *more coming soon*)
6. Select the **User interface** you’d like your session to have (VSCodium or Jupyterlab, *more coming soon*).
7. Click **Next**
8. Define the **name** of the Session Launcher
9. Select the default **compute resources**
10. Click on **Add session launcher**

The environment is now being built by RenkuLab. You can see the status on the session launcher.

![image.png](attachment:b1e28675-3d80-42ba-9a1a-67983f329426:image.png)

When the environment is built, you can launch your session.

# Updating a code-based environment

1. When you want to make changes to your environment (add new packages), first update the environment definition file in the code repository where the environment is defined. 
2. Then, rebuild the environment in RenkuLab: 
    1. Click on the session launcher to open the session launcher side panel.
    2. Navigate to the **Session Environment** section.
    3. Click on **Rebuild**.

# [experimental] Using a dashboard with a code-based environment

<aside>
<img src="/icons/traffic-cone_orange.svg" alt="/icons/traffic-cone_orange.svg" width="40px" />

Temporary and experimental: the description below is a current work-around but we will streamline this workflow in the near future!

</aside>

Your project might have a nice dashboard inside, which you would want others to see. If your repository’s requirements include a dashboard tool (e.g. streamlit or plotly dash), it is relatively simple to have Renku build the image, and convert it to show the dashboard instead of VSCodium. This way, you can have, for example, one launcher for development that you use and another to show others the results. 

To set up a dashboard with an environment built from your repository, you can follow these steps:

1. Follow the steps for creating a [code-based environment](https://www.notion.so/How-to-create-an-environment-with-custom-packages-installed-1960df2efafc801b88f6da59a0aa8234?pvs=21) above.
2. Once the image is done building, edit the environment and change it to a “Custom Environment”
3. Edit the `Command` to be `["bash", "-c"]` and `Args` to correspond to your app - see common examples [here](https://www.notion.so/How-to-use-your-own-docker-image-for-a-Renku-session-11f0df2efafc80af848ffcaf9ccff31c?pvs=21).

Once you are done, your environment configuration should look something like this:

![image.png](attachment:5fafd12c-558d-4666-98b8-4ebda54e0c1b:image.png)

And your launcher set up could be, for example: 

![image.png](attachment:db9ae744-f856-4119-9774-62a13c19b383:image.png)

#