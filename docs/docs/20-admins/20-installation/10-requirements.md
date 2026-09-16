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
