---
title: Model & Algorithm Development
---

# Reproducible environments without the Docker detour

## Point at your dependencies file and Renku builds a session image automatically for you and your collaborators

The "works on my machine" issue is a constant burden that slows down scientific collaboration. Traditional solutions are either fragile (a long `README.md` listing exact package versions) or steep (learning Docker well enough to write and maintain a `Dockerfile` for a non-trivial scientific stack). Neither is a reasonable expectation for a researcher whose primary expertise is hydrology, structural biology or econometrics rather than software engineering.

Renku has a smooth solution: the environment specification you already write in a `requirements.txt`, `environment.yml`, `renv.lock` or similar is enough. Renku reads it, builds the container automatically, and every subsequent session runs inside that container. You do not have to worry about  the Dockerfile, nor `conda env create` nor exchange "did you install the right CUDA version?" emails.

### Environment builds from files you already maintain**

When you create a session launcher in Renku and point it to a code repository, Renku scans the repository for a recognised environment definition file and builds a container image from it. Supported file types include:

- **Python**: `requirements.txt`, `environment.yml` (conda), `pyproject.toml` (Poetry/PEP 517)
- **R**: `renv.lock` (renv package manager)
- **Julia**: `Project.toml` / `Manifest.toml`

The build happens once; subsequent session launches use the built image unless you push a change to your dependency file and click on **Rebuild** to your session launcher. You do not write a Dockerfile, manage a container registry, or deal with base image selection: Renku handles the container plumbing so you can stay in the science layer. For full details, see [How to create an environment with custom packages installed](../../../sessions/guides/environments/create-environment-with-custom-packages-installed).

### Direct integration with GitHub and GitLab: every push is immediately available**

Renku connects directly to your existing code repositories on GitHub or GitLab. Once you [link your Renku account to your GitHub or GitLab account](../../../code/guides/connect-renku-account-to-github-or-gitlab-account), you can point a session launcher at any repository you have access to. Every time you push a commit, after fixing a bug in your analysis script, updating a dependency, or adding a new notebook, it reflects on the code repository.

This means your development cycle is a standard git workflow: edit locally, push to GitHub, launch a Renku session with the latest version of your code already present. No manual file uploads, no `rsync` to a remote server, no "remember to pull before you run" instructions to collaborators.

### Private repositories, without exposing credentials to collaborators**

If your code repository is private, either because it contains unpublished methodology, proprietary algorithms, or pre-publication data processing scripts, Renku handles authentication transparently. You authorise Renku to access the repository using your GitHub or GitLab credentials. Collaborators who launch sessions from the same project use their own GitHub/GitLab credentials to access.  In either case, no one's personal access token is exposed through the Renku interface. No one without access to your code repository can access the code. Even more, if you create a session launcher from the dependencies in your private code repository, only those who have access to the code repository can launch a session. See [How to create an environment from a private code repository](../../../sessions/guides/environments/create-environment-with-custom-packages-private-code-repository) for the step-by-step setup.

### Global environments for rapid prototyping before you commit to a dependency set**

Not every session needs a fully specified custom environment. When you are in an early exploratory phase, either trying out a new library, sketching a new analysis approach, or running a quick sanity check, Renku's pre-built global environments give you a working Python, R, or Julia session in seconds, without configuring anything. Global environments come with a standard scientific stack pre-installed, and you can install additional packages on the fly with `pip install` or `install.packages()` for the duration of the session.

When your prototype hardens into a real analysis, you lock the dependencies into a `requirements.txt` or `renv.lock`, commit them to your repository, and Renku can build a reproducible custom environment from that point forward with session launchers created from code. The transition from "exploratory sketch" to "locked reproducible pipeline" is a single git commit.


:::tip  Remember

Environment definitions live in your code repository alongside your code. That means your git history is also your environment history, roll back a broken dependency change the same way you roll back a buggy function.

:::