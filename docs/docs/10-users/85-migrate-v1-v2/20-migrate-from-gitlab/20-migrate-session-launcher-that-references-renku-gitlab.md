---
title: Migrate a session launcher
description: Find the best approach for your launcher
---
# Migrate a session launcher that references RenkuLab GitLab

:::danger

**DATA REMOVAL DEADLINE - JANUARY 2026**

The RenkuLab GitLab (gitlab.renkulab.io) will soon be shut down. All docker images hosted there will
be removed. Follow the migration instructions below before January 2026 to save your work.

:::

Renku 2.0 supports a variety of ways of working with sessions. To determine what kind of Renku
session environment is right for you and how to migrate, answer the following questions:

## 1. What language does your project use?

* [→ Python](#-im-working-in-python)
* [→ R](#-im-working-in-r)
* [→ other](#-im-working-in-another-language)

### → I’m working in Python

That’s great! Please continue to the next question, [Is your repository public or private?](#2-is-your-projects-code-repository-public-or-private)

### → I’m working in R

If you don’t need any specific packages installed in your R session, follow these steps to create a
new R session launcher in your project:

1. Create a session launcher by clicking the ➕ button in the sessions box.
2. Select **global environment**
3. Select the **R** global environment

If you do need custom packages installed in your R session, see [How to migrate an image from
RenkuLab GitLab to
Dockerhub](10-users/85-migrate-v1-v2/20-migrate-from-gitlab/30-migrate-image-dockerhub.md).


### → I’m working in another language

See [How to migrate an image from RenkuLab GitLab to Dockerhub](10-users/85-migrate-v1-v2/20-migrate-from-gitlab/30-migrate-image-dockerhub.md).

## 2. Is your project’s code repository public or private?

* [→ Public](#-my-code-repository-is-public)
* [→ Private](#-my-code-repository-is-private)

### → My code repository is public

In Renku 2.0, you can have Renku build session environment (docker image) from a code definition
file. In Renku 2.0, this is called **code based environments**. See the following guide to set up a
new code based environment from your code repository.

[How to create an environment with custom packages installed](/docs/users/sessions/guides/create-environment-with-custom-packages-installed)

:::warning

In Renku Legacy, projects came with both `requirements.txt` and `environment.yaml` files. Having
both files confuses the Renku 2.0 system! **Please delete one of the two files environment
definition files.**

:::

Once you’ve created a new code based environment launcher, you can delete the migrated
launcher that references the RenkuLab GitLab.

### → My code repository is private

Please see [Creating an environment from a private code repository](10-users/60-sessions/guides/25-create-environment-with-custom-packages-private-code-repository.md)
