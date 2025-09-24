# How to migrate a session launcher that references RenkuLab GitLab

<aside>
<img src="https://www.notion.so/icons/new-alert_red.svg" alt="https://www.notion.so/icons/new-alert_red.svg" width="40px" />

**DATA REMOVAL DEADLINE - JANUARY 2026**

The RenkuLab GitLab (gitlab.renkulab.io) will soon be shut down. All docker images hosted there will be removed. Follow the migration instructions below before January 2026 to save your work.

</aside>

Renku 2.0 supports a variety of ways of working with sessions. To determine what kind of Renku session environment is right for you and how to migrate, answer the following questions:

## 1. Is your project’s code repository public or private?

### → My code repository is public

That’s great! Please continue to the next question, [2. What language does your project use?](#2-what-language-does-your-project-use)

### → My code repository is private

We will be releasing functionality to connect with images for private code repositories in October. Please hang tight and come back to migrate your launcher after the next major Renku release in October.

To receive updates about major Renku releases, subscribe to the [Renku Newsletter](https://www.notion.so/12d0df2efafc8012a10ec4563b8d8b8c?pvs=21).

## 2. What language does your project use?

### → I’m working in R

If you don’t need any specific packages installed in your R session, follow these steps to create a new R session launcher in your project:

1. Create a session launcher by clicking the ➕ button in the sessions box.
2. Select **global environment**
3. Select the **R** global environment

If you do need custom packages installed in your R session, see [How to migrate an image from RenkuLab GitLab to Dockerhub](/docs/users/projects/guides/migrate-session-launcher-that-references-renku-gitlab).

### → I’m working in Python

In Renku 2.0, you can also have Renku build session environment (docker image) from a code definition file. In Renku 2.0, this is called **code based environments**. See the following guide to set up a new code based environment from your code repository.

[How to create an environment with custom packages installed](/docs/users/sessions/guides/create-environment-with-custom-packages-installed)

<aside>
<img src="https://www.notion.so/icons/warning_yellow.svg" alt="https://www.notion.so/icons/warning_yellow.svg" width="40px" />

**Important**: There is a key difference between the way images were built in Renku Legacy and the new way images are built in Renku 2.0. In Renku Legacy, projects came with both `requirements.txt` and `environment.yaml` files. Having both files confuses the Renku 2.0 system! Please delete one of the two files.

</aside>

Once you’ve created a code based environment launcher, you can delete your original, migrated launcher that references the RenkuLab GitLab.

### → I’m working in another language

See [How to migrate an image from RenkuLab GitLab to Dockerhub](https://www.notion.so/How-to-migrate-an-image-from-RenkuLab-GitLab-to-Dockerhub-26c0df2efafc80eeaf1df0c85c2b73b2?pvs=21).

## How to migrate an image from RenkuLab GitLab to Dockerhub

<aside>
<img src="https://www.notion.so/icons/warning_yellow.svg" alt="https://www.notion.so/icons/warning_yellow.svg" width="40px" />

These instructions have not yet been tested

</aside>

## ~~Method 1: Using Docker Desktop GUI~~

**Step 1: Set up Docker Desktop**

- Install Docker Desktop (has a graphical interface)
- Log into both registries through the GUI:
    - Click the Docker Desktop icon → Sign in
    - Go to Settings → Docker Hub to add your DockerHub credentials
    - Add RenkuLab GitLab registry credentials in Settings → Docker Engine

**Step 2: Use the Images tab**

- Open Docker Desktop
- Go to the "Images" tab
- Your RenkuLab GitLab images should appear if you've pulled them before
- Click the three dots next to an image → "Push to Hub"
- Choose your DockerHub repository

## Method 0: Use CI to push to dockerhub

Avoids having to install docker locally (Windows, locked down computers, …)

## Method 2: Using the Docker command line

## Prerequisites Setup

**Step 1: Create a DockerHub account**

- Go to [https://hub.docker.com](https://hub.docker.com/) and click "Sign Up"
- Choose a username (this will be part of your image URLs)
- Verify your email address

**Step 2: Create a repository on DockerHub**

- Log into DockerHub and click "Create Repository"
- Choose a repository name (should match or relate to your GitLab project)
- Set visibility (Public or Private)
- Click "Create"

## Create a RenkuLab GitLab Personal Access Token

- https://gitlab.renkulab.io/-/user_settings/personal_access_tokens
    - (This URL is the same for everyone)
- with registry scope

## Local Environment Setup

**Step 3: Install Docker locally**

- Ensure Docker is installed and running on your machine
- Verify with: `docker --version`

**Step 4: Log into both registries**

[Authenticate with the container registry | GitLab Docs](https://docs.gitlab.com/user/packages/container_registry/authenticate_with_container_registry/#authenticate-with-a-token)

```bash
*# Log into RenkuLan GitLab registry*
docker login registry.renkulab.io # update to above
*# Enter your RenkuLab username and personal access token

# Log into DockerHub*
docker login
*# Enter your DockerHub username and password*
```

## Image Migration Process

**Step 5: Pull the image from GitLab registry**

Replace with your actual image URL that is listed in your Renku session launcher.

```bash
docker pull registry.renkulab.io/your-username/your-project/your-image:tag
```

**Step 6: Tag the image for DockerHub**

```bash
docker tag registry.renkulab.io/your-username/your-project/your-image:tag your-dockerhub-username/your-repo-name:tag
```

**Step 7: Push to DockerHub**

```bash
docker push your-dockerhub-username/your-repo-name:tag
```

## Verification and Cleanup

**Step 8: Verify the migration**

- Check your DockerHub repository online to confirm the image appears
- Test pulling from DockerHub:
    
    ```bash
    docker pull your-dockerhub-username/your-repo-name:tag
    ```
    

**Step 9: Clean up local images (optional)**

```bash
*# Remove local copies if desired*
docker rmi registry.gitlab.com/your-username/your-project/your-image:tag
docker rmi your-dockerhub-username/your-repo-name:tag
```
