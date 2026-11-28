# renku-remote Helm chart

Installs the components a **remote** Kubernetes cluster needs so that an existing
Renku deployment (the "local" cluster) can launch sessions on it. This is not a full
Renku deployment. See the [remote clusters admin docs](../../docs/docs/20-admins/30-operation/50-remote.md)
for the overall procedure, including the steps on the local cluster.

| Component | Required | Values section |
|---|---|---|
| Amalthea session operator (`amalthea-sessions` sub-chart) | yes | `amalthea-sessions` |
| CSI-rClone (`csi-rclone` sub-chart), for data connectors | no | `csi-rclone` |
| ServiceAccount, token, Role and RoleBinding used by the local cluster | yes (default on) | `crossClusterAccess` |
| Priority classes for resource pool quotas | when pools have quotas | `priorityClasses` |
| Network policies for sessions | recommended (default on) | `networkPolicies` |

The `amalthea-sessions` and `csi-rclone` versions in `Chart.yaml` must be identical to the
ones in `../renku/requirements.yaml` for the Renku version running on the local cluster.

## Install

Install the chart in the namespace where the remote sessions will run:

```bash
helm dependency build helm-chart/renku-remote
helm upgrade --install renku-remote helm-chart/renku-remote \
  --namespace renku-user-sessions --create-namespace \
  -f my-values.yaml
```

Afterwards, the release notes print the commands to build the kubeconfig for the
local cluster from the `<release>-renku-remote-cross-cluster-token` secret.

## CSI-rClone

csi-rclone is cluster-wide: it installs a CSI driver, storage classes and a node plugin
DaemonSet. Enable it in only one release per cluster. The node plugin must run on every node
where sessions can run, so add tolerations for any taints on those nodes
(`csi-rclone.csiNodepluginRclone.tolerations`). `amalthea-sessions.rcloneStorageClass`
must be `<csi-rclone.storageClassName>-secret-annotation`, and the chart fails to render otherwise.
