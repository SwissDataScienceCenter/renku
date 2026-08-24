# Publish an App

Renku can host a web application (a dashboard, a demo, an interactive report) at
a stable public URL that anyone can open, without a Renku account and without
launching a session. This page walks through creating one.

For what an app is and how it differs from a session, see
[App](../sessions/app).

## Before you start

To create an app launcher you need:

- **A public project.** App launchers, and the apps they create, are public-only
  for now.
- **Write permission** on that project. Starting and stopping an app is an
  editor or owner action; opening it is not. See [Project
  Permissions](../collaboration/permissions#project-permissions).
- **An image to run.** Either Renku builds it for you from a code repository
  (see the `Procfile` section [below](#1-listen-on-the-port-renku-assigns)), or
  you supply a container image you have already built and pushed.

## 1. Listen on the port Renku assigns

A session built from code gets a Renku-provided frontend such as JupyterLab,
RStudio, or VSCodium. An app does not: **your image runs your own web server**.
Renku tells it where to listen, through two environment variables that are set
in the container and cannot be overridden:

| Variable             | Value                          | Meaning                                    |
| -------------------- | ------------------------------ | ------------------------------------------ |
| `RENKU_SESSION_PORT` | The launcher's configured port | The port your app must listen on           |
| `RENKU_SESSION_IP`   | `0.0.0.0`                      | Listen on all interfaces, not on localhost |

**If Renku builds your image**, add a file called
[`Procfile`](https://devcenter.heroku.com/articles/procfile) to the root of your
repository, or to the context directory configured on the launcher. Renku reads
it with the [Paketo Procfile
buildpack](https://github.com/paketo-buildpacks/procfile), and its `web:` line is
the command Renku runs:

```procfile
web: streamlit run app.py --server.port $RENKU_SESSION_PORT --server.address 0.0.0.0
```

Some other examples:

```procfile
# Shiny (R)
web: R -e "shiny::runApp('.', host='0.0.0.0', port=as.integer(Sys.getenv('RENKU_SESSION_PORT')))"
```

```procfile
# An ASGI app behind uvicorn
web: uvicorn main:app --host 0.0.0.0 --port $RENKU_SESSION_PORT
```

**If you supply your own image**, there is no `Procfile`. Whatever the image runs
by default (its `ENTRYPOINT` and `CMD`) has to be the web server, and it has to
bind that port on all interfaces.

:::warning

Getting this wrong is the most common reason an app never starts. If your server
binds its framework's default port instead of `$RENKU_SESSION_PORT`, or binds
`127.0.0.1` instead of `0.0.0.0`, nothing ever reaches it. Renku waits for a
response that cannot arrive and then reports an error.

:::

## 2. Create the app launcher

1. Open your project and add a session launcher.
2. Choose **App** as the launcher type.
3. Choose where the image comes from:
   - **Create from code**: point the launcher at the repository holding your app
     and its `Procfile`, and pick a builder. Renku builds the image with
     buildpacks, the same way it builds custom session environments.
   - **External environment**: give the launcher your container image and the
     port your server listens on.
4. Set a [resource class](../sessions/resource-pools-and-classes) if the default
   is not enough for your app.
5. Save the launcher. If Renku is building the image, the launcher shows the
   build progress.

An app launcher sits alongside your session launchers, and the same editing
surfaces (environment, resource class, environment variables) apply to it.

:::note

Editing the launcher does not change a running app. It keeps its old definition
until it is stopped and started again.

:::

## 3. Start the app

Once there is an image to run (the build has succeeded, or you supplied one),
select **Start** on the launcher. The status indicator moves from _Starting_ to
_Live_, and the primary action becomes **Open**. If a previous attempt failed,
the button reads **Restart** instead.

## 4. Share the link

Use **Copy app URL** on the launcher menu, or the **Public URL** shown in the
launcher's panel. Both give you a `renkulab.io` address of the form:

```
https://renkulab.io/p/<namespace>/<project>/apps/<launcher-id>
```

This link can be shared with anyone — it does not require a Renku account.

:::tip

Share the Renku URL above, not the address your browser ends up at once the app
opens — only the Renku URL knows how to wake a sleeping app and stays valid
across the app being stopped and started again.

:::

## Stopping an app

Select **Stop app** on the launcher menu. The launcher and its configuration
stay, so you can start the app again whenever you like — but apps have no
persistent storage, so anything the app wrote to its own filesystem is gone.

Deleting the launcher, or changing the project's visibility away from public,
also stops the app (see [App](../sessions/app#editing-an-app-launcher)).

## Troubleshooting

Cluster operators diagnosing from the Kubernetes side should see
[Apps § Troubleshooting](../../admins/operation/apps#troubleshooting) instead.

**The app reports an error after starting.**
Almost always the port. Check that your server (the `web:` command in your
`Procfile`, or your image's entry point) binds `$RENKU_SESSION_PORT` and host
`0.0.0.0`. An app that never answers is given a few minutes and then marked
failed.

**The app cannot find a file that is in my repository.**
Apps do not clone repositories. The file has to be in the image, which for a
build from code means committed to the repository the launcher builds from.

**A data connector is missing inside the app.**
Check the launcher's panel: it lists the connectors the app mounts and says how
many were left out. Only public connectors that need no credentials are mounted;
see [What an app can reach](../sessions/app#what-an-app-can-reach).

**"Another launcher in this project already has an app."**
A project can have one app at a time, across all of its launchers. Stop the
existing app before starting a different one.

**"An app launcher can only be created in a public project."**
Change the project's visibility to public, or create the app in a different
project.

**The Start button is disabled.**
Either you lack write permission, the project is not public, the image has not
finished building, or another launcher in the project already has an app. Hover
over the button for the specific reason.
