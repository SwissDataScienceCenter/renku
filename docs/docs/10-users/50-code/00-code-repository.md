<!-- Highlight available globally <Highlight color="#c6d000ff">Docusaurus green</Highlight> -->

# Code repository

For the purposes of Renku, a code repository is a git-based collection of code files, hosted at
[GitHub.com](http://github.com), [GitLab.com](http://gitLab.com), or similar.

When a code repository is connected to a [Project](../projects/projects),
the repository is automatically cloned into the
[Session](../sessions/session), where the files can be accessed and run.

## Access to Code Repositories from RenkuLab

Once you have connected your accounts, your access to the code repository in RenkuLab will match your access level on [GitHub/GitLab](./guides/connect-renku-account-to-github-or-gitlab-account):

- If you are able to write to the repository (push and pull rights) you will see a <Highlight color="#059b11ff">green</Highlight> status.
- If you can only see the code repository, you will only be able to pull or clone from the code repository (status in <Highlight color="#f1fb36ff">yellow</Highlight>).
- If you cannot access a given repository at all, your status will be in <Highlight color="#ff3838ff">red</Highlight>.

### Public Code Repositories

Public code repositories may be connected to Renku projects and accessed with no further action. You will be able to read and run the files of the repository in your session.

If you wish you write changes back to the repository, see [How to connect your Renku account to your GitHub or GitLab account](./guides/connect-renku-account-to-github-or-gitlab-account).

### Private Code Repositories

In order to access your **private** code repository from RenkuLab, you need to connect your RenkuLab account with GitHub/GitLab. See [How to connect your Renku account to your GitHub or GitLab account](./guides/connect-renku-account-to-github-or-gitlab-account).
