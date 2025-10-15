---
title: Installing on OpenShift
---

## Preparing the project

The helm installation operates the same on OpenShift as on Kubernetes however
there are some differences such as management of CRDs that must be done by an
administrator. There are also RBAC rules that must be put into place that will
require admin access.

### CRDs

Generate the CRDs out of the helm chart:

```bash
helm template --namespace renku renku renku/renku -f renku-values.yaml --set amalthea.deployCrd=true --set amalthea-sessions.deployCrd=true | yq e '. | select(.kind == "CustomResourceDefinition")' > renku-crds.yaml
```

:::warning
The above command will give you the CRDs from the latest version of Renku. But
you can select the CRDs from a specific Renku version by adding the
`--version=x.x.x` flag to the `helm template` command. Using the latest version
may cause problems or unpredictable behavior if you extract the resource from a
different version of Renku than what you have or will install.
:::

### RBAC

Generate the RBAC for renku-data-services:

```bash
 helm template --namespace renku renku renku/renku -f renku-values.yaml --set amalthea.deployCrd=true --set amalthea-session.deployCrd=true --set dataService.rbac.create=true| yq e '. | select(.kind == "*Role*" and (.metadata.name == "renku-data-service" or .metadata.name == "renku-k8s-watcher"))' > data-services-rbac.yaml
```

This is because renku-data-services requires ClusterRole and ClusterRoleBinding.
In this case, the Role and RoleBinding will also be handled by the admin as
making the distinction in the charts starts to make it overly complicated for
not much benefits.

:::warning
The above command will give you the RBAC from the latest version of Renku. But
you can select the RBAC from a specific Renku version by adding the
`--version=x.x.x` flag to the `helm template` command. Using the latest version
may cause problems or unpredictable behavior if you extract the resource from a
different version of Renku than what you have or will install.
:::

## Cluster access

Login

```bash
oc login --user <username> https://url.to.openshift.cluster --web
```

Create the project

```bash
oc new-project renku
```

## Optional:

If no certificate issuer is available, then a self signed issuer can be created.

Note that this require a ClusterIssuer and thus admin access.

Follow the [Cert-Manager documentation for SelfSigned](https://cert-manager.io/docs/configuration/selfsigned/)


## Prerequisite

### CRDs

Install CRDs generated earlier (as an admin):

```bash
oc apply -f renku-crds.yaml
```
### RBAC

Install the RBAC rules generated earlier (as an admin):

```bash
oc apply -f data-services-rbac.yaml
```

Depending on how many people will manage the Renku deployment(s), it will be
simpler to create a `Group` so that the required roles do not need to be updated
for each every person.

```yaml
kind: Group
apiVersion: user.openshift.io/v1
metadata:
  name: renku-admin
users:
  - user1
  - user2

```

As an admin:

```bash
oc apply -f - renku-admin.yaml
```

Required cluster roles, bindings and cluster role bindings for people installing
Renku:

```yaml
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: amaltheasession-manager
rules:
- apiGroups:
  - amalthea.dev
  resources:
  - amaltheasessions
  verbs:
  - create
  - delete
  - get
  - list
  - patch
  - update
  - watch
- apiGroups:
  - amalthea.dev
  resources:
  - amaltheasessions/finalizers
  verbs:
  - update
- apiGroups:
  - amalthea.dev
  resources:
  - amaltheasessions/status
  verbs:
  - get
  - patch
  - update
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: amaltheasession-manager-for-renku-admin
  namespace: renku
subjects:
  - kind: Group
    apiGroup: rbac.authorization.k8s.io
    name: renku-admin
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: amaltheasession-manager
---
kind: RoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: resourcesquotas-manager-for-renku-admin
subjects:
  - kind: Group
    apiGroup: rbac.authorization.k8s.io
    name: renku-admin
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: resourcesquotas-manager
---
kind: ClusterRole
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: priorityclasses-manager
rules:
- apiGroups:
  - "scheduling.k8s.io"
  resources:
  - priorityclasses
  verbs:
  - create
  - delete
  - get
  - list
  - patch
  - update
  - watch
---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: priorityclasses-manager-for-renku-admin
subjects:
  - kind: User
    apiGroup: rbac.authorization.k8s.io
    name: renku-admin
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: priorityclasses-manager
```

As an admin, setup renku project admin roles:

```bash
oc apply -f renku-roles.yaml
```

### Amalthea session service account

The default Security Context Constraint (SCC) used to start pods will not allow
for Amalthea sessions to run as it uses fixed user values. This means that
currently the `nonroot-v2` SCC must be used. This can be achieved through the
use of a service account with adequate configuration.

Create service account for amalthea-sessions to allow the use of a different
SCC:

```bash
oc --namespace renku create serviceaccount renku-amalthea-sessions-scc-handler
```

Add the required SCC to the service account (must be done as an admin)

```bash
oc adm policy add-scc-to-user nonroot-v2 -z renku-amalthea-sessions-scc-handler -n renku
```

To make use of this service account in your cluster, set the
`localClusterSessionServiceAccount` settings under `dataService` in the Helm chart values file.

```yaml
dataService:
  localClusterSessionServiceAccount: renku-amalthea-sessions-scc-handler
```

### Amalthea ingress

The configuration for the sessions ingress have their own settings (which are
similar to the one from the main ingress). In order to have them use the proper
class, their annotations must be updated:

```yaml
notebooks:
  sessionIngress:
    annotations:
      kubernetes.io/ingress.class: openshift-default
      # remove default nginx specific annotations
      nginx.ingress.kubernetes.io/proxy-body-size: null
      nginx.ingress.kubernetes.io/proxy-request-buffering: null
      nginx.ingress.kubernetes.io/proxy-buffer-size: null

```

### Network Policies:

There are some subtle differences in the DNS setup between a vanilla Kubernetes
cluster and OpenShift. Without diving in the details, the main issue is that the
NetworkPolicy for the AmaltheaSession objects created with Renku won't work as
is in OpenShift.

There are three (maybe four) things to take into account:

- The DNS is using port 5353
- The DNS resolver is in a different namespace
- The DNS resolver pods have a different label to match
- Depending on your network setup there might be IP blocks to unlock

Here is an example of a modified NetworkPolicy that adds port 5353 to the list of
allowed ports as well as updates the namespace and pod selectors to reach the
appropriate dns pods.

It also shows how to unlock part of the internal network to make it accessible to
AmaltheaSession. That last point is completely optional but might be useful
depending on the network setup of the infrastructure hosting the cluster.

```yaml
# values.yaml
networkPolicies:
  sessions:
    egress:
      - ports:
        - port: 53
          protocol: UDP
        - port: 53
          protocol: TCP
        - port: 5353
          protocol: UDP
        - port: 5353
          protocol: TCP
        to:
          - namespaceSelector:
              matchLabels:
                kubernetes.io/metadata.name: openshift-dns
            podSelector:
              matchLabels:
                dns.operator.openshift.io/daemonset-dns: default
      - to:
        - ipBlock:
            cidr: 0.0.0.0/0
            except:
            - 10.0.0.0/8
            - 172.16.0.0/12
            - 192.168.0.0/16
      # Optional: unlock access to part of the internal network
      - to:
        - ipBlock:
            cidr: 172.31.0.0/16
```

## Renku deployment

With everything in place as listed in the previous steps, Renku can now be
installed as usual

```bash
helm upgrade  --install --namespace renku renku renk/renku -f renku-values.yaml --timeout 1800s --skip-crds
```

:::info

Any Renku user can be made a Renku administrator. This is useful for setting up
different global environments, resource pools or integrations. In addition,
Renku administrators can access the projects and similar resources of any user
in the platform. The documentation for assigning the Renku administrator role
can be found [here](/docs/admins/operation/user-management).

:::

## Post-deployment

Currently, Prometheus integration in `renku-data-service` and `renku-k8s-watcher`
uses `/prometheus` as work folder. This folder is not writable as is on
OpenShift due to the user/group id handling. As a simple solution, apply the
following two commands to point the integration to `/tmp`.

```bash
oc -n renku set env deployment/renku-data-service -c data-service prometheus_multiproc_dir=/tmp
oc -n renku set env deployment/renku-k8s-watcher -c k8s-watcher prometheus_multiproc_dir=/tmp
```
:::info

Not doing it will have no consequences on normal functionality outside of
Prometheus however it will have an impact on the logs as errors will be shown
there making things harder to debug in case of problems.

:::
