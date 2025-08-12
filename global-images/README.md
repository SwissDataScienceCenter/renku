# Collection of global images for renkulab

The subdirectories here contain basic spec for images that can be used in global session launchers in RenkuLab.
They are built using [buildpacks](https://github.com/SwissDataScienceCenter/renku-frontend-buildpacks/) and contain
some basic libraries. Consult the environment definition files in the subdirectories to see which packages they contain.

## Building

To build the images, install [pack](https://buildpacks.io/docs/for-platform-operators/how-to/integrate-ci/pack/) then run:

```shell
$ make all
```

## Configuration

There are a few environment variables you can set to modify the build.

| Environment Variable | Description | Default |
------------------------------------------------
| BUILDER | Builder image to use | ghcr.io/swissdatasciencecenter/renku-frontend-buildpacks/selector:0.0.6 |
| DOCKER_PREFIX | Prefix to use for the image | None |
| FRONTEND | The frontend to add (vscodium or jupyterlab) | vscodium |
| PUBLISH | Push the image to the registry | False |
| RUN_IMAGE | Run image to use | ghcr.io/swissdatasciencecenter/renku-frontend-buildpacks/base-image:0.0.6 |
| TAGS | Comma-separated list of image names | None |

## Adding packages to existing images

The python images use poetry (< 2.0) for dependency management. Use poetry to add packages, for example:

```bash
$ cd datascience
$ poetry add tensorflow
```

Make sure `pyproject.toml` and `poetry.lock` are updated and push the changes.

## Adding additional images

To configure an additional image spec to be built, follow these steps:

- create a new sub-directory with the corresponding environment definition file
- add the new directory name to the Makefile targets
- add the directory name to the `FLAVOR` list in the build matrix in `build-global-renkulab-images.yml`

## Publishing

These images are automatically built and published by a GitHub Action whenever the repository is tagged.
