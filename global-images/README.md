# Collection of global images for renkulab

The subdirectories here contain basic spec for images that can be used in global session launchers in RenkuLab.
They are built using [buildpacks](https://github.com/SwissDataScienceCenter/renku-frontend-buildpacks/) and contain
some basic libraries.

## Building

To build the images, install [pack](https://buildpacks.io/docs/for-platform-operators/how-to/integrate-ci/pack/) then run:

```shell
$ pack build \
    --builder ghcr.io/swissdatasciencecenter/renku-frontend-buildpacks/selector:0.0.6 \
    --run-image ghcr.io/swissdatasciencecenter/renku-frontend-buildpacks/base-image:0.0.6 \
    -p global-images/base \
    --env BP_RENKU_FRONTENDS=jupyterlab \
    renku/renkulab-base-py
```

You may specify a different frontend (e.g. `vscodium`) and choose a different environment spec from the `global-images` directory.

## Publishing

These images are automatically built and published by a GitHub Action whenever the repository is tagged.
