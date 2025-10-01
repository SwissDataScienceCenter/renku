---
title: Data Repositories
description: Zenodo, Dataverse, etc.
---

# How to connect data from data repositories (such as Zenodo or Dataverse)

# Which data repositories are supported?

We support connecting with data repositories that have as their backend either InvenioRDM and Dataverse.

**Examples of Invenio data repositories:**

- [Zenodo](https://zenodo.org)
- [CaltechDATA](https://data.caltech.edu)

→ [see all](https://inveniosoftware.org/showcase/)

**Examples of Dataverse data repositories:**

- [Harvard Dataverse](https://dataverse.harvard.edu)

→ [see all](https://dataverse.org)

<aside>
<img src="https://www.notion.so/icons/info-alternate_blue.svg" alt="https://www.notion.so/icons/info-alternate_blue.svg" width="40px" />

Integrations with more data repositories, including ones in the ETH Domain, will be coming soon!

</aside>

# How to connect a dataset from a data repository to a Renku project

1. Find and copy the DOI on the dataset page (see [How to find the DOI on Zenodo](How%20to%20connect%20data%20from%20data%20repositories%20(such%20a%201eb0df2efafc802ab3bef1c47c8c45b4.md) or [How to find the DOI on Dataverse](How%20to%20connect%20data%20from%20data%20repositories%20(such%20a%201eb0df2efafc802ab3bef1c47c8c45b4.md))
2. On the Renku project page, click the ➕ button in the data section to add a new data connector
3. Select the **Import data by DOI** mode
    
    ![image.png](./connect-data-from-data-repositories-10.png)
    
4. Paste the DOI and click **Import**

# How to find the DOI on Zenodo

1. Find the DOI badge box
    
    ![Screenshot 2025-05-06 at 14.20.03.png](./connect-data-from-data-repositories-20.png)
    
2. Click the badge and copy the DOI
    
    ![Screenshot 2025-05-06 at 14.20.10.png](./connect-data-from-data-repositories-30.png)
    

Note: It is possible to use the DOI of a specific version of a Zenodo record, as well as using the “all versions” DOI (which will resolve to the latest version)

![Screenshot 2025-05-06 at 14.20.21.png](./connect-data-from-data-repositories-40.png)

# How to find the DOI on Dataverse

The DOI should appear in the citation box of a dataset:

![Screenshot 2025-05-06 at 14.24.07.png](./connect-data-from-data-repositories-50.png)

The DOI is also shown on the “Metadata” tab:

![Screenshot 2025-05-06 at 14.24.20.png](./connect-data-from-data-repositories-60.png)

# Example project

Here is an example project that uses datasets from both Zenodo and Dataverse: https://renkulab.io/p/renku-team/demo-project-2

To see the datasets, simply click “Launch” on either of the two session launchers available in the project. The data will automatically appear mounted in the `work` directory.