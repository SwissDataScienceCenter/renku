# Integrations and Access to External Services

This reference guide explains what integrations are in Renku, what each kind is used for, and
how the GitHub integration differs from the others.

## What is an integration?

An integration connects your Renku account to an account you have on another service, such as
GitHub, GitLab, or CSCS. Once connected, Renku can act in that service **on your behalf, with
your permissions**. It can never do more than you could do yourself on that service.

You manage your integrations from the **Integrations** page, which you can open from the 👤 user
menu in the top right corner. You can remove Renku's access at any time, either from the
**Integrations** page or from your account settings on the external service.

Integrations are personal. Connecting your account does not give anyone else access to it. Each
person who works on a project connects their own accounts, and each person gets the access they
have on the external service.

Some services are not supported by Renku yet. If a service you need is missing,
for example your institution's GitLab instance, [contact us](/docs/users/community) 
to request it, and you could even contribute the integration yourself.

:::note
If you are using Renku in a different deployment from https://renkulab.io,
reach out to the administrators of that deployment. They can add it by following
[Integrations and Connected Services](/docs/admins/operation/integrations).
:::

## What integrations are used for

### Accessing code repositories

Most integrations are for git hosting services such as GitHub, GitLab.com, or an institution's own
GitLab instance. They let Renku:

- clone **private** repositories into your sessions, and
- **push** changes from your sessions back to repositories you can write to.

Public repositories can be cloned without any integration.
You only need an active integration if you wish to push changes to a repo or to
use private repositories. See [Code repository](/docs/users/code/code-repository) and
[How to connect your Renku account to your GitHub or GitLab account](/docs/users/code/guides/connect-renku-account-to-github-or-gitlab-account) for more information.

### Accessing compute at CSCS

[CSCS](https://www.cscs.ch/) is the Swiss National Supercomputing Centre. The CSCS integration lets Renku start sessions as HPC jobs on CSCS systems, using your own CSCS
account and allocation. These sessions use compute credits from your CSCS project. See
[How the CSCS integration works in Renku](/docs/users/compute/session/guides/cscs-integration).

### Accessing data

Some data connectors, such as Google Drive and Dropbox, use an integration instead of credentials
that you type in. See [Connect Google Drive and Dropbox](/docs/users/data/guides/connect-data/connect-google-drive-and-dropbox).

### Publishing data

The Zenodo and SciCat integrations let Renku upload files from a data connector to a draft deposit
in your Zenodo or SciCat account. Renku creates the draft, and you review and publish it there. See
[Publish data from a data connector to Zenodo](/docs/users/data/guides/export-data-connector-to-zenodo).

### Accessing private container images

The integrations for GitLab and GitHub also let Renku pull **private** container images for your
sessions from their image registries, using your own access:

- **GitLab:** images in the registry of a GitLab instance that is connected as an integration, such
  as GitLab.com.
- **GitHub:** images in the GitHub Container Registry (`ghcr.io`). This uses a **separate** GitHub
  integration from the one for code repositories, so you may see two GitHub entries on the
  Integrations page. Connect the one for images as well if your session images are private on
  `ghcr.io`.

Public images do not need any integration. Which registries are available depends on how your
Renku deployment is configured.

## GitHub integration {#github-integration}

GitHub works differently from the other git integrations. Connecting your GitHub account is not
enough on its own: the **Renku GitHub App** must also be **installed** on the GitHub account or
organization that owns the repository.

There are two steps:

1. **Connect your account** on the Renku **Integrations** page. This tells Renku who you are on
   GitHub.
2. **Install the Renku GitHub App** on each GitHub user account or organization whose repositories
   you want to use, and choose which repositories it can access.

Renku can only reach a repository if **both** of these are true:

- the Renku GitHub App is installed on the repository's owner and allowed to access that
  repository, and
- you yourself have access to the repository on GitHub.

So if a repository shows that Renku cannot access it even though you can see it on GitHub, the
app is most likely not installed for that repository.

### Changing which repositories the app can access

You can change the repositories the Renku GitHub App can access at any time, for example to add a
repository you want to use in Renku:

1. Open the **Integrations** page from the 👤 user menu in the top right corner.
2. In the **GitHub** integration section, click **Update settings for GitHub.com**.
3. On GitHub, select the account or organization, then add or remove repositories and save.

In an organization, you need the same rights to change the repositories as to install the app. See
[Repositories in a GitHub organization](#repositories-in-a-github-organization).

### Repositories in your own GitHub account

You can install the app on your personal account and pick repositories yourself.
You can also pick an option that allows the app to access all repositories in your personal account.

### Repositories in a GitHub organization

Who can install the app in an organization depends on your role in the organization, your role on
the repository, and one organization setting, which we call **Repo admins can install apps** on
this page.

**The Repo admins can install apps setting**

- **What it controls:** whether repository admins can install GitHub Apps on their repositories
  without asking an organization owner. On GitHub, it is the checkbox **Allow repository admins to
  install GitHub Apps for their repositories**.
- **Where:** in the organization's **Settings → Member privileges**, under **GitHub Apps**. See
  [GitHub's documentation](https://docs.github.com/en/organizations/managing-programmatic-access-to-your-organization/limiting-oauth-app-and-github-app-access-requests-and-installations).
- **Who can change it:** organization owners only.
- **On (default):** a repository admin who is not an organization owner can install a GitHub App on
  the repositories they administer. This only applies if the app asks for no organization
  permissions and no repository administration permission.
- **Off:** only organization owners can install apps. Everyone else has to request the
  installation, and an owner has to approve it. Repository admins also cannot add their
  repositories to an existing installation.

**Who can install the Renku GitHub App**

The Renku GitHub App only asks for repository-level permissions, so the table below applies. The
rules are different for apps that ask for organization-level permissions.

| Role in the organization | Role on the repository           | Repo admins _can_ install apps (default) | Repo admins _can not_ install apps |
| ------------------------ | -------------------------------- | ---------------------------------------- | ---------------------------------- |
| Owner                    | Admin (implicit)                 | ✅ Install it yourself                   | ✅ Install it yourself             |
| Member                   | Admin                            | ✅ Install it yourself                   | ❌ Ask an owner                    |
| Member                   | Maintain / Write / Triage / Read | ❌ Ask an owner or a repo admin          | ❌ Ask an owner                    |
| Outside collaborator     | Admin                            | ✅ Install it yourself                   | ❌ Ask an owner                    |
| Outside collaborator     | Write / Read                     | ❌ Ask an owner or a repo admin          | ❌ Ask an owner                    |

:::tip

The app only needs to be installed once per organization. After that, it can be given access to
more repositories, and everyone who has access to those repositories on GitHub can use them in
Renku after connecting their own GitHub account.

:::

:::note

In some cases, a repository that Renku can access does not appear in the repository list on the
Renku GitHub App's settings page on GitHub. This can happen when your role does not allow you to
install apps in the organization, but an organization owner has already given the Renku GitHub App
access to that repository.

:::
