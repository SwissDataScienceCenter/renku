# Job

A Renku job is a non-interactive run that executes a command in the background using a project's [Environment](environment) and compute resources.

A job is submitted from a **Job launcher** (see [Launcher](session-launcher)) and runs without a browser-based interface. Inside the job, all of the project's [Code repository](../code/code-repository)s are cloned and [Data connector](../data/data)s are mounted, as long as you provided any required access credentials when submitting.

Each job submission is identified by a **submission ID** — a short name you choose (or accept from the auto-generated default) that must be unique among your jobs on that launcher. You use the submission ID to tell jobs apart when several are running at once.

A job has access to compute resources (CPU, GPU, RAM, and storage) determined by the resource class you select at submit time. For more information, see [Resource Pools & Classes](resource-pools-and-classes).

The jobs you submit are always private to you. Jobs are not shared between users.

## Job lifecycle

A job moves through states such as **Starting**, **Running**, **Completed**, and **Errored**. While a job is starting or running, you can view its logs or cancel it. When a job has completed or errored, you can view its logs and then **Dismiss** it to remove it from your project and dashboard.

Dismissing a job permanently removes it from the RenkuLab interface. You will not be able to reopen its logs after dismissing it.

:::note

Completed and errored jobs are typically removed automatically after about 24 hours if you have not dismissed them. This retention period may change in the future.

:::

## When to use a Job

Use a Job when you want to run a script or batch process in the background — for example, model training, data processing, or any long-running command that does not require an interactive notebook or editor.

If you need an interactive environment for exploration or development, use a [Session](session) instead. See [How to handle long training runs](guides/handle-long-training-runs) if you are deciding between a Job and workarounds inside an interactive session.
