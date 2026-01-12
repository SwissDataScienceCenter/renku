# Migrate a session launcher that references RenkuLab GitLab

:::danger

**DATA REMOVAL DEADLINE - JANUARY 2026**

The RenkuLab GitLab (gitlab.renkulab.io) will soon be shut down. All docker images hosted there will
be removed. Follow the migration instructions below before January 2026 to save your work.

:::

Renku 2.0 supports a variety of ways of working with sessions. To determine what kind of Renku
session environment is right for you and how to migrate, answer the following questions:

## 1. Is your project’s code repository public or private?

### → My code repository is public

That’s great! Please continue to the next question, [2. What language does your project use?](#2-what-language-does-your-project-use) 

### → My code repository is private

We will be releasing functionality to connect with images for private code repositories in October. Please hang tight and come back to migrate your launcher after the next major Renku release in October. 

To receive updates about major Renku releases, subscribe to the [Renku Newsletter](https://www.notion.so/12d0df2efafc8012a10ec4563b8d8b8c?pvs=21)!

## 2. What language does your project use?

### → I’m working in R

If you don’t need any specific packages installed in your R session, follow these steps to create a new R session launcher in your project:

1. Create a session launcher by clicking the ➕ button in the sessions box.
2. Select **global environment**
3. Select the **R** global environment

If you do need custom packages installed in your R session, see [How to migrate an image from RenkuLab GitLab to Dockerhub](../../migrate-v1-v2/migrate-from-gitlab/migrate-image-dockerhub).


### → I’m working in Python

In Renku 2.0, you can also have Renku build session environment (docker image) from a code definition file. In Renku 2.0, this is called **code based environments**. See the following guide to set up a new code based environment from your code repository. 

[How to create an environment with custom packages installed](/docs/users/sessions/guides/create-environment-with-custom-packages-installed) 

:::info

**Important**: There is a key difference between the way images were built in Renku Legacy and the new way images are built in Renku 2.0. In Renku Legacy, projects came with both `requirements.txt` and `environment.yaml` files. Having both files confuses the Renku 2.0 system! Please delete one of the two files. 

:::

Once you’ve created a code based environment launcher, you can delete your original, migrated launcher that references the RenkuLab GitLab.

### → I’m working in another language

See [How to migrate an image from RenkuLab GitLab to Dockerhub](../../migrate-v1-v2/migrate-from-gitlab/migrate-image-dockerhub).
