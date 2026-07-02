# Launcher

A launcher is a component of a project that defines how compute runs on RenkuLab. Each launcher combines an [Environment](environment) with a default [Resource Pools & Classes](resource-pools-and-classes) configuration.

Renku supports two types of launcher:

1. **Session launcher** — launches an interactive, browser-based [Session](session) for coding and data exploration.
2. **Job launcher** — runs a non-interactive [Job](job) in the background when you submit it.

A launcher contains:

1. an [Environment](environment), which defines the software installed in the run
2. a default resource pool (see [Resource Pools & Classes](resource-pools-and-classes)), which determines the compute resources available

For a Job launcher, you also configure a **job command** that defines what runs when the job is submitted.

A project's launchers are usable to everyone who can see the project. Anyone with access can launch a session or submit a job from a launcher configured in the project.

However, the sessions you launch and the jobs you submit are only accessible to you. They are not shared between users.

## Next steps

- To create an interactive session launcher, see [Add a launcher to your project](guides/environments/).
- To create a job launcher, see [How to create a job launcher](guides/jobs/create-a-job-launcher).
