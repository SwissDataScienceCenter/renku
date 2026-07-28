# Sessions

A Renku session is an interactive run-time environment that can be used to do work on data and code.

A session is created by a **Session Launcher** (see [Launcher](../launcher)) and is a running instance of an [Environment](../environment). Inside a session, all of the project’s [Code repositories](../../code/code-repository) are cloned and [Data connectors](../../data/data) are mounted (as long as the user provided any required access credentials).

A session has access to a certain amount of compute resources (CPU, GPU, RAM and storage). This is determined by the resource class set on the [Launcher](../launcher). For more information about compute resources, see [Resource Pools & Classes](../resource-pools-and-classes).

The sessions you launch are always private to you, but others with access to your project may launch their own instances
of the session.
