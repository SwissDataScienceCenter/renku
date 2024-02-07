.. _admin_sessions-cloud-storage:

Enable mounting cloud storage in users-sessions
-----------------------------------------------

Renkulab supports mounting cloud storage in users-sessions. We use a CSI driver based on 
`rclone <https://rclone.org/>`_.

To enable the feature you need to configure it in your `values-file.yaml`:

.. code-block:: yaml

  # install the CSI driver
  global:
    csi-rclone:
      install: true

  notebooks:
    cloudstorage:
      enabled: true
      storageClass: csi-rclone

  csi-rclone:
    csiNodepluginRclone:
      # optional tolerations if using node pools with taints
      tolerations:
        - key: renku.io/dedicated
          operator: Equal
          value: user
          effect: NoSchedule
        - key: renku.io/gpu
          operator: Equal
          value: "true"
          effect: NoSchedule

The ``global.csi-rclone.install`` option will install the ``csi-rclone`` Helm chart in the same namespace
as the renku chart. 