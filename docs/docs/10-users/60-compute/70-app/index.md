# App

An app is a web application (a dashboard, a demo, an interactive report) that
Renku hosts at a public URL. Anyone with the link can open it, without a
Renku account and without launching a session.

An app is created by an _app launcher_, which is a [Launcher](../launcher) of the
app type, and runs the [Environment](../environment)
configured on that launcher. Unlike a [Session](../session), an app belongs to the
project rather than to you: there is one of it, it stays up between visits, and
everyone who opens the link sees the same running app.

To create one, see [Publish an app](../../use-cases/host-app).

## How an app differs from a session

|                    | Session                           | App                                       |
| ------------------ | --------------------------------- | ----------------------------------------- |
| Who can open it    | You, once you launch it           | Anyone with the link                      |
| How many           | One per person, per launcher      | One per project                           |
| Lifetime           | Until stopped or timed out        | Until stopped                             |
| Code repositories  | Cloned into the session           | Not cloned                                |
| Data connectors    | All of them, with credentials     | Public ones with no stored credentials    |
| Session secrets    | Available to the session          | Not available                             |
| Project visibility | Any                               | Public only                               |

If you want an interactive environment to work in, you want a session. If you
want to publish something for other people to look at, you want an app.

## What an app can reach

An app is open to the internet and is not tied to any particular person, so it
has access to less than a session does.

**Data connectors** are mounted only when they are public, need no stored
credentials, and are not backed by an account you connected, such as Google
Drive or Dropbox. A connector that fails any of these is left out, because
otherwise anyone on the internet could read your data through the app. Mounted
connectors are read-only. See [Data Connector](../../data/data).

**[Code repositories](../../code/code-repository) are not cloned.** Your app's code
has to already be in its image; data connectors and environment variables can
still reach it, as described above and below.

**Session secrets are not available**, for the same reason as credentialed data
connectors.

**Environment variables** set on the launcher are passed to the app. Because an
app is public, treat every variable as public too and do not put secrets in them.

## Apps sleep when nobody is using them

An app that has had no traffic for about fifteen minutes is scaled down to
nothing. It costs no resources while it sleeps and it wakes on the next request,
which takes a few seconds, or longer for a large image.

The Renku app URL handles this for visitors: it shows a waiting screen and
forwards them once the app answers. This is why you share the Renku URL rather
than the address your browser ends up at once the app has opened.

Renku still reports an app as _Live_ while it sleeps. Live means deployed and
healthy, not currently running.

## Editing an app launcher

An app launcher is edited like any other session launcher: its environment,
resource class, and environment variables. Changes do **not** reach a running
app: it keeps its old definition until it is stopped and started again.

Deleting an app launcher stops its app (see [Publish an app](../../use-cases/host-app#stopping-an-app)).
