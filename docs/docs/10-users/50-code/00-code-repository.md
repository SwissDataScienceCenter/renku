# Code repository

For the purposes of Renku, a code repository is a git-based collection of code files, hosted at
[GitHub.com](http://github.com), [GitLab.com](http://gitLab.com), or similar.

When a code repository is connected to a [Project](/docs/users/projects/projects),
the repository is automatically cloned into the
[Session](/docs/users/sessions/session), where the files can be accessed and run.

## Access to Code Repositories from RenkuLab

If you wish you **write (push)** changes back to a code repository, [activate the corresponding GitHub or
GitLab integration](/docs/users/code/guides/connect-renku-account-to-github-or-gitlab-account).

In order to access a **private** code repository from RenkuLab, you'll need to [connect your
RenkuLab account with
GitHub/GitLab](/docs/users/code/guides/connect-renku-account-to-github-or-gitlab-account).

Once you have [connected](/docs/users/code/guides/connect-renku-account-to-github-or-gitlab-account)
your accounts, your access to the code repository in RenkuLab will match your access level on
GitHub/GitLab:

- If you are able to write to the repository (push and pull rights) you will see a green status.
- If you are only able to read the repository (pull rights) you will see a yellow status.
- If you do not have write access to the repository, you will see a red status.
