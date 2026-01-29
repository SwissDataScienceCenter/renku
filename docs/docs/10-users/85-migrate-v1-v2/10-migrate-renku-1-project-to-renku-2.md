# Migrate a Renku Legacy project to Renku 2.0

Time is running short! Renku Legacy was discontinued in October 2025, and you still have access to your Renkulab Gitlab projects until end of January 2026. Hence, move your code repositories as explained in our [detailed migration guides](https://blog.renkulab.io/sunsetting-legacy/#how-to-move-a-code-repository-to-an-external-provider) and in our dedicated videos.

<div style={{"text-align":"center"}}>
    <iframe width="560" height="315" src="https://www.youtube-nocookie.com/embed/videoseries?si=sT-HeSUMib4O3d7b&amp;list=PLagSzgm6aERkLQ8LINw8luZKdv9ru7aHh" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen />

    *Learn the main steps to successfully migrate your project from Renku Legacy*

</div>

The rest of this guide will explain to you how migrating a project from Renku Legacy to Renku 2.0 manually means, and walk you through the process.

:::tip

⚠️ If you have moved a repository to a new home (like GitHub or our new GitLab instance) and want to ensure old links, such as those in published papers, don't break, we have a solution. Simply [**fill out this form**](https://www.notion.so/2540df2efafc80a2bee5f1b7d247c28e?pvs=21) with your new repository location or Renku 2.0 project URL, and we will automatically redirect visitors from the old URL to the new one.

:::

## Manual Migration

As of October 2025, Renku Legacy projects are not available anymore. Hence, you need to migrate a project manually, you can do so by creating a new Renku
2.0 project with all the same code, data, and session components. Here’s how:

1. Create a new project in Renku 2.0
2. **Code:** Add the new home of your Renku Legacy code repository to the Code section in your Renku 2.0 project
   1. See [How to add a code repository to your project](../code/guides/add-code-repository-to-project)
3. **Data**: If you use cloud storage in your project, create a data connector for the cloud storage object.
4. **Session:** Add one session launcher or multiple session launchers according to the language you would like to work in your project.
   1. See [Create an environment for your session](../sessions/guides/environments)

## What happens to my Renku Legacy project assets?

**✅ Code Repository**

Your project code repositories need to be migrated manually.

**✅ ✳️ Datasets & Git LFS Data**

Git LFS data, including Renku Datasets, are a part of your code repository, and will be available
from inside your Renku 2.0 session via the git lfs command line tool.

Git LFS data will not be downloaded automatically upon session launch, so you must run `git lfs
pull` inside your session.

Please, configure access to your cloud data storage provider as a Renku 2.0 [Data connector](../data/data).

:::warning

Please note that Renku 2.0 takes a significantly different approach to data compared to Renku
Legacy. Read more on our blog post: [Deep Dive: What's New in Renku
2.0](https://blog.renkulab.io/deep-dive-2-0/#data-connectors-replace-datasets). Consider moving your
data into an external storage system and connecting it to your project via a [Data
connector](../data/data).

:::

**✅ ✳️ Workflows**

You may continue to use Renku workflows in your session via the CLI.

:::warning

Please note that Renku 2.0 takes a significant different approach to workflows compared
to Renku Legacy. Read more on our blog post [Deep Dive: What's New in Renku
2.0](https://blog.renkulab.io/deep-dive-2-0/#a-new-vision-for-workflows).

:::

**✅ Metadata**

Project title, description, and keywords can be added and modified as you create your new project in Renku 2.0.

One exception is the project image, which is not supported at this time.

**✅ Members**

Please add members directly to your Renku 2.0 project after creating your new project.

:::tip
If you would like to share a set of projects with a group, you can create a Renku 2.0
group! See [How to create a group](../collaboration/guides/create-group).
:::
