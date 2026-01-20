---
title: Migrate a code repository
description: Migrate your repo to another git provider
---

# Migrate a code repository out of RenkuLab GitLab

This guide will walk you through migrating a code repository from the RenkuLab GitLab to a provider
such as [GitHub.com](https://github.com/) or [GitLab.com](https://gitlab.com/).

:::danger

**DATA REMOVAL DEADLINE - JANUARY 2026**

The RenkuLab GitLab (gitlab.renkulab.io) will soon be shut down. All code repositories remaining
there will be removed. Follow the migration instructions below before January 2026 to save your
work.

:::

<p class="image-container-l">
<iframe
  width="560"
  height="315"
  src="https://www.youtube.com/embed/TTRfg9XBfqQ?list=PLagSzgm6aERkLQ8LINw8luZKdv9ru7aHh"
  title="YouTube video player"
  frameborder="0"
  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
  referrerpolicy="strict-origin-when-cross-origin"
  allowfullscreen>
</iframe>
*Bring your RenkuLab GitLab project to the public GitLab instance with the import feature.*
</p>



## 1. Move a code repository to an external provider

1. **Clone your repository** from the RenkuLab GitLab to your local machine

    ```bash
    git clone <renku-repo>
    ```

2. **Create a new empty repository** in your chosen provider (for example GitLab.com or GitHub.com)
3. **Copy the clone URL for the new repository.** We'll call this URL `<new-remote>`.
4. Run the following commands from inside the repository to **push the repository to the new git repository provider**. You may want to name your new git origin (`<new-origin>`) to match the provider, for example `github`.

    ```bash
    cd <renku-repo>
    git remote add <new-origin> <new-remote>
    git push --all <new-origin>
    ```

5. **Important - The next time you push:** Set your `git push` to push to the new origin (git provider):

    ```bash
    git push -u <new-origin> <current-branch>
    ```

Here's a full, real-ish example (with identifiers removed 😉):

```bash
# STEP 1
git clone https://gitlab.renkulab.io/my.username/example-repo.git

# STEP 2
# Create new GitHub repo at
# https://github.com/my.username/example-repo

# STEP 4
cd example-repo
git remote add github git@github.com:my.username/example-repo.git
git push --all github

# STEP 5
# make some code changes...
git push -u github master

```

:::tip

**Does your new git provider not support git LFS data?** For a short term workaround, run the following command to push your code while ignoring the git LFS data: `git push --no-verify`.

In the long term, we recommend that you move your git LFS data to a [cloud storage](../../data/data) that you can connect to your Renku 2.0 project. Feel free to [get in touch](mailto:hello@renku.io) with us if you need help.

:::

## 2. Update your Renku project code connection

Don't forget to update your Renku 2.0 project with the new git repository URL!

1. Copy the clone URL of your new repository.
2. On the code repository in your Renku project, click 'Edit' and replace the old URL with the new
   one.

## 3. Register a redirect (optional)

Would you like links to your original `gitlab.renkulab.io` code repository to **automatically
redirect** to your new repository? [Register the new home of your code
repository](https://www.notion.so/2540df2efafc80a2bee5f1b7d247c28e?pvs=21) and we'll make it happen!
