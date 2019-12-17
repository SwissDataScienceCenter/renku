.. _what_is_renku_verbose:

What is the Renku Project?
==========================

The Renku Project is a web platform (renkulab) and a commandline interface (renku)
built on top of open source components for researchers, data scientists, educators,
and students to help manage:

* code (e.g. script.py, script.R, script.bat, ...),
* data (e.g. data.csv, data.h5, data.zip, ...),
* execution environments (e.g. requirements.txt, Dockerfile, ...), and
* workflows (e.g. workflow.cwl, ...)

in a way that helps make research:

* repeatable,
* reproducible,
* reusable, and
* shareable

It is developed as an open source project by the Swiss Data Science Center in a
team split between EPFL and ETHZ.

Renku Project components
^^^^^^^^^^^^^^^^^^^^^^^^

The two parts to the Renku Project are:
* `renkulab`: a web platform for creating, storing, working on, saving, and sharing
your projects and project templates.
* `renku`: a commandline interface (CLI) for annotating datasets and running workflows.

Renkulab
--------

The Renkulab part of the Renku Project is a web platform for creating, storing,
working on, saving, and sharing your projects and project templates.

Renkulab is glue that makes it possible to develop and share your research entirely
in the cloud. You can, directly from a project's homepage on Renkulab, launch Jupyterlab
and RStudio (or anything else you might run from a Docker container) using files
baked into the renkulab project templates and edit your project there. You can
then push the changes back to Renkulab's instance of GitLab (including adding
any software dependencies you had to requirements.txt or install.R).

When you push changes from the launched Jupyterlab or RStudio, a new Docker image
is built for the project corresponding to the latest commit. This ensures that your
environment is also version controlled, so long as you added your software dependencies
to the Dockerfile, requirements.txt, environment.yml, install.R, etc.). You can
launch an environment from any commit.

Renkulab can be accessed publicly at renkulab.io, but there are also several institutional
deployments of the platform. Contact us if you would like to deploy a new instance.

When you work on a project by launching an interactive environment, you can also
use the `renku` commandline interface described below.

Check out the FAQ for topics not addressed here. [permissions, running other people's notebooks]

renku
-----

The `renku` part of the Renku Project is a commandline interface (CLI) for annotating
datasets and wrapping workflows with metadata for aiding iterative development and
building a lineage. You can use this CLI from within an interactive environment
launched from Renkulab or locally by installing the library onto your machine.

`renku` aims to be "git for data science" by applying version control to executed
code tied together with the input files that were dependencies and the output files
that result.


Renkulab
--------

You can use the public instance available at renkulab.io.

Renkulab can be decomposed into the following pieces that are exposed to the user.

Gitlab:
* storing project repositories
* storing each project's large files in a common git LFS object store
* running CI/CD to rebuild Docker images and send them to the image registry when new commits are pushed
* managing groups & permissions

Jupyterlab & RStudio:
* Interactive environments that you can launch from a project's home page on renkulab
* push changes back to renkulab's gitlab
* project-level configurations: i.e. container #CPU/GPU, memory
* user-level configurable environment: i.e. .bashrc

Renkulab glue: Search & Visualize Knowledge Graph
* projects search: public projects and private projects for which you have access rights
* datasets search: datasets that are created/added to a renku project are made discoverable
* (behind the scenes) knowledge graph database that ingests commits from a project when they are pushed to renkulab's gitlab
* Lineage visualization uses this knowledge graph to display a lineage for each file that has been touched by a renku command

Renku
-----

You can follow these instructions <LINK> for running renku locally if you wish to
forego using renkulab or need to interact with your project locally.

Renku can be decomposed into to the following pieces that are exposed to the user.

git:
* when a renku command is run, a commit is created with the files that were added or changed during the command execution
* this commit also includes some hidden .renku metadata that holds the dataset metadata or describes the workflow
* the commit message contains the command you executed, so you can check `git log` to see what you did in the past (running a workflow, creating a dataset, initializing a project)

git-LFS:
* when you add data with `renku dataset` or call `renku run` to generate output data, metadata is added that flags this data to, upon push, enter the git LFS object store in renkulab's gitlab
* if you (or someone else) then clone the project from renkulab's gitlab (or launch an interactive environment from renkulab), you can decide to skip pulling the LFS data to save time/space

renku glue: DATASETS, RUN, UPDATE, RERUN, and more...
* datasets:
* import & publish datasets from/to places like Zenodo and Dataverse that have DOIs
* auto-populate metadata for imported datasets (and created datasets based on their origins)
* user-annotation of datasets with schema.org metadata
* workflows:
* tracking lineage for a workflow (generate a graph that shows input, execution, and output nodes): `renku run <workflow execution line>` & `renku log`
* simplified iterative workflow development (keep making changes to the code/data until you get the output you want): `renku update <output_filename|all>`
* compare outputs generated by the same (maybe stoachastic) workflow: `renku rerun`
* ...check out the renku docs for more!

From both Renkulab & renku
--------------------------

Templates:
* create projects from python & R templated directory structures and files that include renku (cli) installation and renkulab config for launching interactive environments
* customize the files inside the templates by adding your own software dependencies to `requirements.txt`/`install.R`, the Dockerfile, etc.
* create your own directories for organizing your analysis code & use `renku dataset` commands to organize your datasets
* choose a template by selecting from the dropdown menu on renkulab during project creation, or applying the `--from template` flag using `renku init` locally
* share your templates with others

Dockerfile:
* Base image has installations of renku (CLI), jupyterhub, and jupyterlab and/or rstudio
* Write your dependent library installs into the provided Dockerfile, or python in `requirements.txt`, R in `install.R`

The Philosophy of the Renku Project
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The Renku Project is as useful for independent researchers and data scientists as
it is for labs, collaborations, and courses and workshops.

Use Cases:
+ share published work
+ create work to be published
+ create a link between the source of your data and
+ visualize the connectivity between data from your own project and others
+ create template containers with
