# Sessions

A Renku session is an interactive run-time environment that can be used to do work on data and code.

A session is created by a **Session Launcher** (see [Launcher](../launcher)) and is a running instance of an [Environment](../environment). Inside a session, all of the project’s [Code repositories](../../code/code-repository) are cloned and [Data connectors](../../data/data) are mounted (as long as the user provided any required access credentials).

A session has access to a certain amount of compute resources (CPU, GPU, RAM and storage). This is determined by the resource class set on the [Launcher](../launcher). For more information about compute resources, see [Resource Pools & Classes](../resource-pools-and-classes).

The sessions you launch are always private to you, but others with access to your project may launch their own instances
of the session.

### Managing sessions from the command line

You can manage sessions from the terminal using the [Renku CLI](/docs/users/cli/cli-reference):

```bash
rnk session list                           # list running sessions
rnk session start --launcher <launcher-id> # list running sessions
rnk session logs <session-id>              # view session logs
rnk session stop <session-id>              # stop a session
```

See the [CLI reference](/docs/users/cli/cli-reference) for the full list of session commands.
