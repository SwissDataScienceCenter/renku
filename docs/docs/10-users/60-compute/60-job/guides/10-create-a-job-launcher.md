# Add a job launcher to your project

A job launcher defines the environment, default compute resources, and job command for background [Jobs](../../job) in your project.

:::info

Job launchers do not support **global environments**. You must use **Create from code** or a **custom Docker image**. Global environments are only available for interactive [Sessions](../../session).

:::

## Create a job launcher

1. In the **Sessions** section of the project page, click **+** to add a new launcher.

<p class="image-container-l">
![JobLauncher](./job-creation-10.png)
</p>

2. In the **Select the type of launcher to create** modal, choose **Job**.

<p class="image-container-l">
![JobLauncher](./job-creation-20.png)
</p>

3. Choose and configure an **environment**:
   - **Create from code** — Renku builds an environment from a code repository (for example, one with an `environment.yml` or `requirements.txt`). See [How to create an environment with custom packages installed](../../environment/guides/create-environment-with-custom-packages-installed).
   - **Custom image** — use your own Docker image. See [How to use your own docker image for a Renku session](../../environment/guides/use-your-own-docker-image-for-renku-session).

<p class="image-container-l">
![JobLauncher](./job-creation-30.png)
</p>

4. Configure the **job command** as a JSON array, for example `["python", "my_repo/main.py"]`. This command is required and defines what runs when someone submits a job from this launcher.

   You can optionally set **job args** as a separate JSON array.

5. Set a **name** and optional **description** for the launcher.

6. Select the **resource class** that best fits your expected computational needs. (see [Resource Pool and Classes](../../resource-pools-and-classes))

   :::tip

   Do you need more resources than are available in RenkuLab's public resource classes? [Contact us](../../../community)! We can configure a custom resource pool for your needs upon demand.

   :::

7. Click **Add launcher** (or the equivalent button to save the launcher).

<p class="image-container-l">
![JobLauncher](./job-creation-40.png)
</p>

If your environment is built from code, wait for the environment **build** to succeed before submitting a job. You can rebuild the environment from the launcher card if needed.

## Next steps

Once your job launcher is ready, see [How to submit a job](submit-a-job).
