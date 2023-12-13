.. _admin_sessions-cloud-storage:

Enable mounting cloud storage in users-sessions
-----------------------------------------------

Renkulab supports mounting cloud storage in users-sessions.
To enable the feature you need to set the following values in your `values-file.yaml`:

.. code-block:: yaml

  notebooks:
    cloudstorage:
      s3:
        enabled: true
        installDatashim: true


The `installDatashim` option will deploy the Datashim <https://datashim-io.github.io/datashim/> Helm chart in the same
namespace as the `Renkulab` chart.
