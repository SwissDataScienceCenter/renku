---
title: Apps
---

Apps let project members publish a long-lived web application (a dashboard, a demo, an interactive report) at a stable public URL that anyone can open without a Renku account and without starting a session. An app is backed by a **Knative Service** that Renku creates in the cluster on the user's behalf, and it scales to zero when nobody is using it.

Apps are disabled by default and have prerequisites that have to be in place before you turn them on:

- [Requirements](../installation/requirements#knative): the Knative install itself
- [Configuration](../installation/configuration#knative): the Knative feature flags, the apps domain, and `apps.enabled`

:::warning
An app serves **user-supplied code to anonymous visitors on the public internet**. If you have not already chosen the domain apps are served from, read [Configure the apps domain](../installation/configuration#3-configure-the-apps-domain) first; the domain decides whether a hostile app can reach your users' platform sessions.
:::

## How Apps Differ From Sessions

See [App](../../users/compute/app#how-an-app-differs-from-a-session) for the user-facing comparison.
The admin perspective adds a few more details:

## What Renku Creates

### The Knative Service

One `Service` per app, built from the session launcher's environment. It is created in the namespace Renku launches sessions into, on the **default cluster only**. Unlike sessions, apps are not placed on secondary clusters, so a resource class belonging to one will still put its app on the main cluster.

- **Name**: `<project-slug>-<launcher-id-suffix>`, where the suffix is the last eight characters of the launcher's id. The slug is coerced to a DNS-1035 label and the whole name is capped at 50 characters. It is stable for a given launcher, so stopping and starting an app returns the same name and the same URL.
- **Image**: the launcher's container image, built by Renku or supplied by the user.
- **Port**: the launcher's configured port, as `containerPort`. Knative routes and probes exactly this port.
- **Environment**: Injected first and cannot be overridden. See [Publish an app](../use-cases/host-app#1-listen-on-the-port-renku-assigns). The launcher's own variables are appended after.
- **Security context**: `runAsUser` and `runAsGroup` from the environment's UID and GID.
- **Resources**: if the launcher has a resource class: CPU and memory requests from it, plus a memory limit. There is no CPU limit. A launcher with no resource class gets no resource block at all, and Knative's own defaults apply.
- **Scheduling**: node affinity and tolerations derived from the resource class, falling back to the session defaults, the same as sessions.

Labels on both the `Service` and its pod template:

| Label                        | Value                                            |
| ---------------------------- | ------------------------------------------------ |
| `renku.io/safe-username`     | `DummyRenkuAppUser` (the anonymous app identity) |
| `renku.io/project-id`        | Project id                                       |
| `renku.io/project-id-slug`   | Short fragment of the project id                 |
| `renku.io/project-slug`      | Project slug                                     |
| `renku.io/project-namespace` | Project namespace path, slashes replaced by `-`  |
| `renku.io/launcher-id`       | Session launcher id                              |

`renku.io/project-slug` and `renku.io/project-id-slug` are the pair the [domain template](../installation/configuration#3-configure-the-apps-domain) consumes; the others exist so that both you and Renku can find an app's owner from the cluster.

Renku reads the resulting URL from `status.url` on the `Service`. It never constructs the hostname itself, so whatever your domain template produces is what users get.

### Owned data connector resources

For each data connector an app is allowed to mount, Renku creates a `csi-rclone` `Secret` and `PersistentVolumeClaim` alongside the `Service`, with an **owner reference pointing at the `Service`**. Kubernetes garbage-collects them when the app is deleted, so there is no separate cleanup path to operate and no orphaned-PVC class of incident to watch for.

Their names are derived from the app name plus a hash, which keeps them within DNS length limits; they are not meant to be readable.

### Nothing else

No PVC for the working directory, no ingress object, no session secret. An app's container filesystem is ephemeral: anything it writes is lost when the pod is replaced, which includes every scale-to-zero cycle.

Renku sets no `serviceAccountName` on the pod, so an app runs under the namespace's `default` service account and, unless you have configured otherwise, gets its token mounted. That account should hold no role bindings, which is worth confirming given that the container is running user-supplied code. Setting `automountServiceAccountToken: false` on the `default` service account in the sessions namespace closes it off entirely.

## Scaling and Cold Starts

Renku sets three autoscaling annotations on every app revision:

| Annotation                                                   | Value | Why                                                                           |
| ------------------------------------------------------------ | ----- | ----------------------------------------------------------------------------- |
| `autoscaling.knative.dev/min-scale`                          | `0`   | An idle app costs nothing                                                     |
| `autoscaling.knative.dev/max-scale`                          | `3`   | A finite ceiling on what one app can consume                                  |
| `autoscaling.knative.dev/scale-to-zero-pod-retention-period` | `15m` | Keeps the last pod alive after idle, so the next visitor skips the cold start |

`scale-down-delay` is deliberately **not** set. It would gate every downscale, including 3→2 and 2→1, pinning surplus replicas of a briefly busy app. The retention period is scoped to the 1→0 transition, which is the only one that produces a cold start.

The trade-off to be aware of when planning capacity: an app that is visited once holds a pod for the next fifteen minutes, and that includes visits from crawlers and link-preview fetchers.

A request that arrives while an app is scaled to zero is **held by the Knative activator** while a pod starts, rather than being rejected. On our clusters a cold start has been measured at around 13 seconds end to end for a modest image; larger images take longer. If a cold start exceeds an idle or read timeout anywhere in the chain, the visitor gets a gateway error rather than a slow page, so if you tune anything on the apps path, tune those timeouts generously.

:::info[Ready does not mean warm]
A Knative Service that has scaled to zero still reports `Ready=True`. Renku's app status is derived from that condition, so **`ready` means "deployed and healthy", not "a pod is running"**. This is why the UI routes shared app links through a lobby page that absorbs the cold start, and why you should not read `ready` as a signal that an app is currently consuming resources.
:::

### What the lobby page expects of the request path

Every shared app link points at the UI's lobby page, which wakes the app by requesting it and hands the visitor over once it answers. Its timings, configurable via `apps.appLobby` (see [Tune the app lobby](../installation/configuration#5-tune-the-app-lobby-optional)), are the practical budget the gateway in front of Knative has to fit inside. By default:

- Each probe is held for up to **45 s** before the browser abandons it.
- Failed probes are retried **7 times**, 2 s apart, for a total wait of about **five and a half minutes** before the page gives up and offers a manual retry.

Two consequences for the request path. A proxy read timeout **below the probe timeout** (45 s by default) turns a slow cold start into a gateway error, and because the probe is a `no-cors` request whose response the page cannot inspect, that error is indistinguishable from success: the visitor is forwarded to a broken app rather than being told to wait. And the lobby always requests the app over **https**, rewriting the scheme if `status.url` is `http`, so an apps domain without a working certificate fails at the lobby even when the app itself is healthy.

### Status derivation

| Knative `Ready` condition                                          | Renku app status |
| ------------------------------------------------------------------ | ---------------- |
| `True`                                                             | `ready`          |
| `False` with reason `ProgressDeadlineExceeded` or `RevisionFailed` | `failed`         |
| Anything else                                                      | `pending`        |

Every other `Ready=False` reason is treated as transient, so an app that is still pulling its image or waiting for a node reports `pending` rather than flapping to `failed`.

## Access Control

Apps are public by construction, and the platform enforces that in several independent places. Understanding them is the difference between trusting the feature and hoping it holds.

**An app launcher can only exist in a public project.** Creating or updating one in a private project is rejected at the API layer, and copying a project into a non-public namespace silently drops its app launchers rather than carrying them over.

**Making a project private deletes its apps.** Patching a project's visibility to private triggers deletion of every app deployment belonging to that project, immediately. Deleting an app launcher likewise deletes its app (see [Publish an app](../use-cases/host-app#stopping-an-app)).

**Only credential-free public data connectors are mounted.** An app runs as an anonymous identity, so a connector is mounted only when all three hold:

1. Its visibility is public.
2. It requires no stored credentials: no access key, password, or token.
3. It is not backed by an OAuth2 integration (`drive`, `dropbox`).

The check is **fail-closed**: any error evaluating it excludes the connector. Mounted connectors are read-only.

**Session secrets are never provided to apps**, and **code repositories are never cloned into apps**. Everything an app needs at run time has to be in its image.

## Operating Apps

There is no admin UI for apps. Everything below assumes `kubectl` against the namespace Renku launches sessions into.

List every app in the cluster, with its URL and readiness:

```console
$ kubectl get ksvc -l renku.io/safe-username=DummyRenkuAppUser \
    -o custom-columns='NAME:.metadata.name,PROJECT:.metadata.labels.renku\.io/project-slug,URL:.status.url,READY:.status.conditions[?(@.type=="Ready")].status'
```

Find the app belonging to a project, given its id:

```console
$ kubectl get ksvc -l renku.io/project-id=<project-id>
```

Find which project and launcher an app belongs to:

```console
$ kubectl get ksvc <app-name> -o jsonpath='{.metadata.labels}' | jq
```

See whether an app is currently warm, rather than merely `Ready`:

```console
$ kubectl get pods -l serving.knative.dev/service=<app-name>
```

Inspect a failing app's logs. The `user-container` is the app itself, `queue-proxy` is Knative's sidecar:

```console
$ kubectl logs -l serving.knative.dev/service=<app-name> -c user-container --tail=100
```

Remove an app:

```console
$ kubectl delete ksvc <app-name>
```

Deleting the `Service` also removes its owned `Secret` and `PersistentVolumeClaim` via garbage collection. The session launcher survives, so a project member can start the app again; expect them to, if you deleted it without telling them.

### Troubleshooting

This covers cluster-level causes. For symptoms a project member without `kubectl` access
might report, see [Publish an app § Troubleshooting](../../users/use-cases/host-app#troubleshooting).

| Symptom                                               | Likely cause                                                                                                                                                                  |
| ----------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| App stays `pending`, then reports `failed`            | See [Publish an app](../use-cases/host-app#troubleshooting), most often the port or bind address is wrong.                                                                    |
| `Service` rejected at creation                        | A Knative [feature flag](../installation/configuration#1-enable-the-knative-feature-flags) is missing; check the admission error for the disallowed pod spec field            |
| App has no URL in `status.url`                        | Knative networking has not programmed a route; check the ingress layer and the domain configuration                                                                           |
| App URL resolves but times out                        | DNS or the TLS certificate does not cover this app's hostname; check the depth if you are relying on a wildcard                                                               |
| Lobby fails but the app answers when curled directly  | The apps domain has no valid certificate; the lobby always requests `https`, even when `status.url` is `http`                                                                 |
| Visitors get a gateway error on first load            | Cold start is exceeding a timeout on the request path; raise the read and idle timeouts on the apps route above the lobby's probe timeout (`probeTimeoutMs`, 45 s by default) |
| A data connector is missing inside an app             | It failed the mount predicate: not public, has stored credentials, or is OAuth-backed. Expected behaviour, not a fault.                                                       |
| Users report "an app already exists for this project" | One app per project is a hard limit; the existing one has to be stopped first                                                                                                 |
| Apps missing from the UI after enabling the flag      | The UI reads `APPS_ENABLED` at deploy time; the UI pods need to be rolled                                                                                                     |

## Current Limitations

These are properties of the current release, not permanent design decisions:

- **Public projects only.** There is no mechanism for authenticating a visitor before they reach an app, so there are no private apps.
- **One app per project**, across all of that project's launchers.
- **Default cluster only.** Apps are not created on secondary clusters, even from a resource class that belongs to one.
- **No global environments.** An app launcher's environment is either built from code or a container image the user supplies.
- **No admin panel.** Apps cannot be listed, inspected, or stopped from the Renku administrator UI.
- **No repository cloning and no session secrets** inside an app.
- **No persistent storage.** The container filesystem is ephemeral across scale-to-zero.
- **No per-app quota or rate limiting** beyond `max-scale: 3` and the resource class.
