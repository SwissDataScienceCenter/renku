# How to set up a course in “Advanced mode” (using git)

## Running a course in Advanced Mode

If course participants need to be able to save their work during the course and they are comfortable using git, you can create a project (or a set of projects) for the course, and ask them to create a branch in the git repository where they can commit their changes.

For creating the project:

1. Follow the steps in [Workshop Mode](https://www.notion.so/Workshop-Mode-1460df2efafc804e9fa9e3cebb8262ef?pvs=21) to set up the course project.
2. Add the course participants to your Git code repository, so they can create their own branch.

    <aside>
    <img src="https://www.notion.so/icons/info-alternate_blue.svg" alt="https://www.notion.so/icons/info-alternate_blue.svg" width="40px" />

    For example, if the course git repository is on GitHub, add the course participants to the repository **on GitHub**. When the student links their GitHub account with their Renku account, Renku will automatically reflect their access from GitHub.

    </aside>

3. Instruct course participants to connect their Renku account to their GitHub or GitLab account, as described in [How to connect your Renku account to your GitHub or GitLab account](/docs/users/data/guides/connect-renku-account-to-github-or-gitlab-account)
4. Instruct course participants to work on their branch. Once they’ve launched a session, course participants can create a new branch with the following command:

    ```jsx
    git checkout -b my-branch-name
    ```

    You can read more in the [official git documentation](https://git-scm.com/docs/git-checkout).

5. If you have introduce new material in the master branch, ask the course participants to rebase their branch to get those changes:

    ```bash
    git checkout participant-branch
    git rebase master
    ```


<aside>
❗

Be mindful to protect your code repository from undesired changes from participants via protected branches. You can find more information on this topic both for GitHub ([here](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)) and GitLab ([here](https://docs.gitlab.com/ee/user/project/repository/branches/protected.html)).

</aside>
