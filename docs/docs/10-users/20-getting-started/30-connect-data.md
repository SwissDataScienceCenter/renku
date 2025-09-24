# Connect Data

The data for this project comes from [Open Data Zurich
website](https://data.stadt-zuerich.ch/dataset/ugz_luftschadstoffmessung_tageswerte) and is titled *“Daily updated air quality measurements, since 1983”*.

For this project, we have stored a copy of the data in a publicly accessible folder that you can
link to your Renku project.

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

Check out our guides for creating different types of [data connectors](/docs/users/data/guides/connect-data/index/).

:::
