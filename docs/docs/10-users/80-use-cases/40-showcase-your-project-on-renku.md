# Showcase your project on Renku

Sometimes you need to share or present the results of your project to stakeholders or other colleagues. In this tutorial, you will learn how to serve dashboards from Renku projects through dedicated session launchers. 

**Contents**

## Part 1: Create a Renku project and connect data and code

1. Create a Renku project as explained in [How to Create a new project](/docs/users/projects/guides/create-a-project)
2. Add the data connectors. See our collection of [How To Guides](/docs/users/data/guides/connect-data/) for options for connecting data to your Renku project.
3. Add the code repositories as explained in [How to add a code repository to your project](/docs/users/code/guides/add-code-repository-to-project) 

## Part 2: Create an interactive session to showcase your project

Renku allows you to have multiple session launchers in the same project. For example, you can set up one launcher for working in VScode or RStudio, and create another launcher for visualizing data or an interactive app with Streamlit or Shiny.

1. Add a dedicated launcher for your Streamlit or Shiny app
    1. [How to create a Streamlit app on top of your project](/) /docs/users/sessions/guides/create-dashboard)
    2. [How to create a custom environment and launcher for a Shiny app](/) /docs/users/sessions/guides/create-custom-environment-and-launcher-for-shiny-app)
2. If you’d also like to have a space for regular development work on your Renku project (for example an RStudio environment), create a session launcher via one of the following options:
    1. [How to add a session launcher to your project](/docs/users/sessions/guides/add-session-launcher-to-project)
    2. [How to use your own docker image for a Renku session](/docs/users/sessions/guides/use-your-own-docker-image-for-renku-session)

## Part 3: Share your project

**Share Publicly**

If you want to make your Renku project publicly accessible, set the **visibility** of the project to **Public**. You can change the visibility of the project in the project settings. Only a project owner can change the project’s visibility. Read more in [Project Permissions](/docs/users/collaboration/permissions#project-permissions). 

Making a project public means that everyone on the internet can view the project and launch sessions. 

However, if there are protected resources on the project, for example a data connector that requires credentials or a private git repository, people will have to provide those credentials in order to see those resources. The access to the data and code resources are not changed when you make your Renku project public.

**Share with specific people**

If you want to share your Renku project only with specific people, add them as members to the project. 

You add new members to your project in the project settings. Only a project owner can change the project’s members. Read more in [Project Permissions](/docs/users/collaboration/permissions#project-permissions). 

In order to add people as members to a Renku project, they first have to have created a Renku account. 

## Example Showcase Projects

You can refer to the following projects which illustrate how to showcase a project on Renku:

- Shiny: [NO2 pathway analysis](https://renkulab.io/p/renku-team/n2o-pathway-analysis)
- Streamlit: [Store sales](https://renkulab.io/p/sdsc-renku-research/sales-streamlit)
