
Use this template to create a new QA checklist. To get started navigate to https://github.com/SwissDataScienceCenter/renku/wiki
and create a new page named "QA Checklist YYYYMMDD <renku.version.number>". For a real case this would be something like
"QA Checklist 20230515 0.25.0".

Paste the remainder of this document into the wiki page and fill it out as you are going through the tests.

If possible use Github handles for the names of the testers.

When the QA is complete update the release notes from https://github.com/SwissDataScienceCenter/renku/releases with a link
to the wiki page that contains these notes.

**QA checklist content starts below, exclude everything above this point including this line.**


Name(s): <Name>

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
    2. Go to from https://zenodo.org/ and pick any dataset
    3. In your new Renku project navigate to the Datasets tab
    4. Create a new Renku dataset from the Zeonodo dataset you selected earlier
    5. Ensure the dataset was created successfully
    6. Launch a session in the project
    7. Is the dataset available?
    8. Run `renku dataset ls` can you see the dataset you uploaded?
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

- [ ] Create a project and start a session in all Renku templates
  <details>
    <summary>Details</summary>
    
    1. Pick a template (one of Python, R, Bioconductor or Julia). Skip any templates
      you have already used in other tests.
    2. Create a project
    3. Wait for the image to build
    4. Ensure the image built
    5. Start a session
    6. Ensure the session can be accessed
    7. Ensure the renku CLI is installed and working
    8. Repeat for all templates
  </details>
