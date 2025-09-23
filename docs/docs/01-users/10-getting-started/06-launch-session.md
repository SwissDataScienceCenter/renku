# Launch a Session

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
