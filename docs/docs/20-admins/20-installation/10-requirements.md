---
title: Requirements
---

## Kubernetes or Openshift

It is recommended to have a maintained version that is either actively
supported or has maintenance support. Older versions may work but
it is not safe operating Kubernetes versions that are unsupported.

You can check active releases [here](https://kubernetes.io/releases/).

Similar to Kubernetes an actively supported version of Openshift or OKD
is acceptable.

## Harbor and Shipwright

Renku can host and build images for users. This is an optional but incredibly
useful feature therefore it is strongly recommended that you install
both of these projects in your cluster before installing Renku.

Links to each project:

- [Harbor](https://goharbor.io/)
- [Shipwright](https://shipwright.io/)

We recommend installing both via their respective Helm charts.

## Storage

You should have a container storage interface (CSI) driver which supports
dynamic volume provisioning. If you use managed Kubernetes on any public cloud
you will most likely get this out of the box. Dynamic volume provisioning
allows the storage driver to create volumes from persistent volume claims (PVC).

To find out if you have dynamic volume provisioning you can run the following
command:

```
kubectl get storageclass
```

This should return at least one storage class, if nothing is returned it means
that you do not have a storage driver in your cluster that supports dynamic volume
provisioning.

:::note

The `csi-rclone` storage class should not be used for provisioning storage for sessions.
It is used by Renku to mount data connectors with data hosted on cloud storage. But its
performance is not sufficient to host the local filesystem for a session.

:::

## PostgreSQL

Renku stores its data in PostgreSQL, but does not deploy one. You provide the instance and point
Renku at it through `global.externalServices.postgresql`. It can live in the Renku namespace,
elsewhere in the cluster, or be a managed database from your cloud provider. 

### What Renku needs

- The instance is reachable from the Renku namespace on port 5432.
- The configured user is a superuser. Renku creates its own databases and roles, it does not
  expect them to exist.
- If the database runs *in* the Renku namespace, it needs a NetworkPolicy of its own. Renku
  installs a `default-deny-all-ingress` policy that selects **every** pod in the namespace, so
  without one nothing will be able to connect.

Then point Renku at it:

```yaml
global:
  externalServices:
    postgresql:
      host: postgres.example.org
      username: postgres
      # either an inline password, or a secret that holds one, not both
      existingSecret: my-postgres-credentials
      existingSecretPasswordKey: password
```

### Running it with CloudNativePG

The rest of this section is the recommended setup, and is one way of satisfying the
above. [CloudNativePG](https://cloudnative-pg.io/) manages PostgreSQL through an operator.
Skip it if you already have a database.

#### The operator

CloudNativePG is cluster-wide and installed once, independently of Renku:

```console
$ helm repo add cnpg https://cloudnative-pg.github.io/charts
$ helm upgrade --install cnpg --namespace cnpg-system --create-namespace cnpg/cloudnative-pg
```

#### The cluster

`enableSuperuserAccess` is off by default, and CloudNativePG *deletes* the superuser secret when
it is off. Renku's setup jobs need that user, so turn it on.

```yaml
apiVersion: postgresql.cnpg.io/v1
kind: Cluster
metadata:
  name: renku-pg
spec:
  instances: 1
  enableSuperuserAccess: true
  storage:
    size: 8Gi
```

Size the storage, instance count and [backups](https://cloudnative-pg.io/documentation/current/backup/)
for your deployment.

#### Network policy

The policy to make the cluster reachable in the Renku namespace. The operator rule is
CloudNativePG specific, the rest is just the list of Renku pods that talk to the database.

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: renku-pg-ingress
spec:
  podSelector:
    matchLabels:
      cnpg.io/cluster: renku-pg
  policyTypes:
    - Ingress
  ingress:
    - from:
        - podSelector:
            matchExpressions:
              - key: app
                operator: In
                values:
                  - renku-data-service
                  - renku-data-tasks
                  - renku-k8s-watcher
                  - renku-authz
                  - renku-secrets-storage
                  # the three jobs that create the databases and roles
                  - postgres-setup
        - podSelector: {matchLabels: {app.kubernetes.io/name: keycloakx}}
        # peer instances, when spec.instances > 1
        - podSelector: {matchLabels: {cnpg.io/cluster: renku-pg}}
      ports:
        - {protocol: TCP, port: 5432}
    # the operator polls each instance on 8000, without this the Cluster never reports ready
    - from:
        - namespaceSelector:
            matchLabels:
              kubernetes.io/metadata.name: cnpg-system
          podSelector:
            matchLabels:
              app.kubernetes.io/name: cloudnative-pg
      ports:
        - {protocol: TCP, port: 5432}
        - {protocol: TCP, port: 8000}
```

Create the policy before the `Cluster` to let the operator reach the instance it is starting.

#### Renku values

Point Renku at the read-write service and the secret the operator generates. CloudNativePG keeps
the superuser password in `<cluster>-superuser` under the key `password`, hence
`existingSecretPasswordKey` below.

```yaml
global:
  externalServices:
    postgresql:
      host: renku-pg-rw
      username: postgres
      existingSecret: renku-pg-superuser
      existingSecretPasswordKey: password
```

:::note

Coming from a Renku version that bundled the bitnami `postgresql` chart? Follow
[the migration guide](https://github.com/SwissDataScienceCenter/renku/blob/master/helm-chart/utils/postgres_migrations/bitnami-to-cnpg.md)
before upgrading, it covers moving the existing data into the new cluster.

:::

## Ingress

You should have a functioning ingress controller in your cluster. If you use managed
Kubernetes on public cloud then your cloud provider can provide more information on
whether one is there "out-of-the-box" or you have to install one. On managed Kubernetes
the specific cloud provider will likely have documentation on which ingress controller
you should use and how to install it.

If you are free to choose then we have used [ingress-nginx](https://kubernetes.github.io/ingress-nginx/)
in production without any problems. But there are many others in addition to ingress-nginx.

## TLS certificates

We use [cert-manager](https://cert-manager.io/) in production combined with
[Let's Encrypt/ACME](https://cert-manager.io/docs/configuration/acme/) which allows us to
get TLS certificates provisioned and renewed automatically.

However, there are many alternatives to Let's Encrypt and ACME
(some requiring more manual intervention than others) which are also acceptable.

## Local CLI and similar useful tools

- [kubectl](https://kubernetes.io/docs/reference/kubectl/)
- [helm](https://helm.sh/docs/intro/install/)

In addition to this we strongly recommend a Gitops approach to managing Renku in production.
We use [flux](https://fluxcd.io/) in production for this purpose but there are several other alternatives.

Other tools for operation and monitoring that are not mandatory but really useful:

- [k9s](https://k9scli.io/) and/or [headlamp](https://headlamp.dev/)
- [kubens and kubectx](https://github.com/ahmetb/kubectx)
- Shell prompt that shows the currently active Kubernetes context and namespace,
  for example there is [starship](https://starship.rs/) but there are also many others.

## Knative

[Knative](https://knative.dev/) is only required to be installed if you intend to use Renku Apps.
Renku creates one Knative `Service` per app, so both the CRDs and the Knative **Serving** control
plane have to be present before you enable the feature. Knative Eventing and Functions are not
used.

We install Knative through the [Knative operator](https://knative.dev/docs/install/operator/knative-with-operators/)
and currently run Serving 1.16.

Knative needs a networking layer of its own to program routes for apps. The operator does not
install one, so this is a separate step. We recommend
[`net-gateway-api`](https://github.com/knative-extensions/net-gateway-api), which puts apps behind a
[Gateway API](https://gateway-api.sigs.k8s.io/) gateway with Kourier disabled. That is what we run.
Knative's other supported layers work too, but the apps documentation assumes this one.

You will therefore need a Gateway API implementation in the cluster, either one you run yourself
or a managed one from your cloud provider. The nginx ingress that serves Renku itself is **not** a Knative
networking layer, and apps do not pass through it, so it cannot be reused for this.

:::warning

Install and verify Knative _before_ you enable apps in the Helm chart. The k8s watcher starts
watching Knative Services as soon as the feature is enabled, and it cannot establish that watch
in a cluster where the CRD does not exist.

:::

Knative also gates several pod spec fields behind feature flags that Renku's apps rely on. See
[Configuration](configuration#knative) for the flags and how to set them.

Apps are served from a domain of their own, with a hostname per app, so that domain needs DNS and
TLS that cover names created on the fly. How you arrange that is a Knative concern rather than a
Renku one, so it is covered in
[Configuration](configuration#3-configure-the-apps-domain). Read that section before you provision
anything, because the choice of domain has security consequences that are awkward to undo.
