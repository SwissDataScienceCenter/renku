---
title: Global Environments
---

Renku administrators have the ability to create global environments that are
accessible by any user and can be used in any session launcher.

:::note Image visibility
The Docker images used for global environments have to be public.
:::

## Creating a Global Environment

1. Sign in as a Renku administrator
2. Navigate to the Renku administrator panel, you should have a link to it in
   the menu that shows up when you click on your profile icon in the upper right
   corner of the Renku dashboard.
3. Click on "Add Session Environment".
4. Enter the docker image you want to use for the environment, the name and description.
5. If you are using a Renku image as your base then you need to modify the following options
   in the advanced settings section:
   - Default URL: `/`
   - Mount directory: `/home/renku/work`
   - Work directory: `/home/renku/work`
6. _Optional:_ If the docker image will not run a preconfigured Renku frontend,
   then you should modify the the `UID`, `GID`, `CMD` and `ENTRYPOINT` as well.

:::info[Using images built by Renku]
If you use [images published by Renku](https://github.com/orgs/SwissDataScienceCenter/packages?repo_name=renku)
as a base for your global environment images or you build your images with one of our
[frontend buildpacks](https://github.com/orgs/SwissDataScienceCenter/packages?repo_name=renku-frontend-buildpacks)
then you only need to specify the url, mount and work directory in the advanced settings.
:::

## Customizing Renku Images

There are a few ways you can customize existing Renku images:

1. Apply buildpacks on the Renku run image.
   The modifications you can do are limited to what is supported by the buildpacks we publish in our
   [buildpacks repository](https://github.com/SwissDataScienceCenter/renku-frontend-buildpacks),
   such as installing packages for different programming languages or installing
   extensions for VSCodium or Jupyterlab.
2. Use the Renku run image as a base for building your own custom image, then apply
   one of the Renku frontend buildpacks.
3. Take an existing Renku image that is ready-to-use and modify it. In this case you have to be careful
   to not modify the configuration we have already set up that makes the images work on Renku.

:::info[Cloud Native Buildpacks]
We make extensive use of Cloud Native Buildpacks in Renku. If you want to learn
more about them you can consult the [buildpacks documentation](https://buildpacks.io/docs/).
:::

### 1. Modifications Supported by the Renku Buildpacks

This is the easiest way to build custom images, as long as you need to customize aspects
of the image that are supported by our buildpacks. This is how we build the global environment
images for [renkulab.io](https://renkulab.io).
You can see the code for our global images in the [Renku repository](https://github.com/SwissDataScienceCenter/renku/tree/master/global-images)
and you can find our run image [in our image registry](https://github.com/SwissDataScienceCenter/renku-frontend-buildpacks/pkgs/container/renku-frontend-buildpacks%2Frun-image).

For example if you need a global environment image with custom Python packages,
then you can specify these with Poetry. Once you have the `pyproject.toml` and
`poetry.lock` files, you can use the `pack` [CLI](https://github.com/buildpacks/pack)
to build the images.

:::note
It is your responsibility to monitor and upgrade the buildpack versions, the
version of the run image and the builder used in the pack command. These versions
should be kept as up to date as possible. For example, every time you upgrade your
Renku deployment you should check and upgrade the global images as well.
:::

### 2. Modify the Run Image

This option is similar to the one above. However, you would first modify the [Renku run image](https://github.com/SwissDataScienceCenter/renku-frontend-buildpacks/pkgs/container/renku-frontend-buildpacks%2Frun-image).

```Dockerfile
FROM ghcr.io/swissdatasciencecenter/renku/run-image:0.2.0
USER root
RUN ...
USER renku
```

After you have modified the run image you still have to build a fully functional image
by adding a frontend (e.g. Jupyterlab or VSCodium). The easiest way to do this is via
the `pack` command, similarly to how we build the global images for Renku.

### 3. Modify an Existing Renku Image

You can use the images we build for the [global environments](https://github.com/orgs/SwissDataScienceCenter/packages?repo_name=renku) at [renkulab.io](https://renkulab.io) as a starting point.

For example, if you decide to use `ghcr.io/swissdatasciencecenter/renku/py-basic-jupyterlab:2.10.3`.
Then you can structure your `Dockerfile` as follows:

```Dockerfile
FROM ghcr.io/swissdatasciencecenter/renku/py-basic-jupyterlab:2.10.3
USER root
RUN ...
USER renku
```

:::note
You should pick an image that has a regular semver tag like `2.10.3` above.
But it is your responsibility to check the Renku repository and upgrade your images
when new images are published by Renku. Also, if you are making your custom images right now
you should check the Renku repository and use the most recent semver tag you can find, not `2.10.3`.
:::

Make sure that you switch the user back to `renku` in the image if you
change it and you need to perform any operations that require elevated privileges.

Lastly, you should avoid changing the `ENTRYPOINT` or `CMD` in the new images.
If you do change them, then you have to make sure you are launching a frontend that
is compatible with Renku in the advanced configuration section of the global environment
creation form in the administrator panel.

## Building a Fully Custom Image

This is the most challenging way to create images for Renku. You should make sure you
try the other alternatives outlined above and only if none of them are suitable proceed with this option.

There are some general guidelines we can offer for this:

- Use a Debian or Ubuntu base image.
- Use a non-root user to run the image (we use uid/gid of 1000 and a user called `renku` in our images).
- Consider what should be the working and mount directory and configure accordingly in the global environment.
- Remember that everything that is not inside the mount directory will be removed when a session is paused and resumed. It is especially easy for users to forget this limitation.
- The Kubernetes liveness checks that Renku adds to every session should be compatible
  with whatever you will be launching in the image. If the liveness checks are not working properly
  then the sessions with your new image will not be accessible and will constantly restart.
