# Get Started

This tutorial will guide you through creating a Renku project related to the analysis of pollutants in the city of Zürich. We will be looking at a dataset from Open Data Zurich and using code from GitLab. We'll use RenkuLab to bring the data and code together in an interactive session where we can do analysis and seamlessly share with others. Let’s get started!

## Step 0: Create an RenkuLab Account

To begin using Renku, you need to create an account. Follow these steps:

1. Visit [RenkuLab](https://renkulab.io/v2)
2. Click on **Login** at the top right corner
3. Sign up using your preferred option (edu-ID, GitHub, or ORCID)

## Step 1: Create a Project

You will land in your Renku dashboard. From here, you can create a new project:

1. In the **My projects** section of the dashboard, click on **+** to create a new project
2. For project **Name**, enter **Zurich Air Quality Analysis**
    - (Leave **Owner, Visibility**, and **Description** unchanged)
3. Click **Create**.

## Step 2: Connect Data

The data for this project comes from Open Data Zurich and is titled *“Daily updated air quality measurements, since 1983”*.

[Open Data Zürich - Stadt Zürich](https://data.stadt-zuerich.ch/dataset/ugz_luftschadstoffmessung_tageswerte)

The data is hosted on the Open Data Zurich website. For this project, we have stored a copy of the data in a publicly accessible folder that you can link to your Renku project. 

Here's how to connect this dataset to your Renku project:

1. On the project page, click on **+** in the **Data** section
2. Click on **+ Create a Data Connector**
3. Choose **PolyBox**, select **Shared Folder** and click on **Next**. 
4. Paste this **public link**: https://polybox.ethz.ch/index.php/s/6EsHI6MF83mg52o 
5. Click **Test connection**, and then **Continue**.
6. Fill in the final details:
    1. For **Name**: enter **Zurich Air Quality Data**
    2. Leave **Owner**, **Visibility**, **Mounting point** unchanged
    3. Check the **Read-Only checkbox**, because the data is read-only.
7. Click **+ Add Connector**

## 2.1 (optional) Connect a storage space to save your work

The data connector we added in the previous step is for an existing dataset, and that storage is read-only. If you would like to be able to save the work you do in your Renku session, add a data connector for a storage where you'd like to save that work. For example, you might use a Switchdrive or PolyBox folder, or an S3 bucket. 

:::info

Check out our guides for creating different types of [data connectors](https://www.notion.so/1a50df2efafc80dda40af4b55c113b24?pvs=21)

:::

## Step 3: Add Code Repositories

For this project, we've created a code repository for this project on GitLab. We can connect this git repository to our Renku project in order to use it in our session.

:::note

Code on GitLab: https://gitlab.renkulab.io/learn-renku/air-quality-analysis

:::

Here's how to add the code repository to your project:

1. Click on **+** under the Code Repositories section.
2. Copy and paste the link to Clone with HTTPS from your git project. 
In this case, we already provide you with the code here: 

```
[https://gitlab.renkulab.io/learn-renku/air-quality-analysis.git](https://gitlab.renkulab.io/learn-renku/air-quality-analysis)
```

:::info

You can connect code repositories on GitHub and GitLab.com, too. Check out the **Integrations** page (accessible via the 👤 user menu in the top right) to connect your accounts and sync access to repositories. For more information, see [How to connect your Renku account to your GitHub or GitLab account](https://www.notion.so/How-to-connect-your-Renku-account-to-your-GitHub-or-GitLab-account-3d394998d82b44b4a053e9461949119a?pvs=21).
And, if your institution has a dedicated GitLab instance you would like to integrate with Renku, do not hesitate to [Contact](https://www.notion.so/Contact-dd098db288ff433893a4d4d429da99c1?pvs=21)  us, and we will be happy to add it to the list.

:::

For more information about working with code repositories on Renku, check out the **code** section in the [How To Guides](https://www.notion.so/Renku-How-To-Guides-900f417fc205439789a9fbdc5cadcec8?pvs=21).

## Step 4: Launch a Session

### 4.1 Add a Session Launcher

To run your code and analyze data on RenkuLab, first decide what kind of session environment you would like by creating a session launcher.

1. In the **Sessions** section of your project page, click on **+**.
2. Select the **Python/Jupyter/VSCode for Data Science** environment. This will give you a pre-configured environment with python and data science packages pre-installed. 
3. Click **Next**.
4. Choose the compute resources for your session based on your project’s needs in the drop-down menu, and select the amount of disk space.

:::info

You can have multiple Session Launchers in your project that run different kinds of sessions. Check
out what else you can do with [Renku
sessions](https://www.notion.so/1a70df2efafc81c9ae50d9ef3e7bddbd?pvs=21)

:::

:::tip

Do you need more resources than are available in RenkuLab’s public resource classes? [Contact](https://www.notion.so/Contact-dd098db288ff433893a4d4d429da99c1?pvs=21) us! We can configure a custom resource pool for your team or class.

:::

### 4.2 Launch a Session

1. Click on the **Launch** button in your new Session Launcher to start a session. 

Once your session has launched, you will see the data and code that you linked to your project inside your session ready to use. The data and code each show up in the file system as folders. Open them up and take a look around!

### 4.3 (optional) Install Packages in Your Environment

We have set up a global environment which already has many packages installed. If you need to install other packages, you can proceed once your session is launched, and install any necessary packages using the terminal.

:::info

For a detailed guide for how to install packages, see [How to install packages in your session](https://www.notion.so/How-to-install-packages-in-your-session-f1ffb813996943869b50f6c5b263f5c9?pvs=21).

:::

### 4.4 Code (optional)

Open the notebook `air-quality-analysis/notebooks/exploratory_analysis.ipynb` in the repository. Run the full notebook.

In the final cell, modify plotting part in the for loop, namely the `plt.show()`, to save your figures in the local disk. You can use the following snippets as a reference. 

This code will save the figures in the working directory of the session (2 directories up from the notebook).

:::info

The session working directory (where you land when you first open the session) is a temporary scratch space that is saved for as long as your session is running or paused. Content saved here will be deleted if you shut down the session.

:::

```python
file_save_path = '../../'

plt.savefig(file_save_path+'Trend Analysis of ' + str(parameter) + ' in ' + str(location) +' by Date'+'.png')
plt.savefig(file_save_path+'Trend Analysis of ' + str(parameter) + ' in ' + str(location) +' by Date'+'.pdf')

plt.savefig(file_save_path+'Decomposition Analysis of ' + str(parameter) + ' in ' + str(location) +' by Date'+'.png')
plt.savefig(file_save_path+'Decomposition Analysis of ' + str(parameter) + ' in ' + str(location) +' by Date'+'.pdf')

```

If you did [2.1 (optional) Connect a storage space to save your work](https://www.notion.so/2-1-optional-Connect-a-storage-space-to-save-your-work-1a50df2efafc80188d45c60b18f19c71?pvs=21), try creating the file in your attached storage space (modify the `file_save_path`), and see that it is synced back to the source system.

:::info

The git repository we connected with our project is read-only. However, when you connect your Renku project with a git repository that you have push (write) access to, you will be able to run git commands in the session to save your work back to the code repository! To make this work, first [connect Renku with your GitHub or GitLab account](https://www.notion.so/How-to-connect-your-Renku-account-to-your-GitHub-or-GitLab-account-3d394998d82b44b4a053e9461949119a?pvs=21).

:::

## Step 5: Share Your Project

Collaboration is key in research, and Renku makes it easy to share your project.

### 5.1 Add People to a Project (optional)

In your project page:

1. Open the **Settings** tab.
2. Go to **Project Members** and click on **+**
3. Search for  the people you want to invite by name or username and set their role. 

:::info

Note that in order to add people to your Renku project, they need to already have a Renku account. 

:::

### 5.2 Make Your Project Public (optional)

If you want to share your project with a wider audience, you can make it public. 

1. In your project page, open the **Settings** tab.
2. Change the project visibility settings and select **Public**.
3. Share the project link with anyone you want to access it by copying the URL.

## Conclusion

🎉 Congratulations! You have successfully created a Renku project, connected data and code, launched a session, and shared your project with collaborators. 

You can find this project available here: https://renkulab.io/v2/projects/renku-team/zurich-air-quality-analysis

:::tip

Want to learn more? Check out our full library of [ Renku How-To Guides](https://www.notion.so/Renku-How-To-Guides-900f417fc205439789a9fbdc5cadcec8?pvs=21). 

:::
