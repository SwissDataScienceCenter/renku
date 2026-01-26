# Session

A Renku session is a running interactive run-time environment that can be used to do work on data and code.

A session is created by a [Session Launcher](session-launcher) and is a running instance of an [Environment](environment). Inside a session, all of the project’s [Code repository](../code/code-repository)s are cloned and [Data connector](../data/data)s are mounted (as long as the user provided any required access credentials).

A session has access to a certain amount of compute resources (CPU, GPU, RAM, and storage). This is determined by the resource pool set on the [Session Launcher](session-launcher). For more information about compute resources, see [Resource Pools & Classes](resource-pools-and-classes).

The sessions you launch are always private to you.
