# Launch a Session

## 4.2 Launch a Session

Click on the **Launch** button in your new Session Launcher to start a session. 

Once your session has launched, you will see the data and code that you linked to your project inside your session ready to use. The data and code each show up in the file system as folders. Open them up and take a look around!

## 4.3 (optional) Add a second Session Launcher

When you want to create a RenkuLab session with a set of packages that’s reproducible and shareable, we recommend defining your package dependencies in code. Then, use the ‘Create from Code’ session launcher option to have Renku create a reproducible session environment for you! See [How to create an environment with custom packages installed](/docs/users/sessions/guides/How-to-create-an-environment-with-custom) for details. 

## 4.4 Code (optional)

Open the notebook `air-quality-analysis/notebooks/exploratory_analysis-genova.ipynb` in the repository. Run the full notebook.

In the final cell, modify plotting part in the for loop, namely the `plt.show()`, to save your figures in the local disk. You can use the following snippets as a reference. 

This code will save the figures in the working directory of the session (2 directories up from the notebook).

:::info
The session working directory (where you land when you first open the session) is a temporary scratch space that is saved for as long as your session is running or paused. Content saved here will be deleted if you shut down the session. 

:::

```python
file_save_path = '../../'

plt.savefig(file_save_path+'Trend Analysis of ' + str(parameter) + ' in ' + str(location) +' by Date'+'.png')
plt.savefig(file_save_path+'Trend Analysis of ' + str(parameter) + ' in ' + str(location) +' by Date'+'.pdf')
```

If you did [2.1 (optional) Connect a Storage Space to save your work](https://docs.renkulab.io/en/latest/docs/users/data/guides/connect-data/), try creating the file in your attached storage space (modify the `file_save_path`), and see that it is synced back to the source system. 

:::info

The git repository we connected with our project is read-only. However, when you connect your Renku project with a git repository that you have push (write) access to, you will be able to run git commands in the session to save your work back to the code repository! To make this work, first [connect Renku with your GitHub or GitLab account](https://docs.renkulab.io/en/latest/docs/users/code/guides/connect-renku-account-to-github-or-gitlab-account). 

:::
