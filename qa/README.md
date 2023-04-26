# QA Checklist

Name: <Name>

Date: <yyyy-mm-dd>

Renku helm chart version: <0.25.0>


## Projects

- [ ] Create a new project on Renku (UI)
  <details>
    <summary>Details</summary>

    1. Create a new project in the website
    2. Ensure the KG integration is active
    3. Ensure a session can be successfully started on the project
    4. Ensure said sesssion can be accessed
    5. Ensure the Renku CLI is there
    6. Modify one of the environment files (e.g. `requirements.txt`) and run `renku save`
    7. Shut down the session, launch a new one from the new commit and make sure the package you added is there
  </details>

- [ ] Delete an existing project (UI)
  <details>
    <summary>Details</summary>

    1. Create a new project in the website
    2. Wait fo the image build and KG integration to complete
    3. Delete the project
    4. Ensure the project is truly gone
  </details>

- [ ] Fork a project (UI)
  <details>
    <summary>Details</summary>

    1. Create a new public project in the website
    2. Wait fo the image build and KG integration to complete
    3. Log in as a different user
    4. Fork the project
    5. Ensure that KG integration succeeds in the fork
    6. Ensure that sessions can be launched in the fork
  </details>


## Datasets

- [ ] Create a dataset from the UI
  <details>
    <summary>Details</summary>

    1. Create a new Renku project in the website
    2. Download a dataset from https://zenodo.org/
    3. Upload the data and create the dataset from the UI
    4. Ensure that the download completed
    5. Ensure that large files are in Git LFS
    6. After uploading the dataset try to clone the git project locally (does this take a really long time)
    7. Launch a session in the project
    8. Is the dataset available?
    9. Run `renku dataset ls` can you see the dataset you uploaded?
  </details>

- [ ] Create a dataset with the CLI from a running session
  <details>
    <summary>Details</summary>

    1. Start a renku session
    2. Navigate to https://zenodo.org/
    3. Pick a random dataset and import it in your session with `renku dataset import <zenodo-url>`
    4. Run `git push`
    5. Ensure the dataset was downloaded successfully
    6. Are large files checked in LFS?
    6. Navigate to the "Datasets" section of the project on the website, can you see the dataset you imported?
  </details>

## Workflows

- [ ] Create a workflow file in a session
  <details>
    <summary>Details</summary>
    
    To be filled in
  </details>
  
## Templates

- [ ] Create a project and start a session
  <details>
    <summary>Details</summary>
    
    1. Pick a template
    2. Create a project
    3. Wait for the image to build
    4. Ensure the image built
    5. Start a session
    6. Ensure the session can be accessed
    7. Ensure the renku CLI is installed and working
    8. Repeat for all templates
  </details>
