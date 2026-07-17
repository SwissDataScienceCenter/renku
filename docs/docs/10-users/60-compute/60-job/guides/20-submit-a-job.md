# Submit a job

When a job launcher is configured and its environment image is ready, anyone who can see the project can **Submit** a job from that launcher. Each submission runs the job command in the background.

## Before you submit

Renku may ask you to resolve prerequisites before the job starts, which are the same kinds of checks that apply when launching a [Session](../../session):

- **Code repositories** that Renku cannot access.
- **Session secrets** that you have not provided for the project yet.
- **Data connector** credentials.

See [How to configure a session secret](../../guides/configure-session-secret) for setting up secrets that jobs can use.

## Submit a job

1. On the project page, find the job launcher and click **Submit**.

<p class="image-container-l">
![JobLauncher](./job-submission-10.png)
</p>

2. Review the environment summary in the **Review and submit job** modal.

<p class="image-container-l">
![JobLauncher](./job-submission-20.png)
</p>

3. Enter a **Submission ID**, that is a short unique name for this job run (for example, `run-a1b2c3`). Renku suggests one automatically; you can edit it.

   The submission ID must:
   - Start with a lowercase letter
   - Use only lowercase letters, numbers, and hyphens
   - Be at least 4 characters long

   If a job with the same submission ID already exists on this launcher, Renku warns you in the form. You cannot submit until you choose a different submission ID.

4. Confirm the **Job command** (required). It is pre-filled from the launcher configuration. You can override it for this submission if needed. The command must be a JSON array, for example `["python", "my_repo/main.py"]`.

5. Optionally set **Job args** as a JSON array.

6. Select a **resource class** for this submission, if you want to override the launcher defaults.

7. Click **Submit job**.

<p class="image-container-l">
![JobLauncher](./job-submission-30.png)
</p>

You can submit multiple jobs from the same launcher at the same time. Each job must have its own submission ID.

## After you submit

While the job is starting or running, see [How to monitor, cancel, and dismiss jobs](monitor-cancel-and-dismiss-jobs).
