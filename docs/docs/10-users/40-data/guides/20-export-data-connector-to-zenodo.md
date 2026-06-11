---
title: Publish data from a data connector to Zenodo
---

# Publish data from a data connector to Zenodo

You can export files from a Renku data connector directly to a draft deposit in [Zenodo](https://zenodo.org/). Renku creates the deposit in the background with basic metadata. You can then review, complete, and publish the draft in Zenodo.

:::info

Renku does not publish the deposit automatically. After Renku creates the draft deposit, you must finalize the metadata and publish it in Zenodo.

:::

## Before you begin

Make sure that:

- You have access to the data connector and to the files you want to publish.
- You have a Zenodo account.
- Your Renku account is connected to Zenodo. See [Step 1: activate the Zenodo integration](#step-1-activate-the-zenodo-integration).

## Step 1: activate the Zenodo integration

1. Open the Renku **Integrations** page.
2. Click on the Add button and search for the **Zenodo** integration.
3. Click **Connect**.
4. Follow the Zenodo authorization flow and grant Renku the requested permissions.
5. When you return to Renku, confirm that the Zenodo integration is shown as active.

You only need to do this once per Renku account, unless the integration is disconnected or the authorization expires.

## Step 2: export files from the data connector

1. Open the project, group, or user page that contains the data connector.
2. Click the data connector drop-down menu.
3. Select the option to export or publish the data connector files to Zenodo.
4. Enter the path inside the data connector that contains the files you want to publish.
5. Confirm the export.

Renku will start uploading the files from the selected path to Zenodo in the background.

## Choosing the path to publish

The path you specify is interpreted relative to the data connector. Renku uploads all files found in that path to the Zenodo draft deposit.

### Directory structure is not preserved

It is not possible to preseve a directory structure in Zenodo deposits. If the
selected path contains a directory structure that is important for understanding
or reusing the data, first create an archive file, such as a `.zip` or
`.tar.gz`, with the structure you want to preserve. Then export the path that
contains that archive file.

For example, instead of exporting this structure directly:

```text
results/
├── raw/
│   └── measurements.csv
├── processed/
│   └── summary.csv
└── README.md
```

create an archive such as `results.zip` and export the path containing `results.zip`.

## What happens after you start the export

After you confirm the export:

1. Renku uploads the selected files to Zenodo in the background.
2. Zenodo creates a draft deposit.
3. Renku adds basic metadata to the draft deposit.
4. You review and complete the draft deposit in Zenodo.
5. You publish the deposit from Zenodo when it is ready.

Depending on the number and size of files, the upload can take some time. You can continue working in Renku while the export runs.

## Complete and publish the deposit in Zenodo

When the upload is complete, the deposit export section will show a URL linking to the draft deposit in Zenodo for opening and reviewing the publishing. In Zenodo, you should:

- Check that all expected files are present.
- Add or complete the required metadata, such as title, description, creators, license, and related identifiers.
- Review any archive files to make sure they contain the expected directory structure.
- Publish the deposit when the metadata and files are ready.

Once published, Zenodo assigns a DOI to the deposit according to Zenodo's
publishing workflow. You can then add the published data to Renku via a DOI as
with all other Zenodo datasets.
