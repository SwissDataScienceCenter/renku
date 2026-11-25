# Environment

An environment is a docker-based environment that can run in a RenkuLab interactive [Session](/docs/users/compute/session) or a background [Job](/docs/users/compute/job).

An environment consists of:

1. a reference to a docker image, and
2. the configuration required to run that docker image on RenkuLab.

Renku provides several _global environments_ that come pre-configured and ready to use for interactive sessions. Alternatively, you can create your own custom environment [from a requirements file](/docs/users/compute/environment/guides/create-environment-with-custom-packages-installed) or [with your own docker image](/docs/users/compute/environment/guides/use-your-own-docker-image-for-renku-session). Job launchers use custom environments only (built from code or from an external image).

Environments are connected to a Renku project via a [Launcher](/docs/users/compute/launcher).


## Building environments from code

RenkuLab can build an environment for you from the files in a code repository: no Dockerfile
needed. Add the files you need to the repository and RenkuLab builds the image:

- **Python**: `environment.yml`, `requirements.txt`, `pyproject.toml` (Poetry) or `uv.lock`.
- **R**: `renv.lock` or `environment.yml`, both including Bioconductor support.
- **Extras**: system packages and coding agents (`project.toml`), command-line tools
  (`Brewfile`), startup scripts (`init.d/`), VSCodium extensions (`.vscode/extensions.json`).

Then choose a frontend: VSCodium, JupyterLab, RStudio, ttyd or SSH.

- Reference of all supported files and options: [Building environments from
  code](/docs/users/compute/environment/build-environment-from-code)
- Step-by-step guide: [Create an environment with custom packages
  installed](/docs/users/compute/environment/guides/create-environment-with-custom-packages-installed)
