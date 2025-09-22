# Session Launcher

A session launcher is a component of a project that launches an interactive, browser-based compute [Session](Session%20fd7c8246082145df8bcad675cf919206.md) .

A session launcher contains:

1. a session [Environment](Environment%20338f0b4e5fe04437a36dfa620a126304.md), which defines the software installed in the session
2. a default resource pool (see [Resource Pools & Classes](Resource%20Pools%20&%20Classes%2011f0df2efafc802dbe05f4dcd375431f.md)), which determines the compute resources available in the session

A project’s session launchers are usable to everyone who can see the project. This means that you can configure a session launcher with an [Environment](Environment%20338f0b4e5fe04437a36dfa620a126304.md) , and everyone in the project can use that session launcher to launch a session with the exact same environment.

However, the sessions you launch are only accessible to you. Sessions are not shared between users.