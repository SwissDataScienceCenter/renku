# Create an environment for your session

Renku supports a variety of ways of working with sessions. To determine what kind of Renku session environment is right for you, answer the following questions:

## 1. What language are you working in?

### → I’m working in R

Renku only has one mode for working with R sessions at the moment. When you create a session launcher, select **global environment** and select the **R** global environment. If you need to install additional packages, see [How to install packages on-the-fly in your session](install-packages-on-the-fly-in-your-session).

### → I’m working in Python

We have multiple ways of working in Python in Renku sessions! Please continue to the next question [Would you like packages to be pre-installed and ready to go when you (or anyone else) launches the session?](#2-would-you-like-packages-to-be-pre-installed-and-ready-to-go-when-you-or-anyone-else-launches-the-session).

### → I’m working in another language

Working in some other language? That’s ok! You can run a wide variety of Docker images in Renku sessions! See [use your own docker image for a Renku session](use-your-own-docker-image-for-renku-session)

## 2. Would you like packages to be pre-installed and ready to go when you (or anyone else) launches the session?

### → Yes please!

See [How to create an environment with custom packages installed](create-environment-with-custom-packages-installed)

### → Having packages pre-installed is not so important to me right now.

If you don’t need custom packages installed, you can get started quickly by simply selecting one or Renku’s pre-made **global environments**. When you create a session launcher, select **global environment** and choose one of the pre-made environments.

When you launch a session with a global environment, you can still install packages on the fly. Please note, however, that these packages will not persist once you shut down your session. You will have to re-install them after you shut down and re-launch your session. In addition, the packages you install won’t be available for anyone else who launches a session from the launcher.

If you’d like to permanently add the packages you need to your session environment, see [How to create an environment with custom packages installed](create-environment-with-custom-packages-installed).

- diagram view

    ```mermaid
    graph TD
      AppDev[Do you want to use your Renku session so share an App/Dashboard or is it for doing interactive work?]
      Language[What language are you working in?]
      Packages[Do you want packages pre-installed in your session?]
      PublicPrivate[Is your code repository public or private?]
      GlobalEnv[Global Environment]
      CodeBasedEnv[Code Based Environment]
      GitHubAction[GitHub Action - *coming soon!* Buildpacks + External Image]
      ExternalImage[External Image]
      AppDev -->|Sharing an app/dashboard| ExternalImage
      AppDev -->|Interactive work| Language
      Language -->|Python| Packages
      Language -->|R| GlobalEnv
      Language -->|other| ExternalImage
      Packages -->|yes| PublicPrivate
      Packages -->|no| GlobalEnv
      PublicPrivate -->|public| CodeBasedEnv
      PublicPrivate -->|private| GitHubAction

    ```
