# Environment

An environment is a docker-based environment that can run in a RenkuLab interactive [Session](Session%20fd7c8246082145df8bcad675cf919206.md) .

An environment consists of:

1. a reference to a docker image
2. the configuration required to run that docker image on RenkuLab

Renku provides several *global environments* that come pre-configured and ready to use. Alternatively, you can create your own custom environment with your own docker image.

Environments are connected to a Renku project via a [Session Launcher](Session%20Launcher%20518df05050a7434eb3eb0493181d715c.md).