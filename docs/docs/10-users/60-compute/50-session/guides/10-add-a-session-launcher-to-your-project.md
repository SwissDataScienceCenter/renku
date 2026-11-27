# Add a session launcher to your project

A launcher defines the environment and compute resources for work on your Renku session.
The basic steps for creating a launcher are:

1. In the **Launchers** section of the project page, click on ➕ to add a new launcher.
2. Choose **Session** in the type selector.
3. Choose and configure an **environment** (see [Choosing an environment for your
   session](#choosing-an-environment-for-your-session)).
4. Select the **Resource class** that best fits your expected computational needs (see [Resource
   Pool and Classes](/docs/users/compute/resource-pools-and-classes)).

   :::tip

   Do you need more resources than are available in RenkuLab’s public resource classes? [Contact
   us!](/docs/users/community) We can configure a custom resource pool for your needs upon demand.

   :::

## Choosing an environment for your session

There are three kinds of environments. Pick the first one that fits your needs:

| You want… | Use | Packages persist? |
|-----------|-----|-------------------|
| To start right away, without installing anything up front | A [global environment](#global-environment) | No, reinstall them after each restart |
| Your packages pre-installed for you and everyone using the launcher | An [environment built from code](#environment-built-from-code) | Yes, they are part of the environment |
| Full control, or a language other than Python or R | [Your own Docker image](#your-own-docker-image) | Yes, they are part of the image |

### Global environment

Renku provides pre-made **global environments**, for example for Python and for R. When you
create the launcher, select **Global environment** and choose one of them.

You can still install packages on the fly in the session (see [How to install packages on-the-fly
in your session](/docs/users/compute/session/guides/install-packages-on-the-fly-in-your-session)).
However, these packages are lost when you shut down the session, and they are not available to
anyone else who launches a session from the launcher.

### Environment built from code

Renku builds an environment for you from a code repository that describes your packages. Add the
repository to your project, select **Create from code** when you create the launcher, and Renku
builds the image.

- **Python**: `environment.yml` (Conda), `requirements.txt` (pip), `pyproject.toml` (Poetry),
  `uv.lock` (uv) or `pixi.toml` (pixi). Choose VSCodium or JupyterLab as the user interface.
- **R**: `renv.lock` (renv) or `environment.yml` (Conda), both including Bioconductor support.
  Choose RStudio as the user interface.
- **Extras**, for any language: system packages, command-line tools (Homebrew), startup scripts,
  VSCodium extensions and [coding
  agents](/docs/users/compute/environment/guides/configure-ai-agents-for-a-session-environment).

:::warning

Coding agents can read the secrets and data connectors available to the launcher. [Limit what they
can access](/docs/users/compute/environment/guides/configure-ai-agents-for-a-session-environment#limit-what-agents-can-access)
before you use them.

:::

Next steps:

- Which files to add and how to write them: [Building environments from
  code](/docs/users/compute/environment/build-environment-from-code).
- **Public** repository: [How to create an environment with custom packages
  installed](/docs/users/compute/environment/guides/create-environment-with-custom-packages-installed).
- **Private** repository: [How to create an environment with custom packages from a private code
  repository](/docs/users/compute/environment/guides/create-environment-with-custom-packages-private-code-repository).

### Your own Docker image

Working in another language, or need something the options above don’t cover? You can run a wide
variety of Docker images in Renku sessions. See [How to use your own docker image for a Renku
session](/docs/users/compute/environment/guides/use-your-own-docker-image-for-renku-session).
