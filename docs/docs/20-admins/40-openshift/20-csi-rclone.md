---
title: Installing csi-rclone on OpenShift
---

## Preparing the project

The helm installation operates the same on OpenShift as on Kubernetes however
there are some differences such as management of RBACs that must be done by an
administrator. There are also CSIDriver and StorageClass that are managed by
admins.

### RBAC

Generate the RBAC out of the helm chart:

```bash
helm template --namespace csi-rclone renku/csi-rclone --set rbac.create=true | yq e '. | select(.kind == "Cluster*")' > rbac.yaml
```

:::warning

The above command will give you the RBAC from the latest version of csi-rclone.
But you can select the RBAC from a specific Renku version by adding the
`--version=x.x.x` flag to the `helm template` command. Using the latest version
may cause problems or unpredictable behavior if you extract the resource from a
different version of csi-rclone than what you have or will install.

:::

### CSI driver

Generate the CSIDriver object out of the helm chart:

```bash
helm template --namespace csi-rclone renku/csi-rclone --set driver.create=true | yq e '. | select(.kind == "CSIDriver*")' > driver.yaml
```

### Storage classes

Generate the StorageClass objects out of the helm chart:

```bash
helm template --namespace csi-rclone renku/csi-rclone --set storageClass.create=true | yq e '. | select(.kind == "StorageClass*")' > storageclass.yaml
```

## Cluster access

Login

```bash
oc login --user <username> https://url.to.openshift.cluster --web
```

Create the project

```bash
oc new-project csi-rclone
```

## Prerequisite

### RBAC

Install the RBAC rules generated earlier (as an admin):

```bash
oc apply -f rbac.yaml
```

### CSI driver

Install the CSIDriver object generated earlier (as an admin):

```bash
oc apply -f driver.yaml
```

### Storage classes

Install the StorageClass object generated earlier (as an admin):

```bash
oc apply -f storageclass.yaml
```

### Privileged RoleBinding

The node plugin part requires the container to run in privileged mode (see the
[sci deployment documentation](//kubernetes-csi.github.io/docs/deploying.html)).

This requires that the service account associated with it gets the adequate SCC
applied.

Add the `privileged` SCC to the service account (must be done as an admin):

```bash
oc adm policy add-scc-to-user privileged -z csi-rclone-nodeplugin -n csi-rclone
```

## csi-rclone deployment

With everything in place as listed in the previous steps, csi-rclone can now be
installed as usual

```bash
helm template --namespace csi-rclone csi-rclone renku/csi-rclone  -f values.yaml
```

Example values file:

```yaml
rbac:
  create: false
driver:
  create: false
storageClass:
  create: false

csiControllerRclone:
  rclone:
    image:
      repository: renku/csi-rclone
      tag: v0.5.0

csiNodepluginRclone:
  rclone:
    image:
      repository: renku/csi-rclone
      tag: v0.5.0
```
