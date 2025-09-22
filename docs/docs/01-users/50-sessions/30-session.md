# Session

A Renku session is a running interactive run-time environment that can be used to do work on data and code.

A session is created by a [Session Launcher](Session%20Launcher%20518df05050a7434eb3eb0493181d715c.md) and is a running instance of an [Environment](Environment%20338f0b4e5fe04437a36dfa620a126304.md). Inside a session, all of the project’s  [Code repository](Code%20repository%202bbc38797efe4ed1b5f6be16fd95b82e.md)s are cloned and [Data connector](Data%20connector%203ae1e46fdb094cc48516a104457e5633.md)s are mounted (as long as the user provided any required access credentials).

A session has access to a certain amount of compute resources (CPU, GPU, RAM, and storage). This is determined by the resource pool set on the [Session Launcher](Session%20Launcher%20518df05050a7434eb3eb0493181d715c.md). For more information about compute resources, see [Resource Pools & Classes](Resource%20Pools%20&%20Classes%2011f0df2efafc802dbe05f4dcd375431f.md).

The sessions you launch are always private to you.