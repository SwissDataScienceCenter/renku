# How to set up a course in “Light mode”

## Running a course in Light Mode

This workflow for using Renku for a course is great for workshops where participants only need to run sessions and do not need to save their work beyond the duration of a short-term course.

For creating the project:

1. Create a Renku project as explained in [How to create a new project](/docs/users/projects/guides/create-a-project).
2. Add the data, if needed. See our collection of How To Guides for options for creating data connectors.
    
    [GUIDES](.)
    
3. Add a code repository as explained in [How to add a code repository to your project](/docs/users/code/guides/add-code-repository-to-project) with the course tasks.
4. Create a session launcher for working in your project:
    1. Select an environment:
        1. Check out the environments available in Renku via [How to add a session launcher to your project](/docs/users/sessions/guides/add-session-launcher-to-project). If these are sufficient for your course, use them!
        2. If you need to customize the environment for your course, you can create a custom environment yourself via [How to use your own docker image for a Renku session](/docs/users/sessions/guides/use-your-own-docker-image-for-renku-session), or  [Contact](/docs/community) us and we can create a custom environment for your course!
    2. Set the session launcher’s default resource class to your course’s resource pool, as described in [How to select compute resources for your session](/docs/users/sessions/guides/select-compute-resources-for-session).
        
        <aside>
        <img src="https://www.notion.so/icons/info-alternate_blue.svg" alt="https://www.notion.so/icons/info-alternate_blue.svg" width="40px" />
        
        In order to ensure adequate resources for running the project and to control the consumption of the sessions, remember to assign to the session launcher with the adequate resource class when adding the session launcher to your project. Learn more about custom resource pools for courses: [Request a Custom Resource Pool](Resource%20Pools%20&%20Classes%2011f0df2efafc802dbe05f4dcd375431f.md).
        
        </aside>
