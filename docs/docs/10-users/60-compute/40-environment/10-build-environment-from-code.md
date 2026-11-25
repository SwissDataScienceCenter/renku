# Building environments from code

RenkuLab can build a session environment for you directly from the files in a code repository.
You describe *what* you need (packages, tools, editor extensions, startup steps), and RenkuLab turns them into a ready-to-use Docker image.

This page is a reference for everything you can configure this way. For the step-by-step
instructions to create a launcher, see [Create an environment with custom packages
installed](/docs/users/compute/environment/guides/create-environment-with-custom-packages-installed).


## How RenkuLab reads your repository

RenkuLab looks for all the files described on this page in the **build folder**: the root of the
code repository, or the **context directory** if you set one in the launcher's [advanced
repository settings](/docs/users/compute/environment/guides/create-environment-with-custom-packages-installed#advanced-repository-settings-optional).
Files outside the build folder are ignored.

```
build folder/
├── environment.yml          # or requirements.txt, pyproject.toml, renv.lock, ...  → language (required)
├── project.toml             # build settings: versions, apt packages, coding agents
├── Brewfile                 # extra command-line tools via Homebrew
├── init.d/                  # scripts that run when a session starts
│   └── 00-setup.sh
└── .vscode/
    └── extensions.json      # VSCodium extensions
```

A **language definition file is always required**, even if you only want tools or coding agents.
If you have no packages to install, an empty `requirements.txt` is enough.
Everything else is optional and can be combined with any language and any [frontend](#frontends).

:::warning

Keep only **one** language definition file in the build folder (for example `environment.yml`
*or* `renv.lock`, not both). If several are present, RenkuLab may pick the wrong one or the build
may fail. To have several environments in one repository, put each one in its own folder and point
a separate launcher at each folder with the context directory.

:::

## Python

| Tool | Files in the build folder | Python version set by | 
|------|---------------------------|-----------------------|
| [Conda](#python-conda) (recommended) | `environment.yml` | `python=` in `environment.yml` | 
| [pip](#python-pip) | `requirements.txt` | `BP_CPYTHON_VERSION` in `project.toml` |
| [Poetry](#python-poetry) | `pyproject.toml` (with `[tool.poetry]`), `poetry.lock` | `python` in `pyproject.toml` |
| [uv](#python-uv) | `pyproject.toml`, `uv.lock` | `.python-version` / `requires-python` |

If you use the **JupyterLab** frontend, you don't need to add `jupyterlab` to your dependencies.

### Conda (`environment.yml`) {#python-conda}


<details>
<summary>Here’s an example `environment.yml`:</summary>

    ```yaml
    # Note: name can be changed
    name: "base"
    channels:
      - conda-forge
      - nodefaults.        # important: never use 'defaults'
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

- Pin Python with a single `=` (`python=3.12`), not `==`. `python==3.12` installs `3.12.0`
  without bug fixes, while `python=3.12` installs the latest `3.12.x`.
- Use `conda-forge` (and other community channels such as `bioconda`). The Anaconda `defaults`
  channel is **not allowed**: the build fails if it is listed.
  RenkuLab adds `nodefaults` automatically if it is missing.
- The version of Miniconda used for the build cannot be configured.

### pip (`requirements.txt`) {#python-pip}

<details>
<summary>Here is an example `requirements.txt`:</summary>

    ```
    numpy==2.2.2
    pandas==2.2.3
    ```

</details>

To choose the Python version, add a [`project.toml`](#build-settings-projecttoml):

```toml
[_]
schema-version = "0.2"

[[io.buildpacks.build.env]]
name = "BP_CPYTHON_VERSION"
value = "3.12.*"
```

### Poetry (`pyproject.toml`) {#python-poetry}

RenkuLab runs `poetry install` and sets up a virtual environment. Commit `poetry.lock` for
reproducible builds, and use `package-mode = false` if your repository is not an installable
package.

<details>
<summary>Here is an example `pyproject.toml`:</summary>

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

</details>

### uv (`pyproject.toml` and `uv.lock`) {#python-uv}

RenkuLab installs the exact versions recorded in `uv.lock`. Generate it locally with `uv lock`, and
set the Python version with a `.python-version` file (`uv python pin 3.13`).

In the session, `UV_PROJECT_ENVIRONMENT` points to `$RENKU_WORKING_DIR/.venv`, so the regular
`python` command sees everything installed with `uv add`, without the `--active` flag.

<details>
<summary>Here is an example `pyproject.toml`:</summary>

    ```toml
    [project]
    name = "my-renku-project"
    version = "0.1.0"
    description = ""
    readme = "README.md"
    requires-python = ">=3.12"
    dependencies = [
        "numpy>=2.2.2",
        "pandas>=2.2.3",
        "jupyterlab>=4.3.5",
        "scipy>=1.15.1",
    ]
    ```

</details>

## R

Select **RStudio** as the frontend for R environments.

| Tool | Files in the build folder | R version set by |
|------|---------------------------|------------------|
| [renv](#r-renv) | `renv.lock` (+ `.Rprofile`, `renv/activate.R`) | `R.Version` in `renv.lock` |
| [Conda](#r-conda) | `environment.yml` | `r-base=` in `environment.yml` |

**Which one?** Use renv if your project already uses it, or if you install packages from CRAN and
Bioconductor the usual way. Use Conda if your packages need system libraries that are hard to
install, or if you already manage your tools with Conda. Both support
[Bioconductor](#bioconductor).

### renv (`renv.lock`) {#r-renv}

1. In your R project, initialize renv and install your packages:


   ```r
   install.packages("renv")
   renv::init()
   install.packages(c("dplyr", "ggplot2"))
   ```

2. Snapshot the environment:

   ```r
   renv::snapshot()
   ```

   :::warning

   `renv::snapshot()` only records packages that are installed *and* used in your code.

   :::

3. Commit `renv.lock`, `.Rprofile` and `renv/activate.R` to the build folder.

How the build works:

- **R version:** RenkuLab installs the R version recorded in `renv.lock` (`"R": {"Version": ...}`).
- **System libraries:** common libraries (including HDF5, libxml2, cairo, zlib and image
  libraries) are pre-installed. If a package needs another one, add it as an [apt
  package](#system-packages-apt) or use [Conda](#r-conda).
- **In the session:** the package library is restored into your session storage on the first
  start, so packages you install later are kept. If the R version changes, the library is rebuilt.

### Conda (`environment.yml`) {#r-conda}

CRAN packages are on `conda-forge` as `r-<name>`, Bioconductor packages on `bioconda` as
`bioconductor-<name>` (all lowercase).

```yaml
name: "my-r-project"
channels:
  - conda-forge
  - nodefaults
dependencies:
  - r-base=4.5   # set your R version here
  - r-shiny
  - r-ggplot2
```

- **System libraries are included:** `conda-forge` ships libraries such as libcurl, libxml2 and
  HDF5 as regular packages, installed together with R.
- **In the session:** on the first start, the environment is copied to a writable location in
  your session storage. Packages you add later with `conda install` are kept across restarts, and
  changes to `environment.yml` are synced the next time the session starts.
- Prefer `conda install` (or adding packages to `environment.yml`) over `install.packages()`,
  which compiles from source and can fail on missing system libraries.

### Bioconductor

Pin the Bioconductor release together with the matching R version: each Bioconductor release
supports a single R minor version (for example Bioconductor 3.22 ↔ R 4.5).

**With renv** :
install Bioconductor packages with `BiocManager`, then snapshot. renv records the Bioconductor
release in `renv.lock`, and the build restores packages from that release:

```r
install.packages("BiocManager")
BiocManager::install(version = "3.22")
BiocManager::install("DESeq2")
renv::snapshot()
```

```json
{
  "R": { "Version": "4.5.2", ... },
  "Bioconductor": { "Version": "3.22" },
  ...
}
```

**With Conda:** add the `bioconda` channel and pin `r-base` together with
`bioconductor-biocversion`. The solver refuses mismatched combinations, and system libraries come
from `conda-forge`.

```yaml
name: "my-bioc-project"
channels:
  - conda-forge
  - bioconda
  - nodefaults
dependencies:
  - r-base=4.5
  - bioconductor-biocversion=3.22   # Bioconductor release matching R 4.5
  - bioconductor-deseq2
```

Note that `bioconda` may lag behind new Bioconductor releases: check that the version you need is
on [bioconda](https://bioconda.github.io/) before upgrading.

## Customizing the image

These options work with any language and any frontend.

### Build settings (`project.toml`)

A `project.toml` file in the build folder configures the build. Settings are passed as build
environment variables, and several settings can be combined in one file (see the [deb
sample](https://github.com/SwissDataScienceCenter/renku-frontend-buildpacks/tree/main/samples/deb)):

```toml
[_]
schema-version = "0.2"

[[io.buildpacks.build.env]]
name = "BP_CPYTHON_VERSION"
value = "3.12.*"

[[io.buildpacks.build.env]]
name = "BP_RENKU_AI_AGENTS"
value = "claude"
```

| Variable | Effect |
|----------|--------|
| `BP_CPYTHON_VERSION` | Python version for pip environments |
| `BP_RENKU_AI_AGENTS` | Coding agents to install, see [Coding agents](#coding-agents) |
| `BP_VSCODIUM_VERSION` | VSCodium version |
| `BP_HOMEBREW_VERSION` | Homebrew version (default: latest release) |

### System packages (apt)

Ubuntu packages, for example system libraries needed by R or Python packages, can be listed in
`project.toml`:

```toml
[_]
schema-version = "0.2"

[com.heroku.buildpacks.deb-packages]
install = [
    "libglpk-dev",
    "tree",
]
```

### Homebrew

To install extra command-line tools, add a [`Brewfile`](https://docs.brew.sh/Brew-Bundle-and-Brewfile):

```ruby
brew "lazygit"
brew "gh"
```

The tools are available in the session. The `brew` command itself is not included in the final
image, so add new tools to the `Brewfile` and rebuild.

### Init scripts

Shell scripts in an `init.d/` folder run **every time a session starts**, before the frontend
opens ([init-scripts sample](https://github.com/SwissDataScienceCenter/renku-frontend-buildpacks/tree/main/samples/init-scripts)).
Use them for setup that cannot happen at build time, for example creating folders in your
session storage or writing configuration files.

```
init.d/
├── 00-create-folders.sh
├── 10-configure-git.sh
└── README.md              # ignored: not a .sh file
```

- Only files ending in `.sh` are run, in alphabetical order.
- Keep them fast: they delay every session start.

### VSCodium extensions

List the extensions to pre-install in `.vscode/extensions.json`, using their
[Open VSX](https://open-vsx.org/) identifiers:

```json
{
  "recommendations": [
    "ms-python.python",
    "ms-toolsai.jupyter",
    "REditorSupport.r"
  ]
}
```

- Without this file, the Python and Jupyter extensions are installed by default. When the file is
  present, **only** the listed extensions are installed, so include those two if you need them.
- Extensions are installed into your session storage when the session starts.

### Coding agents

To install AI coding agents in the session, list them in `BP_RENKU_AI_AGENTS` in `project.toml`,
separated by spaces ([coding-agent sample](https://github.com/SwissDataScienceCenter/renku-frontend-buildpacks/tree/main/samples/coding-agent)):

```toml
[_]
schema-version = "0.2"

[[io.buildpacks.build.env]]
name = "BP_RENKU_AI_AGENTS"
value = "pi claude codex"
```

| Value | Agent |
|-------|-------|
| `pi` | [pi](https://www.npmjs.com/package/@earendil-works/pi-coding-agent) |
| `claude` | [Claude Code](https://www.npmjs.com/package/@anthropic-ai/claude-code) |
| `codex` | [Codex](https://www.npmjs.com/package/@openai/codex) |

- Pin a version with `@`, for example `claude@2`.
- To ship shared agent settings, commit a `.pi/`, `.claude/` or `.codex/` folder in the build
  folder. For pi, packages listed in `.pi/settings.json` are installed at build time.
- Each user still signs in to the agent or provides their own API key in the session.

:::warning

Never commit API keys or credentials in these folders: they are copied into the image.

:::

## Frontends

The frontend is the user interface of a session. You choose it when you create a session launcher.

| Frontend | Description |
|----------|-------------|
| VSCodium | Open-source build of VS Code, in the browser |
| JupyterLab | Notebooks, code and data in the browser (Python) |
| RStudio | RStudio |
| ttyd | Terminal in the browser |
| SSH | Connect to the session from your own terminal or editor |

## Building images outside RenkuLab

The [build-image GitHub Action](https://github.com/SwissDataScienceCenter/renku-frontend-buildpacks/tree/main/actions/build-image)
uses the same builder as RenkuLab. Use it to build images in CI, for example for [private
repositories](/docs/users/compute/environment/guides/create-environment-with-custom-packages-private-code-repository).
