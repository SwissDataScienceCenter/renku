# Add a session launcher to your project

A session launcher defines the environment and compute resources for a Renku session. The basic steps for creating a session launcher are:

1. In the **Sessions** section of the project page, click on ➕ to add a new session launcher.
2. Choose and configure an **environment** (see [Creating an environment for your session](#creating-an-environment-for-your-session) below).
3. Select the **Resource class** that best fits your expected computational needs.

   :::tip

   Do you need more resources than are available in RenkuLab’s public resource classes? [Contact
   us!](../../../community) We can configure a custom resource pool for your needs upon demand.

   :::

## Creating an environment for your session

Renku supports a variety of ways of working with sessions. To determine what kind of Renku session
environment is right for you, answer the following questions:

### 1. What language are you working in?

#### → I’m working in R

Renku only has one mode for working with R sessions at the moment. When you create a session
launcher, select **global environment** and select the **R** global environment. If you need to
install additional packages, see [How to install packages on-the-fly in your
session](install-packages-on-the-fly-in-your-session).

#### → I’m working in Python

We have multiple ways of working in Python in Renku sessions! Please continue to the next question
[2. Would you like packages to be pre-installed and ready to go when you (or anyone else) launches
the
session?](#2-would-you-like-packages-to-be-pre-installed-and-ready-to-go-when-you-or-anyone-else-launches-the-session).

#### → I’m working in another language

Working in some other language? That’s ok! You can run a wide variety of Docker images in Renku
sessions! See [How to use your own docker image for a Renku
session](use-your-own-docker-image-for-renku-session)

### 2. Would you like packages to be pre-installed and ready to go when you (or anyone else) launches the session?

#### → Yes please!

If you’d like a set of custom packages to be installed and ready to go when you (or anyone else)
launches a session in your project, you can take advantage of Renku’s code based environments.

With Renku code based environments, you can point Renku to a code repository that contains an
environment definition file, such as a environment.yml, requirements.txt, or pyproject.toml, and
Renku will build a custom environment for your session for you!

→ If your code repository is **public**, see [How to create an environment with custom packages
installed](create-environment-with-custom-packages-installed).

→ If your code repository is **private**, see [How to create an environment with custom packages from
a private code
repository](create-environment-with-custom-packages-private-code-repository).

#### → Having packages pre-installed is not so important to me right now.

If you don’t need custom packages installed, you can get started quickly by simply selecting one or
Renku’s pre-made **global environments**. When you create a session launcher, select **global
environment** and choose one of the pre-made environments.

When you launch a session with a global environment, you can still install packages on the fly.
Please note, however, that these packages will not persist once you shut down your session. You will
have to re-install them after you shut down and re-launch your session. In addition, the packages
you install won’t be available for anyone else who launches a session from the launcher.

If you’d like to permanently add the packages you need to your session environment, see [How to
create an environment with custom packages
installed](create-environment-with-custom-packages-installed).
