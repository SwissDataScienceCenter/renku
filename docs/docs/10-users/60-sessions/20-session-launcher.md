# Session Launcher

A session launcher is a component of a project that launches an interactive, browser-based compute [Session](/docs/users/sessions/session).

A session launcher contains:

1. a session [Environment](/docs/users/sessions/environment), which defines the software installed in the session
2. a default resource pool (see [Resource Pools & Classes](/docs/users/sessions/resource-pools-and-classes)), which determines the compute resources available in the session

A project’s session launchers are usable to everyone who can see the project. This means that you can configure a session launcher with an [Environment](/docs/users/sessions/environment) , and everyone in the project can use that session launcher to launch a session with the exact same environment.

However, the sessions you launch are only accessible to you. Sessions are not shared between users.
