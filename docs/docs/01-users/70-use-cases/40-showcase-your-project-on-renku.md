# Showcase your project on Renku

Sometimes you need to share or present the results of your project to stakeholders or other colleagues. In this tutorial, you will learn how to serve dashboards from Renku projects through dedicated session launchers. 

**Contents**

## Part 1: Create a Renku project and connect data and code

1. Create a Renku project as explained in [[old] How to Create a new project](%5Bold%5D%20How%20to%20Create%20a%20new%20project%207243ec38d7964900a2b0fdeb17cfa4ce.md)
2. Add the data connectors. See our collection of How To Guides for options for connecting data to your Renku project:
    
    [Untitled](Showcase%20your%20project%20on%20Renku%201460df2efafc801e86a6e4f6d99020e7/Untitled%201490df2efafc803289d2d5a207f67904.csv)
    
3. Add the code repositories as explained in [How to add a code repository to your project](How%20to%20add%20a%20code%20repository%20to%20your%20project%2053658e1ef33d431bb3c3129a82d99a5f.md) 

## Part 2: Create an interactive session to showcase your project

Renku allows you to have multiple session launchers in the same project. For example, you can set up one launcher for working in VScode or RStudio, and create another launcher for visualizing data or an interactive app with Streamlit or Shiny.

1. Add a dedicated launcher for your Streamlit or Shiny app
    1. [How to create a Streamlit app on top of your project](How%20to%20create%20a%20Streamlit%20app%20on%20top%20of%20your%20proje%20815f09a34f384396ab1f758db4110f46.md) 
    2. [How to create a custom environment and launcher for a Shiny app](How%20to%20create%20a%20custom%20environment%20and%20launcher%20fo%200a4783d76b8646ce83c97481f28f0381.md) 
2. If you’d also like to have a space for regular development work on your Renku project (for example an RStudio environment), create a session launcher via one of the following options:
    1. [How to add a session launcher to your project](How%20to%20add%20a%20session%20launcher%20to%20your%20project%20601ba47455354413b87c69447aa33831.md) 
    2. [How to use your own docker image for a Renku session](How%20to%20use%20your%20own%20docker%20image%20for%20a%20Renku%20sessi%2011f0df2efafc80af848ffcaf9ccff31c.md) 

## Part 3: Share your project

**Share Publicly**

If you want to make your Renku project publicly accessible, set the **visibility** of the project to **Public**. You can change the visibility of the project in the project settings. Only a project owner can change the project’s visibility. Read more in [Project Permissions](Permissions,%20Roles,%20and%20Access%20Rights%20%5Bdeprecated%5D%209c0aea98b26c4c02ba6323326fa949e1.md). 

Making a project public means that everyone on the internet can view the project and launch sessions. 

However, if there are protected resources on the project, for example a data connector that requires credentials or a private git repository, people will have to provide those credentials in order to see those resources. The access to the data and code resources are not changed when you make your Renku project public.

**Share with specific people**

If you want to share your Renku project only with specific people, add them as members to the project. 

You add new members to your project in the project settings. Only a project owner can change the project’s members. Read more in [Project Permissions](Permissions,%20Roles,%20and%20Access%20Rights%20%5Bdeprecated%5D%209c0aea98b26c4c02ba6323326fa949e1.md). 

In order to add people as members to a Renku project, they first have to have created a Renku account. 

## Example Showcase Projects

You can refer to the following projects which illustrate how to showcase a project on Renku:

- Shiny: [NO2 pathway analysis](https://renkulab.io/v2/projects/renku-team/n2o-pathway-analysis)
- Streamlit: [Store sales](https://renkulab.io/v2/projects/sdsc-renku-research/sales-streamlit)