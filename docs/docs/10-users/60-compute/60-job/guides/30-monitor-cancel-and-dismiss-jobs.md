# Monitor, cancel, and dismiss jobs

After you submit a [Job](../../job), you can follow its progress on the project page, in the launcher side panel, and on your Renku dashboard.

## Where to find your jobs

Your active and recent jobs appear in several places:

- **Under the job launcher** on the project page — each row shows the submission ID and status
- **In the launcher side panel** — open the launcher to see **Your submitted jobs**
- **On your dashboard** — jobs are listed alongside interactive sessions

<!-- TODO: screenshot monitor-cancel-and-dismiss-jobs-10.png — job row under launcher with Running status -->

## Job states

| State         | What it means                           |
| ------------- | --------------------------------------- |
| **Starting**  | Renku is preparing the job              |
| **Running**   | The job command is executing            |
| **Completed** | The job finished successfully           |
| **Errored**   | The job failed                          |
| **Stopping**  | Renku is cancelling or removing the job |

## View logs

While a job is **Starting**, **Running**, **Completed**, or **Errored**, click **View logs** to open the job logs.

<!-- TODO: screenshot monitor-cancel-and-dismiss-jobs-30.png — View logs for a running job -->

Logs are available for 24 hours or until you dismiss the job.

## Cancel a running job

If a job is still **Starting** or **Running**, you can cancel it:

1. Click **Cancel** on the job row or in the launcher panel.
2. Confirm in the modal. Cancelling stops the job and removes it.

<!-- TODO: screenshot monitor-cancel-and-dismiss-jobs-40.png — Cancel job confirmation modal -->

## Dismiss a completed or errored job

When a job has **Completed** or **Errored**, click **Dismiss** to remove it from your project and dashboard.

:::warning

Dismissing a job is permanent. You will not be able to view that job or its logs again in RenkuLab after dismissing it.

:::

<!-- TODO: screenshot monitor-cancel-and-dismiss-jobs-50.png — Completed job with Dismiss action -->

## Multiple jobs at once

You can have multiple jobs running from the same launcher at the same time. Use a different **submission ID** for each job so you can tell them apart.

Resource limits still apply at the resource pool level — if a pool's quota is full, new jobs (and sessions) may not start until resources are available. See [Resource Pools & Classes](../../resource-pools-and-classes).
