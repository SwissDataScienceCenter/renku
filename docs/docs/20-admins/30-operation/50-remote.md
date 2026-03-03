---
title: Remote Clusters
---

:::warning[Under construction]
This is a first write-up, we welcome any constructive feedback to improve this documentation.
:::

The following describes how to setup a cluster to be able to run Renku sessions, and connect it to a Renku web portal.

## Requirements

### Network

- [ ] Ingress from public Internet
  - [ ] load balancer 6443 to 6443 on all cluster nodes (Kubectl API)
  - [ ] load balancer 443 to 443 on all worker nodes for HTTPS access to the user session (JupiterLab or Visual Studio Code for example)
  - [ ] CertManager to provide valid certificates to the user sessions
- [ ] Worker nodes need internet access to retrieve code, container images, and data from repositories
- [ ] Egress setup: Allow traffic to the main cluster and Internet, while preventing access to any cluster services.
  ```yaml
  apiVersion: networking.k8s.io/v1
  kind: NetworkPolicy
  metadata:
    name: egress-from-renku-v2-sessions
  spec:
    egress:
      - to:
          # DNS resolution
          - namespaceSelector:
              matchLabels:
                kubernetes.io/metadata.name: kube-system
            podSelector:
              matchLabels:
                k8s-app: kube-dns
        ports:
          - port: 53
            protocol: UDP
          - port: 53
            protocol: TCP
      - to:
          # Allow access to any port/protocol as long as it is directed
          # outside of the cluster. This is done by excluding
          # IP ranges which are reserved for private networking from
          # the allowed range.
          - ipBlock:
              cidr: 0.0.0.0/0
              except:
                - 10.0.0.0/8
                - 172.16.0.0/12
                - 192.168.0.0/16
    podSelector:
      matchLabels:
        app.kubernetes.io/created-by: controller-manager
        app.kubernetes.io/name: AmaltheaSession
    policyTypes:
      - Egress
  ```

### Priority classes

This will be the priority class(es) we can reference when defining quota requirements / limits.

We recommend to create at least one dedicated priority class for the user sessions.

Here as an example on how to create one, adapt as required:

- ```bash
  kubectl create priorityclass renku-user-sessions-priority --value=1000 --description="default priority for renku sessions"
  ```

### Storage classes

#### CSI-driver with automatic provisioning support on your cluster

_If you already have automatic volume provisioning setup in your cluster, you may skip this._

For example, to install cinder-csi:

```bash
helm repo add cinder-csi <https://kubernetes.github.io/cloud-provider-openstack>
helm install --create-namespace --namespace storage cinder-csi/openstack-cinder-csi --version 2.2.0 -g
```

The storage class associated will be used to provide the working directory of the user session containers.

## User Session Nodes Configuration

User session scheduling is based on label and taints to select nodes where to run the pod associated with each user session.

### Labels

- [ ] `renku.io/node-purpose: user` User sessions are scheduled only on nodes with this label
- [ ] Extra labels to differentiate node pools as required

### Taints

- [ ] Taints to differentiate node pools as required

## Remote Connection for RenkuLab Services

- [ ] Dedicated **Namespace** for the user sessions (for example `renku-user-sessions`)
- [ ] Dedicated **ServiceAccount** (for example `renku-session-manager`) with the following rights

  ```yaml
  apiVersion: rbac.authorization.k8s.io/v1
  kind: Role
  metadata:
    name: renku-session-manager
    namespace: renku-user-sessions
  rules:
    - apiGroups:
        - ""
      resources:
        - pods
        - pods/log
        - services
        - endpoints
        - secrets
        - priorityclasses
        - resourcequotas
      verbs:
        - get
        - list
        - watch
    - apiGroups:
        - ""
      resources:
        - pods
        - secrets
      verbs:
        - delete
    - apiGroups:
        - apps
      resources:
        - statefulsets
      verbs:
        - get
        - list
        - watch
        - patch
    - apiGroups:
        - ""
      resources:
        - secrets
        - resourcequotas
      verbs:
        - create
        - update
        - delete
        - patch
    - apiGroups:
        - scheduling.k8s.io
      resources:
        - priorityclasses
      verbs:
        - get
        - list
        - watch
    - apiGroups:
        - amalthea.dev
      resources:
        - amaltheasessions
      verbs:
        - create
        - update
        - delete
        - patch
        - list
        - get
        - watch
  ```

## Deploy the User Session Operator (AmaltheaSession)

- [ ] Retrieve the helm chart repository:

  ```bash
  helm repo add renku https://swissdatasciencecenter.github.io/helm-charts
  helm repo update
  ```

- [ ] User session operator in the `renku-user-sessions` dedicated namespace:

  ```bash
  helm install \
    --generate-name \
    --create-namespace \
    --namespace renku-user-sessions \
    renku/amalthea-sessions
  ```

- [ ] CSI-rClone Operator (for remote storage)

  ```bash
  helm install --set-json='csiNodepluginRclone.tolerations=[{"effect": "NoSchedule", "operator": "Exists"}]' csi-rclone renku/csi-rclone
  ```

## Configuration parameters

Send to the **RenkuLab Administrators** the following parameters

- For the connection
  - [ ] **Namespace** to use
  - [ ] **ServiceAccount** name & authentication token
  - [ ] **Base URL** of the user session running on the remote cluster
- For the sessions
  - [ ] The name(s) of your **storage classes** and their role
  - [ ] The name(s) of your **priority classes** and their role
  - [ ] The name(s) of your **resource classes**, and for each class:
    - [ ] The priority classe it should be associated with
    - [ ] The label(s) to use to select specific cluster nodes
    - [ ] The taint(s) to tolerate
    - [ ] The requests in terms of CPU, RAM, GPUs, default and maximum storage size
- HTTPS Certificates
  - [ ] The name of the **secret** generated by CertManager, or if we should use the default provider of your cluster
