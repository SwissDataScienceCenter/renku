# Environment

An environment is a docker-based environment that can run in a RenkuLab interactive [Session](session) .

An environment consists of:

1. a reference to a docker image
2. the configuration required to run that docker image on RenkuLab

Renku provides several *global environments* that come pre-configured and ready to use. Alternatively, you can create your own custom environment with your own docker image.

Environments are connected to a Renku project via a [Session Launcher](session-launcher).
