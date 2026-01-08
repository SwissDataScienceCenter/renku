# How to set up a course in “Light mode”

## Running a course in Light Mode

This workflow for using Renku for a course is great for workshops where participants only need to run sessions and do not need to save their work beyond the duration of a short-term course.

For creating the project:

1. Create a Renku project as explained in [How to create a new project](../../projects/guides/create-a-project).
2. Add the data, if needed. See our collection of guides for [connecting data to your Renku project](10-users/40-data/guides/00-connect-data/index.md)
    
3. Add a code repository as explained in [How to add a code repository to your project](../../code/guides/add-code-repository-to-project) with the course tasks.
4. Create a session launcher for working in your project:
    1. Select an environment:
        1. Check out the environments available in Renku via [How to add a session launcher to your project](../../sessions/guides/add-session-launcher-to-project). If these are sufficient for your course, use them!
        2. If you need to customize the environment for your course, you can [create a custom environment](10-users/60-sessions/guides/20-create-environment-with-custom-packages-installed.md) or [use your own docker image for a Renku session](../../sessions/guides/use-your-own-docker-image-for-renku-session).
    2. Set the session launcher’s default resource class to your course’s resource pool, as described in [How to select compute resources for your session](../../sessions/guides/select-compute-resources-for-session).
        
        :::info

        In order to ensure adequate resources for running the project and to control the consumption of the sessions, remember to assign to the session launcher with the adequate resource class when adding the session launcher to your project. 
        
        Learn more about custom resource pools for courses: [Request a Custom Resource Pool](../../sessions/resource-pools-and-classes#request-custom-resource-pool).
        
        :::
