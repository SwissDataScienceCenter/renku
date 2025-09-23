# How to migrate a code repository out of RenkuLab GitLab

Renku version: 2.0
Type: How-to Guide

<aside>
<img src="https://www.notion.so/icons/new-alert_red.svg" alt="https://www.notion.so/icons/new-alert_red.svg" width="40px" />

**DATA REMOVAL DEADLINE - JANUARY 2026**

The RenkuLab GitLab (gitlab.renkulab.io) will soon be shut down. All code repositories remaining there will be removed. Follow the migration instructions below before January 2026 to save your work. 

</aside>

### How to Move a Code Repository to an External Provider

Here are the steps to migrate a code repository from the RenkuLab GitLab to a provider such as [GitHub.com](https://github.com/) or [GitLab.com](https://gitlab.com/):

1. **Clone your repository** from the RenkuLab GitLab to your local machine
    
    ```bash
    git clone <renku-repo>
    
    ```
    
2. **Create a new empty repository** in your chosen provider (for example GitLab.com or GitHub.com)
3. **Copy the clone URL for the new repository.** We'll call this URL `<new-remote>`.
4. Run the following commands from inside the repository to **push the repository to the new git repository provider**. You may want to name your new git origin (`<new-origin>`) to match the provider, for example `github`.
    
    ```bash
    cd <renku-repo>
    git remote add <new-origin> <new-remote>
    git push --all <new-origin>
    
    ```
    
5. **Important - The next time you push:** Set your `git push` to push to the new origin (git provider):
    
    ```bash
    git push -u <new-origin> <current-branch>
    
    ```
    
6. Don't forget to **update your Renku 2.0 project with the new git repository URL!** Copy the clone URL of your new repository. Then, on the code repository in your Renku project, click 'Edit' and replace the old URL with the new one.

<aside>
<img src="https://www.notion.so/icons/light-bulb_yellow.svg" alt="https://www.notion.so/icons/light-bulb_yellow.svg" width="40px" />

**Tip:** Would you like links to your original `gitlab.renkulab.io` code repository to **automatically redirect** to your new repository? [Register the new home of your code repository](https://www.notion.so/2540df2efafc80a2bee5f1b7d247c28e?pvs=21) and we'll make it happen!

</aside>

Here's a full, real-ish example (with identifiers removed 😉):

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

<aside>
<img src="https://www.notion.so/icons/light-bulb_yellow.svg" alt="https://www.notion.so/icons/light-bulb_yellow.svg" width="40px" />

**Does your new git provider not support git LFS data?** For a short term workaround, run the following command to push your code while ignoring the git LFS data: `git push --no-verify`.

In the long term, we recommend that you move your git LFS data to a [cloud storage](Data%20connector%203ae1e46fdb094cc48516a104457e5633.md) that you can connect to your Renku 2.0 project. Feel free to [get in touch](mailto:hello@renku.io) with us if you need help.

</aside>