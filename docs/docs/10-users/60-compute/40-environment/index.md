# Environment

An environment is a docker-based environment that can run in a RenkuLab interactive [Session](../session) or a background [Job](../job).

An environment consists of:

1. a reference to a docker image, and
2. the configuration required to run that docker image on RenkuLab.

Renku provides several _global environments_ that come pre-configured and ready to use for interactive sessions. Alternatively, you can create your own custom environment with your own docker image. Job launchers use custom environments only (built from code or from a custom image).

Environments are connected to a Renku project via a [Launcher](../launcher).
