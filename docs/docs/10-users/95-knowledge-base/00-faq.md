# FAQ

## Data

### Is my data copied into the session?

Data is mounted into your Renku session, not copied. No data is transferred until you access it in a
session.

### What happens to my data when I leave Renku?

Data connectors point to external storage that you own (e.g. S3, Azure Blob, SwitchDrive). Renku never copies or retains that data, so leaving Renku has no effect on it. Any files you saved in mounted working directories are written back to your connected storage, while files that were only in the session's local filesystem are lost when the session ends.

## Sessions

### What can I do with a Renku session?

You can use Renku sessions for pretty much any computational task that has a graphical user
interface. Though you mostly see examples of Python and R in RenkuLab, you can use any programming
language in Renku.

You can [configure a custom environment for your Renku
session](../sessions/guides/environments/create-environment-with-custom-packages-installed), and
even [use your own docker image for a Renku
session](../sessions/guides/environments/use-your-own-docker-image-for-renku-session).

### Can I keep a session running after I close the browser?

If you work logged into Renku, your session will still be running even if you close your browser and
shut down your computer. Sessions in the public resource pool will go to a Pause state after 2 hours
of the session not using CPU resources. When you resume your session, you will have access to your
work where you left it. A paused session that is not resumed before 14 days will be automatically
shut-down.

### Why does my session fail to start?

The most common causes are:

- **Environment build failure**: if your session uses a code-based environment, check that your dependency file (`requirements.txt`, `environment.yml`, etc.) is valid and that all packages exist. Build logs are shown in the session launcher.
- **Insufficient resources**: the selected resource class may not be enough to run your session. Try a bigger resource class.
- **Credential issues**: if a data connector requires credentials, Renku will prompt you before launch. Missing or expired credentials will prevent the session from mounting the data and may eventually cause it to fail.

If the problem persists, [contact us](../community).

### Can I share a project with someone who doesn't have a Renku account?

You can make a project **public**, which allows anyone to view it and its public unrestricted data connectors without logging in. However, launching a session or accessing private data connectors always requires a Renku account. For this case, your collaboratosr will need to [create a free account](../getting-started/tutorial-start).

## Projects & Groups

### What's the difference between a group and a project?

A **project** is where the work lives: it holds your data connectors, code repositories and session launchers. A **group** is an organisational container that holds multiple projects and lets you manage member access in one place. Use a project when working alone or sharing a single piece of work and create a group when you want to give a team (e.g. a course cohort or a research lab) consistent access to several projects at once.

## Billing

### Is Renku free? When is RenkuLab not free?

Everyone is welcome to use RenkuLab and run sessions in our free tier resource pool. If you need
more computational resources than are available in our free tier,
[Contact](../community) us to set up a
contract for a custom resource pool on RenkuLab.
